package org.aurora.launcher.download.queue;

import org.aurora.launcher.download.core.DownloadRequest;
import org.aurora.launcher.download.core.DownloadTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DownloadQueueTest {

    private DownloadQueue queue;

    @BeforeEach
    void setUp() {
        queue = new DownloadQueue(4);
    }

    @Test
    void emptyQueue() {
        assertEquals(0, queue.getQueueSize());
        assertEquals(0, queue.getActiveCount());
        assertTrue(queue.getQueuedTasks().isEmpty());
        assertTrue(queue.getActiveTasks().isEmpty());
    }

    @Test
    void submitTask() {
        DownloadRequest request = new DownloadRequest();
        request.setId("test-id");
        DownloadTask task = new DownloadTask(request);
        
        queue.submit(task);
        
        assertEquals(1, queue.getQueueSize());
        List<DownloadTask> queued = queue.getQueuedTasks();
        assertEquals(1, queued.size());
        assertEquals("test-id", queued.get(0).getId());
    }

    @Test
    void submitMultipleTasks() {
        DownloadRequest req1 = new DownloadRequest();
        req1.setId("task1");
        DownloadRequest req2 = new DownloadRequest();
        req2.setId("task2");
        DownloadRequest req3 = new DownloadRequest();
        req3.setId("task3");
        
        DownloadTask task1 = new DownloadTask(req1);
        DownloadTask task2 = new DownloadTask(req2);
        DownloadTask task3 = new DownloadTask(req3);
        
        queue.submitAll(Arrays.asList(task1, task2, task3));
        
        assertEquals(3, queue.getQueueSize());
    }

    @Test
    void cancelQueuedTask() {
        DownloadRequest request = new DownloadRequest();
        request.setId("cancel-id");
        DownloadTask task = new DownloadTask(request);
        
        queue.submit(task);
        assertEquals(1, queue.getQueueSize());
        
        queue.cancel("cancel-id");
        
        assertEquals(0, queue.getQueueSize());
    }

    @Test
    void pauseAndResume() {
        DownloadRequest request = new DownloadRequest();
        request.setId("pause-id");
        DownloadTask task = new DownloadTask(request);
        
        queue.submit(task);
        queue.pause("pause-id");
        
        queue.resume("pause-id");
    }

    @Test
    void maxConcurrent() {
        assertEquals(4, queue.getMaxConcurrent());
        
        queue.setMaxConcurrent(8);
        assertEquals(8, queue.getMaxConcurrent());
    }

    @Test
    void shutdown() {
        DownloadRequest req = new DownloadRequest();
        req.setId("task1");
        DownloadTask task = new DownloadTask(req);
        queue.submit(task);
        
        queue.shutdown();
        
        assertEquals(0, queue.getQueueSize());
    }
}