package org.aurora.launcher.launcher.profile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GameProfile {
    private String id;
    private String name;
    private String versionId;
    private Path gameDir;
    private String javaPath;
    private String memoryPreset;
    private long minMemory;
    private long maxMemory;
    private boolean autoMemory;
    private List<String> customJvmArgs;
    private List<String> customGameArgs;
    private String lastPlayed;
    private String icon;

    public GameProfile() {
        this.customJvmArgs = new ArrayList<>();
        this.customGameArgs = new ArrayList<>();
        this.autoMemory = true;
        this.memoryPreset = "auto";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Path getGameDir() {
        return gameDir;
    }

    public void setGameDir(Path gameDir) {
        this.gameDir = gameDir;
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getMemoryPreset() {
        return memoryPreset;
    }

    public void setMemoryPreset(String memoryPreset) {
        this.memoryPreset = memoryPreset;
    }

    public long getMinMemory() {
        return minMemory;
    }

    public void setMinMemory(long minMemory) {
        this.minMemory = minMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public boolean isAutoMemory() {
        return autoMemory;
    }

    public void setAutoMemory(boolean autoMemory) {
        this.autoMemory = autoMemory;
    }

    public List<String> getCustomJvmArgs() {
        return customJvmArgs;
    }

    public void setCustomJvmArgs(List<String> customJvmArgs) {
        this.customJvmArgs = customJvmArgs != null ? customJvmArgs : new ArrayList<>();
    }

    public List<String> getCustomGameArgs() {
        return customGameArgs;
    }

    public void setCustomGameArgs(List<String> customGameArgs) {
        this.customGameArgs = customGameArgs != null ? customGameArgs : new ArrayList<>();
    }

    public String getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(String lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void addJvmArg(String arg) {
        if (arg != null && !arg.isEmpty()) {
            customJvmArgs.add(arg);
        }
    }

    public void addGameArg(String arg) {
        if (arg != null && !arg.isEmpty()) {
            customGameArgs.add(arg);
        }
    }
}