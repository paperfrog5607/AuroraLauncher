package org.aurora.launcher.diagnostic.report;

public class MinecraftInfo {
    private String version;
    private String modLoader;
    private String modLoaderVersion;
    private String gameDir;

    public MinecraftInfo() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModLoader() {
        return modLoader;
    }

    public void setModLoader(String modLoader) {
        this.modLoader = modLoader;
    }

    public String getModLoaderVersion() {
        return modLoaderVersion;
    }

    public void setModLoaderVersion(String modLoaderVersion) {
        this.modLoaderVersion = modLoaderVersion;
    }

    public String getGameDir() {
        return gameDir;
    }

    public void setGameDir(String gameDir) {
        this.gameDir = gameDir;
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Minecraft ").append(version != null ? version : "Unknown");
        if (modLoader != null) {
            sb.append(" with ").append(modLoader);
            if (modLoaderVersion != null) {
                sb.append(" ").append(modLoaderVersion);
            }
        }
        return sb.toString();
    }
}