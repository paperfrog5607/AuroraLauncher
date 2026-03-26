package org.aurora.launcher.ui.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.aurora.launcher.core.mirror.MirrorManager;
import org.aurora.launcher.core.net.HttpClient;
import org.aurora.launcher.core.path.PathManager;
import org.aurora.launcher.launcher.install.GameInstaller;
import org.aurora.launcher.launcher.install.InstallOptions;
import org.aurora.launcher.launcher.install.ProgressCallback;
import org.aurora.launcher.launcher.version.AssetIndex;
import org.aurora.launcher.launcher.version.JavaVersionInfo;
import org.aurora.launcher.launcher.version.Library;
import org.aurora.launcher.launcher.version.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VersionDownloadService {
    private static final Logger logger = LoggerFactory.getLogger(VersionDownloadService.class);
    
    private final GameInstaller gameInstaller;
    private final HttpClient httpClient;
    private final Map<String, DownloadTaskEntry> activeTasks;
    private final Gson gson;
    
    public VersionDownloadService() {
        PathManager pathManager = PathManager.getInstance();
        Path versionsDir = pathManager.getVersionsDirectory();
        Path librariesDir = versionsDir.resolve("libraries");
        Path assetsDir = versionsDir.resolve("assets");
        this.gameInstaller = new GameInstaller(versionsDir, librariesDir, assetsDir);
        this.httpClient = new HttpClient();
        this.activeTasks = new HashMap<>();
        this.gson = new Gson();
    }
    
    public CompletableFuture<DownloadResult> downloadVersion(org.aurora.launcher.api.mojang.VersionInfo apiVersionInfo, 
            Consumer<DownloadProgress> callback) {
        CompletableFuture<DownloadResult> future = new CompletableFuture<>();
        
        Task<DownloadResult> task = new Task<DownloadResult>() {
            @Override
            protected DownloadResult call() throws Exception {
                try {
                    String versionId = apiVersionInfo.getId();
                    String versionJsonUrl = apiVersionInfo.getUrl();
                    
                    logger.info("Downloading version {} from {}", versionId, versionJsonUrl);
                    
                    String mirroredUrl = MirrorManager.getInstance().transformUrl(versionJsonUrl);
                    logger.info("Transformed URL: {}", mirroredUrl);
                    
                    logger.info("Starting HTTP GET request...");
                    String jsonContent = httpClient.get(mirroredUrl);
                    logger.info("HTTP GET completed, received {} characters", jsonContent.length());
                    
                    VersionInfo fullVersionInfo = parseVersionJson(versionId, jsonContent);
                    
                    InstallOptions options = InstallOptions.minimal();
                    logger.info("Starting game installation...");
                    gameInstaller.install(fullVersionInfo, options, new ProgressCallback() {
                        @Override
                        public void onProgress(String stage, double progress, long current, long total) {
                            updateProgress((long) progress, 100);
                            Platform.runLater(() -> {
                                if (callback != null) {
                                    callback.accept(new DownloadProgress(stage, progress / 100.0, current, total));
                                }
                            });
                        }
                        
                        @Override
                        public void onMessage(String message) {
                            updateMessage(message);
                        }
                        
                        @Override
                        public void onError(Exception error) {
                            logger.error("Installation error: ", error);
                        }
                        
                        @Override
                        public void onComplete() {
                            logger.info("Installation completed");
                        }
                    }).join();
                    
                    logger.info("Game installation finished");
                    return new DownloadResult(versionId, true, null);
                } catch (Exception e) {
                    logger.error("Download failed", e);
                    return new DownloadResult(apiVersionInfo.getId(), false, e.getMessage());
                }
            }
        };
        
        activeTasks.put(apiVersionInfo.getId(), new DownloadTaskEntry(apiVersionInfo.getId(), task));
        task.setOnSucceeded(e -> {
            activeTasks.remove(apiVersionInfo.getId());
            future.complete(task.getValue());
        });
        task.setOnFailed(e -> {
            activeTasks.remove(apiVersionInfo.getId());
            future.completeExceptionally(task.getException());
        });
        
        new Thread(task).start();
        return future;
    }
    
    private VersionInfo parseVersionJson(String versionId, String json) {
        JsonObject root = gson.fromJson(json, JsonObject.class);
        
        VersionInfo info = new VersionInfo();
        info.setId(versionId);
        
        if (root.has("releaseTime")) {
            info.setReleaseTime(java.time.Instant.parse(root.get("releaseTime").getAsString()));
        }
        
        if (root.has("mainClass")) {
            String mainClassStr = root.get("mainClass").getAsString();
            info.setMainClass(mainClassStr);
        }
        
        if (root.has("javaVersion")) {
            JsonObject javaVersion = root.getAsJsonObject("javaVersion");
            JavaVersionInfo javaInfo = new JavaVersionInfo();
            if (javaVersion.has("majorVersion")) {
                javaInfo.setMajorVersion(javaVersion.get("majorVersion").getAsInt());
            }
            info.setJavaVersion(javaInfo);
        }
        
        if (root.has("libraries")) {
            JsonArray librariesArray = root.getAsJsonArray("libraries");
            Type libraryListType = new TypeToken<List<Library>>(){}.getType();
            List<Library> libraries = gson.fromJson(librariesArray, libraryListType);
            info.setLibraries(libraries);
        }
        
        if (root.has("assetIndex")) {
            JsonObject assetIndex = root.getAsJsonObject("assetIndex");
            AssetIndex assetIndexInfo = new AssetIndex();
            assetIndexInfo.setId(assetIndex.get("id").getAsString());
            if (assetIndex.has("url")) {
                assetIndexInfo.setUrl(assetIndex.get("url").getAsString());
            }
            if (assetIndex.has("sha1")) {
                assetIndexInfo.setSha1(assetIndex.get("sha1").getAsString());
            }
            if (assetIndex.has("size")) {
                assetIndexInfo.setSize(assetIndex.get("size").getAsLong());
            }
            if (assetIndex.has("totalSize")) {
                assetIndexInfo.setTotalSize(assetIndex.get("totalSize").getAsLong());
            }
            info.setAssetIndex(assetIndexInfo);
        }
        
        if (root.has("downloads")) {
            JsonObject downloads = root.getAsJsonObject("downloads");
            
            if (downloads.has("client")) {
                JsonObject client = downloads.getAsJsonObject("client");
                if (client.has("url")) {
                    info.setClientDownloadUrl(client.get("url").getAsString());
                }
                if (client.has("sha1")) {
                    info.setClientVersionHash(client.get("sha1").getAsString());
                }
            }
        }
        
        return info;
    }
    
    public void cancelDownload(String versionId) {
        DownloadTaskEntry entry = activeTasks.get(versionId);
        if (entry != null) {
            entry.task.cancel();
            activeTasks.remove(versionId);
        }
    }
    
    public boolean isVersionDownloaded(String versionId) {
        return gameInstaller.isInstalled(versionId);
    }
    
    public boolean hasActiveDownload(String versionId) {
        return activeTasks.containsKey(versionId);
    }
    
    public static class DownloadProgress {
        public final String stage;
        public final double progress;
        public final long current;
        public final long total;
        
        public DownloadProgress(String stage, double progress, long current, long total) {
            this.stage = stage;
            this.progress = progress;
            this.current = current;
            this.total = total;
        }
    }
    
    public static class DownloadResult {
        public final String versionId;
        public final boolean success;
        public final String error;
        
        public DownloadResult(String versionId, boolean success, String error) {
            this.versionId = versionId;
            this.success = success;
            this.error = error;
        }
    }
    
    private static class DownloadTaskEntry {
        final String versionId;
        final Task<DownloadResult> task;
        
        DownloadTaskEntry(String versionId, Task<DownloadResult> task) {
            this.versionId = versionId;
            this.task = task;
        }
    }
}