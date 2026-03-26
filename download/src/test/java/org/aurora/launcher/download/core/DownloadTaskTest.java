package org.aurora.launcher.download.core;

import org.aurora.launcher.download.progress.ProgressCallback;
import org.aurora.launcher.download.progress.ProgressEvent;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DownloadTaskTest {

    @Test
    void createFromRequest() {
        DownloadRequest request = new DownloadRequest();
        request.setId("test-id");
        request.setUrl("https://example.com/file.zip");
        request.setTargetPath(Paths.get("/tmp/file.zip"));

        DownloadTask task = new DownloadTask(request);

        assertEquals("test-id", task.getId());
        assertEquals(request, task.getRequest());
        assertEquals(DownloadStatus.PENDING, task.getStatus());
        assertEquals(0, task.getDownloadedBytes());
        assertEquals(0, task.getTotalBytes());
        assertEquals(0, task.getRetryCount());
    }

    @Test
    void statusTransitions() {
        DownloadRequest request = new DownloadRequest();
        DownloadTask task = new DownloadTask(request);

        assertEquals(DownloadStatus.PENDING, task.getStatus());

        task.setStatus(DownloadStatus.DOWNLOADING);
        assertEquals(DownloadStatus.DOWNLOADING, task.getStatus());

        task.pause();
        assertEquals(DownloadStatus.PAUSED, task.getStatus());

        task.resume();
        assertEquals(DownloadStatus.DOWNLOADING, task.getStatus());

        task.cancel();
        assertEquals(DownloadStatus.CANCELLED, task.getStatus());
    }

    @Test
    void progressTracking() {
        DownloadRequest request = new DownloadRequest();
        DownloadTask task = new DownloadTask(request);

        task.setTotalBytes(1000);
        task.setDownloadedBytes(500);

        assertEquals(1000, task.getTotalBytes());
        assertEquals(500, task.getDownloadedBytes());
    }

    @Test
    void retryCount() {
        DownloadRequest request = new DownloadRequest();
        DownloadTask task = new DownloadTask(request);

        assertEquals(0, task.getRetryCount());
        task.incrementRetryCount();
        assertEquals(1, task.getRetryCount());
        task.incrementRetryCount();
        assertEquals(2, task.getRetryCount());
    }

    @Test
    void callbacks() {
        DownloadRequest request = new DownloadRequest();
        DownloadTask task = new DownloadTask(request);

        List<String> events = new ArrayList<>();
        ProgressCallback callback = new ProgressCallback() {
            @Override
            public void onProgress(ProgressEvent event) {
                events.add("progress");
            }

            @Override
            public void onComplete(org.aurora.launcher.download.core.DownloadResult result) {
                events.add("complete");
            }

            @Override
            public void onError(Exception error) {
                events.add("error");
            }
        };

        task.addCallback(callback);
        task.notifyCallbacks(new ProgressEvent(100, 1000, 100, null));

        assertEquals(1, events.size());
        assertEquals("progress", events.get(0));
    }

    @Test
    void priorityFromRequest() {
        DownloadRequest request = new DownloadRequest();
        request.setPriority(10);

        DownloadTask task = new DownloadTask(request);
        assertEquals(10, task.getPriority());
    }
}