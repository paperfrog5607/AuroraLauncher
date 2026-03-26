package org.aurora.launcher.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

public class CloudSaveManager {

    private static final Logger logger = LoggerFactory.getLogger(CloudSaveManager.class);
    private static CloudSaveManager instance;

    private final String authToken;
    private final ExecutorService executor;
    private final Path localSaveDir;
    private final Map<String, SaveMetadata> cloudMetadata;

    private static final String SAVE_CONFIG_DIR = System.getProperty("user.home") + "/.aurora/cloud_saves";

    private CloudSaveManager(String authToken) {
        this.authToken = authToken;
        this.executor = Executors.newCachedThreadPool();
        this.localSaveDir = Paths.get(System.getProperty("user.home"), ".aurora", "saves");
        this.cloudMetadata = new ConcurrentHashMap<>();
        loadCloudMetadata();
    }

    public static synchronized CloudSaveManager getInstance(String authToken) {
        if (instance == null) {
            instance = new CloudSaveManager(authToken);
        }
        return instance;
    }

    public void uploadSave(String gameId, String saveName, File saveDirectory, UploadCallback callback) {
        executor.submit(() -> {
            try {
                Path zipPath = Paths.get(SAVE_CONFIG_DIR, "temp_" + System.currentTimeMillis() + ".zip");
                zipDirectory(saveDirectory.toPath(), zipPath);
                
                String url = "http://localhost:8080/api/cloud/saves/" + gameId + "/" + saveName;
                String response = uploadFile(url, zipPath.toFile());
                
                Files.deleteIfExists(zipPath);
                
                SaveMetadata metadata = new SaveMetadata();
                metadata.gameId = gameId;
                metadata.saveName = saveName;
                metadata.cloudUrl = extractString(response, "url");
                metadata.lastSynced = System.currentTimeMillis();
                metadata.localPath = saveDirectory.getAbsolutePath();
                metadata.fileSize = getDirectorySize(saveDirectory);
                
                cloudMetadata.put(gameId + ":" + saveName, metadata);
                saveCloudMetadata();
                
                callback.onSuccess(metadata);
                logger.info("Uploaded save: {}/{}", gameId, saveName);
            } catch (Exception e) {
                logger.error("Failed to upload save", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void downloadSave(String gameId, String saveName, File destDir, DownloadCallback callback) {
        executor.submit(() -> {
            try {
                String key = gameId + ":" + saveName;
                SaveMetadata metadata = cloudMetadata.get(key);
                
                if (metadata == null || metadata.cloudUrl == null) {
                    callback.onError("Save not found in cloud");
                    return;
                }
                
                downloadFile(metadata.cloudUrl, destDir.toPath());
                
                metadata.lastSynced = System.currentTimeMillis();
                metadata.localPath = destDir.getAbsolutePath();
                saveCloudMetadata();
                
                callback.onSuccess(destDir);
                logger.info("Downloaded save: {}/{}", gameId, saveName);
            } catch (Exception e) {
                logger.error("Failed to download save", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void syncSave(String gameId, String saveName, SyncCallback callback) {
        executor.submit(() -> {
            String key = gameId + ":" + saveName;
            SaveMetadata metadata = cloudMetadata.get(key);
            
            if (metadata == null) {
                callback.onError("No save metadata found for: " + saveName);
                return;
            }
            
            File localSave = new File(metadata.localPath);
            if (!localSave.exists()) {
                downloadSave(gameId, saveName, localSave.getParentFile(), 
                    new DownloadCallback() {
                        public void onSuccess(File dir) { callback.onSuccess("Downloaded from cloud"); }
                        public void onError(String error) { callback.onError(error); }
                    });
                return;
            }
            
            long localModified = localSave.lastModified();
            long cloudModified = metadata.lastSynced;
            
            if (localModified > cloudModified) {
                uploadSave(gameId, saveName, localSave, new UploadCallback() {
                    public void onSuccess(SaveMetadata m) { callback.onSuccess("Uploaded to cloud"); }
                    public void onError(String error) { callback.onError(error); }
                });
            } else if (cloudModified > localModified) {
                downloadSave(gameId, saveName, localSave.getParentFile(),
                    new DownloadCallback() {
                        public void onSuccess(File dir) { callback.onSuccess("Downloaded from cloud"); }
                        public void onError(String error) { callback.onError(error); }
                    });
            } else {
                callback.onSuccess("Already in sync");
            }
        });
    }

    public void listCloudSaves(String gameId, ListCallback callback) {
        executor.submit(() -> {
            try {
                String url = "http://localhost:8080/api/cloud/saves/" + gameId;
                String response = sendGet(url, authToken);
                
                List<SaveMetadata> saves = parseSaveList(response, gameId);
                callback.onSuccess(saves);
            } catch (Exception e) {
                logger.error("Failed to list cloud saves", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void deleteCloudSave(String gameId, String saveName, DeleteCallback callback) {
        executor.submit(() -> {
            try {
                String url = "http://localhost:8080/api/cloud/saves/" + gameId + "/" + saveName;
                sendDelete(url, authToken);
                
                cloudMetadata.remove(gameId + ":" + saveName);
                saveCloudMetadata();
                
                callback.onSuccess();
                logger.info("Deleted cloud save: {}/{}", gameId, saveName);
            } catch (Exception e) {
                logger.error("Failed to delete cloud save", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public Map<String, SaveMetadata> getCloudMetadata() {
        return new HashMap<>(cloudMetadata);
    }

    private long getDirectorySize(File dir) {
        long size = 0;
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                size += file.isDirectory() ? getDirectorySize(file) : file.length();
            }
        }
        return size;
    }

    private void zipDirectory(Path source, Path zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
            Files.walk(source).filter(p -> !Files.isDirectory(p)).forEach(p -> {
                try {
                    ZipEntry entry = new ZipEntry(source.relativize(p).toString());
                    zos.putNextEntry(entry);
                    Files.copy(p, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    logger.error("Failed to zip: " + p, e);
                }
            });
        }
    }

    private String uploadFile(String url, File file) throws IOException {
        String boundary = Long.toHexString(System.currentTimeMillis());
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("Authorization", "Bearer " + authToken);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(("--" + boundary + "\r\n").getBytes());
            os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n").getBytes());
            os.write("\r\n".getBytes());
            Files.copy(file.toPath(), os);
            os.write("\r\n".getBytes());
            os.write(("--" + boundary + "--\r\n").getBytes());
        }

        return new String(conn.getInputStream().readAllBytes());
    }

    private void downloadFile(String url, Path dest) throws IOException {
        java.net.URL fileUrl = new java.net.URL(url);
        Files.createDirectories(dest.getParent());
        try (InputStream is = fileUrl.openStream()) {
            String filename = fileUrl.getFile().substring(fileUrl.getFile().lastIndexOf('/') + 1);
            Files.copy(is, dest.resolve(filename));
        }
    }

    private List<SaveMetadata> parseSaveList(String json, String gameId) {
        List<SaveMetadata> saves = new ArrayList<>();
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\\{\"name\":\"([^\"]+)\",\"size\":(\\d+),\"modified\":(\\d+)\\}");
        java.util.regex.Matcher m = p.matcher(json);
        
        while (m.find()) {
            SaveMetadata save = new SaveMetadata();
            save.gameId = gameId;
            save.saveName = m.group(1);
            save.fileSize = Long.parseLong(m.group(2));
            save.lastSynced = Long.parseLong(m.group(3));
            saves.add(save);
        }
        return saves;
    }

    private void saveCloudMetadata() {
        try {
            Files.createDirectories(Paths.get(SAVE_CONFIG_DIR));
            Path metadataFile = Paths.get(SAVE_CONFIG_DIR, "cloud_saves.json");
            
            StringBuilder sb = new StringBuilder();
            sb.append("{\"saves\":{");
            boolean first = true;
            for (Map.Entry<String, SaveMetadata> entry : cloudMetadata.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                SaveMetadata m = entry.getValue();
                sb.append("\"").append(entry.getKey()).append("\":{");
                sb.append("\"gameId\":\"").append(m.gameId).append("\",");
                sb.append("\"saveName\":\"").append(m.saveName).append("\",");
                sb.append("\"localPath\":\"").append(m.localPath).append("\",");
                sb.append("\"cloudUrl\":\"").append(m.cloudUrl).append("\",");
                sb.append("\"fileSize\":").append(m.fileSize).append(",");
                sb.append("\"lastSynced\":").append(m.lastSynced);
                sb.append("}");
            }
            sb.append("}}");
            
            Files.writeString(metadataFile, sb.toString());
        } catch (IOException e) {
            logger.error("Failed to save cloud metadata", e);
        }
    }

    private void loadCloudMetadata() {
        Path metadataFile = Paths.get(SAVE_CONFIG_DIR, "cloud_saves.json");
        if (!Files.exists(metadataFile)) return;
        
        try {
            String content = Files.readString(metadataFile);
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "\"([^\"]+)\":\\{\"gameId\":\"([^\"]+)\",\"saveName\":\"([^\"]+)\"," +
                "\"localPath\":\"([^\"]*)\",\"cloudUrl\":\"([^\"]*)\",\"fileSize\":(\\d+),\"lastSynced\":(\\d+)\\}");
            java.util.regex.Matcher m = p.matcher(content);
            
            while (m.find()) {
                SaveMetadata metadata = new SaveMetadata();
                metadata.gameId = m.group(2);
                metadata.saveName = m.group(3);
                metadata.localPath = m.group(4);
                metadata.cloudUrl = m.group(5);
                metadata.fileSize = Long.parseLong(m.group(6));
                metadata.lastSynced = Long.parseLong(m.group(7));
                cloudMetadata.put(m.group(1), metadata);
            }
        } catch (IOException e) {
            logger.error("Failed to load cloud metadata", e);
        }
    }

    private String sendGet(String url, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return new String(conn.getInputStream().readAllBytes());
    }

    private String sendDelete(String url, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return new String(conn.getInputStream().readAllBytes());
    }

    private String extractString(String json, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    public static class SaveMetadata implements Serializable {
        public String gameId;
        public String saveName;
        public String localPath;
        public String cloudUrl;
        public long fileSize;
        public long lastSynced;
    }

    public interface UploadCallback {
        void onSuccess(SaveMetadata metadata);
        void onError(String error);
    }

    public interface DownloadCallback {
        void onSuccess(File directory);
        void onError(String error);
    }

    public interface SyncCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface ListCallback {
        void onSuccess(List<SaveMetadata> saves);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
}