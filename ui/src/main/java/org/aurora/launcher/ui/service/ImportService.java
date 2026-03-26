package org.aurora.launcher.ui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ImportService {
    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);
    private static ImportService instance;
    
    private final ModDetectionService modDetection;
    private Map<String, Object> auroraMetadata;
    private Map<String, Object> curseforgeManifest;
    private Map<String, Object> modrinthIndex;
    
    public static ImportService getInstance() {
        if (instance == null) {
            instance = new ImportService();
        }
        return instance;
    }
    
    private ImportService() {
        modDetection = ModDetectionService.getInstance();
    }
    
    public CompletableFuture<ImportResult> importFile(Path filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                auroraMetadata = null;
                curseforgeManifest = null;
                modrinthIndex = null;
                
                if (filePath.toString().endsWith(".aurora")) {
                    return importAuroraFormat(filePath);
                } else if (filePath.toString().endsWith(".zip")) {
                    return importCurseForgeFormat(filePath);
                } else if (filePath.toString().endsWith(".mrpack")) {
                    return importModrinthFormat(filePath);
                } else {
                    return ImportResult.error("Unsupported file format");
                }
            } catch (Exception e) {
                logger.error("Import failed", e);
                return ImportResult.error("Import failed: " + e.getMessage());
            }
        });
    }
    
    private ImportResult importAuroraFormat(Path filePath) throws Exception {
        Map<String, Object> meta = null;
        Map<String, Object> manifest = null;
        Map<String, Object> mrIndex = null;
        
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(filePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                String content = new String(zis.readAllBytes());
                
                if (name.equals("aurora.json")) {
                    meta = parseJson(content);
                } else if (name.equals("manifest.json")) {
                    manifest = parseJson(content);
                } else if (name.equals("modrinth.index.json")) {
                    mrIndex = parseJson(content);
                }
                
                zis.closeEntry();
            }
        }
        
        this.auroraMetadata = meta;
        this.curseforgeManifest = manifest;
        this.modrinthIndex = mrIndex;
        
        ImportResult result = new ImportResult();
        result.setSuccess(true);
        result.setFormat("aurora");
        result.setAuroraMetadata(meta);
        
        if (meta != null && meta.containsKey("modpack")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> modpack = (Map<String, Object>) meta.get("modpack");
            result.setName((String) modpack.get("name"));
            result.setVersion((String) modpack.get("version"));
        }
        
        if (meta != null && meta.containsKey("files")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> files = (List<Map<String, Object>>) meta.get("files");
            result.setModCount(files.size());
        }
        
        return result;
    }
    
    private ImportResult importCurseForgeFormat(Path filePath) throws Exception {
        Map<String, Object> manifest = null;
        
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(filePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.equals("manifest.json")) {
                    String content = new String(zis.readAllBytes());
                    manifest = parseJson(content);
                }
                zis.closeEntry();
            }
        }
        
        this.curseforgeManifest = manifest;
        
        ImportResult result = new ImportResult();
        result.setSuccess(true);
        result.setFormat("curseforge");
        result.setCurseforgeManifest(manifest);
        
        if (manifest != null) {
            result.setName((String) manifest.get("name"));
            result.setVersion((String) manifest.get("version"));
            
            if (manifest.containsKey("files")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> files = (List<Map<String, Object>>) manifest.get("files");
                result.setModCount(files.size());
            }
        }
        
        return result;
    }
    
    private ImportResult importModrinthFormat(Path filePath) throws Exception {
        Map<String, Object> index = null;
        
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(filePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.equals("modrinth.index.json")) {
                    String content = new String(zis.readAllBytes());
                    index = parseJson(content);
                }
                zis.closeEntry();
            }
        }
        
        this.modrinthIndex = index;
        
        ImportResult result = new ImportResult();
        result.setSuccess(true);
        result.setFormat("modrinth");
        result.setModrinthIndex(index);
        
        if (index != null) {
            result.setName((String) index.get("name"));
            result.setVersion((String) index.get("version_id"));
            
            if (index.containsKey("files")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> files = (List<Map<String, Object>>) index.get("files");
                result.setModCount(files.size());
            }
        }
        
        return result;
    }
    
    public List<DownloadOption> getDownloadOptions(String modFilename) {
        List<DownloadOption> options = new ArrayList<>();
        
        if (auroraMetadata != null && auroraMetadata.containsKey("files")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> files = (List<Map<String, Object>>) auroraMetadata.get("files");
            for (Map<String, Object> file : files) {
                String originalFilename = (String) file.get("originalFilename");
                if (modFilename.equals(originalFilename)) {
                    
                    if (file.containsKey("modrinth")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> mr = (Map<String, Object>) file.get("modrinth");
                        DownloadOption opt = new DownloadOption();
                        opt.setSource("modrinth");
                        opt.setProjectId((String) mr.get("projectID"));
                        opt.setVersionId((String) mr.get("versionID"));
                        opt.setDownloadUrl((String) mr.get("downloadUrl"));
                        opt.setFilename((String) mr.get("filename"));
                        options.add(opt);
                    }
                    
                    if (file.containsKey("curseforge")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> cf = (Map<String, Object>) file.get("curseforge");
                        DownloadOption opt = new DownloadOption();
                        opt.setSource("curseforge");
                        opt.setProjectId(String.valueOf(cf.get("projectID")));
                        opt.setFileId((Integer) cf.get("fileID"));
                        opt.setDownloadUrl((String) cf.get("downloadUrl"));
                        opt.setFilename((String) cf.get("filename"));
                        options.add(opt);
                    }
                }
            }
        }
        
        return options;
    }
    
    public boolean hasAuroraMetadata() {
        return auroraMetadata != null;
    }
    
    public boolean hasDualPlatformInfo() {
        if (auroraMetadata == null || !auroraMetadata.containsKey("files")) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> files = (List<Map<String, Object>>) auroraMetadata.get("files");
        for (Map<String, Object> file : files) {
            boolean hasMr = file.containsKey("modrinth");
            boolean hasCf = file.containsKey("curseforge");
            if (hasMr && hasCf) {
                return true;
            }
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String content) {
        Map<String, Object> result = new LinkedHashMap<>();
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
            Map<String, Object> obj = new LinkedHashMap<>();
            parseObject(json, start + 1, findMatchingBrace(json, start), obj);
            return obj;
        } else if (c == '[') {
            List<Object> list = new ArrayList<>();
            parseArray(json, start + 1, findMatchingBracket(json, start), list);
            return list;
        } else if (Character.isDigit(c)) {
            return parseNumber(json, start);
        } else if (json.substring(start, Math.min(start + 4, json.length())).equals("true")) {
            return true;
        } else if (json.substring(start, Math.min(start + 5, json.length())).equals("false")) {
            return false;
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
    
    public static class ImportResult {
        private boolean success;
        private String error;
        private String format;
        private String name;
        private String version;
        private int modCount;
        private Map<String, Object> auroraMetadata;
        private Map<String, Object> curseforgeManifest;
        private Map<String, Object> modrinthIndex;
        
        public static ImportResult error(String error) {
            ImportResult r = new ImportResult();
            r.success = false;
            r.error = error;
            return r;
        }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public int getModCount() { return modCount; }
        public void setModCount(int modCount) { this.modCount = modCount; }
        
        public Map<String, Object> getAuroraMetadata() { return auroraMetadata; }
        public void setAuroraMetadata(Map<String, Object> auroraMetadata) { this.auroraMetadata = auroraMetadata; }
        
        public Map<String, Object> getCurseforgeManifest() { return curseforgeManifest; }
        public void setCurseforgeManifest(Map<String, Object> curseforgeManifest) { this.curseforgeManifest = curseforgeManifest; }
        
        public Map<String, Object> getModrinthIndex() { return modrinthIndex; }
        public void setModrinthIndex(Map<String, Object> modrinthIndex) { this.modrinthIndex = modrinthIndex; }
    }
    
    public static class DownloadOption {
        private String source;
        private String projectId;
        private String versionId;
        private int fileId;
        private String downloadUrl;
        private String filename;
        
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        
        public String getProjectId() { return projectId; }
        public void setProjectId(String projectId) { this.projectId = projectId; }
        
        public String getVersionId() { return versionId; }
        public void setVersionId(String versionId) { this.versionId = versionId; }
        
        public int getFileId() { return fileId; }
        public void setFileId(int fileId) { this.fileId = fileId; }
        
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        
        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
    }
}
