package org.aurora.launcher.launcher.version;

import java.util.ArrayList;
import java.util.List;

public class VersionManifest {
    private List<VersionInfo> versions;
    private VersionInfo latestRelease;
    private VersionInfo latestSnapshot;

    public VersionManifest() {
        this.versions = new ArrayList<>();
    }

    public List<VersionInfo> getVersions() {
        return versions;
    }

    public void setVersions(List<VersionInfo> versions) {
        this.versions = versions != null ? versions : new ArrayList<>();
    }

    public VersionInfo getLatestRelease() {
        return latestRelease;
    }

    public void setLatestRelease(VersionInfo latestRelease) {
        this.latestRelease = latestRelease;
    }

    public VersionInfo getLatestSnapshot() {
        return latestSnapshot;
    }

    public void setLatestSnapshot(VersionInfo latestSnapshot) {
        this.latestSnapshot = latestSnapshot;
    }

    public VersionInfo getVersionById(String id) {
        if (versions == null || id == null) return null;
        for (VersionInfo info : versions) {
            if (id.equals(info.getId())) {
                return info;
            }
        }
        return null;
    }

    public List<VersionInfo> getVersionsByType(VersionType type) {
        List<VersionInfo> result = new ArrayList<>();
        if (versions == null) return result;
        for (VersionInfo info : versions) {
            if (info.getType() == type) {
                result.add(info);
            }
        }
        return result;
    }
}