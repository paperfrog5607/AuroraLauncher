package org.aurora.launcher.diagnostic.report;

public class SystemInfo {
    private String osName;
    private String osVersion;
    private String osArch;
    private int cpuCores;
    private long totalMemory;
    private String gpuName;
    private String gpuDriver;

    public SystemInfo() {
        loadSystemInfo();
    }

    private void loadSystemInfo() {
        this.osName = System.getProperty("os.name", "Unknown");
        this.osVersion = System.getProperty("os.version", "Unknown");
        this.osArch = System.getProperty("os.arch", "Unknown");
        this.cpuCores = Runtime.getRuntime().availableProcessors();
        this.totalMemory = Runtime.getRuntime().maxMemory();
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getTotalMemoryMB() {
        return totalMemory / (1024 * 1024);
    }

    public String getGpuName() {
        return gpuName;
    }

    public void setGpuName(String gpuName) {
        this.gpuName = gpuName;
    }

    public String getGpuDriver() {
        return gpuDriver;
    }

    public void setGpuDriver(String gpuDriver) {
        this.gpuDriver = gpuDriver;
    }

    public String getSummary() {
        return String.format("%s %s (%s), %d cores, %d MB RAM",
            osName, osVersion, osArch, cpuCores, getTotalMemoryMB());
    }
}