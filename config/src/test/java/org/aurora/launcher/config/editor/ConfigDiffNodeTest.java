package org.aurora.launcher.config.editor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfigDiffNodeTest {
    
    @Test
    void constructor_allFields_setsCorrectly() {
        ConfigDiffNode node = new ConfigDiffNode("key", "old", "new", ConfigDiffNode.DiffType.MODIFIED);
        
        assertEquals("key", node.getKey());
        assertEquals("old", node.getOldValue());
        assertEquals("new", node.getNewValue());
        assertEquals(ConfigDiffNode.DiffType.MODIFIED, node.getDiffType());
    }
    
    @Test
    void diffType_values() {
        assertEquals(4, ConfigDiffNode.DiffType.values().length);
        assertNotNull(ConfigDiffNode.DiffType.valueOf("ADDED"));
        assertNotNull(ConfigDiffNode.DiffType.valueOf("REMOVED"));
        assertNotNull(ConfigDiffNode.DiffType.valueOf("MODIFIED"));
        assertNotNull(ConfigDiffNode.DiffType.valueOf("UNCHANGED"));
    }
}