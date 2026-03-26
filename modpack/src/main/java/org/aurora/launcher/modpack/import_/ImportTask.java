package org.aurora.launcher.modpack.import_;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImportTask {
    
    public enum TaskState {
        PENDING, PARSING, DOWNLOADING, EXTRACTING, COMPLETED, FAILED, CANCELLED
    }
    
    private final String taskId;
    private final Path sourceFile;
    private final Path targetDir;
    private volatile TaskState state;
    private volatile double progress;
    private volatile String currentStep;
    private volatile String errorMessage;
    private volatile int totalMods;
    private volatile int downloadedMods;
    private volatile long totalBytes;
    private volatile long downloadedBytes;
    private final List<String> failedMods;
    private InstanceInfo instanceInfo;
    
    public ImportTask(Path sourceFile, Path targetDir) {
        this.taskId = UUID.randomUUID().toString();
        this.sourceFile = sourceFile;
        this.targetDir = targetDir;
        this.state = TaskState.PENDING;
        this.progress = 0.0;
        this.failedMods = new ArrayList<>();
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public Path getSourceFile() {
        return sourceFile;
    }
    
    public Path getTargetDir() {
        return targetDir;
    }
    
    public TaskState getState() {
        return state;
    }
    
    public void setState(TaskState state) {
        this.state = state;
    }
    
    public double getProgress() {
        return progress;
    }
    
    public void setProgress(double progress) {
        this.progress = progress;
    }
    
    public void updateProgress() {
        if (totalMods > 0) {
            this.progress = (double) downloadedMods / totalMods;
        }
    }
    
    public String getCurrentStep() {
        return currentStep;
    }
    
    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public int getTotalMods() {
        return totalMods;
    }
    
    public void setTotalMods(int totalMods) {
        this.totalMods = totalMods;
    }
    
    public int getDownloadedMods() {
        return downloadedMods;
    }
    
    public void setDownloadedMods(int downloadedMods) {
        this.downloadedMods = downloadedMods;
        updateProgress();
    }
    
    public void incrementDownloadedMods() {
        this.downloadedMods++;
        updateProgress();
    }
    
    public long getTotalBytes() {
        return totalBytes;
    }
    
    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }
    
    public long getDownloadedBytes() {
        return downloadedBytes;
    }
    
    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }
    
    public List<String> getFailedMods() {
        return failedMods;
    }
    
    public void addFailedMod(String modName) {
        failedMods.add(modName);
    }
    
    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }
    
    public void setInstanceInfo(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }
    
    public boolean isRunning() {
        return state == TaskState.PARSING || state == TaskState.DOWNLOADING || state == TaskState.EXTRACTING;
    }
    
    public boolean isCompleted() {
        return state == TaskState.COMPLETED;
    }
    
    public boolean isFailed() {
        return state == TaskState.FAILED;
    }
    
    public boolean isCancelled() {
        return state == TaskState.CANCELLED;
    }
    
    public void cancel() {
        if (state == TaskState.PENDING || isRunning()) {
            state = TaskState.CANCELLED;
        }
    }
    
    public static class InstanceInfo {
        private String name;
        private String version;
        private String author;
        private String minecraftVersion;
        private String loaderType;
        private String loaderVersion;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getMinecraftVersion() { return minecraftVersion; }
        public void setMinecraftVersion(String minecraftVersion) { this.minecraftVersion = minecraftVersion; }
        public String getLoaderType() { return loaderType; }
        public void setLoaderType(String loaderType) { this.loaderType = loaderType; }
        public String getLoaderVersion() { return loaderVersion; }
        public void setLoaderVersion(String loaderVersion) { this.loaderVersion = loaderVersion; }
    }
}