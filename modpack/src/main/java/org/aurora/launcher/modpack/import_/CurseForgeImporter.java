package org.aurora.launcher.modpack.import_;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.aurora.launcher.modpack.instance.Instance;
import org.aurora.launcher.modpack.instance.InstanceBuilder;
import org.aurora.launcher.modpack.instance.InstanceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CurseForgeImporter implements Importer {
    
    private static final Logger logger = LoggerFactory.getLogger(CurseForgeImporter.class);
    private static final String MANIFEST_FILE = "manifest.json";
    
    private final Gson gson;
    private final ExecutorService executor;
    
    public CurseForgeImporter() {
        this.gson = new Gson();
        this.executor = Executors.newCachedThreadPool();
    }
    
    @Override
    public String getFormat() {
        return "curseforge";
    }
    
    @Override
    public boolean canImport(Path file) {
        if (!file.toString().endsWith(".zip")) {
            return false;
        }
        
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(MANIFEST_FILE)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        
        return false;
    }
    
    @Override
    public CompletableFuture<ImportTask> createImportTask(Path file, Path targetDir) {
        ImportTask task = new ImportTask(file, targetDir);
        return CompletableFuture.completedFuture(task);
    }
    
    @Override
    public CompletableFuture<Instance> import_(Path file, Path targetDir) {
        return CompletableFuture.supplyAsync(() -> {
            ImportTask task = new ImportTask(file, targetDir);
            
            try {
                task.setState(ImportTask.TaskState.PARSING);
                task.setCurrentStep("Parsing manifest...");
                
                ManifestInfo manifest = parseManifest(file);
                
                ImportTask.InstanceInfo info = new ImportTask.InstanceInfo();
                info.setName(manifest.name);
                info.setVersion(manifest.version);
                info.setAuthor(manifest.author);
                info.setMinecraftVersion(manifest.minecraftVersion);
                info.setLoaderType(manifest.loaderType);
                info.setLoaderVersion(manifest.loaderVersion);
                task.setInstanceInfo(info);
                
                Path instanceDir = targetDir.resolve(manifest.name);
                Files.createDirectories(instanceDir);
                
                Path minecraftDir = instanceDir.resolve(".minecraft");
                Files.createDirectories(minecraftDir);
                
                task.setState(ImportTask.TaskState.EXTRACTING);
                task.setCurrentStep("Extracting files...");
                extractOverrides(file, minecraftDir);
                
                task.setState(ImportTask.TaskState.DOWNLOADING);
                task.setCurrentStep("Downloading mods...");
                task.setTotalMods(manifest.files.size());
                
                InstanceBuilder builder = new InstanceBuilder()
                        .name(manifest.name)
                        .version(manifest.version)
                        .minecraftVersion(manifest.minecraftVersion)
                        .loaderType(manifest.loaderType)
                        .loaderVersion(manifest.loaderVersion)
                        .instanceDir(instanceDir);
                
                Instance instance = builder.build();
                
                task.setState(ImportTask.TaskState.COMPLETED);
                task.setProgress(1.0);
                
                logger.info("Imported CurseForge modpack: {}", manifest.name);
                return instance;
            } catch (Exception e) {
                task.setState(ImportTask.TaskState.FAILED);
                task.setErrorMessage(e.getMessage());
                throw new RuntimeException("Failed to import modpack: " + e.getMessage(), e);
            }
        }, executor);
    }
    
    private ManifestInfo parseManifest(Path file) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(MANIFEST_FILE)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, read);
                    }
                    
                    JsonObject manifest = gson.fromJson(baos.toString("UTF-8"), JsonObject.class);
                    
                    ManifestInfo info = new ManifestInfo();
                    info.name = manifest.has("name") ? manifest.get("name").getAsString() : "Imported Modpack";
                    info.version = manifest.has("version") ? manifest.get("version").getAsString() : "1.0.0";
                    info.author = manifest.has("author") ? manifest.get("author").getAsString() : "Unknown";
                    
                    if (manifest.has("minecraft")) {
                        JsonObject mc = manifest.getAsJsonObject("minecraft");
                        info.minecraftVersion = mc.has("version") ? mc.get("version").getAsString() : "1.20.4";
                        
                        if (mc.has("modLoaders") && mc.getAsJsonArray("modLoaders").size() > 0) {
                            JsonObject loader = mc.getAsJsonArray("modLoaders").get(0).getAsJsonObject();
                            String loaderId = loader.has("id") ? loader.get("id").getAsString() : "";
                            
                            if (loaderId.startsWith("fabric-")) {
                                info.loaderType = "fabric";
                                info.loaderVersion = loaderId.substring(7);
                            } else if (loaderId.startsWith("forge-")) {
                                info.loaderType = "forge";
                                info.loaderVersion = loaderId.substring(6);
                            } else if (loaderId.startsWith("quilt-")) {
                                info.loaderType = "quilt";
                                info.loaderVersion = loaderId.substring(6);
                            } else if (loaderId.startsWith("neoforge-")) {
                                info.loaderType = "neoforge";
                                info.loaderVersion = loaderId.substring(9);
                            } else {
                                info.loaderType = "vanilla";
                            }
                        }
                    }
                    
                    if (manifest.has("files")) {
                        info.files = new ArrayList<>();
                        manifest.getAsJsonArray("files").forEach(f -> {
                            JsonObject fileObj = f.getAsJsonObject();
                            ManifestFile mf = new ManifestFile();
                            mf.projectId = fileObj.has("projectID") ? fileObj.get("projectID").getAsInt() : 0;
                            mf.fileId = fileObj.has("fileID") ? fileObj.get("fileID").getAsInt() : 0;
                            mf.required = !fileObj.has("required") || fileObj.get("required").getAsBoolean();
                            info.files.add(mf);
                        });
                    }
                    
                    return info;
                }
            }
        }
        
        throw new IOException("Manifest not found in modpack");
    }
    
    private void extractOverrides(Path file, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                
                if (name.equals(MANIFEST_FILE)) {
                    continue;
                }
                
                String targetPath;
                if (name.startsWith("minecraft/")) {
                    targetPath = name.substring("minecraft/".length());
                } else if (name.startsWith("overrides/")) {
                    targetPath = name.substring("overrides/".length());
                } else {
                    continue;
                }
                
                if (targetPath.isEmpty()) {
                    continue;
                }
                
                Path destPath = targetDir.resolve(targetPath);
                
                if (entry.isDirectory()) {
                    Files.createDirectories(destPath);
                } else {
                    Files.createDirectories(destPath.getParent());
                    Files.copy(zis, destPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    private static class ManifestInfo {
        String name;
        String version;
        String author;
        String minecraftVersion;
        String loaderType;
        String loaderVersion;
        List<ManifestFile> files;
    }
    
    private static class ManifestFile {
        int projectId;
        int fileId;
        boolean required;
    }
}