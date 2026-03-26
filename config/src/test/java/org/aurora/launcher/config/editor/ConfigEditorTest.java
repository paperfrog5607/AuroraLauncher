package org.aurora.launcher.config.editor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ConfigEditorTest {
    
    @TempDir
    Path tempDir;
    
    private Path configPath;
    
    @BeforeEach
    void setUp() throws Exception {
        configPath = tempDir.resolve("test.properties");
    }
    
    @Test
    void load_emptyFile_createsEditor() throws Exception {
        Files.createFile(configPath);
        
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        assertNotNull(editor);
        assertFalse(editor.isModified());
    }
    
    @Test
    void load_existingFile_loadsContent() throws Exception {
        Files.write(configPath, "testKey=testValue\n".getBytes());
        
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        assertTrue(editor.has("testKey"));
        assertEquals("testValue", editor.get("testKey"));
    }
    
    @Test
    void set_newKey_addsEntry() throws Exception {
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        editor.set("newKey", "newValue");
        
        assertTrue(editor.has("newKey"));
        assertEquals("newValue", editor.get("newKey"));
        assertTrue(editor.isModified());
    }
    
    @Test
    void set_existingKey_updatesValue() throws Exception {
        Files.write(configPath, "testKey=oldValue\n".getBytes());
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        editor.set("testKey", "newValue");
        
        assertEquals("newValue", editor.get("testKey"));
    }
    
    @Test
    void get_withType_returnsConvertedValue() throws Exception {
        Files.write(configPath, "number=42\n".getBytes());
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        Integer value = editor.get("number", Integer.class);
        
        assertEquals(42, value);
    }
    
    @Test
    void get_withDefaultValue_returnsValueIfExists() throws Exception {
        Files.write(configPath, "key=value\n".getBytes());
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        String value = editor.get("key", String.class, "default");
        
        assertEquals("value", value);
    }
    
    @Test
    void remove_existingKey_removesEntry() throws Exception {
        Files.write(configPath, "testKey=value\n".getBytes());
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        editor.remove("testKey");
        
        assertFalse(editor.has("testKey"));
        assertTrue(editor.isModified());
    }
    
    @Test
    void has_existingKey_returnsTrue() throws Exception {
        Files.write(configPath, "testKey=value\n".getBytes());
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        assertTrue(editor.has("testKey"));
    }
    
    @Test
    void has_missingKey_returnsFalse() throws Exception {
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        assertFalse(editor.has("missingKey"));
    }
    
    @Test
    void save_writesToFile() throws Exception {
        ConfigEditor editor = ConfigEditor.load(configPath);
        editor.set("key1", "value1");
        editor.set("key2", 100);
        
        editor.save();
        
        String content = new String(Files.readAllBytes(configPath));
        assertTrue(content.contains("key1"));
        assertTrue(content.contains("value1"));
    }
    
    @Test
    void saveAs_writesToDifferentFile() throws Exception {
        ConfigEditor editor = ConfigEditor.load(configPath);
        editor.set("key", "value");
        
        Path newPath = tempDir.resolve("new.properties");
        editor.saveAs(newPath);
        
        assertTrue(Files.exists(newPath));
    }
    
    @Test
    void reload_reloadsFromFile() throws Exception {
        Files.write(configPath, "key=oldValue\n".getBytes());
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        editor.set("key", "newValue");
        editor.reload();
        
        assertEquals("oldValue", editor.get("key"));
    }
    
    @Test
    void isModified_afterSave_returnsFalse() throws Exception {
        ConfigEditor editor = ConfigEditor.load(configPath);
        editor.set("key", "value");
        editor.save();
        
        assertFalse(editor.isModified());
    }
}