package org.aurora.launcher.modpack.instance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class InstanceTest {
    
    private Instance instance;
    
    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId("test-instance-1");
        instance.setName("Test Instance");
        instance.setVersion("1.0.0");
    }
    
    @Test
    void testInstanceCreation() {
        assertNotNull(instance);
        assertEquals("test-instance-1", instance.getId());
        assertEquals("Test Instance", instance.getName());
        assertEquals("1.0.0", instance.getVersion());
        assertEquals(Instance.InstanceState.READY, instance.getState());
    }
    
    @Test
    void testInstanceTags() {
        instance.addTag("tech");
        instance.addTag("survival");
        
        assertEquals(2, instance.getTags().size());
        assertTrue(instance.getTags().contains("tech"));
        assertTrue(instance.getTags().contains("survival"));
        
        instance.addTag("tech");
        assertEquals(2, instance.getTags().size());
        
        instance.removeTag("tech");
        assertEquals(1, instance.getTags().size());
        assertFalse(instance.getTags().contains("tech"));
    }
    
    @Test
    void testInstanceDirectories(@TempDir Path tempDir) {
        instance.setInstanceDir(tempDir.resolve("test-instance"));
        
        assertEquals(tempDir.resolve("test-instance/.minecraft"), instance.getMinecraftDir());
        assertEquals(tempDir.resolve("test-instance/.minecraft/mods"), instance.getModsDir());
        assertEquals(tempDir.resolve("test-instance/.minecraft/config"), instance.getConfigDir());
        assertEquals(tempDir.resolve("test-instance/.minecraft/saves"), instance.getSavesDir());
    }
    
    @Test
    void testInstanceState() {
        assertEquals(Instance.InstanceState.READY, instance.getState());
        
        instance.setState(Instance.InstanceState.RUNNING);
        assertEquals(Instance.InstanceState.RUNNING, instance.getState());
        
        instance.setState(Instance.InstanceState.ERROR);
        assertEquals(Instance.InstanceState.ERROR, instance.getState());
    }
    
    @Test
    void testPlayTime() {
        assertEquals(0, instance.getPlayTime());
        
        instance.setPlayTime(3600);
        assertEquals(3600, instance.getPlayTime());
    }
}