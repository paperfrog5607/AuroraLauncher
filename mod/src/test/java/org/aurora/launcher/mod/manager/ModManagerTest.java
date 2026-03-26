package org.aurora.launcher.mod.manager;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ModManagerTest {

    @Test
    void shouldCreateModManager() {
        ModManager manager = new ModManager(Paths.get("mods"));
        
        assertNotNull(manager);
        assertEquals(Paths.get("mods"), manager.getModsDir());
    }

    @Test
    void shouldReturnEmptyListWhenNotScanned() {
        ModManager manager = new ModManager(Paths.get("mods"));
        
        assertTrue(manager.getAllMods().isEmpty());
        assertTrue(manager.getEnabledMods().isEmpty());
        assertTrue(manager.getDisabledMods().isEmpty());
    }
}