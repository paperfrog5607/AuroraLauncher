package org.aurora.launcher.modpack.instance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InstanceManager {
    
    private static final Logger logger = LoggerFactory.getLogger(InstanceManager.class);
    private static final String INSTANCE_CONFIG_FILE = "instance.json";
    
    private final Path instancesDir;
    private final Map<String, Instance> instances = new ConcurrentHashMap<>();
    private final Gson gson;
    private final List<InstanceListener> listeners = new CopyOnWriteArrayList<>();
    
    public InstanceManager(Path instancesDir) {
        this.instancesDir = instancesDir;
        this.gson = createGson();
    }
    
    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) -> 
                        context.serialize(src.toString()))
                .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, typeOfT, context) -> 
                        Instant.parse(json.getAsString()))
                .setPrettyPrinting()
                .create();
    }
    
    public void loadInstances() throws IOException {
        if (!Files.exists(instancesDir)) {
            Files.createDirectories(instancesDir);
            return;
        }
        
        try (Stream<Path> dirs = Files.list(instancesDir)) {
            dirs.filter(Files::isDirectory)
                .forEach(this::loadInstance);
        }
        
        logger.info("Loaded {} instances", instances.size());
    }
    
    private void loadInstance(Path instanceDir) {
        Path configFile = instanceDir.resolve(INSTANCE_CONFIG_FILE);
        if (!Files.exists(configFile)) {
            logger.warn("No config file found in {}", instanceDir);
            return;
        }
        
        try (Reader reader = Files.newBufferedReader(configFile)) {
            Instance instance = gson.fromJson(reader, Instance.class);
            instance.setInstanceDir(instanceDir);
            instances.put(instance.getId(), instance);
            logger.debug("Loaded instance: {}", instance.getName());
        } catch (Exception e) {
            logger.error("Failed to load instance from {}: {}", instanceDir, e.getMessage());
        }
    }
    
    public CompletableFuture<Instance> create(InstanceBuilder builder) {
        return CompletableFuture.supplyAsync(() -> {
            Instance instance = builder.build();
            
            InstanceValidator validator = new InstanceValidator();
            InstanceValidator.ValidationResult result = validator.validate(instance);
            if (!result.isValid()) {
                throw new RuntimeException("Invalid instance: " + result.getErrors());
            }
            
            Path instanceDir = instance.getInstanceDir();
            if (instanceDir == null) {
                instanceDir = instancesDir.resolve(instance.getId());
                instance.setInstanceDir(instanceDir);
            }
            
            try {
                createInstanceDirectory(instance);
                saveInstanceConfig(instance);
                instances.put(instance.getId(), instance);
                notifyListeners(InstanceEvent.CREATED, instance);
                logger.info("Created instance: {}", instance.getName());
                return instance;
            } catch (IOException e) {
                throw new RuntimeException("Failed to create instance: " + e.getMessage(), e);
            }
        });
    }
    
    private void createInstanceDirectory(Instance instance) throws IOException {
        Path instanceDir = instance.getInstanceDir();
        Files.createDirectories(instanceDir);
        
        Path minecraftDir = instance.getMinecraftDir();
        Files.createDirectories(minecraftDir);
        Files.createDirectories(instance.getModsDir());
        Files.createDirectories(instance.getConfigDir());
        Files.createDirectories(instance.getSavesDir());
    }
    
    private void saveInstanceConfig(Instance instance) throws IOException {
        Path configFile = instance.getInstanceDir().resolve(INSTANCE_CONFIG_FILE);
        try (Writer writer = Files.newBufferedWriter(configFile)) {
            gson.toJson(instance, writer);
        }
    }
    
    public CompletableFuture<Void> delete(String instanceId) {
        return CompletableFuture.runAsync(() -> {
            Instance instance = instances.get(instanceId);
            if (instance == null) {
                throw new RuntimeException("Instance not found: " + instanceId);
            }
            
            try {
                deleteDirectory(instance.getInstanceDir());
                instances.remove(instanceId);
                notifyListeners(InstanceEvent.DELETED, instance);
                logger.info("Deleted instance: {}", instance.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete instance: " + e.getMessage(), e);
            }
        });
    }
    
    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            logger.warn("Failed to delete: {}", p);
                        }
                    });
            }
        }
    }
    
    public CompletableFuture<Instance> clone(String instanceId, String newName) {
        return CompletableFuture.supplyAsync(() -> {
            Instance original = instances.get(instanceId);
            if (original == null) {
                throw new RuntimeException("Instance not found: " + instanceId);
            }
            
            InstanceBuilder builder = new InstanceBuilder()
                    .name(newName)
                    .version(original.getVersion())
                    .minecraftVersion(original.getConfig().getMinecraftVersion())
                    .loaderType(original.getConfig().getLoaderType())
                    .loaderVersion(original.getConfig().getLoaderVersion())
                    .tags(original.getTags());
            
            Instance cloned = builder.build();
            
            try {
                createInstanceDirectory(cloned);
                
                Path originalDir = original.getInstanceDir();
                Path clonedDir = cloned.getInstanceDir();
                
                copyDirectory(originalDir.resolve(".minecraft"), clonedDir.resolve(".minecraft"));
                
                saveInstanceConfig(cloned);
                instances.put(cloned.getId(), cloned);
                notifyListeners(InstanceEvent.CREATED, cloned);
                logger.info("Cloned instance {} to {}", original.getName(), newName);
                return cloned;
            } catch (IOException e) {
                throw new RuntimeException("Failed to clone instance: " + e.getMessage(), e);
            }
        });
    }
    
    private void copyDirectory(Path source, Path target) throws IOException {
        if (!Files.exists(source)) return;
        
        Files.walk(source).forEach(sourcePath -> {
            try {
                Path targetPath = target.resolve(source.relativize(sourcePath));
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.warn("Failed to copy: {}", sourcePath);
            }
        });
    }
    
    public CompletableFuture<Void> rename(String instanceId, String newName) {
        return CompletableFuture.runAsync(() -> {
            Instance instance = instances.get(instanceId);
            if (instance == null) {
                throw new RuntimeException("Instance not found: " + instanceId);
            }
            
            String oldName = instance.getName();
            instance.setName(newName);
            
            try {
                saveInstanceConfig(instance);
                notifyListeners(InstanceEvent.UPDATED, instance);
                logger.info("Renamed instance from {} to {}", oldName, newName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to rename instance: " + e.getMessage(), e);
            }
        });
    }
    
    public Optional<Instance> getInstance(String instanceId) {
        return Optional.ofNullable(instances.get(instanceId));
    }
    
    public List<Instance> getAllInstances() {
        return new ArrayList<>(instances.values());
    }
    
    public List<Instance> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllInstances();
        }
        
        String lowerQuery = query.toLowerCase();
        return instances.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(lowerQuery) ||
                            (i.getTags() != null && i.getTags().stream()
                                .anyMatch(t -> t.toLowerCase().contains(lowerQuery))))
                .collect(Collectors.toList());
    }
    
    public CompletableFuture<Void> update(Instance instance) {
        return CompletableFuture.runAsync(() -> {
            if (!instances.containsKey(instance.getId())) {
                throw new RuntimeException("Instance not found: " + instance.getId());
            }
            
            try {
                saveInstanceConfig(instance);
                instances.put(instance.getId(), instance);
                notifyListeners(InstanceEvent.UPDATED, instance);
                logger.info("Updated instance: {}", instance.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to update instance: " + e.getMessage(), e);
            }
        });
    }
    
    public CompletableFuture<Void> repair(Instance instance) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path minecraftDir = instance.getMinecraftDir();
                if (!Files.exists(minecraftDir)) {
                    Files.createDirectories(minecraftDir);
                }
                
                Path modsDir = instance.getModsDir();
                if (!Files.exists(modsDir)) {
                    Files.createDirectories(modsDir);
                }
                
                Path configDir = instance.getConfigDir();
                if (!Files.exists(configDir)) {
                    Files.createDirectories(configDir);
                }
                
                Path savesDir = instance.getSavesDir();
                if (!Files.exists(savesDir)) {
                    Files.createDirectories(savesDir);
                }
                
                saveInstanceConfig(instance);
                notifyListeners(InstanceEvent.UPDATED, instance);
                logger.info("Repaired instance: {}", instance.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to repair instance: " + e.getMessage(), e);
            }
        });
    }
    
    public void addInstanceListener(InstanceListener listener) {
        listeners.add(listener);
    }
    
    public void removeInstanceListener(InstanceListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(InstanceEvent event, Instance instance) {
        for (InstanceListener listener : listeners) {
            try {
                listener.onInstanceEvent(event, instance);
            } catch (Exception e) {
                logger.warn("Listener error: {}", e.getMessage());
            }
        }
    }
    
    public Path getInstancesDir() {
        return instancesDir;
    }
    
    public int getInstanceCount() {
        return instances.size();
    }
    
    public enum InstanceEvent {
        CREATED, UPDATED, DELETED
    }
    
    @FunctionalInterface
    public interface InstanceListener {
        void onInstanceEvent(InstanceEvent event, Instance instance);
    }
}