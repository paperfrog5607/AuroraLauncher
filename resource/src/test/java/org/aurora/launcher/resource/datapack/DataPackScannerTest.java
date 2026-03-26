package org.aurora.launcher.resource.datapack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DataPackScannerTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    void scanWorld_emptyDir_returnsEmptyList() {
        DataPackScanner scanner = new DataPackScanner();
        
        List<DataPack> packs = scanner.scanWorld("test", tempDir).join();
        
        assertTrue(packs.isEmpty());
    }
    
    @Test
    void scanGlobal_emptyDir_returnsEmptyList() {
        DataPackScanner scanner = new DataPackScanner();
        
        List<DataPack> packs = scanner.scanGlobal(tempDir).join();
        
        assertTrue(packs.isEmpty());
    }
    
    @Test
    void scanGlobal_withZipFile_detectsPack() throws Exception {
        Path zipPath = tempDir.resolve("data.zip");
        createTestZip(zipPath, "pack.mcmeta", "{\"pack\":{\"pack_format\":10}}".getBytes());
        
        DataPackScanner scanner = new DataPackScanner();
        List<DataPack> packs = scanner.scanGlobal(tempDir).join();
        
        assertEquals(1, packs.size());
        assertEquals("data", packs.get(0).getId());
        assertEquals(10, packs.get(0).getPackFormat());
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