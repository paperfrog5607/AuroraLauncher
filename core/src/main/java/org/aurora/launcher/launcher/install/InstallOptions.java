package org.aurora.launcher.launcher.install;

import org.aurora.launcher.launcher.version.VersionInfo;

public class InstallOptions {
    private boolean includeAssets = true;
    private boolean includeNatives = true;
    private boolean verifyDownloads = true;
    private int maxConcurrentDownloads = 4;
    private java.nio.file.Path customTargetPath;

    public InstallOptions() {
    }

    public boolean isIncludeAssets() {
        return includeAssets;
    }

    public void setIncludeAssets(boolean includeAssets) {
        this.includeAssets = includeAssets;
    }

    public boolean isIncludeNatives() {
        return includeNatives;
    }

    public void setIncludeNatives(boolean includeNatives) {
        this.includeNatives = includeNatives;
    }

    public boolean isVerifyDownloads() {
        return verifyDownloads;
    }

    public void setVerifyDownloads(boolean verifyDownloads) {
        this.verifyDownloads = verifyDownloads;
    }

    public int getMaxConcurrentDownloads() {
        return maxConcurrentDownloads;
    }

    public void setMaxConcurrentDownloads(int maxConcurrentDownloads) {
        this.maxConcurrentDownloads = maxConcurrentDownloads;
    }

    public java.nio.file.Path getCustomTargetPath() {
        return customTargetPath;
    }

    public void setCustomTargetPath(java.nio.file.Path customTargetPath) {
        this.customTargetPath = customTargetPath;
    }

    public static InstallOptions defaults() {
        return new InstallOptions();
    }

    public static InstallOptions minimal() {
        InstallOptions options = new InstallOptions();
        options.setIncludeAssets(false);
        options.setIncludeNatives(false);
        return options;
    }
}