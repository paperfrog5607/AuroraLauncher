package org.aurora.launcher.config.parser;

import java.io.*;
import java.util.*;

public class PropertiesParser implements ConfigParser {
    
    @Override
    public Map<String, Object> parse(InputStream input) throws ConfigParseException {
        Properties props = new Properties();
        try {
            props.load(input);
        } catch (IOException e) {
            throw new ConfigParseException("Failed to parse properties", e);
        }
        
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : props.stringPropertyNames()) {
            result.put(key, props.getProperty(key));
        }
        return result;
    }
    
    @Override
    public void write(OutputStream output, Map<String, Object> config) throws ConfigParseException {
        Properties props = new Properties();
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            props.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
        }
        
        try {
            props.store(output, null);
        } catch (IOException e) {
            throw new ConfigParseException("Failed to write properties", e);
        }
    }
    
    public Properties parseProperties(InputStream input) throws ConfigParseException {
        Properties props = new Properties();
        try {
            props.load(input);
        } catch (IOException e) {
            throw new ConfigParseException("Failed to parse properties", e);
        }
        return props;
    }
    
    public void writeProperties(OutputStream output, Properties props) throws ConfigParseException {
        try {
            props.store(output, null);
        } catch (IOException e) {
            throw new ConfigParseException("Failed to write properties", e);
        }
    }
    
    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"properties"};
    }
}