package org.aurora.launcher.mod.scanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModInfoTest {

    @Test
    void shouldCreateModInfo() {
        ModInfo info = new ModInfo();
        info.setId("test-mod");
        info.setName("Test Mod");
        info.setVersion("1.0.0");
        info.setDescription("A test mod");
        
        assertEquals("test-mod", info.getId());
        assertEquals("Test Mod", info.getName());
        assertEquals("1.0.0", info.getVersion());
        assertEquals("A test mod", info.getDescription());
    }

    @Test
    void shouldHaveDefaultValues() {
        ModInfo info = new ModInfo();
        
        assertNotNull(info.getAuthors());
        assertNotNull(info.getDependencies());
        assertTrue(info.getAuthors().isEmpty());
        assertTrue(info.getDependencies().isEmpty());
    }

    @Test
    void shouldAddAuthors() {
        ModInfo info = new ModInfo();
        
        info.addAuthor("Author1");
        info.addAuthor("Author2");
        
        assertEquals(2, info.getAuthors().size());
        assertTrue(info.getAuthors().contains("Author1"));
    }

    @Test
    void shouldSetLoader() {
        ModInfo info = new ModInfo();
        
        info.setLoader(ModInfo.ModLoader.FABRIC);
        
        assertEquals(ModInfo.ModLoader.FABRIC, info.getLoader());
    }

    @Test
    void shouldHaveModLoaderEnum() {
        assertEquals(4, ModInfo.ModLoader.values().length);
        assertNotNull(ModInfo.ModLoader.valueOf("FABRIC"));
        assertNotNull(ModInfo.ModLoader.valueOf("FORGE"));
        assertNotNull(ModInfo.ModLoader.valueOf("QUILT"));
        assertNotNull(ModInfo.ModLoader.valueOf("NEOFORGE"));
    }
}