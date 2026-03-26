package org.aurora.launcher.modpack.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.aurora.launcher.modpack.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ModrinthExporter implements Exporter {
    
    private static final Logger logger = LoggerFactory.getLogger(ModrinthExporter.class);
    private static final String INDEX_FILE = "modrinth.index.json";
    
    private final Gson gson;
    
    public ModrinthExporter() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    @Override
    public String getFormat() {
        return "modrinth";
    }
    
    @Override
    public CompletableFuture<Path> export(Instance instance, ExportOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            Path modsDir = instance.getModsDir();
            if (modsDir == null || !Files.exists(modsDir)) {
                throw new RuntimeException("Mods directory not found");
            }
            
            String exportName = options.getName() != null ? options.getName() : instance.getName();
            Path outputFile = options.getOutputPath();
            if (outputFile == null) {
                outputFile = Paths.get(exportName + ".mrpack");
            }
            
            if (Files.exists(outputFile) && !options.isOverrideExisting()) {
                throw new RuntimeException("Output file already exists: " + outputFile);
            }
            
            try {
                Files.createDirectories(outputFile.getParent());
                
                JsonObject index = createIndex(instance, options);
                List<ModFile> mods = collectMods(modsDir, options);
                
                try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(outputFile))) {
                    addFilesToIndex(index, mods);
                    addIndex(zos, index);
                    addOverrides(zos, instance, options);
                }
                
                logger.info("Exported Modrinth modpack to: {}", outputFile);
                return outputFile;
            } catch (Exception e) {
                throw new RuntimeException("Failed to export modpack: " + e.getMessage(), e);
            }
        });
    }
    
    private JsonObject createIndex(Instance instance, ExportOptions options) {
        JsonObject index = new JsonObject();
        index.addProperty("formatVersion", 1);
        index.addProperty("game", "minecraft");
        index.addProperty("versionId", options.getVersion() != null ? options.getVersion() : instance.getVersion());
        index.addProperty("name", options.getName() != null ? options.getName() : instance.getName());
        
        JsonObject dependencies = new JsonObject();
        dependencies.addProperty("minecraft", instance.getConfig().getMinecraftVersion());
        
        String loaderType = instance.getConfig().getLoaderType();
        String loaderVersion = instance.getConfig().getLoaderVersion();
        
        if (loaderType != null && !"vanilla".equalsIgnoreCase(loaderType)) {
            switch (loaderType.toLowerCase()) {
                case "fabric":
                    dependencies.addProperty("fabric-loader", loaderVersion != null ? loaderVersion : "latest");
                    break;
                case "forge":
                    dependencies.addProperty("forge", loaderVersion != null ? loaderVersion : "latest");
                    break;
                case "quilt":
                    dependencies.addProperty("quilt-loader", loaderVersion != null ? loaderVersion : "latest");
                    break;
                case "neoforge":
                    dependencies.addProperty("neoforge", loaderVersion != null ? loaderVersion : "latest");
                    break;
            }
        }
        
        index.add("dependencies", dependencies);
        
        return index;
    }
    
    private List<ModFile> collectMods(Path modsDir, ExportOptions options) throws IOException {
        List<ModFile> mods = new ArrayList<>();
        Set<String> excluded = new HashSet<>(options.getExcludedMods());
        
        try (Stream<Path> files = Files.list(modsDir)) {
            files.filter(p -> p.toString().endsWith(".jar") && !p.toString().endsWith(".disabled"))
                 .filter(p -> !excluded.contains(p.getFileName().toString()))
                 .forEach(p -> {
                     try {
                         ModFile file = new ModFile();
                         file.setPath(p);
                         file.setFileName(p.getFileName().toString());
                         file.setFileSize(Files.size(p));
                         file.setSha1(calculateSha1(p));
                         file.setSha512(calculateSha512(p));
                         mods.add(file);
                     } catch (Exception e) {
                         logger.warn("Failed to process mod file: {}", p);
                     }
                 });
        }
        
        return mods;
    }
    
    private String calculateSha1(Path file) throws Exception {
        return calculateHash(file, "SHA-1");
    }
    
    private String calculateSha512(Path file) throws Exception {
        return calculateHash(file, "SHA-512");
    }
    
    private String calculateHash(Path file, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (byte b : digest.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    private void addFilesToIndex(JsonObject index, List<ModFile> mods) {
        JsonArray files = new JsonArray();
        
        for (ModFile mod : mods) {
            JsonObject file = new JsonObject();
            file.addProperty("path", "mods/" + mod.getFileName());
            
            JsonObject hashes = new JsonObject();
            hashes.addProperty("sha1", mod.getSha1());
            hashes.addProperty("sha512", mod.getSha512());
            file.add("hashes", hashes);
            
            file.addProperty("fileSize", mod.getFileSize());
            
            JsonArray downloads = new JsonArray();
            if (mod.getDownloadUrl() != null) {
                downloads.add(mod.getDownloadUrl());
            }
            file.add("downloads", downloads);
            
            files.add(file);
        }
        
        index.add("files", files);
    }
    
    private void addIndex(ZipOutputStream zos, JsonObject index) throws IOException {
        ZipEntry entry = new ZipEntry(INDEX_FILE);
        zos.putNextEntry(entry);
        zos.write(gson.toJson(index).getBytes("UTF-8"));
        zos.closeEntry();
    }
    
    private void addOverrides(ZipOutputStream zos, Instance instance, ExportOptions options) throws IOException {
        Path minecraftDir = instance.getMinecraftDir();
        if (minecraftDir == null || !Files.exists(minecraftDir)) return;
        
        addDirectoryOverride(zos, minecraftDir.resolve("config"), "override/config");
        addDirectoryOverride(zos, minecraftDir.resolve("defaultconfigs"), "override/defaultconfigs");
        
        if (options.isIncludeWorlds()) {
            addDirectoryOverride(zos, minecraftDir.resolve("saves"), "override/saves");
        }
    }
    
    private void addDirectoryOverride(ZipOutputStream zos, Path dir, String basePath) throws IOException {
        if (!Files.exists(dir)) return;
        
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.filter(Files::isRegularFile)
                .forEach(p -> {
                    try {
                        String entryName = basePath + "/" + dir.relativize(p).toString().replace("\\", "/");
                        ZipEntry entry = new ZipEntry(entryName);
                        zos.putNextEntry(entry);
                        Files.copy(p, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        logger.warn("Failed to add override file: {}", p);
                    }
                });
        }
    }
    
    private static class ModFile {
        private Path path;
        private String fileName;
        private long fileSize;
        private String sha1;
        private String sha512;
        private String downloadUrl;
        private String projectId;
        private String versionId;
        
        public Path getPath() { return path; }
        public void setPath(Path path) { this.path = path; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        public String getSha1() { return sha1; }
        public void setSha1(String sha1) { this.sha1 = sha1; }
        public String getSha512() { return sha512; }
        public void setSha512(String sha512) { this.sha512 = sha512; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public String getProjectId() { return projectId; }
        public void setProjectId(String projectId) { this.projectId = projectId; }
        public String getVersionId() { return versionId; }
        public void setVersionId(String versionId) { this.versionId = versionId; }
    }
}