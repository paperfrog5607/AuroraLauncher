package org.aurora.launcher.modpack.template;

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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateManager.class);
    private static final String TEMPLATE_EXTENSION = ".json";
    
    private final Path templatesDir;
    private final Map<String, Template> templates = new HashMap<>();
    private final Gson gson;
    
    public TemplateManager(Path templatesDir) {
        this.templatesDir = templatesDir;
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
    
    public void loadTemplates() throws IOException {
        if (!Files.exists(templatesDir)) {
            Files.createDirectories(templatesDir);
            return;
        }
        
        try (Stream<Path> files = Files.list(templatesDir)) {
            files.filter(p -> p.toString().endsWith(TEMPLATE_EXTENSION))
                 .forEach(this::loadTemplate);
        }
        
        logger.info("Loaded {} templates", templates.size());
    }
    
    private void loadTemplate(Path templateFile) {
        try (Reader reader = Files.newBufferedReader(templateFile)) {
            Template template = gson.fromJson(reader, Template.class);
            templates.put(template.getId(), template);
            logger.debug("Loaded template: {}", template.getName());
        } catch (Exception e) {
            logger.error("Failed to load template from {}: {}", templateFile, e.getMessage());
        }
    }
    
    public CompletableFuture<Template> saveTemplate(Template template) {
        return CompletableFuture.supplyAsync(() -> {
            if (template.getId() == null || template.getId().isEmpty()) {
                template.setId(UUID.randomUUID().toString());
            }
            
            Path templateFile = templatesDir.resolve(template.getName() + TEMPLATE_EXTENSION);
            
            try {
                Files.createDirectories(templatesDir);
                
                try (Writer writer = Files.newBufferedWriter(templateFile)) {
                    gson.toJson(template, writer);
                }
                
                templates.put(template.getId(), template);
                logger.info("Saved template: {}", template.getName());
                return template;
            } catch (IOException e) {
                throw new RuntimeException("Failed to save template: " + e.getMessage(), e);
            }
        });
    }
    
    public CompletableFuture<Void> deleteTemplate(String templateId) {
        return CompletableFuture.runAsync(() -> {
            Template template = templates.get(templateId);
            if (template == null) {
                throw new RuntimeException("Template not found: " + templateId);
            }
            
            Path templateFile = templatesDir.resolve(template.getName() + TEMPLATE_EXTENSION);
            
            try {
                Files.deleteIfExists(templateFile);
                templates.remove(templateId);
                logger.info("Deleted template: {}", template.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete template: " + e.getMessage(), e);
            }
        });
    }
    
    public Optional<Template> getTemplate(String templateId) {
        return Optional.ofNullable(templates.get(templateId));
    }
    
    public Optional<Template> getTemplateByName(String name) {
        return templates.values().stream()
                .filter(t -> t.getName().equals(name))
                .findFirst();
    }
    
    public List<Template> getAllTemplates() {
        return new ArrayList<>(templates.values());
    }
    
    public List<Template> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTemplates();
        }
        
        String lowerQuery = query.toLowerCase();
        return templates.values().stream()
                .filter(t -> t.getName().toLowerCase().contains(lowerQuery) ||
                            (t.getDescription() != null && t.getDescription().toLowerCase().contains(lowerQuery)) ||
                            (t.getTags() != null && t.getTags().stream()
                                .anyMatch(tag -> tag.toLowerCase().contains(lowerQuery))))
                .collect(Collectors.toList());
    }
    
    public List<Template> getByMinecraftVersion(String mcVersion) {
        return templates.values().stream()
                .filter(t -> mcVersion.equals(t.getMinecraftVersion()))
                .collect(Collectors.toList());
    }
    
    public List<Template> getByLoaderType(String loaderType) {
        return templates.values().stream()
                .filter(t -> loaderType.equalsIgnoreCase(t.getLoaderType()))
                .collect(Collectors.toList());
    }
    
    public List<Template> getByTag(String tag) {
        return templates.values().stream()
                .filter(t -> t.getTags() != null && t.getTags().contains(tag))
                .collect(Collectors.toList());
    }
    
    public CompletableFuture<Template> createFromInstance(org.aurora.launcher.modpack.instance.Instance instance, 
                                                          String name, String description) {
        return CompletableFuture.supplyAsync(() -> {
            Template template = new TemplateBuilder()
                    .name(name)
                    .description(description)
                    .fromInstance(instance)
                    .build();
            
            return saveTemplate(template).join();
        });
    }
    
    public Path getTemplatesDir() {
        return templatesDir;
    }
    
    public int getTemplateCount() {
        return templates.size();
    }
}