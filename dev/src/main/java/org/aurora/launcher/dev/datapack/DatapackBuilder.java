package org.aurora.launcher.dev.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DatapackBuilder {
    private String name;
    private String description;
    private int packFormat = 26;
    private Map<String, String> functions;
    private Map<String, JsonElement> lootTables;
    private Map<String, JsonElement> advancements;
    private Map<String, JsonElement> tags;
    
    private final Gson gson;

    public DatapackBuilder() {
        this.functions = new HashMap<>();
        this.lootTables = new HashMap<>();
        this.advancements = new HashMap<>();
        this.tags = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPackFormat() {
        return packFormat;
    }

    public void setPackFormat(int packFormat) {
        this.packFormat = packFormat;
    }

    public DatapackBuilder function(String name, String content) {
        functions.put(name, content);
        return this;
    }

    public DatapackBuilder lootTable(String name, org.aurora.launcher.dev.datapack.loot_table.LootTableBuilder builder) {
        lootTables.put(name, builder.build());
        return this;
    }

    public DatapackBuilder advancement(String name, org.aurora.launcher.dev.datapack.advancement.AdvancementBuilder builder) {
        advancements.put(name, builder.build());
        return this;
    }

    public DatapackBuilder tag(String name, org.aurora.launcher.dev.datapack.tag.TagBuilder builder) {
        tags.put(name, builder.build());
        return this;
    }

    public Path build(Path outputDir) throws IOException {
        Path packDir = outputDir.resolve(name);
        Files.createDirectories(packDir);
        
        writePackMcmeta(packDir);
        
        for (Map.Entry<String, String> entry : functions.entrySet()) {
            writeFunction(packDir, entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, JsonElement> entry : lootTables.entrySet()) {
            writeJson(packDir, "loot_tables", entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, JsonElement> entry : advancements.entrySet()) {
            writeJson(packDir, "advancements", entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, JsonElement> entry : tags.entrySet()) {
            writeTag(packDir, entry.getKey(), entry.getValue());
        }
        
        return packDir;
    }

    private void writePackMcmeta(Path packDir) throws IOException {
        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", packFormat);
        pack.addProperty("description", description != null ? description : "");
        
        JsonObject root = new JsonObject();
        root.add("pack", pack);
        
        writeString(packDir.resolve("pack.mcmeta"), gson.toJson(root));
    }

    private void writeFunction(Path packDir, String name, String content) throws IOException {
        String[] parts = name.split(":");
        String namespace = parts.length > 1 ? parts[0] : "minecraft";
        String funcName = parts.length > 1 ? parts[1] : parts[0];
        
        Path funcPath = packDir.resolve("data").resolve(namespace).resolve("functions").resolve(funcName + ".mcfunction");
        Files.createDirectories(funcPath.getParent());
        writeString(funcPath, content);
    }

    private void writeJson(Path packDir, String type, String name, JsonElement content) throws IOException {
        String[] parts = name.split(":");
        String namespace = parts.length > 1 ? parts[0] : "minecraft";
        String fileName = parts.length > 1 ? parts[1] : parts[0];
        
        Path jsonPath = packDir.resolve("data").resolve(namespace).resolve(type).resolve(fileName + ".json");
        Files.createDirectories(jsonPath.getParent());
        writeString(jsonPath, gson.toJson(content));
    }

    private void writeTag(Path packDir, String name, JsonElement content) throws IOException {
        String[] parts = name.split(":");
        String namespace = parts.length > 1 ? parts[0] : "minecraft";
        String fileName = parts.length > 1 ? parts[1] : parts[0];
        
        Path tagPath = packDir.resolve("data").resolve(namespace).resolve("tags").resolve(fileName + ".json");
        Files.createDirectories(tagPath.getParent());
        writeString(tagPath, gson.toJson(content));
    }

    private void writeString(Path path, String content) throws IOException {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }
}