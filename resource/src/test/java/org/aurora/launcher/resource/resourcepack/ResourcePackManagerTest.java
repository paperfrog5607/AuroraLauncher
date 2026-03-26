package org.aurora.launcher.resource.resourcepack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ResourcePackManagerTest {
    
    @TempDir
    Path tempDir;
    
    private Path resourcePacksDir;
    private ResourcePackManager manager;
    
    @BeforeEach
    void setUp() throws Exception {
        resourcePacksDir = tempDir.resolve("resourcepacks");
        Files.createDirectories(resourcePacksDir);
        manager = new ResourcePackManager(resourcePacksDir);
    }
    
    @Test
    void scan_returnsPacks() {
        List<ResourcePack> packs = manager.scan().join();
        
        assertNotNull(packs);
    }
    
    @Test
    void enable_addsPackToEnabled() {
        manager.enable("test-pack").join();
        
        List<String> order = manager.getPackOrder();
        assertTrue(order.contains("test-pack"));
    }
    
    @Test
    void enable_noDuplicate() {
        manager.enable("test-pack").join();
        manager.enable("test-pack").join();
        
        List<String> order = manager.getPackOrder();
        assertEquals(1, order.size());
    }
    
    @Test
    void disable_removesFromEnabled() {
        manager.enable("test-pack").join();
        manager.disable("test-pack").join();
        
        List<String> order = manager.getPackOrder();
        assertFalse(order.contains("test-pack"));
    }
    
    @Test
    void moveUp_changesOrder() {
        manager.enable("pack1").join();
        manager.enable("pack2").join();
        manager.enable("pack3").join();
        
        manager.moveUp("pack3").join();
        
        List<String> order = manager.getPackOrder();
        assertEquals(1, order.indexOf("pack3"));
    }
    
    @Test
    void moveDown_changesOrder() {
        manager.enable("pack1").join();
        manager.enable("pack2").join();
        manager.enable("pack3").join();
        
        manager.moveDown("pack1").join();
        
        List<String> order = manager.getPackOrder();
        assertEquals(1, order.indexOf("pack1"));
    }
    
    @Test
    void importPack_copiesFile() throws Exception {
        Path source = tempDir.resolve("import.zip");
        Files.write(source, "test data".getBytes());
        
        manager.importPack(source).join();
        
        assertTrue(Files.exists(resourcePacksDir.resolve("import.zip")));
    }
    
    @Test
    void savePackOrder_updatesOrder() {
        manager.savePackOrder(Arrays.asList("pack3", "pack2", "pack1"));
        
        List<String> order = manager.getPackOrder();
        assertEquals(Arrays.asList("pack3", "pack2", "pack1"), order);
    }
}