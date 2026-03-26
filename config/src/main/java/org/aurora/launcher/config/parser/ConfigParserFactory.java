package org.aurora.launcher.config.parser;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ConfigParserFactory {
    
    private static final Map<String, ConfigParser> parsers = new HashMap<>();
    
    static {
        registerParser(new PropertiesParser());
        registerParser(new JsonParser());
        registerParser(new TomlParser());
        registerParser(new YamlParser());
    }
    
    private ConfigParserFactory() {
    }
    
    public static void registerParser(ConfigParser parser) {
        for (String ext : parser.getSupportedExtensions()) {
            parsers.put(ext.toLowerCase(), parser);
        }
    }
    
    public static ConfigParser getParser(String extension) {
        return parsers.get(extension.toLowerCase());
    }
    
    public static ConfigParser getParserByFile(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            String extension = fileName.substring(dotIndex + 1);
            return getParser(extension);
        }
        return null;
    }
    
    public static boolean isSupported(String extension) {
        return parsers.containsKey(extension.toLowerCase());
    }
}