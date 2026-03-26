package org.aurora.launcher.resource.shaderpack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ShaderPackScannerTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    void scan_emptyDir_returnsEmptyList() throws Exception {
        ShaderPackScanner scanner = new ShaderPackScanner();
        
        List<ShaderPack> packs = scanner.scan(tempDir);
        
        assertTrue(packs.isEmpty());
    }
    
    @Test
    void scan_nonExistentDir_returnsEmptyList() throws Exception {
        ShaderPackScanner scanner = new ShaderPackScanner();
        
        List<ShaderPack> packs = scanner.scan(tempDir.resolve("nonexistent"));
        
        assertTrue(packs.isEmpty());
    }
    
    @Test
    void scan_zipFile_detectsPack() throws Exception {
        Path zipPath = tempDir.resolve("shader.zip");
        createTestZip(zipPath);
        
        ShaderPackScanner scanner = new ShaderPackScanner();
        List<ShaderPack> packs = scanner.scan(tempDir);
        
        assertEquals(1, packs.size());
        assertEquals("shader", packs.get(0).getId());
        assertEquals(ShaderPack.ShaderPackType.VANILLA, packs.get(0).getType());
    }
    
    private void createTestZip(Path zipPath) throws Exception {
        java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(Files.newOutputStream(zipPath));
        zos.close();
    }
}