package org.aurora.launcher.mod.scanner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public class ModFile {
    
    private final Path file;
    private final String fileName;
    private final boolean enabled;
    private long fileSize;
    private String sha1;
    private Instant lastModified;
    
    public ModFile(Path file) {
        this.file = file;
        this.fileName = file.getFileName().toString();
        this.enabled = !fileName.endsWith(".disabled");
    }
    
    public Path getFile() {
        return file;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getSha1() {
        return sha1;
    }
    
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }
    
    public Instant getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }
    
    public Path getDisabledPath() {
        if (enabled) {
            return Paths.get(file.toString() + ".disabled");
        }
        return file;
    }
    
    public Path getEnabledPath() {
        if (!enabled) {
            String path = file.toString();
            if (path.endsWith(".disabled")) {
                return Paths.get(path.substring(0, path.length() - 9));
            }
        }
        return file;
    }
}