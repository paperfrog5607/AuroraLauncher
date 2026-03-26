package org.aurora.launcher.mod.scanner;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ModFileTest {

    @Test
    void shouldCreateModFile() {
        Path path = Paths.get("/mods/test.jar");
        ModFile modFile = new ModFile(path);
        
        assertEquals(path, modFile.getFile());
        assertEquals("test.jar", modFile.getFileName());
        assertTrue(modFile.isEnabled());
    }

    @Test
    void shouldDetectDisabledFile() {
        Path path = Paths.get("/mods/test.jar.disabled");
        ModFile modFile = new ModFile(path);
        
        assertFalse(modFile.isEnabled());
    }

    @Test
    void shouldSetProperties() {
        ModFile modFile = new ModFile(Paths.get("/test.jar"));
        
        modFile.setFileSize(1024);
        modFile.setSha1("abc123");
        modFile.setLastModified(Instant.now());
        
        assertEquals(1024, modFile.getFileSize());
        assertEquals("abc123", modFile.getSha1());
        assertNotNull(modFile.getLastModified());
    }

    @Test
    void shouldCheckEnabledStatus() {
        ModFile enabled = new ModFile(Paths.get("test.jar"));
        ModFile disabled = new ModFile(Paths.get("test.jar.disabled"));
        
        assertTrue(enabled.isEnabled());
        assertFalse(disabled.isEnabled());
    }
}