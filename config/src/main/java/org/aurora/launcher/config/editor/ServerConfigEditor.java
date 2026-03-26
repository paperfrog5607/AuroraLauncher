package org.aurora.launcher.config.editor;

import org.aurora.launcher.config.parser.ConfigParseException;

import java.io.IOException;
import java.nio.file.Path;

public class ServerConfigEditor extends ConfigEditor {
    
    public ServerConfigEditor(Path instancePath) throws IOException, ConfigParseException {
        this.configPath = instancePath.resolve("server.properties");
        this.parser = org.aurora.launcher.config.parser.ConfigParserFactory.getParser("properties");
        this.entries = new java.util.LinkedHashMap<>();
        this.modified = false;
        
        if (java.nio.file.Files.exists(configPath)) {
            reload();
        }
    }
    
    public void setServerPort(int port) {
        set("server-port", port);
    }
    
    public int getServerPort() {
        return get("server-port", Integer.class, 25565);
    }
    
    public void setMaxPlayers(int maxPlayers) {
        set("max-players", maxPlayers);
    }
    
    public int getMaxPlayers() {
        return get("max-players", Integer.class, 20);
    }
    
    public void setDifficulty(String difficulty) {
        set("difficulty", difficulty);
    }
    
    public String getDifficulty() {
        return get("difficulty", String.class, "normal");
    }
    
    public void setMotd(String motd) {
        set("motd", motd);
    }
    
    public String getMotd() {
        return get("motd", String.class, "A Minecraft Server");
    }
    
    public void setOnlineMode(boolean onlineMode) {
        set("online-mode", onlineMode);
    }
    
    public boolean isOnlineMode() {
        return get("online-mode", Boolean.class, true);
    }
    
    public void setViewDistance(int distance) {
        set("view-distance", distance);
    }
    
    public int getViewDistance() {
        return get("view-distance", Integer.class, 10);
    }
    
    public void setSpawnProtection(int range) {
        set("spawn-protection", range);
    }
    
    public int getSpawnProtection() {
        return get("spawn-protection", Integer.class, 16);
    }
    
    public void setAllowNether(boolean allow) {
        set("allow-nether", allow);
    }
    
    public boolean isAllowNether() {
        return get("allow-nether", Boolean.class, true);
    }
    
    public void setEnableCommandBlock(boolean enable) {
        set("enable-command-block", enable);
    }
    
    public boolean isEnableCommandBlock() {
        return get("enable-command-block", Boolean.class, false);
    }
    
    public void setBukkitSetting(String path, Object value) {
        set("bukkit." + path, value);
    }
    
    public Object getBukkitSetting(String path) {
        return get("bukkit." + path);
    }
    
    public void setPaperSetting(String path, Object value) {
        set("paper." + path, value);
    }
    
    public Object getPaperSetting(String path) {
        return get("paper." + path);
    }
    
    public void setForgeConfig(String modId, String path, Object value) {
        set("forge." + modId + "." + path, value);
    }
    
    public Object getForgeConfig(String modId, String path) {
        return get("forge." + modId + "." + path);
    }
    
    public void setFabricConfig(String modId, String path, Object value) {
        set("fabric." + modId + "." + path, value);
    }
    
    public Object getFabricConfig(String modId, String path) {
        return get("fabric." + modId + "." + path);
    }
}