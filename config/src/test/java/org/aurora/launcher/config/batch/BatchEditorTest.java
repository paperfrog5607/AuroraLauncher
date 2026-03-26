package org.aurora.launcher.config.batch;

import org.aurora.launcher.config.editor.ConfigEditor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class BatchEditorTest {
    
    @TempDir
    Path tempDir;
    
    private BatchEditor batchEditor;
    
    @BeforeEach
    void setUp() {
        batchEditor = new BatchEditor();
    }
    
    @Test
    void addSetOperation_addsOperation() {
        batchEditor.addSetOperation("key", "value");
        
        assertEquals(1, batchEditor.getOperations().size());
        assertTrue(batchEditor.getOperations().get(0) instanceof SetOperation);
    }
    
    @Test
    void addRemoveOperation_addsOperation() {
        batchEditor.addRemoveOperation("key");
        
        assertEquals(1, batchEditor.getOperations().size());
        assertTrue(batchEditor.getOperations().get(0) instanceof RemoveOperation);
    }
    
    @Test
    void addRenameOperation_addsOperation() {
        batchEditor.addRenameOperation("oldKey", "newKey");
        
        assertEquals(1, batchEditor.getOperations().size());
        assertTrue(batchEditor.getOperations().get(0) instanceof RenameOperation);
    }
    
    @Test
    void execute_singleFile_appliesOperations() throws Exception {
        Path configPath = tempDir.resolve("test.properties");
        Files.write(configPath, "key1=value1\n".getBytes());
        
        batchEditor.addSetOperation("key2", "value2");
        batchEditor.addRemoveOperation("key1");
        
        BatchResult result = batchEditor.execute(configPath);
        
        assertTrue(result.isAllSuccess());
        assertEquals(1, result.getSuccessCount());
        
        ConfigEditor editor = ConfigEditor.load(configPath);
        assertFalse(editor.has("key1"));
        assertEquals("value2", editor.get("key2"));
    }
    
    @Test
    void execute_multipleFiles_appliesToAll() throws Exception {
        Path config1 = tempDir.resolve("config1.properties");
        Path config2 = tempDir.resolve("config2.properties");
        Files.write(config1, "key1=value1\n".getBytes());
        Files.write(config2, "key1=value1\n".getBytes());
        
        batchEditor.addSetOperation("newKey", "newValue");
        
        BatchResult result = batchEditor.execute(Arrays.asList(config1, config2));
        
        assertTrue(result.isAllSuccess());
        assertEquals(2, result.getSuccessCount());
    }
    
    @Test
    void execute_withEditor_appliesOperations() throws Exception {
        Path configPath = tempDir.resolve("test.properties");
        ConfigEditor editor = ConfigEditor.load(configPath);
        
        batchEditor.addSetOperation("key", "value");
        
        BatchResult result = batchEditor.execute(editor);
        
        assertTrue(result.isAllSuccess());
        assertEquals("value", editor.get("key"));
    }
    
    @Test
    void preview_printsOperations() {
        batchEditor.addSetOperation("key1", "value1");
        batchEditor.addRemoveOperation("key2");
        
        assertDoesNotThrow(() -> batchEditor.preview(tempDir.resolve("test.properties")));
    }
    
    @Test
    void saveScript_and_loadScript() throws Exception {
        batchEditor.addSetOperation("key1", "value1");
        batchEditor.addRemoveOperation("key2");
        batchEditor.addRenameOperation("oldKey", "newKey");
        
        Path scriptPath = tempDir.resolve("script.json");
        batchEditor.saveScript(scriptPath);
        
        assertTrue(Files.exists(scriptPath));
        
        BatchEditor newEditor = new BatchEditor();
        newEditor.loadScript(scriptPath);
        
        assertEquals(3, newEditor.getOperations().size());
    }
}