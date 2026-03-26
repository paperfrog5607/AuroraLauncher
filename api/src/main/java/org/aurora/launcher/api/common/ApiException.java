package org.aurora.launcher.api.common;

public class ApiException extends RuntimeException {
    
    private final int statusCode;
    
    public ApiException(String message) {
        super(message);
        this.statusCode = -1;
    }
    
    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }
    
    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}