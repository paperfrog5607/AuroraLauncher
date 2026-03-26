package org.aurora.launcher.resource.language;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class LanguageEditorTest {
    
    private LanguageFile file;
    private LanguageEditor editor;
    
    @BeforeEach
    void setUp() {
        file = new LanguageFile("zh_cn", "test");
        editor = new LanguageEditor(file);
    }
    
    @Test
    void setEntry_addsEntry() {
        editor.setEntry("key", "value");
        
        assertEquals("value", file.get("key"));
    }
    
    @Test
    void removeEntry_deletesEntry() {
        editor.setEntry("key", "value");
        editor.removeEntry("key");
        
        assertFalse(file.has("key"));
    }
    
    @Test
    void addEntries_addsMultiple() {
        Map<String, String> entries = new HashMap<>();
        entries.put("key1", "value1");
        entries.put("key2", "value2");
        
        editor.addEntries(entries);
        
        assertEquals("value1", file.get("key1"));
        assertEquals("value2", file.get("key2"));
    }
    
    @Test
    void undo_revertsChange() {
        editor.setEntry("key", "value1");
        editor.setEntry("key", "value2");
        
        editor.undo();
        
        assertEquals("value1", file.get("key"));
    }
    
    @Test
    void redo_redoChange() {
        editor.setEntry("key", "value1");
        editor.setEntry("key", "value2");
        editor.undo();
        
        editor.redo();
        
        assertEquals("value2", file.get("key"));
    }
    
    @Test
    void canUndo_canRedo_afterChange() {
        assertFalse(editor.canUndo());
        assertFalse(editor.canRedo());
        
        editor.setEntry("key", "value");
        
        assertTrue(editor.canUndo());
        assertFalse(editor.canRedo());
    }
    
    @Test
    void search_findsMatches() {
        editor.setEntry("test.key", "Test Value");
        editor.setEntry("other.key", "Other");
        
        List<String> results = editor.search("test");
        
        assertTrue(results.contains("test.key"));
    }
    
    @Test
    void getMissingKeys_findsMissing() {
        LanguageFile reference = new LanguageFile();
        reference.set("key1", "value1");
        reference.set("key2", "value2");
        
        file.set("key1", "value1");
        
        List<String> missing = editor.getMissingKeys(reference);
        
        assertTrue(missing.contains("key2"));
        assertEquals(1, missing.size());
    }
    
    @Test
    void listener_notifiedOnChange() {
        final boolean[] changed = {false};
        editor.addListener((key, oldValue, newValue) -> changed[0] = true);
        
        editor.setEntry("key", "value");
        
        assertTrue(changed[0]);
    }
}