package org.aurora.launcher.config.parser;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class YamlParser implements ConfigParser {
    
    private final Yaml yaml;
    
    public YamlParser() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        this.yaml = new Yaml(options);
    }
    
    @Override
    public Map<String, Object> parse(InputStream input) throws ConfigParseException {
        try {
            Object data = yaml.load(input);
            if (data instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) data;
                return map;
            }
            throw new ConfigParseException("Expected YAML map");
        } catch (Exception e) {
            throw new ConfigParseException("Failed to parse YAML", e);
        }
    }
    
    @Override
    public void write(OutputStream output, Map<String, Object> config) throws ConfigParseException {
        try {
            yaml.dump(config, new OutputStreamWriter(output));
        } catch (Exception e) {
            throw new ConfigParseException("Failed to write YAML", e);
        }
    }
    
    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"yaml", "yml"};
    }
}