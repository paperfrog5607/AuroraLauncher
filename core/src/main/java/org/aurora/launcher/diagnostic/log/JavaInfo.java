package org.aurora.launcher.diagnostic.log;

public class JavaInfo {
    private String version;
    private String vendor;
    private String home;
    private int majorVersion;

    public JavaInfo() {
    }

    public JavaInfo(String version, String vendor) {
        this.version = version;
        this.vendor = vendor;
        this.majorVersion = parseMajorVersion(version);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        this.majorVersion = parseMajorVersion(version);
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    private int parseMajorVersion(String version) {
        if (version == null || version.isEmpty()) return 8;
        
        String v = version;
        if (v.startsWith("1.")) {
            v = v.substring(2);
        }
        
        int dotIndex = v.indexOf(".");
        if (dotIndex > 0) {
            v = v.substring(0, dotIndex);
        }
        
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return 8;
        }
    }
}