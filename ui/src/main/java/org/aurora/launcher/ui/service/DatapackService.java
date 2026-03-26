package org.aurora.launcher.ui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DatapackService {
    private static final Logger logger = LoggerFactory.getLogger(DatapackService.class);
    private static DatapackService instance;
    
    private Path currentDatapackPath;
    private String currentNamespace;
    private final Set<String> enabledModDatapacks = new LinkedHashSet<>();
    private final Map<String, Path> modDatapackPaths = new ConcurrentHashMap<>();
    private boolean hasUnsavedChanges = false;
    
    public static DatapackService getInstance() {
        if (instance == null) {
            instance = new DatapackService();
        }
        return instance;
    }
    
    private DatapackService() {}
    
    public void createNew(String namespace) {
        currentNamespace = namespace;
        currentDatapackPath = Paths.get("datapacks", "custom", "data", namespace);
        
        try {
            Files.createDirectories(currentDatapackPath.resolve("recipes"));
            Files.createDirectories(currentDatapackPath.resolve("loot_tables").resolve("blocks"));
            Files.createDirectories(currentDatapackPath.resolve("loot_tables").resolve("entities"));
            Files.createDirectories(currentDatapackPath.resolve("advancements"));
            Files.createDirectories(currentDatapackPath.resolve("tags").resolve("blocks"));
            Files.createDirectories(currentDatapackPath.resolve("tags").resolve("items"));
            Files.createDirectories(currentDatapackPath.resolve("tags").resolve("entity_types"));
            Files.createDirectories(currentDatapackPath.resolve("functions"));
            
            logger.info("Created new datapack namespace: {}", namespace);
        } catch (Exception e) {
            logger.error("Failed to create datapack: {}", namespace, e);
        }
    }
    
    public void open(Path datapackPath) {
        if (!Files.exists(datapackPath)) {
            logger.warn("Datapack not found: {}", datapackPath);
            return;
        }
        
        this.currentDatapackPath = datapackPath;
        this.currentNamespace = datapackPath.getFileName().toString();
    }
    
    public void scanModDatapacks(Path modsPath, ModDetectionService modDetection) {
        modDatapackPaths.clear();
        
        if (!Files.exists(modsPath)) {
            logger.warn("Mods path not found: {}", modsPath);
            return;
        }
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsPath, "*.jar")) {
            for (Path jar : stream) {
                String modId = detectModIdFromJar(jar);
                if (modId != null) {
                    Path datapackInMod = scanJarForDatapacks(jar);
                    if (datapackInMod != null) {
                        modDatapackPaths.put(modId, datapackInMod);
                        if (!enabledModDatapacks.contains(modId)) {
                            enabledModDatapacks.add(modId);
                        }
                        logger.info("Found datapack in mod {}: {}", modId, datapackInMod);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to scan mod datapacks", e);
        }
        
        logger.info("Found {} mods with datapacks", modDatapackPaths.size());
    }
    
    private String detectModIdFromJar(Path jarPath) {
        return ModDetectionService.getInstance()
            .detectModInfo(jarPath)
            .getModId();
    }
    
    private Path scanJarForDatapacks(Path jarPath) {
        return null;
    }
    
    public void setModDatapackVisible(String modId, boolean visible) {
        if (visible) {
            enabledModDatapacks.add(modId);
        } else {
            enabledModDatapacks.remove(modId);
        }
    }
    
    public boolean isModDatapackVisible(String modId) {
        return enabledModDatapacks.contains(modId);
    }
    
    public Set<String> getVisibleModDatapacks() {
        return new LinkedHashSet<>(enabledModDatapacks);
    }
    
    public Map<String, Path> getAllModDatapackPaths() {
        return new HashMap<>(modDatapackPaths);
    }
    
    public Path getCurrentPath() {
        return currentDatapackPath;
    }
    
    public String getCurrentNamespace() {
        return currentNamespace;
    }
    
    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }
    
    public void markChanged() {
        hasUnsavedChanges = true;
    }
    
    public List<Path> getFiles(String type) {
        List<Path> files = new ArrayList<>();
        
        if (currentDatapackPath != null) {
            Path typePath = currentDatapackPath.resolve(type);
            if (Files.exists(typePath)) {
                try {
                    Files.walk(typePath)
                        .filter(Files::isRegularFile)
                        .forEach(files::add);
                } catch (Exception e) {
                    logger.error("Failed to list files in: {}", typePath, e);
                }
            }
        }
        
        return files;
    }
    
    public void saveRecipe(String name, String json) {
        if (currentDatapackPath == null) {
            logger.warn("No datapack is currently open");
            return;
        }
        
        try {
            Path recipePath = currentDatapackPath.resolve("recipes").resolve(name + ".json");
            Files.createDirectories(recipePath.getParent());
            Files.writeString(recipePath, json);
            hasUnsavedChanges = true;
            logger.info("Saved recipe: {}", name);
        } catch (Exception e) {
            logger.error("Failed to save recipe: {}", name, e);
        }
    }
    
    public void saveLootTable(String type, String name, String json) {
        if (currentDatapackPath == null) {
            return;
        }
        
        try {
            Path lootPath = currentDatapackPath.resolve("loot_tables").resolve(type).resolve(name + ".json");
            Files.createDirectories(lootPath.getParent());
            Files.writeString(lootPath, json);
            hasUnsavedChanges = true;
            logger.info("Saved loot table: {}/{}", type, name);
        } catch (Exception e) {
            logger.error("Failed to save loot table: {}/{}", type, name, e);
        }
    }
    
    public void saveAdvancement(String name, String json) {
        if (currentDatapackPath == null) {
            return;
        }
        
        try {
            Path advPath = currentDatapackPath.resolve("advancements").resolve(name + ".json");
            Files.createDirectories(advPath.getParent());
            Files.writeString(advPath, json);
            hasUnsavedChanges = true;
            logger.info("Saved advancement: {}", name);
        } catch (Exception e) {
            logger.error("Failed to save advancement: {}", name, e);
        }
    }
    
    public void saveTag(String type, String name, String json) {
        if (currentDatapackPath == null) {
            return;
        }
        
        try {
            Path tagPath = currentDatapackPath.resolve("tags").resolve(type).resolve(name + ".json");
            Files.createDirectories(tagPath.getParent());
            Files.writeString(tagPath, json);
            hasUnsavedChanges = true;
            logger.info("Saved tag: {}/{}", type, name);
        } catch (Exception e) {
            logger.error("Failed to save tag: {}/{}", type, name, e);
        }
    }
    
    public String generateShapedRecipe(String output, int count, Map<Character, String> ingredients, String... pattern) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"type\": \"minecraft:crafting_shaped\",\n");
        sb.append("  \"pattern\": [\n");
        for (int i = 0; i < pattern.length; i++) {
            sb.append("    \"").append(pattern[i]).append("\"");
            if (i < pattern.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");
        sb.append("  \"key\": {\n");
        int idx = 0;
        for (Map.Entry<Character, String> entry : ingredients.entrySet()) {
            sb.append("    \"").append(entry.getKey()).append("\": {\n");
            sb.append("      \"item\": \"").append(entry.getValue()).append("\"\n");
            sb.append("    }");
            if (idx < ingredients.size() - 1) sb.append(",");
            sb.append("\n");
            idx++;
        }
        sb.append("  },\n");
        sb.append("  \"result\": {\n");
        sb.append("    \"item\": \"").append(output).append("\"");
        if (count > 1) sb.append(",\n    \"count\": ").append(count);
        sb.append("\n  }\n");
        sb.append("}");
        return sb.toString();
    }
    
    public String generateShapelessRecipe(String output, int count, List<String> ingredients) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"type\": \"minecraft:crafting_shapeless\",\n");
        sb.append("  \"ingredients\": [\n");
        for (int i = 0; i < ingredients.size(); i++) {
            sb.append("    {\"item\": \"").append(ingredients.get(i)).append("\"}");
            if (i < ingredients.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");
        sb.append("  \"result\": {\n");
        sb.append("    \"item\": \"").append(output).append("\"");
        if (count > 1) sb.append(",\n    \"count\": ").append(count);
        sb.append("\n  }\n");
        sb.append("}");
        return sb.toString();
    }
    
    public String generateFurnaceRecipe(String output, String input, String experience, String cookingTime) {
        return String.format("""
            {
              "type": "minecraft:furnace",
              "ingredient": {
                "item": "%s"
              },
              "result": "%s",
              "experience": %s,
              "cookingtime": %s
            }
            """, input, output, experience != null ? experience : "0", cookingTime != null ? cookingTime : "200");
    }
    
    public String generateLootTable(String... entries) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"pools\": [\n");
        for (int i = 0; i < entries.length; i++) {
            sb.append("    ").append(entries[i]);
            if (i < entries.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }
    
    public String generateLootEntry(String item, int minCount, int maxCount, float weight) {
        return String.format("""
            {
              "rolls": {
                "min": %d,
                "max": %d
              },
              "entries": [
                {
                  "type": "minecraft:item",
                  "name": "%s",
                  "weight": %d
                }
              ]
            }
            """, minCount, maxCount, item, (int) weight);
    }
}
