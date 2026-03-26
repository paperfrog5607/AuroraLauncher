package org.aurora.launcher.steam.model;

/**
 * Steam游戏模型
 */
public class SteamGame {
    
    private String appId;
    private String name;
    private String type;
    private String description;
    private String logoUrl;
    private String headerImage;
    private String backgroundImage;
    private String storeUrl;
    private String installPath;
    private boolean isInstalled;
    private long playTime;
    private long lastPlayed;

    public SteamGame() {
    }

    public SteamGame(String appId, String name) {
        this.appId = appId;
        this.name = name;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getAppIdAsInt() {
        try {
            return Integer.parseInt(appId);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getStoreUrl() {
        return storeUrl;
    }

    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }

    public String getInstallPath() {
        return installPath;
    }

    public void setInstallPath(String installPath) {
        this.installPath = installPath;
        this.isInstalled = installPath != null && !installPath.isEmpty();
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public String getPlayTimeFormatted() {
        if (playTime < 60) {
            return playTime + " 分钟";
        } else if (playTime < 1440) {
            return (playTime / 60) + " 小时";
        } else {
            return String.format("%.1f 小时", playTime / 1440f);
        }
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getLastPlayedFormatted() {
        if (lastPlayed <= 0) {
            return "从未游玩";
        }
        java.time.Instant instant = java.time.Instant.ofEpochSecond(lastPlayed);
        java.time.LocalDateTime dateTime = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }

    /**
     * 获取Steam商店URL
     */
    public String getStorePageUrl() {
        if (appId != null && !appId.isEmpty()) {
            return "https://store.steamcommunity.com/app/" + appId;
        }
        return storeUrl;
    }

    /**
     * 获取SteamDB URL
     */
    public String getSteamDbUrl() {
        if (appId != null && !appId.isEmpty()) {
            return "https://steamdb.info/app/" + appId;
        }
        return null;
    }

    @Override
    public String toString() {
        return "SteamGame{" +
                "appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", isInstalled=" + isInstalled +
                ", playTime=" + playTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SteamGame steamGame = (SteamGame) o;
        return appId != null && appId.equals(steamGame.appId);
    }

    @Override
    public int hashCode() {
        return appId != null ? appId.hashCode() : 0;
    }
}
