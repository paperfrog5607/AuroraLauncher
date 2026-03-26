package org.aurora.launcher.config.compare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigComparatorTest {
    
    @TempDir
    Path tempDir;
    
    private ConfigComparator comparator;
    
    @BeforeEach
    void setUp() {
        comparator = new ConfigComparator();
    }
    
    @Test
    void compare_mapsWithDifferences_returnsComparison() {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "value1");
        config1.put("key2", "oldValue");
        
        Map<String, Object> config2 = new HashMap<>();
        config2.put("key1", "value1");
        config2.put("key2", "newValue");
        config2.put("key3", "value3");
        
        ConfigComparison comparison = comparator.compare(config1, config2);
        
        assertTrue(comparison.hasDifferences());
        assertEquals(2, comparison.getTotalDifferences());
        assertEquals(1, comparison.getModifiedCount());
        assertEquals(1, comparison.getAddedCount());
    }
    
    @Test
    void compare_identicalMaps_returnsEmptyComparison() {
        Map<String, Object> config = new HashMap<>();
        config.put("key1", "value1");
        config.put("key2", "value2");
        
        ConfigComparison comparison = comparator.compare(config, config);
        
        assertFalse(comparison.hasDifferences());
        assertEquals(0, comparison.getTotalDifferences());
    }
    
    @Test
    void getAddedEntries_returnsOnlyAdded() {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "value1");
        
        Map<String, Object> config2 = new HashMap<>();
        config2.put("key1", "value1");
        config2.put("key2", "value2");
        
        ConfigComparison comparison = comparator.compare(config1, config2);
        List<ConfigDiff> added = comparator.getAddedEntries(comparison);
        
        assertEquals(1, added.size());
        assertEquals("key2", added.get(0).getKey());
        assertEquals(ConfigDiff.DiffType.ADDED, added.get(0).getType());
    }
    
    @Test
    void getRemovedEntries_returnsOnlyRemoved() {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "value1");
        config1.put("key2", "value2");
        
        Map<String, Object> config2 = new HashMap<>();
        config2.put("key1", "value1");
        
        ConfigComparison comparison = comparator.compare(config1, config2);
        List<ConfigDiff> removed = comparator.getRemovedEntries(comparison);
        
        assertEquals(1, removed.size());
        assertEquals("key2", removed.get(0).getKey());
        assertEquals(ConfigDiff.DiffType.REMOVED, removed.get(0).getType());
    }
    
    @Test
    void getModifiedEntries_returnsOnlyModified() {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "oldValue");
        
        Map<String, Object> config2 = new HashMap<>();
        config2.put("key1", "newValue");
        
        ConfigComparison comparison = comparator.compare(config1, config2);
        List<ConfigDiff> modified = comparator.getModifiedEntries(comparison);
        
        assertEquals(1, modified.size());
        assertEquals("key1", modified.get(0).getKey());
        assertEquals(ConfigDiff.DiffType.MODIFIED, modified.get(0).getType());
    }
    
    @Test
    void compare_files_returnsComparison() throws Exception {
        Path file1 = tempDir.resolve("config1.properties");
        Path file2 = tempDir.resolve("config2.properties");
        Files.write(file1, "key1=value1\nkey2=old\n".getBytes());
        Files.write(file2, "key1=value1\nkey2=new\n".getBytes());
        
        ConfigComparison comparison = comparator.compare(file1, file2);
        
        assertTrue(comparison.hasDifferences());
        assertEquals(1, comparison.getModifiedCount());
    }
    
    @Test
    void exportDiff_writesReport() throws Exception {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "old");
        
        Map<String, Object> config2 = new HashMap<>();
        config2.put("key1", "new");
        
        ConfigComparison comparison = comparator.compare(config1, config2);
        
        Path output = tempDir.resolve("diff.txt");
        comparator.exportDiff(comparison, output);
        
        String content = new String(Files.readAllBytes(output));
        assertTrue(content.contains("MODIFIED"));
        assertTrue(content.contains("key1"));
    }
}