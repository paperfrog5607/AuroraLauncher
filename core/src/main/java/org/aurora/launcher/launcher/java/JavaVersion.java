package org.aurora.launcher.launcher.java;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Objects;

public class JavaVersion {
    private String path;
    private String version;
    private int majorVersion;
    private String vendor;
    private String architecture;

    public JavaVersion() {
    }

    public JavaVersion(String path, int majorVersion) {
        this.path = path;
        this.majorVersion = majorVersion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public Path getJavaExecutable() {
        if (path == null) return null;
        Path javaHome = Paths.get(path);
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return javaHome.resolve("bin").resolve("javaw.exe");
        } else {
            return javaHome.resolve("bin").resolve("java");
        }
    }

    public boolean meets(int requiredVersion) {
        return majorVersion >= requiredVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaVersion that = (JavaVersion) o;
        return majorVersion == that.majorVersion &&
               Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, majorVersion);
    }
}