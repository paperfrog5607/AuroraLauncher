package org.aurora.launcher.download.queue;

import org.aurora.launcher.download.core.DownloadRequest;
import org.aurora.launcher.download.core.DownloadTask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PriorityTaskTest {

    @Test
    void createWithTask() {
        DownloadRequest request = new DownloadRequest();
        request.setId("test-id");
        request.setPriority(10);
        
        DownloadTask task = new DownloadTask(request);
        PriorityTask priorityTask = new PriorityTask(task, 10);
        
        assertEquals(task, priorityTask.getTask());
        assertEquals(10, priorityTask.getPriority());
        assertEquals("test-id", priorityTask.getTaskId());
    }

    @Test
    void compareHigherPriority() {
        DownloadTask task1 = new DownloadTask(new DownloadRequest());
        DownloadRequest request2 = new DownloadRequest();
        request2.setPriority(5);
        DownloadTask task2 = new DownloadTask(request2);
        
        PriorityTask pt1 = new PriorityTask(task1, 10);
        PriorityTask pt2 = new PriorityTask(task2, 5);
        
        assertTrue(pt1.compareTo(pt2) < 0);
        assertTrue(pt2.compareTo(pt1) > 0);
    }

    @Test
    void compareSamePriority() {
        DownloadTask task1 = new DownloadTask(new DownloadRequest());
        DownloadTask task2 = new DownloadTask(new DownloadRequest());
        
        PriorityTask pt1 = new PriorityTask(task1, 5);
        PriorityTask pt2 = new PriorityTask(task2, 5);
        
        assertEquals(0, pt1.compareTo(pt2));
    }
}