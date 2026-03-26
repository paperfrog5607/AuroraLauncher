package org.aurora.launcher.resource.texture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextureManagerTest {
    
    private TextureManager manager;
    
    @BeforeEach
    void setUp() {
        manager = new TextureManager(50);
    }
    
    @Test
    void getCacheMaxSize_returnsConfiguredSize() {
        assertEquals(50, manager.getCacheMaxSize());
    }
    
    @Test
    void clearCache_emptiesCache() {
        manager.clearCache();
        
        assertEquals(0, manager.getCacheSize());
    }
    
    @Test
    void getCacheSize_initiallyZero() {
        assertEquals(0, manager.getCacheSize());
    }
}