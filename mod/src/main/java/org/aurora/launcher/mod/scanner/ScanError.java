package org.aurora.launcher.mod.scanner;

import java.nio.file.Path;

public class ScanError {
    
    private Path file;
    private String message;
    private Exception exception;
    
    public ScanError(Path file, String message) {
        this.file = file;
        this.message = message;
    }
    
    public ScanError(Path file, String message, Exception exception) {
        this.file = file;
        this.message = message;
        this.exception = exception;
    }
    
    public Path getFile() {
        return file;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Exception getException() {
        return exception;
    }
}