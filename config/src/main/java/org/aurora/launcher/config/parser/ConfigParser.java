package org.aurora.launcher.config.parser;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface ConfigParser {
    
    Map<String, Object> parse(InputStream input) throws ConfigParseException;
    
    void write(OutputStream output, Map<String, Object> config) throws ConfigParseException;
    
    String[] getSupportedExtensions();
}