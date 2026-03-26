package org.aurora.launcher.ui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModDetectionService {
    private static final Logger logger = LoggerFactory.getLogger(ModDetectionService.class);
    private static ModDetectionService instance;
    
    private final Set<String> installedMods = ConcurrentHashMap.newKeySet();
    private final Map<String, ModInfo> modInfoMap = new ConcurrentHashMap<>();
    private final Map<String, Boolean> modVisibility = new ConcurrentHashMap<>();
    private Path currentInstancePath;
    
    public static ModDetectionService getInstance() {
        if (instance == null) {
            instance = new ModDetectionService();
        }
        return instance;
    }
    
    private ModDetectionService() {}
    
    public void scanInstance(Path instancePath) {
        this.currentInstancePath = instancePath;
        installedMods.clear();
        modInfoMap.clear();
        
        Path modsPath = instancePath.resolve("mods");
        if (!Files.exists(modsPath)) {
            logger.warn("No mods directory found at: {}", modsPath);
            return;
        }
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsPath, "*.jar")) {
            for (Path jar : stream) {
                ModInfo info = detectModInfo(jar);
                if (info != null) {
                    installedMods.add(info.getModId());
                    modInfoMap.put(info.getModId(), info);
                    modVisibility.putIfAbsent(info.getModId(), true);
                    logger.info("Detected mod: {} ({})", info.getModId(), info.getName());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to scan mods directory", e);
        }
        
        logger.info("Scan complete. Found {} mods", installedMods.size());
    }
    
    public ModInfo detectModInfo(Path jarPath) {
        ModInfo info = new ModInfo();
        info.setFilePath(jarPath);
        info.setOriginalFilename(jarPath.getFileName().toString());
        
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            ZipEntry modsToml = zip.getEntry("META-INF/mods.toml");
            if (modsToml != null) {
                String content = new String(zip.getInputStream(modsToml).readAllBytes());
                parseForgeModInfo(info, content);
                return info;
            }
            
            ZipEntry fabricJson = zip.getEntry("fabric.mod.json");
            if (fabricJson != null) {
                String content = new String(zip.getInputStream(fabricJson).readAllBytes());
                parseFabricModInfo(info, content);
                return info;
            }
            
            ZipEntry neoforgeJson = zip.getEntry("META-INF/neoforge.mods.toml");
            if (neoforgeJson != null) {
                String content = new String(zip.getInputStream(neoforgeJson).readAllBytes());
                parseNeoForgeModInfo(info, content);
                return info;
            }
            
        } catch (Exception e) {
            logger.debug("Failed to read mod info from: {}", jarPath);
        }
        
        return null;
    }
    
    private void parseForgeModInfo(ModInfo info, String content) {
        info.setLoader("forge");
        info.setModId(extractValue(content, "modId"));
        info.setName(extractValue(content, "name"));
        info.setVersion(extractValue(content, "version"));
        info.setDescription(extractValue(content, "description"));
        info.setUrl(extractValue(content, "displayURL"));
    }
    
    private void parseFabricModInfo(ModInfo info, String content) {
        info.setLoader("fabric");
        
        String id = extractJsonValue(content, "id");
        String name = extractJsonValue(content, "name");
        String version = extractJsonValue(content, "version");
        String description = extractJsonValue(content, "description");
        
        info.setModId(id);
        info.setName(name != null ? name : id);
        info.setVersion(version);
        info.setDescription(description);
        
        String icon = extractJsonValue(content, "icon");
        if (icon != null) {
            info.setHasIcon(true);
        }
    }
    
    private void parseNeoForgeModInfo(ModInfo info, String content) {
        info.setLoader("neoforge");
        info.setModId(extractValue(content, "modId"));
        info.setName(extractValue(content, "displayName"));
        info.setVersion(extractValue(content, "version"));
    }
    
    private String extractValue(String content, String key) {
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.startsWith(key) && line.contains("=")) {
                String value = line.substring(line.indexOf("=") + 1).trim();
                value = value.replace("\"", "").replace("'", "").replace(",", "");
                return value.isEmpty() ? null : value;
            }
        }
        return null;
    }
    
    private String extractJsonValue(String content, String key) {
        String pattern = "\"" + key + "\"";
        int idx = content.indexOf(pattern);
        if (idx < 0) return null;
        
        int colon = content.indexOf(":", idx);
        int start = content.indexOf("\"", colon);
        int end = content.indexOf("\"", start + 1);
        
        if (start >= 0 && end > start) {
            String value = content.substring(start + 1, end);
            return value.isEmpty() ? null : value;
        }
        
        colon = content.indexOf(",", colon);
        start = content.indexOf("\"", colon);
        end = content.indexOf("\"", start + 1);
        
        if (start >= 0 && end > start) {
            String value = content.substring(start + 1, end);
            return value.isEmpty() ? null : value;
        }
        
        return null;
    }
    
    public boolean isModInstalled(String modId) {
        return installedMods.contains(modId);
    }
    
    public Set<String> getInstalledMods() {
        return new HashSet<>(installedMods);
    }
    
    public Optional<ModInfo> getModInfo(String modId) {
        return Optional.ofNullable(modInfoMap.get(modId));
    }
    
    public void setModVisibility(String modId, boolean visible) {
        modVisibility.put(modId, visible);
    }
    
    public boolean isModVisible(String modId) {
        return modVisibility.getOrDefault(modId, true);
    }
    
    public Map<String, Boolean> getVisibilitySettings() {
        return new HashMap<>(modVisibility);
    }
    
    public Path getCurrentInstancePath() {
        return currentInstancePath;
    }
    
    public static class ModInfo {
        private Path filePath;
        private String originalFilename;
        private String modId;
        private String name;
        private String version;
        private String description;
        private String url;
        private String loader;
        private boolean hasIcon;
        
        public Path getFilePath() { return filePath; }
        public void setFilePath(Path filePath) { this.filePath = filePath; }
        
        public String getOriginalFilename() { return originalFilename; }
        public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
        
        public String getModId() { return modId; }
        public void setModId(String modId) { this.modId = modId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getLoader() { return loader; }
        public void setLoader(String loader) { this.loader = loader; }
        
        public boolean hasIcon() { return hasIcon; }
        public void setHasIcon(boolean hasIcon) { this.hasIcon = hasIcon; }
    }
}
