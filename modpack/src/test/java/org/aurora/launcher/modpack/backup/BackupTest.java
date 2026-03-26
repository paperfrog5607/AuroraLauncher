package org.aurora.launcher.modpack.backup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BackupTest {
    
    private Backup backup;
    
    @BeforeEach
    void setUp() {
        backup = new Backup();
    }
    
    @Test
    void testBackupCreation() {
        backup.setId("backup-1");
        backup.setInstanceId("instance-1");
        backup.setName("Test Backup");
        
        assertEquals("backup-1", backup.getId());
        assertEquals("instance-1", backup.getInstanceId());
        assertEquals("Test Backup", backup.getName());
    }
    
    @Test
    void testBackupTypes() {
        backup.setType(Backup.BackupType.FULL);
        assertEquals(Backup.BackupType.FULL, backup.getType());
        
        backup.setType(Backup.BackupType.INCREMENTAL);
        assertEquals(Backup.BackupType.INCREMENTAL, backup.getType());
        
        backup.setType(Backup.BackupType.CONFIG_ONLY);
        assertEquals(Backup.BackupType.CONFIG_ONLY, backup.getType());
        
        backup.setType(Backup.BackupType.WORLD_ONLY);
        assertEquals(Backup.BackupType.WORLD_ONLY, backup.getType());
    }
    
    @Test
    void testFormattedSize() {
        backup.setSize(512);
        assertEquals("512 B", backup.getFormattedSize());
        
        backup.setSize(2048);
        assertEquals("2.0 KB", backup.getFormattedSize());
        
        backup.setSize(1048576);
        assertEquals("1.0 MB", backup.getFormattedSize());
        
        backup.setSize(1073741824);
        assertEquals("1.00 GB", backup.getFormattedSize());
    }
    
    @Test
    void testTimestamps() {
        Instant now = Instant.now();
        backup.setCreatedTime(now);
        
        assertEquals(now, backup.getCreatedTime());
    }
}