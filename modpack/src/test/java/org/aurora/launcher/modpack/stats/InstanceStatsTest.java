package org.aurora.launcher.modpack.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InstanceStatsTest {
    
    private InstanceStats stats;
    
    @BeforeEach
    void setUp() {
        stats = new InstanceStats();
    }
    
    @Test
    void testStatsCreation() {
        stats.setInstanceId("instance-1");
        stats.setInstanceName("Test Instance");
        stats.setModCount(50);
        stats.setResourcePackCount(2);
        stats.setShaderPackCount(1);
        stats.setWorldCount(3);
        
        assertEquals("instance-1", stats.getInstanceId());
        assertEquals("Test Instance", stats.getInstanceName());
        assertEquals(50, stats.getModCount());
        assertEquals(2, stats.getResourcePackCount());
        assertEquals(1, stats.getShaderPackCount());
        assertEquals(3, stats.getWorldCount());
    }
    
    @Test
    void testSizeCalculations() {
        stats.setModSize(1024 * 1024 * 100);
        stats.setConfigSize(1024 * 1024 * 10);
        stats.setWorldSize(1024 * 1024 * 50);
        stats.setTotalSize(1024 * 1024 * 160);
        
        assertEquals(1024 * 1024 * 100, stats.getModSize());
        assertEquals(1024 * 1024 * 10, stats.getConfigSize());
        assertEquals(1024 * 1024 * 50, stats.getWorldSize());
        assertEquals(1024 * 1024 * 160, stats.getTotalSize());
    }
    
    @Test
    void testFormattedTotalSize() {
        stats.setTotalSize(512);
        assertEquals("512 B", stats.getFormattedTotalSize());
        
        stats.setTotalSize(2048);
        assertEquals("2.0 KB", stats.getFormattedTotalSize());
        
        stats.setTotalSize(1048576);
        assertEquals("1.0 MB", stats.getFormattedTotalSize());
        
        stats.setTotalSize(1073741824);
        assertEquals("1.00 GB", stats.getFormattedTotalSize());
    }
    
    @Test
    void testFormattedPlayTime() {
        stats.setPlayTimeSeconds(30);
        assertEquals("30s", stats.getFormattedPlayTime());
        
        stats.setPlayTimeSeconds(120);
        assertEquals("2m 0s", stats.getFormattedPlayTime());
        
        stats.setPlayTimeSeconds(3661);
        assertEquals("1h 1m", stats.getFormattedPlayTime());
        
        stats.setPlayTimeSeconds(7325);
        assertEquals("2h 2m", stats.getFormattedPlayTime());
    }
    
    @Test
    void testModSizes() {
        stats.addModSize("mod1.jar", 1024);
        stats.addModSize("mod2.jar", 2048);
        
        assertEquals(2, stats.getModSizes().size());
        assertEquals(1024L, stats.getModSizes().get("mod1.jar").longValue());
        assertEquals(2048L, stats.getModSizes().get("mod2.jar").longValue());
    }
    
    @Test
    void testLaunchCount() {
        stats.setLaunchCount(42);
        assertEquals(42, stats.getLaunchCount());
    }
}