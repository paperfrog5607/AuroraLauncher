package org.aurora.launcher.config.batch;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BatchResult {
    
    private int successCount;
    private int failCount;
    private List<BatchError> errors;
    private List<Path> processedFiles;
    
    public BatchResult() {
        this.errors = new ArrayList<>();
        this.processedFiles = new ArrayList<>();
    }
    
    public boolean isAllSuccess() {
        return failCount == 0;
    }
    
    public List<BatchError> getErrors() {
        return errors;
    }
    
    public void addError(BatchError error) {
        errors.add(error);
        failCount++;
    }
    
    public void addProcessedFile(Path file) {
        processedFiles.add(file);
        successCount++;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public int getFailCount() {
        return failCount;
    }
    
    public List<Path> getProcessedFiles() {
        return processedFiles;
    }
    
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    
    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }
}