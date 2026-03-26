package org.aurora.launcher.launcher.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.aurora.launcher.launcher.GameDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LauncherConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(LauncherConfig.class);
    private static LauncherConfig instance;
    
    private static final String CONFIG_FILE = "launcher_config.json";
    
    private String javaPath = "java";
    private int javaMemory = 2048;
    private String gameResolution = "1280x720";
    private boolean fullscreen = false;
    private boolean showLogs = true;
    
    private DownloadSource downloadSource = DownloadSource.OFFICIAL;
    private String customDownloadUrl = "";
    
    private String selectedVersion = "";
    private String selectedAccount = "";
    
    public enum DownloadSource {
        OFFICIAL("官方源", "https://launchermeta.mojang.com"),
        BMCLAPI("BMCLAPI", "https://bmclapi2.bangbang93.com"),
        MCBBS("MCBBS", "https://download.mcbbs.net"),
        CUSTOM("自定义", "");
        
        private final String name;
        private final String baseUrl;
        
        DownloadSource(String name, String baseUrl) {
            this.name = name;
            this.baseUrl = baseUrl;
        }
        
        public String getName() { return name; }
        public String getBaseUrl() { return baseUrl; }
    }
    
    public static synchronized LauncherConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }
    
    public static LauncherConfig load() {
        Path configPath = getConfigPath();
        
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                Gson gson = new Gson();
                instance = gson.fromJson(reader, LauncherConfig.class);
                if (instance == null) {
                    instance = new LauncherConfig();
                }
                logger.info("Loaded config from {}", configPath);
            } catch (Exception e) {
                logger.error("Failed to load config", e);
                instance = new LauncherConfig();
            }
        } else {
            instance = new LauncherConfig();
            logger.info("Using default config");
        }
        
        return instance;
    }
    
    public void save() {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(this, writer);
                logger.info("Saved config to {}", configPath);
            }
        } catch (Exception e) {
            logger.error("Failed to save config", e);
        }
    }
    
    private static Path getConfigPath() {
        return GameDirectory.getInstance().getConfigsDir().resolve(CONFIG_FILE);
    }
    
    public String getJavaPath() {
        return javaPath;
    }
    
    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }
    
    public int getJavaMemory() {
        return javaMemory;
    }
    
    public void setJavaMemory(int javaMemory) {
        this.javaMemory = javaMemory;
    }
    
    public String getGameResolution() {
        return gameResolution;
    }
    
    public void setGameResolution(String gameResolution) {
        this.gameResolution = gameResolution;
    }
    
    public boolean isFullscreen() {
        return fullscreen;
    }
    
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }
    
    public boolean isShowLogs() {
        return showLogs;
    }
    
    public void setShowLogs(boolean showLogs) {
        this.showLogs = showLogs;
    }
    
    public DownloadSource getDownloadSource() {
        return downloadSource;
    }
    
    public void setDownloadSource(DownloadSource downloadSource) {
        this.downloadSource = downloadSource;
    }
    
    public String getCustomDownloadUrl() {
        return customDownloadUrl;
    }
    
    public void setCustomDownloadUrl(String customDownloadUrl) {
        this.customDownloadUrl = customDownloadUrl;
    }
    
    public String getDownloadBaseUrl() {
        if (downloadSource == DownloadSource.CUSTOM && !customDownloadUrl.isEmpty()) {
            return customDownloadUrl;
        }
        return downloadSource.getBaseUrl();
    }
    
    public String getSelectedVersion() {
        return selectedVersion;
    }
    
    public void setSelectedVersion(String selectedVersion) {
        this.selectedVersion = selectedVersion;
    }
    
    public String getSelectedAccount() {
        return selectedAccount;
    }
    
    public void setSelectedAccount(String selectedAccount) {
        this.selectedAccount = selectedAccount;
    }
}
