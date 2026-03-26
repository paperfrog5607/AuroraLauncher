package org.aurora.launcher.core.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import static org.junit.jupiter.api.Assertions.*;

class ZipUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void compress_createsZipFile() throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);
        Files.write(sourceDir.resolve("test.txt"), "hello".getBytes());
        
        Path zipPath = tempDir.resolve("test.zip");
        ZipUtils.compress(sourceDir, zipPath);
        
        assertTrue(Files.exists(zipPath));
    }

    @Test
    void extract_extractsZipFile() throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);
        Files.write(sourceDir.resolve("test.txt"), "hello".getBytes());
        
        Path zipPath = tempDir.resolve("test.zip");
        ZipUtils.compress(sourceDir, zipPath);
        
        Path extractDir = tempDir.resolve("extracted");
        ZipUtils.extract(zipPath, extractDir);
        
        assertTrue(Files.exists(extractDir.resolve("test.txt")));
        assertEquals("hello", new String(Files.readAllBytes(extractDir.resolve("test.txt"))));
    }

    @Test
    void listEntries_returnsEntries() throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir);
        Files.write(sourceDir.resolve("test.txt"), "hello".getBytes());
        
        Path zipPath = tempDir.resolve("test.zip");
        ZipUtils.compress(sourceDir, zipPath);
        
        List<String> entries = ZipUtils.listEntries(zipPath);
        
        assertFalse(entries.isEmpty());
    }
}