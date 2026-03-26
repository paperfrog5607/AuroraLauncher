package org.aurora.launcher.modpack.verify;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class VerifyReport {
    
    private String instanceId;
    private Instant checkTime;
    private List<FileIssue> missingFiles;
    private List<FileIssue> corruptedFiles;
    private List<DependencyIssue> missingDependencies;
    private List<DependencyIssue> conflictDependencies;
    private List<ConfigIssue> configIssues;
    private boolean passed;
    private int filesChecked;
    private int issuesFound;
    
    public VerifyReport() {
        this.missingFiles = new ArrayList<>();
        this.corruptedFiles = new ArrayList<>();
        this.missingDependencies = new ArrayList<>();
        this.conflictDependencies = new ArrayList<>();
        this.configIssues = new ArrayList<>();
        this.checkTime = Instant.now();
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public Instant getCheckTime() {
        return checkTime;
    }
    
    public void setCheckTime(Instant checkTime) {
        this.checkTime = checkTime;
    }
    
    public List<FileIssue> getMissingFiles() {
        return missingFiles;
    }
    
    public void setMissingFiles(List<FileIssue> missingFiles) {
        this.missingFiles = missingFiles != null ? missingFiles : new ArrayList<>();
    }
    
    public void addMissingFile(FileIssue issue) {
        missingFiles.add(issue);
    }
    
    public List<FileIssue> getCorruptedFiles() {
        return corruptedFiles;
    }
    
    public void setCorruptedFiles(List<FileIssue> corruptedFiles) {
        this.corruptedFiles = corruptedFiles != null ? corruptedFiles : new ArrayList<>();
    }
    
    public void addCorruptedFile(FileIssue issue) {
        corruptedFiles.add(issue);
    }
    
    public List<DependencyIssue> getMissingDependencies() {
        return missingDependencies;
    }
    
    public void setMissingDependencies(List<DependencyIssue> missingDependencies) {
        this.missingDependencies = missingDependencies != null ? missingDependencies : new ArrayList<>();
    }
    
    public void addMissingDependency(DependencyIssue issue) {
        missingDependencies.add(issue);
    }
    
    public List<DependencyIssue> getConflictDependencies() {
        return conflictDependencies;
    }
    
    public void setConflictDependencies(List<DependencyIssue> conflictDependencies) {
        this.conflictDependencies = conflictDependencies != null ? conflictDependencies : new ArrayList<>();
    }
    
    public void addConflictDependency(DependencyIssue issue) {
        conflictDependencies.add(issue);
    }
    
    public List<ConfigIssue> getConfigIssues() {
        return configIssues;
    }
    
    public void setConfigIssues(List<ConfigIssue> configIssues) {
        this.configIssues = configIssues != null ? configIssues : new ArrayList<>();
    }
    
    public void addConfigIssue(ConfigIssue issue) {
        configIssues.add(issue);
    }
    
    public boolean isPassed() {
        return passed;
    }
    
    public void setPassed(boolean passed) {
        this.passed = passed;
    }
    
    public int getFilesChecked() {
        return filesChecked;
    }
    
    public void setFilesChecked(int filesChecked) {
        this.filesChecked = filesChecked;
    }
    
    public int getIssuesFound() {
        return issuesFound;
    }
    
    public void setIssuesFound(int issuesFound) {
        this.issuesFound = issuesFound;
    }
    
    public void calculateTotals() {
        issuesFound = missingFiles.size() + corruptedFiles.size() + 
                missingDependencies.size() + conflictDependencies.size() + configIssues.size();
        passed = issuesFound == 0;
    }
    
    public int getTotalIssues() {
        return missingFiles.size() + corruptedFiles.size() + 
                missingDependencies.size() + conflictDependencies.size() + configIssues.size();
    }
    
    public static class FileIssue {
        private Path path;
        private IssueType type;
        private String expectedHash;
        private String actualHash;
        private String message;
        
        public FileIssue() {
        }
        
        public FileIssue(Path path, IssueType type) {
            this.path = path;
            this.type = type;
        }
        
        public Path getPath() { return path; }
        public void setPath(Path path) { this.path = path; }
        public IssueType getType() { return type; }
        public void setType(IssueType type) { this.type = type; }
        public String getExpectedHash() { return expectedHash; }
        public void setExpectedHash(String expectedHash) { this.expectedHash = expectedHash; }
        public String getActualHash() { return actualHash; }
        public void setActualHash(String actualHash) { this.actualHash = actualHash; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        @Override
        public String toString() {
            return "FileIssue{" +
                    "path=" + path +
                    ", type=" + type +
                    (message != null ? ", message='" + message + '\'' : "") +
                    '}';
        }
    }
    
    public static class DependencyIssue {
        private String modId;
        private String modName;
        private String requiredMod;
        private String requiredVersion;
        private IssueType type;
        private String message;
        
        public DependencyIssue() {
        }
        
        public DependencyIssue(String modId, String requiredMod, IssueType type) {
            this.modId = modId;
            this.requiredMod = requiredMod;
            this.type = type;
        }
        
        public String getModId() { return modId; }
        public void setModId(String modId) { this.modId = modId; }
        public String getModName() { return modName; }
        public void setModName(String modName) { this.modName = modName; }
        public String getRequiredMod() { return requiredMod; }
        public void setRequiredMod(String requiredMod) { this.requiredMod = requiredMod; }
        public String getRequiredVersion() { return requiredVersion; }
        public void setRequiredVersion(String requiredVersion) { this.requiredVersion = requiredVersion; }
        public IssueType getType() { return type; }
        public void setType(IssueType type) { this.type = type; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        @Override
        public String toString() {
            return "DependencyIssue{" +
                    "modId='" + modId + '\'' +
                    ", requiredMod='" + requiredMod + '\'' +
                    ", type=" + type +
                    '}';
        }
    }
    
    public static class ConfigIssue {
        private Path configPath;
        private String key;
        private String expectedValue;
        private String actualValue;
        private String message;
        
        public ConfigIssue() {
        }
        
        public ConfigIssue(Path configPath, String key, String message) {
            this.configPath = configPath;
            this.key = key;
            this.message = message;
        }
        
        public Path getConfigPath() { return configPath; }
        public void setConfigPath(Path configPath) { this.configPath = configPath; }
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getExpectedValue() { return expectedValue; }
        public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }
        public String getActualValue() { return actualValue; }
        public void setActualValue(String actualValue) { this.actualValue = actualValue; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        @Override
        public String toString() {
            return "ConfigIssue{" +
                    "configPath=" + configPath +
                    ", key='" + key + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
    
    public enum IssueType {
        MISSING, CORRUPTED, VERSION_MISMATCH, CONFLICT, INVALID_FORMAT
    }
}