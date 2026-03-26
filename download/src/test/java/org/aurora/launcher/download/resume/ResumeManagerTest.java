package org.aurora.launcher.download.resume;

import org.aurora.launcher.download.core.DownloadRequest;
import org.aurora.launcher.download.core.DownloadTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class ResumeManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void saveAndLoadRecord() {
        ResumeManager manager = new ResumeManager(tempDir);
        DownloadRequest request = new DownloadRequest();
        request.setId("test-id");
        request.setUrl("https://example.com/file.zip");
        request.setTargetPath(tempDir.resolve("file.zip"));
        
        DownloadTask task = new DownloadTask(request);
        task.setTotalBytes(1000);
        task.setDownloadedBytes(500);
        
        manager.saveRecord(task);
        
        Optional<ResumeRecord> loaded = manager.loadRecord("test-id");
        
        assertTrue(loaded.isPresent());
        assertEquals("test-id", loaded.get().getId());
        assertEquals("https://example.com/file.zip", loaded.get().getUrl());
        assertEquals(1000, loaded.get().getTotalSize());
        assertEquals(500, loaded.get().getDownloadedSize());
    }

    @Test
    void loadNonExistentRecord() {
        ResumeManager manager = new ResumeManager(tempDir);
        
        Optional<ResumeRecord> loaded = manager.loadRecord("non-existent");
        
        assertFalse(loaded.isPresent());
    }

    @Test
    void deleteRecord() {
        ResumeManager manager = new ResumeManager(tempDir);
        DownloadRequest request = new DownloadRequest();
        request.setId("delete-id");
        request.setUrl("https://example.com/file.zip");
        request.setTargetPath(tempDir.resolve("file.zip"));
        
        DownloadTask task = new DownloadTask(request);
        task.setTotalBytes(1000);
        
        manager.saveRecord(task);
        assertTrue(manager.loadRecord("delete-id").isPresent());
        
        manager.deleteRecord("delete-id");
        assertFalse(manager.loadRecord("delete-id").isPresent());
    }
}