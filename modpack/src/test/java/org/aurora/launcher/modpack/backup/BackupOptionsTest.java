package org.aurora.launcher.modpack.backup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class BackupOptionsTest {
    
    @Test
    void testDefaultOptions() {
        BackupOptions options = new BackupOptions();
        
        assertEquals(Backup.BackupType.FULL, options.getType());
        assertTrue(options.isIncludeWorlds());
        assertTrue(options.isIncludeConfigs());
        assertTrue(options.isIncludeMods());
        assertFalse(options.isIncludeLogs());
        assertEquals(10, options.getMaxBackups());
    }
    
    @Test
    void testFullPreset() {
        BackupOptions options = BackupOptions.full();
        
        assertEquals(Backup.BackupType.FULL, options.getType());
        assertTrue(options.isIncludeWorlds());
        assertTrue(options.isIncludeConfigs());
        assertTrue(options.isIncludeMods());
    }
    
    @Test
    void testConfigOnlyPreset() {
        BackupOptions options = BackupOptions.configOnly();
        
        assertEquals(Backup.BackupType.CONFIG_ONLY, options.getType());
        assertFalse(options.isIncludeWorlds());
        assertTrue(options.isIncludeConfigs());
        assertFalse(options.isIncludeMods());
    }
    
    @Test
    void testWorldOnlyPreset() {
        BackupOptions options = BackupOptions.worldOnly();
        
        assertEquals(Backup.BackupType.WORLD_ONLY, options.getType());
        assertTrue(options.isIncludeWorlds());
        assertFalse(options.isIncludeConfigs());
        assertFalse(options.isIncludeMods());
    }
    
    @Test
    void testCustomOptions() {
        BackupOptions options = new BackupOptions();
        options.setType(Backup.BackupType.INCREMENTAL);
        options.setIncludeWorlds(false);
        options.setIncludeMods(false);
        options.setMaxBackups(5);
        options.setName("Custom Backup");
        options.setDescription("Test description");
        
        assertEquals(Backup.BackupType.INCREMENTAL, options.getType());
        assertFalse(options.isIncludeWorlds());
        assertFalse(options.isIncludeMods());
        assertEquals(5, options.getMaxBackups());
        assertEquals("Custom Backup", options.getName());
        assertEquals("Test description", options.getDescription());
    }
}