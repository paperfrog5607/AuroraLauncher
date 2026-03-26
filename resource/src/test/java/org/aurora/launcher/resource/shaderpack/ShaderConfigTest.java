package org.aurora.launcher.resource.shaderpack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ShaderConfigTest {
    
    @TempDir
    Path tempDir;
    
    private Path configPath;
    private ShaderConfig config;
    
    @BeforeEach
    void setUp() {
        configPath = tempDir.resolve("shader.properties");
        config = new ShaderConfig(configPath);
    }
    
    @Test
    void get_missingKey_returnsNull() {
        assertNull(config.get("missing"));
    }
    
    @Test
    void get_withDefault_returnsDefault() {
        assertEquals("default", config.get("missing", "default"));
    }
    
    @Test
    void set_andGet_works() {
        config.set("key", "value");
        
        assertEquals("value", config.get("key"));
    }
    
    @Test
    void save_andLoad_persistsData() throws Exception {
        config.set("key1", "value1");
        config.set("key2", "value2");
        config.save();
        
        ShaderConfig loaded = new ShaderConfig(configPath);
        loaded.load();
        
        assertEquals("value1", loaded.get("key1"));
        assertEquals("value2", loaded.get("key2"));
    }
    
    @Test
    void remove_deletesKey() {
        config.set("key", "value");
        config.remove("key");
        
        assertNull(config.get("key"));
    }
    
    @Test
    void getKeys_returnsAllKeys() {
        config.set("key1", "value1");
        config.set("key2", "value2");
        
        assertTrue(config.getKeys().contains("key1"));
        assertTrue(config.getKeys().contains("key2"));
    }
}