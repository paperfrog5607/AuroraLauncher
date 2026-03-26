package org.aurora.launcher.config;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApiConfig {

    private static final String CONFIG_FILE = "api_config.json";
    private static ApiConfig instance;

    @SerializedName("curseforge_api_key")
    private String curseForgeApiKey;

    @SerializedName("modrinth_token")
    private String modrinthToken;

    @SerializedName("bmclapi_key")
    private String bmclapiKey;

    public ApiConfig() {
    }

    public static ApiConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public static ApiConfig load() {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                Gson gson = new Gson();
                instance = gson.fromJson(reader, ApiConfig.class);
                if (instance == null) {
                    instance = new ApiConfig();
                }
            } catch (Exception e) {
                System.err.println("Failed to load API config: " + e.getMessage());
                instance = new ApiConfig();
            }
        } else {
            instance = new ApiConfig();
        }
        return instance;
    }

    public void save() {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                Gson gson = new Gson();
                gson.toJson(this, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to save API config: " + e.getMessage());
        }
    }

    private static Path getConfigPath() {
        String userHome = System.getProperty("user.home");
        String osName = System.getProperty("os.name").toLowerCase();

        Path baseDir;
        if (osName.contains("win")) {
            String appData = System.getenv("APPDATA");
            baseDir = (appData != null) ? Paths.get(appData) : Paths.get(userHome);
        } else if (osName.contains("mac")) {
            baseDir = Paths.get(userHome, "Library", "Application Support");
        } else {
            baseDir = Paths.get(userHome, ".config");
        }

        return baseDir.resolve("AuroraLauncher").resolve(CONFIG_FILE);
    }

    public String getCurseForgeApiKey() {
        return curseForgeApiKey;
    }

    public void setCurseForgeApiKey(String curseForgeApiKey) {
        this.curseForgeApiKey = curseForgeApiKey;
    }

    public String getModrinthToken() {
        return modrinthToken;
    }

    public void setModrinthToken(String modrinthToken) {
        this.modrinthToken = modrinthToken;
    }

    public String getBmclapiKey() {
        return bmclapiKey;
    }

    public void setBmclapiKey(String bmclapiKey) {
        this.bmclapiKey = bmclapiKey;
    }
}