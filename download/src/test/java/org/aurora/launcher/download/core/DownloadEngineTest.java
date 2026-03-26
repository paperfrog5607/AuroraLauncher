package org.aurora.launcher.download.core;

import org.aurora.launcher.download.config.DownloadConfig;
import org.aurora.launcher.download.queue.DownloadQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;

class DownloadEngineTest {

    private DownloadEngine engine;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        DownloadConfig config = new DownloadConfig();
        config.setTempDir(tempDir);
        engine = new DownloadEngine(config);
    }

    @Test
    void createEngine() {
        assertNotNull(engine);
        assertEquals(0, engine.getActiveCount());
        assertEquals(0, engine.getQueuedCount());
    }

    @Test
    void downloadWithInvalidUrl() {
        DownloadRequest request = new DownloadRequest();
        request.setId("test-id");
        request.setUrl("invalid-url");
        request.setTargetPath(tempDir.resolve("test.txt"));
        
        CompletableFuture<DownloadResult> future = engine.download(request);
        DownloadResult result = future.join();
        
        assertEquals(DownloadStatus.FAILED, result.getStatus());
        assertNotNull(result.getError());
    }

    @Test
    void cancelNonExistentTask() {
        assertDoesNotThrow(() -> engine.cancel("non-existent"));
    }

    @Test
    void cancelAll() {
        assertDoesNotThrow(() -> engine.cancelAll());
    }

    @Test
    void setMaxConcurrent() {
        engine.setMaxConcurrent(8);
        assertEquals(8, engine.getMaxConcurrent());
    }

    @Test
    void pauseNonExistentTask() {
        assertDoesNotThrow(() -> engine.pause("non-existent"));
    }

    @Test
    void resumeNonExistentTask() {
        assertDoesNotThrow(() -> engine.resume("non-existent"));
    }

    @Test
    void shutdown() {
        assertDoesNotThrow(() -> engine.shutdown());
    }

    @Test
    void getConfig() {
        assertNotNull(engine.getConfig());
    }

    @Test
    void getQueue() {
        DownloadQueue queue = engine.getQueue();
        assertNotNull(queue);
    }
}