package org.aurora.launcher.config.parser;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.*;
import java.util.*;

public class TomlParser implements ConfigParser {
    
    @Override
    public Map<String, Object> parse(InputStream input) throws ConfigParseException {
        TomlParseResult result;
        try {
            result = Toml.parse(new InputStreamReader(input));
        } catch (Exception e) {
            throw new ConfigParseException("Failed to parse TOML", e);
        }
        
        if (result.hasErrors()) {
            throw new ConfigParseException("TOML parse errors: " + result.errors());
        }
        
        return tomlTableToMap(result);
    }
    
    @Override
    public void write(OutputStream output, Map<String, Object> config) throws ConfigParseException {
        try {
            String toml = mapToToml(config);
            output.write(toml.getBytes());
        } catch (IOException e) {
            throw new ConfigParseException("Failed to write TOML", e);
        }
    }
    
    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"toml"};
    }
    
    private Map<String, Object> tomlTableToMap(TomlTable table) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : table.keySet()) {
            Object value = table.get(key);
            if (value instanceof TomlTable) {
                map.put(key, tomlTableToMap((TomlTable) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }
    
    private String mapToToml(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) value;
                sb.append("[").append(entry.getKey()).append("]\n");
                sb.append(mapToTomlBody(subMap));
                sb.append("\n");
            } else {
                sb.append(entry.getKey()).append(" = ").append(valueToToml(value)).append("\n");
            }
        }
        return sb.toString();
    }
    
    private String mapToTomlBody(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(" = ").append(valueToToml(entry.getValue())).append("\n");
        }
        return sb.toString();
    }
    
    private String valueToToml(Object value) {
        if (value == null) {
            return "\"\"";
        } else if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value instanceof Boolean) {
            return value.toString();
        } else {
            return value.toString();
        }
    }
}