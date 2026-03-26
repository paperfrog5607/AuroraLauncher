package org.aurora.launcher.modpack.import_;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.aurora.launcher.modpack.instance.Instance;
import org.aurora.launcher.modpack.instance.InstanceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModrinthImporter implements Importer {
    
    private static final Logger logger = LoggerFactory.getLogger(ModrinthImporter.class);
    private static final String INDEX_FILE = "modrinth.index.json";
    
    private final Gson gson;
    private final ExecutorService executor;
    
    public ModrinthImporter() {
        this.gson = new Gson();
        this.executor = Executors.newCachedThreadPool();
    }
    
    @Override
    public String getFormat() {
        return "modrinth";
    }
    
    @Override
    public boolean canImport(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".mrpack") && !fileName.endsWith(".zip")) {
            return false;
        }
        
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(INDEX_FILE)) {
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
                task.setCurrentStep("Parsing modrinth.index.json...");
                
                IndexInfo index = parseIndex(file);
                
                ImportTask.InstanceInfo info = new ImportTask.InstanceInfo();
                info.setName(index.name);
                info.setVersion(index.versionId);
                info.setMinecraftVersion(index.minecraftVersion);
                info.setLoaderType(index.loaderType);
                info.setLoaderVersion(index.loaderVersion);
                task.setInstanceInfo(info);
                
                Path instanceDir = targetDir.resolve(index.name);
                Files.createDirectories(instanceDir);
                
                Path minecraftDir = instanceDir.resolve(".minecraft");
                Files.createDirectories(minecraftDir);
                Files.createDirectories(minecraftDir.resolve("mods"));
                
                task.setState(ImportTask.TaskState.EXTRACTING);
                task.setCurrentStep("Extracting overrides...");
                extractOverrides(file, minecraftDir);
                
                task.setState(ImportTask.TaskState.DOWNLOADING);
                task.setCurrentStep("Downloading mods...");
                task.setTotalMods(index.files.size());
                
                InstanceBuilder builder = new InstanceBuilder()
                        .name(index.name)
                        .version(index.versionId)
                        .minecraftVersion(index.minecraftVersion)
                        .loaderType(index.loaderType)
                        .loaderVersion(index.loaderVersion)
                        .instanceDir(instanceDir);
                
                Instance instance = builder.build();
                
                task.setState(ImportTask.TaskState.COMPLETED);
                task.setProgress(1.0);
                
                logger.info("Imported Modrinth modpack: {}", index.name);
                return instance;
            } catch (Exception e) {
                task.setState(ImportTask.TaskState.FAILED);
                task.setErrorMessage(e.getMessage());
                throw new RuntimeException("Failed to import modpack: " + e.getMessage(), e);
            }
        }, executor);
    }
    
    private IndexInfo parseIndex(Path file) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(INDEX_FILE)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, read);
                    }
                    
                    JsonObject index = gson.fromJson(baos.toString("UTF-8"), JsonObject.class);
                    
                    IndexInfo info = new IndexInfo();
                    info.formatVersion = index.has("formatVersion") ? index.get("formatVersion").getAsInt() : 1;
                    info.game = index.has("game") ? index.get("game").getAsString() : "minecraft";
                    info.name = index.has("name") ? index.get("name").getAsString() : "Imported Modpack";
                    info.versionId = index.has("versionId") ? index.get("versionId").getAsString() : "1.0.0";
                    
                    if (index.has("dependencies")) {
                        JsonObject deps = index.getAsJsonObject("dependencies");
                        info.minecraftVersion = deps.has("minecraft") ? deps.get("minecraft").getAsString() : "1.20.4";
                        
                        if (deps.has("fabric-loader")) {
                            info.loaderType = "fabric";
                            info.loaderVersion = deps.get("fabric-loader").getAsString();
                        } else if (deps.has("forge")) {
                            info.loaderType = "forge";
                            info.loaderVersion = deps.get("forge").getAsString();
                        } else if (deps.has("quilt-loader")) {
                            info.loaderType = "quilt";
                            info.loaderVersion = deps.get("quilt-loader").getAsString();
                        } else if (deps.has("neoforge")) {
                            info.loaderType = "neoforge";
                            info.loaderVersion = deps.get("neoforge").getAsString();
                        } else {
                            info.loaderType = "vanilla";
                        }
                    }
                    
                    if (index.has("files")) {
                        info.files = new ArrayList<>();
                        JsonArray filesArray = index.getAsJsonArray("files");
                        filesArray.forEach(f -> {
                            JsonObject fileObj = f.getAsJsonObject();
                            ModFile mf = new ModFile();
                            mf.path = fileObj.has("path") ? fileObj.get("path").getAsString() : "";
                            
                            if (fileObj.has("hashes")) {
                                JsonObject hashes = fileObj.getAsJsonObject("hashes");
                                mf.sha1 = hashes.has("sha1") ? hashes.get("sha1").getAsString() : "";
                                mf.sha512 = hashes.has("sha512") ? hashes.get("sha512").getAsString() : "";
                            }
                            
                            if (fileObj.has("downloads")) {
                                mf.downloads = new ArrayList<>();
                                fileObj.getAsJsonArray("downloads").forEach(d -> mf.downloads.add(d.getAsString()));
                            }
                            
                            info.files.add(mf);
                        });
                    }
                    
                    return info;
                }
            }
        }
        
        throw new IOException("modrinth.index.json not found in modpack");
    }
    
    private void extractOverrides(Path file, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                
                if (name.equals(INDEX_FILE)) {
                    continue;
                }
                
                String targetPath;
                if (name.startsWith("override/")) {
                    targetPath = name.substring("override/".length());
                } else if (name.startsWith("overrides/")) {
                    targetPath = name.substring("overrides/".length());
                } else if (name.startsWith("server-overrides/")) {
                    continue;
                } else if (name.startsWith("client-overrides/")) {
                    targetPath = name.substring("client-overrides/".length());
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
    
    private static class IndexInfo {
        int formatVersion;
        String game;
        String name;
        String versionId;
        String minecraftVersion;
        String loaderType;
        String loaderVersion;
        List<ModFile> files;
    }
    
    private static class ModFile {
        String path;
        String sha1;
        String sha512;
        List<String> downloads;
    }
}