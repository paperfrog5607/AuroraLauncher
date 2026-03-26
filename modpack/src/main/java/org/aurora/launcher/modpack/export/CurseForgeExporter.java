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

public class CurseForgeExporter implements Exporter {
    
    private static final Logger logger = LoggerFactory.getLogger(CurseForgeExporter.class);
    private static final String MANIFEST_FILE = "manifest.json";
    private static final String MINECRAFT_FOLDER = "minecraft";
    
    private final Gson gson;
    
    public CurseForgeExporter() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    @Override
    public String getFormat() {
        return "curseforge";
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
                outputFile = Paths.get(exportName + ".zip");
            }
            
            if (Files.exists(outputFile) && !options.isOverrideExisting()) {
                throw new RuntimeException("Output file already exists: " + outputFile);
            }
            
            try {
                Files.createDirectories(outputFile.getParent());
                
                JsonObject manifest = createManifest(instance, options);
                List<ModEntry> mods = collectMods(modsDir, options);
                
                try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(outputFile))) {
                    addManifest(zos, manifest);
                    addModsToManifest(manifest, mods);
                    writeManifest(zos, manifest);
                    
                    if (options.isIncludeWorlds()) {
                        addWorlds(zos, instance);
                    }
                    
                    addOverrides(zos, instance, options);
                }
                
                logger.info("Exported CurseForge modpack to: {}", outputFile);
                return outputFile;
            } catch (Exception e) {
                throw new RuntimeException("Failed to export modpack: " + e.getMessage(), e);
            }
        });
    }
    
    private JsonObject createManifest(Instance instance, ExportOptions options) {
        JsonObject manifest = new JsonObject();
        manifest.addProperty("manifestType", "minecraftModpack");
        manifest.addProperty("manifestVersion", 1);
        manifest.addProperty("name", options.getName() != null ? options.getName() : instance.getName());
        manifest.addProperty("version", options.getVersion() != null ? options.getVersion() : instance.getVersion());
        manifest.addProperty("author", options.getAuthor() != null ? options.getAuthor() : "Unknown");
        
        JsonObject minecraft = new JsonObject();
        minecraft.addProperty("version", instance.getConfig().getMinecraftVersion());
        
        JsonObject modLoader = new JsonObject();
        modLoader.addProperty("id", getLoaderId(instance.getConfig().getLoaderType(), 
                instance.getConfig().getLoaderVersion()));
        modLoader.addProperty("primary", true);
        
        JsonArray modLoaders = new JsonArray();
        modLoaders.add(modLoader);
        minecraft.add("modLoaders", modLoaders);
        
        manifest.add("minecraft", minecraft);
        
        return manifest;
    }
    
    private String getLoaderId(String loaderType, String loaderVersion) {
        if (loaderType == null || "vanilla".equalsIgnoreCase(loaderType)) {
            return "vanilla";
        }
        
        String type = loaderType.toLowerCase();
        String version = loaderVersion != null ? loaderVersion : "latest";
        
        switch (type) {
            case "fabric":
                return "fabric-" + version;
            case "forge":
                return "forge-" + version;
            case "quilt":
                return "quilt-" + version;
            case "neoforge":
                return "neoforge-" + version;
            default:
                return type + "-" + version;
        }
    }
    
    private List<ModEntry> collectMods(Path modsDir, ExportOptions options) throws IOException {
        List<ModEntry> mods = new ArrayList<>();
        Set<String> excluded = new HashSet<>(options.getExcludedMods());
        
        try (Stream<Path> files = Files.list(modsDir)) {
            files.filter(p -> p.toString().endsWith(".jar") && !p.toString().endsWith(".disabled"))
                 .forEach(p -> {
                     try {
                         ModEntry entry = new ModEntry();
                         entry.setFile(p);
                         entry.setFileName(p.getFileName().toString());
                         entry.setFileSize(Files.size(p));
                         entry.setSha1(calculateSha1(p));
                         entry.setSha512(calculateSha512(p));
                         mods.add(entry);
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
    
    private void addManifest(ZipOutputStream zos, JsonObject manifest) throws IOException {
        ZipEntry entry = new ZipEntry(MANIFEST_FILE);
        zos.putNextEntry(entry);
        zos.write(gson.toJson(manifest).getBytes("UTF-8"));
        zos.closeEntry();
    }
    
    private void addModsToManifest(JsonObject manifest, List<ModEntry> mods) {
        JsonArray files = new JsonArray();
        
        for (ModEntry mod : mods) {
            JsonObject file = new JsonObject();
            file.addProperty("projectID", mod.getProjectId());
            file.addProperty("fileID", mod.getFileId());
            file.addProperty("required", true);
            files.add(file);
        }
        
        manifest.add("files", files);
    }
    
    private void writeManifest(ZipOutputStream zos, JsonObject manifest) throws IOException {
        ZipEntry entry = new ZipEntry(MANIFEST_FILE);
        zos.putNextEntry(entry);
        zos.write(gson.toJson(manifest).getBytes("UTF-8"));
        zos.closeEntry();
    }
    
    private void addWorlds(ZipOutputStream zos, Instance instance) throws IOException {
        Path savesDir = instance.getSavesDir();
        if (savesDir == null || !Files.exists(savesDir)) return;
        
        try (Stream<Path> walk = Files.walk(savesDir)) {
            walk.filter(Files::isRegularFile)
                .forEach(p -> {
                    try {
                        String entryName = MINECRAFT_FOLDER + "/saves/" + savesDir.relativize(p).toString().replace("\\", "/");
                        ZipEntry entry = new ZipEntry(entryName);
                        zos.putNextEntry(entry);
                        Files.copy(p, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        logger.warn("Failed to add world file: {}", p);
                    }
                });
        }
    }
    
    private void addOverrides(ZipOutputStream zos, Instance instance, ExportOptions options) throws IOException {
        Path configDir = instance.getConfigDir();
        if (configDir != null && Files.exists(configDir)) {
            try (Stream<Path> walk = Files.walk(configDir)) {
                walk.filter(Files::isRegularFile)
                    .forEach(p -> {
                        try {
                            String entryName = MINECRAFT_FOLDER + "/config/" + configDir.relativize(p).toString().replace("\\", "/");
                            ZipEntry entry = new ZipEntry(entryName);
                            zos.putNextEntry(entry);
                            Files.copy(p, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            logger.warn("Failed to add config file: {}", p);
                        }
                    });
            }
        }
    }
    
    private static class ModEntry {
        private Path file;
        private String fileName;
        private long fileSize;
        private String sha1;
        private String sha512;
        private int projectId = 0;
        private int fileId = 0;
        
        public Path getFile() { return file; }
        public void setFile(Path file) { this.file = file; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        public String getSha1() { return sha1; }
        public void setSha1(String sha1) { this.sha1 = sha1; }
        public String getSha512() { return sha512; }
        public void setSha512(String sha512) { this.sha512 = sha512; }
        public int getProjectId() { return projectId; }
        public void setProjectId(int projectId) { this.projectId = projectId; }
        public int getFileId() { return fileId; }
        public void setFileId(int fileId) { this.fileId = fileId; }
    }
}