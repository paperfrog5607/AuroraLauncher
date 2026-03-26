package org.aurora.launcher.config.editor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfigEntryTest {
    
    @Test
    void constructor_keyValue_setsFields() {
        ConfigEntry entry = new ConfigEntry("testKey", "testValue");
        
        assertEquals("testKey", entry.getKey());
        assertEquals("testValue", entry.getValue());
    }
    
    @Test
    void constructor_keyValueComment_setsFields() {
        ConfigEntry entry = new ConfigEntry("testKey", "testValue", "test comment");
        
        assertEquals("testKey", entry.getKey());
        assertEquals("testValue", entry.getValue());
        assertEquals("test comment", entry.getComment());
    }
    
    @Test
    void setters_updateFields() {
        ConfigEntry entry = new ConfigEntry();
        entry.setKey("newKey");
        entry.setValue(123);
        entry.setComment("new comment");
        entry.setSection("server");
        
        assertEquals("newKey", entry.getKey());
        assertEquals(123, entry.getValue());
        assertEquals("new comment", entry.getComment());
        assertEquals("server", entry.getSection());
    }
}