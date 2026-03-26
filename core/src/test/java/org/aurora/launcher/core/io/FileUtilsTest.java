package org.aurora.launcher.core.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void copyDirectory_copiesAllFiles() throws Exception {
        Path source = tempDir.resolve("source");
        Path target = tempDir.resolve("target");
        Files.createDirectories(source);
        Files.write(source.resolve("test.txt"), "hello".getBytes());

        FileUtils.copyDirectory(source, target);

        assertTrue(Files.exists(target.resolve("test.txt")));
        assertEquals("hello", new String(Files.readAllBytes(target.resolve("test.txt"))));
    }

    @Test
    void deleteDirectory_deletesAllFiles() throws Exception {
        Path dir = tempDir.resolve("toDelete");
        Files.createDirectories(dir);
        Files.write(dir.resolve("test.txt"), "hello".getBytes());

        FileUtils.deleteDirectory(dir);

        assertFalse(Files.exists(dir));
    }

    @Test
    void getDirectorySize_returnsCorrectSize() throws Exception {
        Path dir = tempDir.resolve("sizeTest");
        Files.createDirectories(dir);
        Files.write(dir.resolve("test.txt"), "hello".getBytes());

        long size = FileUtils.getDirectorySize(dir);

        assertEquals(5, size);
    }

    @Test
    void listFiles_returnsMatchingFiles() throws Exception {
        Path dir = tempDir.resolve("listTest");
        Files.createDirectories(dir);
        Files.write(dir.resolve("test.txt"), "hello".getBytes());
        Files.write(dir.resolve("test.json"), "{}".getBytes());

        List<Path> files = FileUtils.listFiles(dir, ".txt");

        assertEquals(1, files.size());
        assertTrue(files.get(0).toString().endsWith("test.txt"));
    }

    @Test
    void createDirectoryIfNotExists_createsDirectory() throws Exception {
        Path newDir = tempDir.resolve("newDir");

        FileUtils.createDirectoryIfNotExists(newDir);

        assertTrue(Files.exists(newDir));
    }

    @Test
    void readAllText_returnsFileContent() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "hello world".getBytes());

        String content = FileUtils.readAllText(file);

        assertEquals("hello world", content);
    }

    @Test
    void writeAllText_writesContent() throws Exception {
        Path file = tempDir.resolve("test.txt");

        FileUtils.writeAllText(file, "hello world");

        assertEquals("hello world", new String(Files.readAllBytes(file)));
    }

    @Test
    void readAllBytes_returnsFileBytes() throws Exception {
        Path file = tempDir.resolve("test.bin");
        byte[] data = new byte[]{1, 2, 3, 4, 5};
        Files.write(file, data);

        byte[] result = FileUtils.readAllBytes(file);

        assertArrayEquals(data, result);
    }

    @Test
    void writeAllBytes_writesBytes() throws Exception {
        Path file = tempDir.resolve("test.bin");
        byte[] data = new byte[]{1, 2, 3, 4, 5};

        FileUtils.writeAllBytes(file, data);

        assertArrayEquals(data, Files.readAllBytes(file));
    }
}