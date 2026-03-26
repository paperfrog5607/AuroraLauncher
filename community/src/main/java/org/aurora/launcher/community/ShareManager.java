package org.aurora.launcher.community;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

public class ShareManager {

    private static final Logger logger = LoggerFactory.getLogger(ShareManager.class);
    private static ShareManager instance;

    private final Map<String, ShareCode> activeShareCodes;
    private final ExecutorService uploadExecutor;
    private final String authToken;

    private static final String SHARE_CODES_FILE = System.getProperty("user.home") + "/.aurora/share_codes.dat";

    private ShareManager(String authToken) {
        this.activeShareCodes = new ConcurrentHashMap<>();
        this.uploadExecutor = Executors.newCachedThreadPool();
        this.authToken = authToken;
        loadShareCodes();
    }

    public static synchronized ShareManager getInstance(String authToken) {
        if (instance == null) {
            instance = new ShareManager(authToken);
        }
        return instance;
    }

    public void uploadModpack(File modpackFile, ModpackInfo info, UploadCallback callback) {
        uploadExecutor.submit(() -> {
            try {
                String url = "http://localhost:8080/api/packages";
                String json = String.format(
                    "{\"name\":\"%s\",\"description\":\"%s\",\"type\":\"MODPACK\",\"version\":\"%s\",\"game_id\":\"%s\"}",
                    info.name, info.description, info.version, info.gameId != null ? info.gameId : "");
                
                String response = sendPost(url, json, authToken);
                
                String packageId = extractString(response, "id");
                
                String uploadUrl = "http://localhost:8080/api/packages/" + packageId + "/upload";
                String uploadResponse = uploadFile(uploadUrl, modpackFile);
                
                callback.onSuccess(packageId, extractString(uploadResponse, "url"));
            } catch (Exception e) {
                logger.error("Upload failed", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void uploadMod(File modFile, ModInfo info, UploadCallback callback) {
        uploadExecutor.submit(() -> {
            try {
                String url = "http://localhost:8080/api/packages";
                String json = String.format(
                    "{\"name\":\"%s\",\"description\":\"%s\",\"type\":\"MOD\",\"version\":\"%s\"}",
                    info.name, info.description, info.version);
                
                String response = sendPost(url, json, authToken);
                String packageId = extractString(response, "id");
                
                String uploadUrl = "http://localhost:8080/api/packages/" + packageId + "/upload";
                uploadFile(uploadUrl, modFile);
                
                callback.onSuccess(packageId, uploadUrl);
            } catch (Exception e) {
                logger.error("Mod upload failed", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void downloadPackage(String packageId, File destDir, DownloadCallback callback) {
        uploadExecutor.submit(() -> {
            try {
                String url = "http://localhost:8080/api/packages/" + packageId;
                String response = sendGet(url, authToken);
                
                String fileUrl = extractString(response, "file_url");
                if (fileUrl.isEmpty()) {
                    callback.onError("File URL not found");
                    return;
                }
                
                downloadFile(fileUrl, destDir);
                callback.onSuccess(destDir);
            } catch (Exception e) {
                logger.error("Download failed", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public String generateShareCode(String packageId, ShareCodeType type) {
        String code = generateRandomCode(8);
        ShareCode shareCode = new ShareCode();
        shareCode.packageId = packageId;
        shareCode.type = type;
        shareCode.createdAt = System.currentTimeMillis();
        shareCode.expiresAt = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000);
        
        activeShareCodes.put(code, shareCode);
        saveShareCodes();
        
        logger.info("Generated share code: {} for package: {}", code, packageId);
        return code;
    }

    public ShareResult importFromShareCode(String code, ImportCallback callback) {
        ShareCode shareCode = activeShareCodes.get(code);
        
        if (shareCode == null) {
            return new ShareResult(false, "Invalid or expired share code");
        }
        
        if (System.currentTimeMillis() > shareCode.expiresAt) {
            activeShareCodes.remove(code);
            return new ShareResult(false, "Share code expired");
        }

        final ShareResult[] result = new ShareResult[1];
        downloadPackage(shareCode.packageId, Paths.get(System.getProperty("user.home"), ".aurora", "imports").toFile(),
            new DownloadCallback() {
                public void onSuccess(File dir) {
                    result[0] = new ShareResult(true, "Import successful", dir.getAbsolutePath());
                }
                public void onError(String error) {
                    result[0] = new ShareResult(false, error);
                }
            });
        
        return result[0];
    }

    public void uploadSave(File saveFile, String saveName, UploadCallback callback) {
        uploadExecutor.submit(() -> {
            try {
                Path tempZip = Files.createTempFile("save_" + System.currentTimeMillis(), ".zip");
                zipDirectory(saveFile.toPath(), tempZip);
                
                String url = "http://localhost:8080/api/packages";
                String json = String.format(
                    "{\"name\":\"%s\",\"type\":\"SAVE\"}", saveName);
                
                String response = sendPost(url, json, authToken);
                String packageId = extractString(response, "id");
                
                String uploadUrl = "http://localhost:8080/api/packages/" + packageId + "/upload";
                uploadFile(uploadUrl, tempZip.toFile());
                
                Files.deleteIfExists(tempZip);
                callback.onSuccess(packageId, uploadUrl);
            } catch (Exception e) {
                logger.error("Save upload failed", e);
                callback.onError(e.getMessage());
            }
        });
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
                    logger.error("Failed to zip file: " + p, e);
                }
            });
        }
    }

    private String sendPost(String url, String json, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setDoOutput(true);
        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        return new String(conn.getInputStream().readAllBytes());
    }

    private String sendGet(String url, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return new String(conn.getInputStream().readAllBytes());
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

    private void downloadFile(String url, File destDir) throws IOException {
        java.net.URL fileUrl = new java.net.URL(url);
        try (InputStream is = fileUrl.openStream()) {
            String filename = fileUrl.getFile().substring(fileUrl.getFile().lastIndexOf('/') + 1);
            Files.copy(is, destDir.toPath().resolve(filename));
        }
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void saveShareCodes() {
        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream(SHARE_CODES_FILE))) {
            dos.writeInt(activeShareCodes.size());
            for (Map.Entry<String, ShareCode> entry : activeShareCodes.entrySet()) {
                dos.writeUTF(entry.getKey());
                dos.writeUTF(entry.getValue().packageId);
                dos.writeUTF(entry.getValue().type.name());
                dos.writeLong(entry.getValue().createdAt);
                dos.writeLong(entry.getValue().expiresAt);
            }
        } catch (IOException e) {
            logger.error("Failed to save share codes", e);
        }
    }

    private void loadShareCodes() {
        File file = new File(SHARE_CODES_FILE);
        if (!file.exists()) return;
        
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                String key = dis.readUTF();
                ShareCode code = new ShareCode();
                code.packageId = dis.readUTF();
                code.type = ShareCodeType.valueOf(dis.readUTF());
                code.createdAt = dis.readLong();
                code.expiresAt = dis.readLong();
                if (System.currentTimeMillis() < code.expiresAt) {
                    activeShareCodes.put(key, code);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load share codes", e);
        }
    }

    private String extractString(String json, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    public static class ShareCode {
        public String packageId;
        public ShareCodeType type;
        public long createdAt;
        public long expiresAt;
    }

    public enum ShareCodeType { MODPACK, MOD, SAVE }

    public static class ModpackInfo {
        public String name;
        public String description;
        public String version;
        public String gameId;
    }

    public static class ModInfo {
        public String name;
        public String description;
        public String version;
    }

    public interface UploadCallback {
        void onSuccess(String packageId, String url);
        void onError(String error);
    }

    public interface DownloadCallback {
        void onSuccess(File directory);
        void onError(String error);
    }

    public interface ImportCallback {
        void onSuccess(String localPath);
        void onError(String error);
    }

    public static class ShareResult {
        public boolean success;
        public String message;
        public String localPath;
        
        public ShareResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public ShareResult(boolean success, String message, String localPath) {
            this.success = success;
            this.message = message;
            this.localPath = localPath;
        }
    }
}