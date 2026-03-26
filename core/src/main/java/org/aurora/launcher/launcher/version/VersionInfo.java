package org.aurora.launcher.launcher.version;

import java.time.Instant;
import java.util.List;

public class VersionInfo {
    private String id;
    private VersionType type;
    private String url;
    private String assetIndexUrl;
    private String clientDownloadUrl;
    private String clientVersionHash;
    private JavaVersionInfo javaVersion;
    private String mainClass;
    private List<Library> libraries;
    private AssetIndex assetIndex;
    private Instant releaseTime;
    private Instant complianceLevel;

    public VersionInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VersionType getType() {
        return type;
    }

    public void setType(VersionType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAssetIndexUrl() {
        return assetIndexUrl;
    }

    public void setAssetIndexUrl(String assetIndexUrl) {
        this.assetIndexUrl = assetIndexUrl;
    }

    public String getClientDownloadUrl() {
        return clientDownloadUrl;
    }

    public void setClientDownloadUrl(String clientDownloadUrl) {
        this.clientDownloadUrl = clientDownloadUrl;
    }

    public String getClientVersionHash() {
        return clientVersionHash;
    }

    public void setClientVersionHash(String clientVersionHash) {
        this.clientVersionHash = clientVersionHash;
    }

    public JavaVersionInfo getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(JavaVersionInfo javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public List<Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<Library> libraries) {
        this.libraries = libraries;
    }

    public AssetIndex getAssetIndex() {
        return assetIndex;
    }

    public void setAssetIndex(AssetIndex assetIndex) {
        this.assetIndex = assetIndex;
    }

    public Instant getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Instant releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Instant getComplianceLevel() {
        return complianceLevel;
    }

    public void setComplianceLevel(Instant complianceLevel) {
        this.complianceLevel = complianceLevel;
    }
}