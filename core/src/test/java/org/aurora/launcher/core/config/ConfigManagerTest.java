package org.aurora.launcher.core.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

    @TempDir
    Path tempDir;

    private Path configPath;
    private ConfigManager configManager;

    @BeforeEach
    void setUp() throws Exception {
        configPath = tempDir.resolve("config.json");
        configManager = new ConfigManager(configPath);
    }

    @Test
    void load_emptyFile_createsDefaultConfig() throws Exception {
        configManager.load();
        
        assertTrue(configManager.has("version"));
    }

    @Test
    void save_writesToFile() throws Exception {
        configManager.load();
        configManager.set("testKey", "testValue");
        configManager.save();
        
        String content = new String(Files.readAllBytes(configPath));
        assertTrue(content.contains("testKey"));
        assertTrue(content.contains("testValue"));
    }

    @Test
    void get_returnsCorrectValue() throws Exception {
        configManager.load();
        configManager.set("testInt", 123);
        
        assertEquals(123, configManager.get("testInt", Integer.class).intValue());
    }

    @Test
    void get_nestedKey_returnsCorrectValue() throws Exception {
        configManager.load();
        configManager.set("java.defaultMemory", 4096);
        
        assertEquals(4096, configManager.get("java.defaultMemory", Integer.class).intValue());
    }

    @Test
    void set_nestedKey_createsNestedObject() throws Exception {
        configManager.load();
        configManager.set("java.customPath", "/path/to/java");
        
        assertEquals("/path/to/java", configManager.get("java.customPath", String.class));
    }

    @Test
    void has_existingKey_returnsTrue() throws Exception {
        configManager.load();
        configManager.set("testKey", "testValue");
        
        assertTrue(configManager.has("testKey"));
    }

    @Test
    void has_missingKey_returnsFalse() throws Exception {
        configManager.load();
        
        assertFalse(configManager.has("nonExistentKey"));
    }

    @Test
    void remove_removesKey() throws Exception {
        configManager.load();
        configManager.set("testKey", "testValue");
        configManager.remove("testKey");
        
        assertFalse(configManager.has("testKey"));
    }
}