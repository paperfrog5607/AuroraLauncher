package org.aurora.launcher.launcher.version;

public enum VersionType {
    RELEASE,
    SNAPSHOT,
    OLD_ALPHA,
    OLD_BETA;

    public static VersionType fromString(String value) {
        if (value == null) return RELEASE;
        switch (value.toLowerCase()) {
            case "release":
                return RELEASE;
            case "snapshot":
                return SNAPSHOT;
            case "old_alpha":
                return OLD_ALPHA;
            case "old_beta":
                return OLD_BETA;
            default:
                return RELEASE;
        }
    }
}