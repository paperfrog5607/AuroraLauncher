package org.aurora.launcher.modpack.instance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class InstanceBuilderTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    void testBasicBuild() {
        Instance instance = new InstanceBuilder()
                .name("Test Modpack")
                .version("1.0.0")
                .minecraftVersion("1.20.4")
                .loaderType("fabric")
                .loaderVersion("0.15.7")
                .build();
        
        assertNotNull(instance);
        assertNotNull(instance.getId());
        assertEquals("Test Modpack", instance.getName());
        assertEquals("1.0.0", instance.getVersion());
        assertEquals("1.20.4", instance.getConfig().getMinecraftVersion());
        assertEquals("fabric", instance.getConfig().getLoaderType());
        assertEquals("0.15.7", instance.getConfig().getLoaderVersion());
    }
    
    @Test
    void testBuildWithTags() {
        Instance instance = new InstanceBuilder()
                .name("Tech Modpack")
                .addTag("tech")
                .addTag("hardcore")
                .build();
        
        assertEquals(2, instance.getTags().size());
        assertTrue(instance.getTags().contains("tech"));
        assertTrue(instance.getTags().contains("hardcore"));
    }
    
    @Test
    void testBuildWithInstanceDir() {
        Path instanceDir = tempDir.resolve("my-instance");
        
        Instance instance = new InstanceBuilder()
                .name("My Instance")
                .instanceDir(instanceDir)
                .build();
        
        assertEquals(instanceDir, instance.getInstanceDir());
    }
    
    @Test
    void testBuildWithMemoryConfig() {
        InstanceConfig.MemoryConfig memoryConfig = new InstanceConfig.MemoryConfig();
        memoryConfig.setMinMB(4096);
        memoryConfig.setMaxMB(8192);
        
        Instance instance = new InstanceBuilder()
                .name("Memory Test")
                .memoryConfig(memoryConfig)
                .build();
        
        assertEquals(4096, instance.getConfig().getMemory().getMinMB());
        assertEquals(8192, instance.getConfig().getMemory().getMaxMB());
    }
    
    @Test
    void testBuildWithCustomId() {
        Instance instance = new InstanceBuilder()
                .id("custom-id-123")
                .name("Custom ID Instance")
                .build();
        
        assertEquals("custom-id-123", instance.getId());
    }
    
    @Test
    void testBuildWithVanillaLoader() {
        Instance instance = new InstanceBuilder()
                .name("Vanilla Instance")
                .minecraftVersion("1.20.4")
                .loaderType("vanilla")
                .build();
        
        assertEquals("vanilla", instance.getConfig().getLoaderType());
        assertEquals(ModLoaderInfo.LoaderType.VANILLA, instance.getLoader().getType());
    }
}