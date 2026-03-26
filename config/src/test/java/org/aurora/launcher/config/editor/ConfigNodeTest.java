package org.aurora.launcher.config.editor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfigNodeTest {
    
    @Test
    void constructor_keyValue_setsFields() {
        ConfigNode node = new ConfigNode("testKey", "testValue");
        
        assertEquals("testKey", node.getKey());
        assertEquals("testValue", node.getValue());
        assertEquals("String", node.getType());
    }
    
    @Test
    void addChild_createsSection() {
        ConfigNode parent = new ConfigNode("server", null);
        parent.addChild(new ConfigNode("port", 25565));
        
        assertTrue(parent.isSection());
        assertFalse(parent.isLeaf());
        assertEquals(1, parent.getChildren().size());
    }
    
    @Test
    void isLeaf_noChildren_returnsTrue() {
        ConfigNode node = new ConfigNode("key", "value");
        
        assertTrue(node.isLeaf());
        assertFalse(node.isSection());
    }
    
    @Test
    void setExpanded_changesState() {
        ConfigNode node = new ConfigNode("key", "value");
        
        node.setExpanded(true);
        
        assertTrue(node.isExpanded());
    }
}