package org.aurora.launcher.resource.language;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class LanguageFileManagerTest {
    
    private LanguageFileManager manager;
    
    @BeforeEach
    void setUp() {
        manager = new LanguageFileManager();
    }
    
    @Test
    void create_returnsNewFile() {
        LanguageFile file = manager.create("test_mod", "zh_cn").join();
        
        assertNotNull(file);
        assertEquals("zh_cn", file.getLanguageCode());
        assertEquals("test_mod", file.getModId());
    }
    
    @Test
    void compare_detectsDifferences() {
        LanguageFile base = new LanguageFile();
        base.set("key1", "value1");
        base.set("key2", "old");
        
        LanguageFile other = new LanguageFile();
        other.set("key1", "value1");
        other.set("key2", "new");
        other.set("key3", "value3");
        
        Map<String, LanguageDiff> diffs = manager.compare(base, other);
        
        assertTrue(diffs.containsKey("key2"));
        assertEquals(LanguageDiff.DiffType.MODIFIED, diffs.get("key2").getType());
        
        assertTrue(diffs.containsKey("key3"));
        assertEquals(LanguageDiff.DiffType.ADDED, diffs.get("key3").getType());
    }
}