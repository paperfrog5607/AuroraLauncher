package org.aurora.launcher.resource.resourcepack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ResourcePackScannerTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    void scan_emptyDir_returnsEmptyList() {
        ResourcePackScanner scanner = new ResourcePackScanner();
        
        List<ResourcePack> packs = scanner.scan(tempDir).join();
        
        assertNotNull(packs);
        assertTrue(packs.isEmpty());
    }
    
    @Test
    void scan_nonExistentDir_returnsEmptyList() {
        ResourcePackScanner scanner = new ResourcePackScanner();
        
        List<ResourcePack> packs = scanner.scan(tempDir.resolve("nonexistent")).join();
        
        assertNotNull(packs);
        assertTrue(packs.isEmpty());
    }
    
    @Test
    void scan_zipFile_parsesPack() throws Exception {
        Path zipPath = tempDir.resolve("test.zip");
        createTestZip(zipPath, "pack.mcmeta", "{\"pack\":{\"pack_format\":34,\"description\":\"Test\"}}".getBytes());
        
        ResourcePackScanner scanner = new ResourcePackScanner();
        List<ResourcePack> packs = scanner.scan(tempDir).join();
        
        assertEquals(1, packs.size());
        assertEquals("test", packs.get(0).getId());
        assertEquals(34, packs.get(0).getPackFormat());
        assertEquals("Test", packs.get(0).getDescription());
        assertEquals(ResourcePack.ResourcePackType.ZIP, packs.get(0).getType());
    }
    
    @Test
    void scan_folder_parsesPack() throws Exception {
        Path folderPath = tempDir.resolve("test_folder");
        Files.createDirectories(folderPath);
        Files.write(folderPath.resolve("pack.mcmeta"), "{\"pack\":{\"pack_format\":22}}".getBytes());
        
        ResourcePackScanner scanner = new ResourcePackScanner();
        List<ResourcePack> packs = scanner.scan(tempDir).join();
        
        assertEquals(1, packs.size());
        assertEquals("test_folder", packs.get(0).getId());
        assertEquals(22, packs.get(0).getPackFormat());
        assertEquals(ResourcePack.ResourcePackType.FOLDER, packs.get(0).getType());
    }
    
    private void createTestZip(Path zipPath, String entryName, byte[] data) throws Exception {
        java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(Files.newOutputStream(zipPath));
        java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(entryName);
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
        zos.close();
    }
}