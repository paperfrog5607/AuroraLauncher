package org.aurora.launcher.resource.language;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LanguageFileTest {
    
    @Test
    void constructor_defaultValues() {
        LanguageFile file = new LanguageFile();
        
        assertNotNull(file.getEntries());
        assertTrue(file.getEntries().isEmpty());
    }
    
    @Test
    void constructor_withParams() {
        LanguageFile file = new LanguageFile("zh_cn", "test_mod");
        
        assertEquals("zh_cn", file.getLanguageCode());
        assertEquals("test_mod", file.getModId());
    }
    
    @Test
    void setAndGet_works() {
        LanguageFile file = new LanguageFile();
        file.set("key", "value");
        
        assertEquals("value", file.get("key"));
    }
    
    @Test
    void remove_deletesKey() {
        LanguageFile file = new LanguageFile();
        file.set("key", "value");
        file.remove("key");
        
        assertFalse(file.has("key"));
    }
    
    @Test
    void has_checksExistence() {
        LanguageFile file = new LanguageFile();
        file.set("key", "value");
        
        assertTrue(file.has("key"));
        assertFalse(file.has("missing"));
    }
    
    @Test
    void size_returnsCount() {
        LanguageFile file = new LanguageFile();
        file.set("key1", "value1");
        file.set("key2", "value2");
        
        assertEquals(2, file.size());
    }
}