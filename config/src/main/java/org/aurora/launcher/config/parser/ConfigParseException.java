package org.aurora.launcher.config.parser;

public class ConfigParseException extends Exception {
    
    public ConfigParseException(String message) {
        super(message);
    }
    
    public ConfigParseException(String message, Throwable cause) {
        super(message, cause);
    }
}