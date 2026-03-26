package org.aurora.launcher.modpack.instance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InstanceConfigTest {
    
    private InstanceConfig config;
    
    @BeforeEach
    void setUp() {
        config = new InstanceConfig();
    }
    
    @Test
    void testDefaultValues() {
        assertNotNull(config.getMemory());
        assertNotNull(config.getJava());
        assertNotNull(config.getWindow());
        assertNotNull(config.getCustomArgs());
    }
    
    @Test
    void testMinecraftVersion() {
        config.setMinecraftVersion("1.20.4");
        assertEquals("1.20.4", config.getMinecraftVersion());
    }
    
    @Test
    void testLoaderConfig() {
        config.setLoaderType("fabric");
        config.setLoaderVersion("0.15.7");
        
        assertEquals("fabric", config.getLoaderType());
        assertEquals("0.15.7", config.getLoaderVersion());
    }
    
    @Test
    void testMemoryConfig() {
        InstanceConfig.MemoryConfig memory = config.getMemory();
        
        assertEquals("standard", memory.getPreset());
        assertEquals(2048, memory.getMinMB());
        assertEquals(4096, memory.getMaxMB());
        
        memory.setPreset("high");
        memory.setMinMB(4096);
        memory.setMaxMB(8192);
        
        assertEquals("high", memory.getPreset());
        assertEquals(4096, memory.getMinMB());
        assertEquals(8192, memory.getMaxMB());
    }
    
    @Test
    void testJavaConfig() {
        InstanceConfig.JavaConfig java = config.getJava();
        
        java.setPath("/path/to/java");
        assertEquals("/path/to/java", java.getPath());
        
        java.getArgs().add("-Xmx4G");
        assertTrue(java.getArgs().contains("-Xmx4G"));
    }
    
    @Test
    void testWindowConfig() {
        InstanceConfig.WindowConfig window = config.getWindow();
        
        assertEquals(854, window.getWidth());
        assertEquals(480, window.getHeight());
        assertFalse(window.isFullscreen());
        
        window.setWidth(1920);
        window.setHeight(1080);
        window.setFullscreen(true);
        
        assertEquals(1920, window.getWidth());
        assertEquals(1080, window.getHeight());
        assertTrue(window.isFullscreen());
    }
    
    @Test
    void testCustomArgs() {
        config.addCustomArg("-XX:+UseG1GC");
        config.addCustomArg("-Xmx4G");
        
        assertEquals(2, config.getCustomArgs().size());
        assertTrue(config.getCustomArgs().contains("-XX:+UseG1GC"));
    }
}