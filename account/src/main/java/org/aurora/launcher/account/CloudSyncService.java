package org.aurora.launcher.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;

public class CloudSyncService {

    private static final Logger logger = LoggerFactory.getLogger(CloudSyncService.class);
    private static CloudSyncService instance;

    private final ScheduledExecutorService scheduler;
    private final Path localConfigDir;
    private final String authToken;
    private volatile boolean syncEnabled;

    private CloudSyncService(String authToken) {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.localConfigDir = Paths.get(System.getProperty("user.home"), ".aurora", "config");
        this.authToken = authToken;
        this.syncEnabled = false;
    }

    public static synchronized CloudSyncService getInstance(String authToken) {
        if (instance == null) {
            instance = new CloudSyncService(authToken);
        }
        return instance;
    }

    public void startAutoSync(long intervalMinutes) {
        syncEnabled = true;
        scheduler.scheduleAtFixedRate(() -> {
            if (syncEnabled) {
                syncToCloud();
            }
        }, intervalMinutes, intervalMinutes, TimeUnit.MINUTES);
        logger.info("Auto sync started with interval: {} minutes", intervalMinutes);
    }

    public void stopAutoSync() {
        syncEnabled = false;
        logger.info("Auto sync stopped");
    }

    public SyncResult syncToCloud() {
        SyncResult result = new SyncResult();
        try {
            ConfigData localConfig = loadLocalConfig();
            if (localConfig == null) {
                result.success = false;
                result.message = "No local config to sync";
                return result;
            }

            String url = "http://localhost:8080/api/sync/config";
            String response = sendPut(url, localConfig.toJson(), authToken);
            
            result.success = true;
            result.message = "Sync completed";
            result.timestamp = System.currentTimeMillis();
            logger.info("Config synced to cloud");
        } catch (Exception e) {
            result.success = false;
            result.message = "Sync failed: " + e.getMessage();
            logger.error("Cloud sync failed", e);
        }
        return result;
    }

    public SyncResult syncFromCloud() {
        SyncResult result = new SyncResult();
        try {
            String url = "http://localhost:8080/api/sync/config";
            String response = sendGet(url, authToken);
            
            ConfigData cloudConfig = ConfigData.fromJson(response);
            saveLocalConfig(cloudConfig);
            
            result.success = true;
            result.message = "Download completed";
            result.timestamp = System.currentTimeMillis();
            logger.info("Config synced from cloud");
        } catch (Exception e) {
            result.success = false;
            result.message = "Download failed: " + e.getMessage();
            logger.error("Cloud download failed", e);
        }
        return result;
    }

    public SyncResult forceSync() {
        SyncResult upload = syncToCloud();
        if (!upload.success) {
            return upload;
        }
        return syncFromCloud();
    }

    private ConfigData loadLocalConfig() {
        Path configFile = localConfigDir.resolve("user_config.json");
        if (!Files.exists(configFile)) {
            return null;
        }
        try {
            String content = Files.readString(configFile);
            return ConfigData.fromJson(content);
        } catch (IOException e) {
            logger.error("Failed to load local config", e);
            return null;
        }
    }

    private void saveLocalConfig(ConfigData config) {
        try {
            Files.createDirectories(localConfigDir);
            Files.writeString(localConfigDir.resolve("user_config.json"), config.toJson());
        } catch (IOException e) {
            logger.error("Failed to save local config", e);
        }
    }

    private String sendPut(String url, String json, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("PUT");
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

    public static class ConfigData implements Serializable {
        public String userId;
        public String username;
        public GameSettings gameSettings;
        public UIPreferences uiPreferences;

        public String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"userId\":\"").append(userId).append("\",");
            sb.append("\"username\":\"").append(username).append("\",");
            sb.append("\"gameSettings\":").append(gameSettings.toJson()).append(",");
            sb.append("\"uiPreferences\":").append(uiPreferences.toJson());
            sb.append("}");
            return sb.toString();
        }

        public static ConfigData fromJson(String json) {
            ConfigData data = new ConfigData();
            data.userId = extractString(json, "userId");
            data.username = extractString(json, "username");
            data.gameSettings = GameSettings.fromJson(extractObject(json, "gameSettings"));
            data.uiPreferences = UIPreferences.fromJson(extractObject(json, "uiPreferences"));
            return data;
        }

        private static String extractString(String json, String key) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher m = p.matcher(json);
            return m.find() ? m.group(1) : "";
        }

        private static String extractObject(String json, String key) {
            int start = json.indexOf("\"" + key + "\"");
            if (start == -1) return "{}";
            int colon = json.indexOf(":", start);
            int braceStart = json.indexOf("{", colon);
            int braceEnd = json.lastIndexOf("}");
            return json.substring(braceStart, braceEnd + 1);
        }
    }

    public static class GameSettings implements Serializable {
        public String defaultGamePath;
        public int defaultMemoryMB;
        public boolean vsyncEnabled;
        public String language;

        public String toJson() {
            return String.format("{\"defaultGamePath\":\"%s\",\"defaultMemoryMB\":%d,\"vsyncEnabled\":%b,\"language\":\"%s\"}",
                defaultGamePath, defaultMemoryMB, vsyncEnabled, language);
        }

        public static GameSettings fromJson(String json) {
            GameSettings s = new GameSettings();
            s.defaultGamePath = extractString(json, "defaultGamePath");
            s.defaultMemoryMB = extractInt(json, "defaultMemoryMB");
            s.vsyncEnabled = json.contains("\"vsyncEnabled\":true");
            s.language = extractString(json, "language");
            return s;
        }

        private static String extractString(String json, String key) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher m = p.matcher(json);
            return m.find() ? m.group(1) : "";
        }

        private static int extractInt(String json, String key) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
            java.util.regex.Matcher m = p.matcher(json);
            return m.find() ? Integer.parseInt(m.group(1)) : 0;
        }
    }

    public static class UIPreferences implements Serializable {
        public String theme;
        public int fontSize;
        public boolean showNotifications;

        public String toJson() {
            return String.format("{\"theme\":\"%s\",\"fontSize\":%d,\"showNotifications\":%b}",
                theme, fontSize, showNotifications);
        }

        public static UIPreferences fromJson(String json) {
            UIPreferences p = new UIPreferences();
            p.theme = extractString(json, "theme");
            p.fontSize = extractInt(json, "fontSize");
            p.showNotifications = json.contains("\"showNotifications\":true");
            return p;
        }

        private static String extractString(String json, String key) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher m = p.matcher(json);
            return m.find() ? m.group(1) : "";
        }

        private static int extractInt(String json, String key) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
            java.util.regex.Matcher m = p.matcher(json);
            return m.find() ? Integer.parseInt(m.group(1)) : 0;
        }
    }

    public static class SyncResult {
        public boolean success;
        public String message;
        public long timestamp;
    }
}