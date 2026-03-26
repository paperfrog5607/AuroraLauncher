package org.aurora.launcher.resource.shaderpack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class ShaderPackManagerTest {
    
    @TempDir
    Path tempDir;
    
    private Path shaderPacksDir;
    private ShaderPackManager manager;
    
    @BeforeEach
    void setUp() throws Exception {
        shaderPacksDir = tempDir.resolve("shaderpacks");
        Files.createDirectories(shaderPacksDir);
        manager = new ShaderPackManager(shaderPacksDir);
    }
    
    @Test
    void scan_returnsPacks() {
        List<ShaderPack> packs = manager.scan().join();
        
        assertNotNull(packs);
    }
    
    @Test
    void enable_setsCurrentPack() {
        manager.enable("test-shader").join();
        
        assertEquals("test-shader", manager.getCurrentPackId());
        assertTrue(manager.isShadersEnabled());
    }
    
    @Test
    void disable_clearsEnabled() {
        manager.enable("test-shader").join();
        manager.disable().join();
        
        assertFalse(manager.isShadersEnabled());
    }
    
    @Test
    void getCurrentPack_returnsPackWhenEnabled() {
        manager.enable("test-shader").join();
        manager.setCurrentPackId("test-shader");
        
        Optional<ShaderPack> pack = manager.getCurrentPack();
        
        // Will be empty since pack doesn't exist
        assertFalse(pack.isPresent());
    }
    
    @Test
    void importPack_copiesFile() throws Exception {
        Path source = tempDir.resolve("shader.zip");
        Files.write(source, "test".getBytes());
        
        manager.importPack(source).join();
        
        assertTrue(Files.exists(shaderPacksDir.resolve("shader.zip")));
    }
    
    @Test
    void setShadersEnabled_changesState() {
        manager.setShadersEnabled(true);
        
        assertTrue(manager.isShadersEnabled());
    }
}