package org.aurora.launcher.resource.texture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.awt.Image;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TextureExtractorTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    void extractFromMod_nonExistent_returnsEmpty() {
        TextureExtractor extractor = new TextureExtractor();
        
        List<TexturePreview> textures = extractor.extractFromMod(tempDir.resolve("nonexistent.zip")).join();
        
        assertTrue(textures.isEmpty());
    }
    
    @Test
    void extractFromFolder_emptyDir_returnsEmpty() {
        TextureExtractor extractor = new TextureExtractor();
        
        List<TexturePreview> textures = extractor.extractFromFolder(tempDir).join();
        
        assertTrue(textures.isEmpty());
    }
    
    @Test
    void extractSingle_nonExistent_returnsNull() {
        TextureExtractor extractor = new TextureExtractor();
        
        Image image = extractor.extractSingle(tempDir.resolve("test.zip"), "test.png").join();
        
        assertNull(image);
    }
}