package org.aurora.launcher.ui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ExportService {
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
    private static ExportService instance;
    
    private final ModDetectionService modDetection;
    private final ModrinthClient modrinthClient;
    private final CurseForgeClient curseforgeClient;
    
    public static ExportService getInstance() {
        if (instance == null) {
            instance = new ExportService();
        }
        return instance;
    }
    
    private ExportService() {
        modDetection = ModDetectionService.getInstance();
        modrinthClient = new ModrinthClient();
        curseforgeClient = new CurseForgeClient();
    }
    
    public CompletableFuture<Path> exportDualPlatform(Path instancePath, Path outputPath, ExportOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path modsPath = instancePath.resolve("mods");
                if (!Files.exists(modsPath)) {
                    throw new RuntimeException("No mods directory found");
                }
                
                List<ModExportInfo> modInfos = scanMods(modsPath);
                
                Map<String, Object> auroraMeta = buildAuroraMetadata(modInfos, options);
                
                String manifestJson = buildCurseForgeManifest(modInfos, options);
                
                String modrinthIndex = buildModrinthIndex(modInfos, options);
                
                try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(outputPath))) {
                    zos.putNextEntry(new ZipEntry("aurora.json"));
                    zos.write(auroraMeta.toString().getBytes());
                    zos.closeEntry();
                    
                    zos.putNextEntry(new ZipEntry("manifest.json"));
                    zos.write(manifestJson.getBytes());
                    zos.closeEntry();
                    
                    zos.putNextEntry(new ZipEntry("modrinth.index.json"));
                    zos.write(modrinthIndex.getBytes());
                    zos.closeEntry();
                    
                    for (ModExportInfo info : modInfos) {
                        if (Files.exists(info.getFilePath())) {
                            zos.putNextEntry(new ZipEntry("mods/" + info.getExportedFilename()));
                            Files.copy(info.getFilePath(), zos);
                            zos.closeEntry();
                        }
                    }
                }
                
                logger.info("Exported dual-platform modpack to: {}", outputPath);
                return outputPath;
                
            } catch (Exception e) {
                logger.error("Failed to export modpack", e);
                throw new RuntimeException("Export failed: " + e.getMessage(), e);
            }
        });
    }
    
    private List<ModExportInfo> scanMods(Path modsPath) throws Exception {
        List<ModExportInfo> infos = new ArrayList<>();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsPath, "*.jar")) {
            for (Path jar : stream) {
                ModExportInfo info = new ModExportInfo();
                info.setFilePath(jar);
                info.setOriginalFilename(jar.getFileName().toString());
                
                String sha1 = calculateSha1(jar);
                info.setSha1(sha1);
                
                ModrinthMatch mrMatch = lookupModrinth(sha1);
                if (mrMatch != null) {
                    info.setModrinthProjectId(mrMatch.projectId);
                    info.setModrinthVersionId(mrMatch.versionId);
                    info.setModrinthFilename(mrMatch.filename);
                    info.setModrinthDownloadUrl(mrMatch.downloadUrl);
                }
                
                CurseForgeMatch cfMatch = lookupCurseForge(sha1);
                if (cfMatch != null) {
                    info.setCurseforgeProjectId(cfMatch.projectId);
                    info.setCurseforgeFileId(cfMatch.fileId);
                    info.setCurseforgeFilename(cfMatch.filename);
                    info.setCurseforgeDownloadUrl(cfMatch.downloadUrl);
                }
                
                info.setExportedFilename(determineExportedFilename(info, null));
                
                infos.add(info);
            }
        }
        
        return infos;
    }
    
    private String determineExportedFilename(ModExportInfo info, String pattern) {
        if (pattern == null || pattern.equals("{original}")) {
            return info.getOriginalFilename();
        }
        
        return pattern
            .replace("{modid}", info.getModId() != null ? info.getModId() : "unknown")
            .replace("{version}", info.getVersion() != null ? info.getVersion() : "")
            .replace("{original}", info.getOriginalFilename())
            + ".jar";
    }
    
    private String calculateSha1(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] data = Files.readAllBytes(file);
        byte[] hash = digest.digest(data);
        
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    private ModrinthMatch lookupModrinth(String sha1) {
        try {
            return modrinthClient.getVersionByHash(sha1, "sha1");
        } catch (Exception e) {
            logger.debug("No Modrinth match for: {}", sha1);
            return null;
        }
    }
    
    private CurseForgeMatch lookupCurseForge(String sha1) {
        try {
            return curseforgeClient.getFileByFingerprint(sha1);
        } catch (Exception e) {
            logger.debug("No CurseForge match for: {}", sha1);
            return null;
        }
    }
    
    private Map<String, Object> buildAuroraMetadata(List<ModExportInfo> infos, ExportOptions options) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("format", "aurora");
        meta.put("version", "1.0");
        
        Map<String, Object> modpack = new LinkedHashMap<>();
        modpack.put("name", options.getName());
        modpack.put("version", options.getVersion());
        modpack.put("author", options.getAuthor());
        meta.put("modpack", modpack);
        
        List<Map<String, Object>> files = new ArrayList<>();
        for (ModExportInfo info : infos) {
            Map<String, Object> file = new LinkedHashMap<>();
            file.put("originalFilename", info.getOriginalFilename());
            file.put("sha1", info.getSha1());
            file.put("exportedFilename", info.getExportedFilename());
            
            if (info.getModrinthProjectId() != null) {
                Map<String, Object> mr = new LinkedHashMap<>();
                mr.put("projectID", info.getModrinthProjectId());
                mr.put("versionID", info.getModrinthVersionId());
                mr.put("filename", info.getModrinthFilename());
                mr.put("downloadUrl", info.getModrinthDownloadUrl());
                file.put("modrinth", mr);
            }
            
            if (info.getCurseforgeProjectId() > 0) {
                Map<String, Object> cf = new LinkedHashMap<>();
                cf.put("projectID", info.getCurseforgeProjectId());
                cf.put("fileID", info.getCurseforgeFileId());
                cf.put("filename", info.getCurseforgeFilename());
                cf.put("downloadUrl", info.getCurseforgeDownloadUrl());
                file.put("curseforge", cf);
            }
            
            files.add(file);
        }
        meta.put("files", files);
        
        return meta;
    }
    
    private String buildCurseForgeManifest(List<ModExportInfo> infos, ExportOptions options) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"minecraft\": {\n");
        sb.append("    \"version\": \"").append(options.getMinecraftVersion()).append("\",\n");
        sb.append("    \"modLoaders\": [\n");
        sb.append("      {\n");
        sb.append("        \"id\": \"").append(options.getLoaderId()).append("\",\n");
        sb.append("        \"primary\": true\n");
        sb.append("      }\n");
        sb.append("    ]\n");
        sb.append("  },\n");
        sb.append("  \"manifestType\": \"minecraftModpack\",\n");
        sb.append("  \"manifestVersion\": 1,\n");
        sb.append("  \"name\": \"").append(escapeJson(options.getName())).append("\",\n");
        sb.append("  \"version\": \"").append(escapeJson(options.getVersion())).append("\",\n");
        sb.append("  \"author\": \"").append(escapeJson(options.getAuthor())).append("\",\n");
        sb.append("  \"files\": [\n");
        
        for (int i = 0; i < infos.size(); i++) {
            ModExportInfo info = infos.get(i);
            sb.append("    {\n");
            sb.append("      \"projectID\": ").append(info.getCurseforgeProjectId()).append(",\n");
            sb.append("      \"fileID\": ").append(info.getCurseforgeFileId()).append(",\n");
            sb.append("      \"required\": true");
            
            if (info.getCurseforgeFilename() != null) {
                sb.append(",\n      \"filename\": \"").append(escapeJson(info.getCurseforgeFilename())).append("\"");
            }
            
            sb.append("\n    }");
            if (i < infos.size() - 1) sb.append(",");
            sb.append("\n");
        }
        
        sb.append("  ],\n");
        sb.append("  \"overrides\": \"\"\n");
        sb.append("}");
        
        return sb.toString();
    }
    
    private String buildModrinthIndex(List<ModExportInfo> infos, ExportOptions options) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"format_version\": 1,\n");
        sb.append("  \"game\": \"minecraft\",\n");
        sb.append("  \"name\": \"").append(escapeJson(options.getName())).append("\",\n");
        sb.append("  \"version_id\": \"").append(escapeJson(options.getVersion())).append("\",\n");
        sb.append("  \"files\": [\n");
        
        for (int i = 0; i < infos.size(); i++) {
            ModExportInfo info = infos.get(i);
            sb.append("    {\n");
            sb.append("      \"path\": \"mods/").append(info.getExportedFilename()).append("\",\n");
            sb.append("      \"hashes\": {\"sha1\": \"").append(info.getSha1()).append("\"},\n");
            
            if (info.getModrinthDownloadUrl() != null) {
                sb.append("      \"downloads\": [\"").append(info.getModrinthDownloadUrl()).append("\"]");
            } else {
                sb.append("      \"downloads\": []");
            }
            
            sb.append("\n    }");
            if (i < infos.size() - 1) sb.append(",");
            sb.append("\n");
        }
        
        sb.append("  ]\n");
        sb.append("}");
        
        return sb.toString();
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
    
    public static class ModExportInfo {
        private Path filePath;
        private String originalFilename;
        private String modId;
        private String version;
        private String sha1;
        private String exportedFilename;
        
        private String modrinthProjectId;
        private String modrinthVersionId;
        private String modrinthFilename;
        private String modrinthDownloadUrl;
        
        private int curseforgeProjectId;
        private int curseforgeFileId;
        private String curseforgeFilename;
        private String curseforgeDownloadUrl;
        
        public Path getFilePath() { return filePath; }
        public void setFilePath(Path filePath) { this.filePath = filePath; }
        
        public String getOriginalFilename() { return originalFilename; }
        public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
        
        public String getModId() { return modId; }
        public void setModId(String modId) { this.modId = modId; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getSha1() { return sha1; }
        public void setSha1(String sha1) { this.sha1 = sha1; }
        
        public String getExportedFilename() { return exportedFilename; }
        public void setExportedFilename(String exportedFilename) { this.exportedFilename = exportedFilename; }
        
        public String getModrinthProjectId() { return modrinthProjectId; }
        public void setModrinthProjectId(String modrinthProjectId) { this.modrinthProjectId = modrinthProjectId; }
        
        public String getModrinthVersionId() { return modrinthVersionId; }
        public void setModrinthVersionId(String modrinthVersionId) { this.modrinthVersionId = modrinthVersionId; }
        
        public String getModrinthFilename() { return modrinthFilename; }
        public void setModrinthFilename(String modrinthFilename) { this.modrinthFilename = modrinthFilename; }
        
        public String getModrinthDownloadUrl() { return modrinthDownloadUrl; }
        public void setModrinthDownloadUrl(String modrinthDownloadUrl) { this.modrinthDownloadUrl = modrinthDownloadUrl; }
        
        public int getCurseforgeProjectId() { return curseforgeProjectId; }
        public void setCurseforgeProjectId(int curseforgeProjectId) { this.curseforgeProjectId = curseforgeProjectId; }
        
        public int getCurseforgeFileId() { return curseforgeFileId; }
        public void setCurseforgeFileId(int curseforgeFileId) { this.curseforgeFileId = curseforgeFileId; }
        
        public String getCurseforgeFilename() { return curseforgeFilename; }
        public void setCurseforgeFilename(String curseforgeFilename) { this.curseforgeFilename = curseforgeFilename; }
        
        public String getCurseforgeDownloadUrl() { return curseforgeDownloadUrl; }
        public void setCurseforgeDownloadUrl(String curseforgeDownloadUrl) { this.curseforgeDownloadUrl = curseforgeDownloadUrl; }
    }
    
    public static class ModrinthMatch {
        public String projectId;
        public String versionId;
        public String filename;
        public String downloadUrl;
    }
    
    public static class CurseForgeMatch {
        public int projectId;
        public int fileId;
        public String filename;
        public String downloadUrl;
    }
    
    public static class ExportOptions {
        private String name;
        private String version;
        private String author;
        private String minecraftVersion;
        private String loaderId;
        private boolean preserveOriginalFilenames = true;
        private String customFilenamePattern = "{original}";
        private Path outputPath;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public String getMinecraftVersion() { return minecraftVersion; }
        public void setMinecraftVersion(String minecraftVersion) { this.minecraftVersion = minecraftVersion; }
        
        public String getLoaderId() { return loaderId; }
        public void setLoaderId(String loaderId) { this.loaderId = loaderId; }
        
        public boolean isPreserveOriginalFilenames() { return preserveOriginalFilenames; }
        public void setPreserveOriginalFilenames(boolean preserveOriginalFilenames) { 
            this.preserveOriginalFilenames = preserveOriginalFilenames; 
        }
        
        public String getCustomFilenamePattern() { return customFilenamePattern; }
        public void setCustomFilenamePattern(String customFilenamePattern) { 
            this.customFilenamePattern = customFilenamePattern; 
        }
        
        public Path getOutputPath() { return outputPath; }
        public void setOutputPath(Path outputPath) { this.outputPath = outputPath; }
    }
    
    private static class ModrinthClient {
        public ModrinthMatch getVersionByHash(String hash, String algorithm) {
            return null;
        }
    }
    
    private static class CurseForgeClient {
        public CurseForgeMatch getFileByFingerprint(String fingerprint) {
            return null;
        }
    }
}
