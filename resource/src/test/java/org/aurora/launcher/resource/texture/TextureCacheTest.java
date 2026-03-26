package org.aurora.launcher.resource.texture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;

class TextureCacheTest {
    
    private TextureCache cache;
    
    @BeforeEach
    void setUp() {
        cache = new TextureCache(10);
    }
    
    @Test
    void get_empty_returnsNull() {
        assertNull(cache.get(java.nio.file.Paths.get("test"), "texture"));
    }
    
    @Test
    void put_andGet_works() {
        java.awt.Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        java.nio.file.Path path = java.nio.file.Paths.get("test.zip");
        
        cache.put(path, "texture.png", image);
        
        assertNotNull(cache.get(path, "texture.png"));
    }
    
    @Test
    void contains_checksExistence() {
        java.awt.Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        java.nio.file.Path path = java.nio.file.Paths.get("test.zip");
        
        assertFalse(cache.contains(path, "texture.png"));
        
        cache.put(path, "texture.png", image);
        
        assertTrue(cache.contains(path, "texture.png"));
    }
    
    @Test
    void remove_deletesEntry() {
        java.awt.Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        java.nio.file.Path path = java.nio.file.Paths.get("test.zip");
        
        cache.put(path, "texture.png", image);
        cache.remove(path, "texture.png");
        
        assertNull(cache.get(path, "texture.png"));
    }
    
    @Test
    void clear_emptiesCache() {
        java.awt.Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        java.nio.file.Path path = java.nio.file.Paths.get("test.zip");
        
        cache.put(path, "texture1.png", image);
        cache.put(path, "texture2.png", image);
        cache.clear();
        
        assertEquals(0, cache.size());
    }
    
    @Test
    void size_returnsCount() {
        java.awt.Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        java.nio.file.Path path = java.nio.file.Paths.get("test.zip");
        
        assertEquals(0, cache.size());
        
        cache.put(path, "texture1.png", image);
        cache.put(path, "texture2.png", image);
        
        assertEquals(2, cache.size());
    }
    
    @Test
    void getMaxSize_returnsConfiguredSize() {
        assertEquals(10, cache.getMaxSize());
    }
}