package org.aurora.launcher.core.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.aurora.launcher.core.io.FileUtils;
import org.aurora.launcher.core.io.JsonUtils;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {
    private final Path configPath;
    private JsonObject config;

    public ConfigManager(Path configPath) {
        this.configPath = configPath;
        this.config = new JsonObject();
    }

    public void load() throws IOException {
        if (configPath.toFile().exists()) {
            String content = FileUtils.readAllText(configPath);
            config = JsonParser.parseString(content).getAsJsonObject();
        } else {
            config = createDefaultConfig();
            save();
        }
    }

    public void save() throws IOException {
        FileUtils.writeAllText(configPath, JsonUtils.toJsonPretty(config));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        String[] parts = key.split("\\.");
        JsonObject current = config;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i])) {
                return null;
            }
            current = current.getAsJsonObject(parts[i]);
        }
        
        String lastKey = parts[parts.length - 1];
        if (!current.has(lastKey)) {
            return null;
        }
        
        if (type == String.class) {
            return (T) current.get(lastKey).getAsString();
        } else if (type == Integer.class) {
            return (T) Integer.valueOf(current.get(lastKey).getAsInt());
        } else if (type == Boolean.class) {
            return (T) Boolean.valueOf(current.get(lastKey).getAsBoolean());
        } else if (type == Long.class) {
            return (T) Long.valueOf(current.get(lastKey).getAsLong());
        } else if (type == Double.class) {
            return (T) Double.valueOf(current.get(lastKey).getAsDouble());
        }
        
        return null;
    }

    public void set(String key, Object value) {
        String[] parts = key.split("\\.");
        JsonObject current = config;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i])) {
                current.add(parts[i], new JsonObject());
            }
            current = current.getAsJsonObject(parts[i]);
        }
        
        String lastKey = parts[parts.length - 1];
        if (value instanceof String) {
            current.addProperty(lastKey, (String) value);
        } else if (value instanceof Integer) {
            current.addProperty(lastKey, (Integer) value);
        } else if (value instanceof Boolean) {
            current.addProperty(lastKey, (Boolean) value);
        } else if (value instanceof Long) {
            current.addProperty(lastKey, (Long) value);
        } else if (value instanceof Double) {
            current.addProperty(lastKey, (Double) value);
        } else {
            current.addProperty(lastKey, String.valueOf(value));
        }
    }

    public boolean has(String key) {
        String[] parts = key.split("\\.");
        JsonObject current = config;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i])) {
                return false;
            }
            current = current.getAsJsonObject(parts[i]);
        }
        
        return current.has(parts[parts.length - 1]);
    }

    public void remove(String key) {
        String[] parts = key.split("\\.");
        JsonObject current = config;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i])) {
                return;
            }
            current = current.getAsJsonObject(parts[i]);
        }
        
        current.remove(parts[parts.length - 1]);
    }

    private JsonObject createDefaultConfig() {
        JsonObject defaultConfig = new JsonObject();
        defaultConfig.addProperty("version", "1.0.0");
        defaultConfig.addProperty("language", "zh_CN");
        defaultConfig.addProperty("theme", "dark");
        
        JsonObject javaConfig = new JsonObject();
        javaConfig.addProperty("autoDetect", true);
        javaConfig.addProperty("customPath", (String) null);
        javaConfig.addProperty("defaultMemory", 4096);
        defaultConfig.add("java", javaConfig);
        
        JsonObject launcherConfig = new JsonObject();
        launcherConfig.addProperty("checkUpdates", true);
        launcherConfig.addProperty("autoStart", false);
        launcherConfig.addProperty("closeAfterLaunch", false);
        defaultConfig.add("launcher", launcherConfig);
        
        JsonObject downloadConfig = new JsonObject();
        downloadConfig.addProperty("concurrent", 4);
        downloadConfig.addProperty("timeout", 30000);
        downloadConfig.addProperty("retryCount", 3);
        defaultConfig.add("download", downloadConfig);
        
        JsonObject uiConfig = new JsonObject();
        uiConfig.addProperty("windowWidth", 1200);
        uiConfig.addProperty("windowHeight", 800);
        uiConfig.addProperty("rememberPosition", true);
        defaultConfig.add("ui", uiConfig);
        
        return defaultConfig;
    }
}