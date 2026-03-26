package org.aurora.launcher.config.compare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigDiffReporterTest {
    
    private ConfigDiffReporter reporter;
    private ConfigComparator comparator;
    
    @BeforeEach
    void setUp() {
        reporter = new ConfigDiffReporter();
        comparator = new ConfigComparator();
    }
    
    @Test
    void generateTextReport_containsSummary() {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "old");
        
        Map<String, Object> config2 = new HashMap<>();
        config2.put("key1", "new");
        
        ConfigComparison comparison = comparator.compare(config1, config2);
        
        String report = reporter.generateTextReport(comparison);
        
        assertTrue(report.contains("Config Comparison Report"));
        assertTrue(report.contains("Modified: 1"));
        assertTrue(report.contains("MODIFIED"));
    }
    
    @Test
    void generateHtmlReport_containsHtmlTags() {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "old");
        
        Map<String, Object> config2 = new HashMap<>();
        config2.put("key1", "new");
        
        ConfigComparison comparison = comparator.compare(config1, config2);
        
        String report = reporter.generateHtmlReport(comparison);
        
        assertTrue(report.contains("<!DOCTYPE html>"));
        assertTrue(report.contains("<html>"));
        assertTrue(report.contains("</html>"));
    }
    
    @Test
    void generateMarkdownReport_containsMarkdown() {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "old");
        
        Map<String, Object> config2 = new HashMap<>();
        config2.put("key1", "new");
        
        ConfigComparison comparison = comparator.compare(config1, config2);
        
        String report = reporter.generateMarkdownReport(comparison);
        
        assertTrue(report.contains("# Config Comparison Report"));
        assertTrue(report.contains("| Type | Count |"));
        assertTrue(report.contains("MODIFIED"));
    }
    
    @Test
    void setFilter_filtersByType() {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "old");
        
        Map<String, Object> config2 = new HashMap<>();
        config2.put("key1", "new");
        config2.put("key2", "value");
        
        ConfigComparison comparison = comparator.compare(config1, config2);
        
        reporter.setFilter(ConfigDiff.DiffType.MODIFIED);
        String report = reporter.generateTextReport(comparison);
        
        assertTrue(report.contains("key1"));
        assertFalse(report.contains("key2"));
    }
}