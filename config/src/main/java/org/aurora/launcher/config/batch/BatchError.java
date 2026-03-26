package org.aurora.launcher.config.batch;

import java.nio.file.Path;

public class BatchError {
    
    private Path file;
    private String operation;
    private String message;
    
    public BatchError() {
    }
    
    public BatchError(Path file, String operation, String message) {
        this.file = file;
        this.operation = operation;
        this.message = message;
    }
    
    public Path getFile() {
        return file;
    }
    
    public void setFile(Path file) {
        this.file = file;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}