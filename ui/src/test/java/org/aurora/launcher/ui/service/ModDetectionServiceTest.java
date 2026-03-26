package org.aurora.launcher.ui.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ModDetectionServiceTest {
    
    private ModDetectionService service;
    
    @BeforeEach
    void setUp() {
        service = ModDetectionService.getInstance();
    }
    
    @Test
    void testGetInstanceReturnsSameInstance() {
        ModDetectionService instance1 = ModDetectionService.getInstance();
        ModDetectionService instance2 = ModDetectionService.getInstance();
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }
    
    @Test
    void testIsModInstalledReturnsFalseWhenNoModsScanned() {
        boolean result = service.isModInstalled("some-mod");
        assertFalse(result, "No mods should be installed before scan");
    }
    
    @Test
    void testGetInstalledModsReturnsEmptySetWhenNoScan() {
        Set<String> mods = service.getInstalledMods();
        assertNotNull(mods);
        assertTrue(mods.isEmpty(), "Should return empty set when no mods scanned");
    }
    
    @Test
    void testGetModInfoReturnsEmptyOptionalWhenNoScan() {
        Optional<ModDetectionService.ModInfo> info = service.getModInfo("any-mod");
        assertTrue(info.isEmpty(), "Should return empty Optional when mod not found");
    }
    
    @Test
    void testSetModVisibilityAndIsModVisible() {
        String modId = "test-mod";
        service.setModVisibility(modId, false);
        assertFalse(service.isModVisible(modId), "Mod visibility should be false after setting");
        
        service.setModVisibility(modId, true);
        assertTrue(service.isModVisible(modId), "Mod visibility should be true after setting");
    }
    
    @Test
    void testIsModVisibleReturnsDefaultTrueForUnknownMod() {
        boolean visible = service.isModVisible("unknown-mod");
        assertTrue(visible, "Should return true by default for unknown mods");
    }
    
    @Test
    void testGetVisibilitySettingsReturnsMap() {
        service.setModVisibility("mod1", false);
        service.setModVisibility("mod2", true);
        
        var settings = service.getVisibilitySettings();
        assertNotNull(settings);
        assertFalse(settings.get("mod1"), "mod1 should be false");
        assertTrue(settings.get("mod2"), "mod2 should be true");
    }
    
    @Test
    void testGetCurrentInstancePathReturnsNullBeforeScan() {
        Path path = service.getCurrentInstancePath();
        assertNull(path, "Current instance path should be null before scan");
    }
}