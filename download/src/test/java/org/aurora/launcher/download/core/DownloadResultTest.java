package org.aurora.launcher.download.core;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

class DownloadResultTest {

    @Test
    void defaultValues() {
        DownloadResult result = new DownloadResult();
        assertNull(result.getId());
        assertNull(result.getStatus());
        assertNull(result.getFilePath());
        assertEquals(0, result.getBytesDownloaded());
        assertEquals(0, result.getTotalBytes());
        assertEquals(0, result.getDuration());
        assertEquals(0, result.getAverageSpeed());
        assertNull(result.getError());
    }

    @Test
    void settersAndGetters() {
        DownloadResult result = new DownloadResult();
        Path path = Paths.get("/tmp/file.zip");

        result.setId("test-id");
        result.setStatus(DownloadStatus.COMPLETED);
        result.setFilePath(path);
        result.setBytesDownloaded(1024);
        result.setTotalBytes(2048);
        result.setDuration(5000);
        result.setAverageSpeed(204);
        result.setError("test error");

        assertEquals("test-id", result.getId());
        assertEquals(DownloadStatus.COMPLETED, result.getStatus());
        assertEquals(path, result.getFilePath());
        assertEquals(1024, result.getBytesDownloaded());
        assertEquals(2048, result.getTotalBytes());
        assertEquals(5000, result.getDuration());
        assertEquals(204, result.getAverageSpeed());
        assertEquals("test error", result.getError());
    }

    @Test
    void downloadStatusEnum() {
        assertEquals(6, DownloadStatus.values().length);
        assertEquals(DownloadStatus.PENDING, DownloadStatus.valueOf("PENDING"));
        assertEquals(DownloadStatus.DOWNLOADING, DownloadStatus.valueOf("DOWNLOADING"));
        assertEquals(DownloadStatus.PAUSED, DownloadStatus.valueOf("PAUSED"));
        assertEquals(DownloadStatus.COMPLETED, DownloadStatus.valueOf("COMPLETED"));
        assertEquals(DownloadStatus.FAILED, DownloadStatus.valueOf("FAILED"));
        assertEquals(DownloadStatus.CANCELLED, DownloadStatus.valueOf("CANCELLED"));
    }
}