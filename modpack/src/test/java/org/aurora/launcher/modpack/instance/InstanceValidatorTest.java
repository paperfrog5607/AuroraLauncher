package org.aurora.launcher.modpack.instance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InstanceValidatorTest {
    
    private InstanceValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new InstanceValidator();
    }
    
    @Test
    void testValidInstance() {
        Instance instance = new Instance();
        instance.setId("test-id");
        instance.setName("Test Instance");
        instance.setInstanceDir(java.nio.file.Paths.get("/tmp/test"));
        
        InstanceConfig config = new InstanceConfig();
        config.setMinecraftVersion("1.20.4");
        config.setLoaderType("vanilla");
        instance.setConfig(config);
        
        InstanceValidator.ValidationResult result = validator.validate(instance);
        
        assertTrue(result.isValid());
    }
    
    @Test
    void testNullInstance() {
        InstanceValidator.ValidationResult result = validator.validate(null);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Instance is null"));
    }
    
    @Test
    void testMissingId() {
        Instance instance = new Instance();
        instance.setName("Test");
        instance.setConfig(new InstanceConfig());
        instance.getConfig().setMinecraftVersion("1.20.4");
        
        InstanceValidator.ValidationResult result = validator.validate(instance);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("ID")));
    }
    
    @Test
    void testMissingName() {
        Instance instance = new Instance();
        instance.setId("test-id");
        instance.setConfig(new InstanceConfig());
        instance.getConfig().setMinecraftVersion("1.20.4");
        
        InstanceValidator.ValidationResult result = validator.validate(instance);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("name")));
    }
    
    @Test
    void testMissingMinecraftVersion() {
        Instance instance = new Instance();
        instance.setId("test-id");
        instance.setName("Test");
        instance.setConfig(new InstanceConfig());
        
        InstanceValidator.ValidationResult result = validator.validate(instance);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("Minecraft version")));
    }
    
    @Test
    void testMissingLoaderVersionForModded() {
        Instance instance = new Instance();
        instance.setId("test-id");
        instance.setName("Test");
        instance.setConfig(new InstanceConfig());
        instance.getConfig().setMinecraftVersion("1.20.4");
        instance.getConfig().setLoaderType("fabric");
        
        InstanceValidator.ValidationResult result = validator.validate(instance);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("Loader version")));
    }
    
    @Test
    void testInvalidMemoryConfig() {
        Instance instance = new Instance();
        instance.setId("test-id");
        instance.setName("Test");
        instance.setConfig(new InstanceConfig());
        instance.getConfig().setMinecraftVersion("1.20.4");
        instance.getConfig().getMemory().setMinMB(8192);
        instance.getConfig().getMemory().setMaxMB(4096);
        
        InstanceValidator.ValidationResult result = validator.validate(instance);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("memory")));
    }
}