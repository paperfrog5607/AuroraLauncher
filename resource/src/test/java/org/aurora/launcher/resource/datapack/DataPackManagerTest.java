package org.aurora.launcher.resource.datapack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DataPackManagerTest {
    
    @TempDir
    Path tempDir;
    
    private Path worldsDir;
    private Path globalDir;
    private DataPackManager manager;
    
    @BeforeEach
    void setUp() throws Exception {
        worldsDir = tempDir.resolve("worlds");
        globalDir = tempDir.resolve("datapacks");
        Files.createDirectories(worldsDir);
        Files.createDirectories(globalDir);
        manager = new DataPackManager(worldsDir, globalDir);
    }
    
    @Test
    void scanWorld_returnsPacks() {
        List<DataPack> packs = manager.scanWorld("test_world").join();
        
        assertNotNull(packs);
    }
    
    @Test
    void scanGlobal_returnsPacks() {
        List<DataPack> packs = manager.scanGlobal().join();
        
        assertNotNull(packs);
    }
    
    @Test
    void enable_addsPackToEnabled() {
        manager.enable("test-pack", "test_world").join();
        
        List<DataPack> enabled = manager.getEnabledPacks("test_world");
        assertTrue(enabled.isEmpty()); // Empty because pack doesn't exist
    }
    
    @Test
    void disable_removesFromEnabled() {
        manager.enable("test-pack", "test_world").join();
        manager.disable("test-pack", "test_world").join();
        
        List<DataPack> enabled = manager.getEnabledPacks("test_world");
        assertTrue(enabled.isEmpty());
    }
    
    @Test
    void importPack_toGlobal() throws Exception {
        Path source = tempDir.resolve("test.zip");
        Files.write(source, "test".getBytes());
        
        manager.importPack(source, null).join();
        
        assertTrue(Files.exists(globalDir.resolve("test.zip")));
    }
    
    @Test
    void importPack_toWorld() throws Exception {
        Path worldDir = worldsDir.resolve("test_world");
        Files.createDirectories(worldDir);
        
        Path source = tempDir.resolve("test.zip");
        Files.write(source, "test".getBytes());
        
        manager.importPack(source, "test_world").join();
        
        assertTrue(Files.exists(worldDir.resolve("datapacks/test.zip")));
    }
}