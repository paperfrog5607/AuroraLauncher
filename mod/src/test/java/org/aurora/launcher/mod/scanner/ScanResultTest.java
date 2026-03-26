package org.aurora.launcher.mod.scanner;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScanResultTest {

    @Test
    void shouldCreateScanResult() {
        ScanResult result = new ScanResult();
        
        assertNotNull(result.getMods());
        assertNotNull(result.getDisabledMods());
        assertNotNull(result.getInvalidMods());
        assertNotNull(result.getErrors());
        assertTrue(result.getMods().isEmpty());
    }

    @Test
    void shouldAddMods() {
        ScanResult result = new ScanResult();
        ModInfo mod = new ModInfo();
        mod.setId("test");
        
        result.addMod(mod);
        
        assertEquals(1, result.getMods().size());
        assertEquals("test", result.getMods().get(0).getId());
    }

    @Test
    void shouldSetScanTime() {
        ScanResult result = new ScanResult();
        
        result.setScanTime(java.time.Instant.now());
        
        assertNotNull(result.getScanTime());
    }

    @Test
    void shouldCountMods() {
        ScanResult result = new ScanResult();
        
        result.addMod(new ModInfo());
        result.addMod(new ModInfo());
        result.addDisabledMod(java.nio.file.Paths.get("test.jar.disabled"));
        
        assertEquals(2, result.getMods().size());
        assertEquals(1, result.getDisabledMods().size());
    }
}