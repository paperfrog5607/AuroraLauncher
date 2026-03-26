package org.aurora.launcher.launcher.version;

public class JavaVersionInfo {
    private String component;
    private int majorVersion;

    public JavaVersionInfo() {
    }

    public JavaVersionInfo(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public boolean meets(int requiredVersion) {
        return majorVersion >= requiredVersion;
    }
}