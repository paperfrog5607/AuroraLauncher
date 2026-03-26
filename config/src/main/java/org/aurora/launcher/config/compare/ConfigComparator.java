package org.aurora.launcher.config.compare;

import org.aurora.launcher.config.parser.ConfigParser;
import org.aurora.launcher.config.parser.ConfigParserFactory;
import org.aurora.launcher.config.parser.ConfigParseException;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfigComparator {
    
    public ConfigComparison compare(Path config1, Path config2) throws IOException, ConfigParseException {
        Map<String, Object> map1 = loadConfig(config1);
        Map<String, Object> map2 = loadConfig(config2);
        return compare(map1, map2);
    }
    
    public ConfigComparison compare(Map<String, Object> config1, Map<String, Object> config2) {
        List<ConfigDiff> diffs = new ArrayList<>();
        
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(config1.keySet());
        allKeys.addAll(config2.keySet());
        
        for (String key : allKeys) {
            Object val1 = config1.get(key);
            Object val2 = config2.get(key);
            
            if (val1 == null && val2 != null) {
                diffs.add(new ConfigDiff(key, ConfigDiff.DiffType.ADDED, null, val2));
            } else if (val1 != null && val2 == null) {
                diffs.add(new ConfigDiff(key, ConfigDiff.DiffType.REMOVED, val1, null));
            } else if (val1 != null && val2 != null && !val1.equals(val2)) {
                diffs.add(new ConfigDiff(key, ConfigDiff.DiffType.MODIFIED, val1, val2));
            }
        }
        
        return new ConfigComparison(config1, config2, diffs);
    }
    
    public ConfigComparison compare(Path config, Map<String, Object> expected) throws IOException, ConfigParseException {
        Map<String, Object> actual = loadConfig(config);
        return compare(actual, expected);
    }
    
    public List<ConfigDiff> getDifferences(ConfigComparison comparison) {
        return new ArrayList<>(comparison.getDiffs());
    }
    
    public List<ConfigDiff> getAddedEntries(ConfigComparison comparison) {
        List<ConfigDiff> added = new ArrayList<>();
        for (ConfigDiff diff : comparison.getDiffs()) {
            if (diff.getType() == ConfigDiff.DiffType.ADDED) {
                added.add(diff);
            }
        }
        return added;
    }
    
    public List<ConfigDiff> getRemovedEntries(ConfigComparison comparison) {
        List<ConfigDiff> removed = new ArrayList<>();
        for (ConfigDiff diff : comparison.getDiffs()) {
            if (diff.getType() == ConfigDiff.DiffType.REMOVED) {
                removed.add(diff);
            }
        }
        return removed;
    }
    
    public List<ConfigDiff> getModifiedEntries(ConfigComparison comparison) {
        List<ConfigDiff> modified = new ArrayList<>();
        for (ConfigDiff diff : comparison.getDiffs()) {
            if (diff.getType() == ConfigDiff.DiffType.MODIFIED) {
                modified.add(diff);
            }
        }
        return modified;
    }
    
    public void exportDiff(ConfigComparison comparison, Path output) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# Config Diff Report");
        lines.add("# Generated: " + new java.util.Date());
        lines.add("");
        
        for (ConfigDiff diff : comparison.getDiffs()) {
            lines.add("[" + diff.getType() + "] " + diff.getKey());
            if (diff.getOldValue() != null) {
                lines.add("  Old: " + diff.getOldValue());
            }
            if (diff.getNewValue() != null) {
                lines.add("  New: " + diff.getNewValue());
            }
            lines.add("");
        }
        
        Files.write(output, lines);
    }
    
    public void applyPatch(Path config, Path patch) throws IOException {
        throw new UnsupportedOperationException("Patch application not yet implemented");
    }
    
    private Map<String, Object> loadConfig(Path path) throws IOException, ConfigParseException {
        if (!Files.exists(path)) {
            return new LinkedHashMap<>();
        }
        
        ConfigParser parser = ConfigParserFactory.getParserByFile(path);
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported file type: " + path);
        }
        
        try (InputStream input = Files.newInputStream(path)) {
            return parser.parse(input);
        }
    }
}