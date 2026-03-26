package org.aurora.launcher.ui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePackService {
    private static final Logger logger = LoggerFactory.getLogger(ResourcePackService.class);
    private static ResourcePackService instance;
    
    private Path currentResourcePackPath;
    private Map<String, Object> packMetadata = new ConcurrentHashMap<>();
    private boolean hasUnsavedChanges = false;
    
    public static ResourcePackService getInstance() {
        if (instance == null) {
            instance = new ResourcePackService();
        }
        return instance;
    }
    
    private ResourcePackService() {}
    
    public void createNew(String name, String description, int packFormat) {
        currentResourcePackPath = Paths.get("resourcepacks", name);
        
        try {
            Files.createDirectories(currentResourcePackPath.resolve("assets"));
            
            packMetadata.put("pack_format", packFormat);
            packMetadata.put("description", description);
            
            String mcmeta = String.format("""
                {
                    "pack": {
                        "pack_format": %d,
                        "description": "%s"
                    }
                }
                """, packFormat, escapeJson(description));
            
            Files.writeString(
                currentResourcePackPath.resolve("pack.mcmeta"),
                mcmeta
            );
            
            logger.info("Created new resource pack: {}", name);
        } catch (Exception e) {
            logger.error("Failed to create resource pack: {}", name, e);
        }
    }
    
    public void open(Path resourcePackPath) {
        if (!Files.exists(resourcePackPath)) {
            logger.warn("Resource pack not found: {}", resourcePackPath);
            return;
        }
        
        this.currentResourcePackPath = resourcePackPath;
        loadMetadata();
    }
    
    private void loadMetadata() {
        Path mcmetaPath = currentResourcePackPath.resolve("pack.mcmeta");
        if (!Files.exists(mcmetaPath)) {
            logger.warn("No pack.mcmeta found");
            return;
        }
        
        try {
            String content = Files.readString(mcmetaPath);
            Map<String, Object> meta = parseJson(content);
            
            if (meta.containsKey("pack")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> pack = (Map<String, Object>) meta.get("pack");
                packMetadata.put("pack_format", pack.get("pack_format"));
                packMetadata.put("description", pack.get("description"));
            }
            
            logger.info("Loaded resource pack metadata");
        } catch (Exception e) {
            logger.error("Failed to load pack.mcmeta", e);
        }
    }
    
    public void save() {
        if (currentResourcePackPath == null) {
            logger.warn("No resource pack is currently open");
            return;
        }
        
        try {
            Path mcmetaPath = currentResourcePackPath.resolve("pack.mcmeta");
            
            int packFormat = (int) packMetadata.getOrDefault("pack_format", 15);
            String description = (String) packMetadata.getOrDefault("description", "");
            
            String mcmeta = String.format("""
                {
                    "pack": {
                        "pack_format": %d,
                        "description": "%s"
                    }
                }
                """, packFormat, escapeJson(description));
            
            Files.writeString(mcmetaPath, mcmeta);
            hasUnsavedChanges = false;
            
            logger.info("Saved resource pack: {}", currentResourcePackPath);
        } catch (Exception e) {
            logger.error("Failed to save resource pack", e);
        }
    }
    
    public void setDescription(String description) {
        packMetadata.put("description", description);
        hasUnsavedChanges = true;
    }
    
    public void setPackFormat(int packFormat) {
        packMetadata.put("pack_format", packFormat);
        hasUnsavedChanges = true;
    }
    
    public String getDescription() {
        return (String) packMetadata.getOrDefault("description", "");
    }
    
    public int getPackFormat() {
        return (int) packMetadata.getOrDefault("pack_format", 15);
    }
    
    public Path getCurrentPath() {
        return currentResourcePackPath;
    }
    
    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }
    
    public List<Path> getFiles(String subPath) {
        List<Path> files = new ArrayList<>();
        
        if (currentResourcePackPath == null) {
            return files;
        }
        
        Path searchPath = currentResourcePackPath.resolve(subPath);
        if (!Files.exists(searchPath)) {
            return files;
        }
        
        try {
            Files.walk(searchPath)
                .filter(Files::isRegularFile)
                .forEach(files::add);
        } catch (Exception e) {
            logger.error("Failed to list files in: {}", searchPath, e);
        }
        
        return files;
    }
    
    public Path getAssetsPath() {
        return currentResourcePackPath != null 
            ? currentResourcePackPath.resolve("assets") 
            : null;
    }
    
    public Path getModelsPath(String namespace) {
        return getAssetsPath() != null
            ? getAssetsPath().resolve(namespace).resolve("models")
            : null;
    }
    
    public Path getTexturesPath(String namespace) {
        return getAssetsPath() != null
            ? getAssetsPath().resolve(namespace).resolve("textures")
            : null;
    }
    
    public void createTexture(String namespace, String path, byte[] data) {
        if (getTexturesPath(namespace) == null) {
            return;
        }
        
        try {
            Path texturePath = getTexturesPath(namespace).resolve(path);
            Files.createDirectories(texturePath.getParent());
            Files.write(texturePath, data);
            hasUnsavedChanges = true;
            logger.info("Created texture: {}", texturePath);
        } catch (Exception e) {
            logger.error("Failed to create texture: {}", path, e);
        }
    }
    
    public void export(Path destination) {
        if (currentResourcePackPath == null) {
            logger.warn("No resource pack to export");
            return;
        }
        
        try {
            if (Files.exists(destination)) {
                Files.delete(destination);
            }
            
            Files.walk(currentResourcePackPath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(source -> {
                    try {
                        Path relative = currentResourcePackPath.relativize(source);
                        Path dest = destination.resolve(relative);
                        Files.createDirectories(dest.getParent());
                        Files.copy(source, dest);
                    } catch (Exception e) {
                        logger.error("Failed to copy file: {}", source, e);
                    }
                });
            
            logger.info("Exported resource pack to: {}", destination);
        } catch (Exception e) {
            logger.error("Failed to export resource pack", e);
        }
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String content) {
        Map<String, Object> result = new ConcurrentHashMap<>();
        
        content = content.trim();
        if (content.startsWith("{")) {
            parseObject(content, 1, content.length() - 1, result);
        }
        
        return result;
    }
    
    private void parseObject(String json, int start, int end, Map<String, Object> result) {
        int i = start;
        while (i < end) {
            skipWhitespace(json, i, end);
            if (json.charAt(i) == '}') break;
            
            String key = parseString(json, i);
            i += key.length() + 2;
            
            skipWhitespace(json, i, end);
            i++;
            
            Object value = parseValue(json, i, end);
            result.put(key, value);
            
            skipWhitespace(json, i, end);
            if (json.charAt(i) == '}') break;
            i++;
        }
    }
    
    private String parseString(String json, int start) {
        int quote1 = json.indexOf('"', start);
        int quote2 = json.indexOf('"', quote1 + 1);
        return json.substring(quote1 + 1, quote2);
    }
    
    private Object parseValue(String json, int start, int end) {
        skipWhitespace(json, start, end);
        char c = json.charAt(start);
        
        if (c == '"') {
            return parseString(json, start);
        } else if (c == '{') {
            Map<String, Object> obj = new ConcurrentHashMap<>();
            parseObject(json, start + 1, findMatchingBrace(json, start), obj);
            return obj;
        } else if (c == '[') {
            List<Object> list = new ArrayList<>();
            parseArray(json, start + 1, findMatchingBracket(json, start), list);
            return list;
        } else if (Character.isDigit(c)) {
            return parseNumber(json, start);
        } else if (json.substring(start, start + 4).equals("true")) {
            return true;
        } else if (json.substring(start, start + 5).equals("false")) {
            return false;
        } else if (json.substring(start, start + 4).equals("null")) {
            return null;
        }
        
        return null;
    }
    
    private void parseArray(String json, int start, int end, List<Object> result) {
        int i = start;
        while (i < end) {
            skipWhitespace(json, i, end);
            if (json.charAt(i) == ']') break;
            
            result.add(parseValue(json, i, end));
            
            skipWhitespace(json, i, end);
            if (json.charAt(i) == ']') break;
            i++;
        }
    }
    
    private int parseNumber(String json, int start) {
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) {
            end++;
        }
        String num = json.substring(start, end);
        return num.contains(".") ? (int) Double.parseDouble(num) : Integer.parseInt(num);
    }
    
    private void skipWhitespace(String json, int start, int end) {
        int i = start;
        while (i < end && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
    }
    
    private int findMatchingBrace(String json, int start) {
        int count = 1;
        int i = start + 1;
        while (i < json.length() && count > 0) {
            char c = json.charAt(i);
            if (c == '{') count++;
            else if (c == '}') count--;
            i++;
        }
        return i - 1;
    }
    
    private int findMatchingBracket(String json, int start) {
        int count = 1;
        int i = start + 1;
        while (i < json.length() && count > 0) {
            char c = json.charAt(i);
            if (c == '[') count++;
            else if (c == ']') count--;
            i++;
        }
        return i - 1;
    }
}
