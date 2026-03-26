package org.aurora.launcher.api.common;

public class ApiError {
    
    private final int code;
    private final String message;
    private final String details;
    
    public ApiError(int code, String message) {
        this(code, message, null);
    }
    
    public ApiError(int code, String message, String details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getDetails() {
        return details;
    }
    
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }
    
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }
    
    @Override
    public String toString() {
        return "ApiError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}