package org.aurora.launcher.download.progress;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class ProgressTrackerTest {

    @Test
    void initialValues() {
        ProgressTracker tracker = new ProgressTracker(1000);
        
        assertEquals(1000, tracker.getTotalBytes());
        assertEquals(0, tracker.getDownloadedBytes());
        assertEquals(0.0, tracker.getProgress());
    }

    @Test
    void updateProgress() {
        ProgressTracker tracker = new ProgressTracker(1000);
        
        tracker.update(500);
        
        assertEquals(500, tracker.getDownloadedBytes());
        assertEquals(0.5, tracker.getProgress(), 0.001);
    }

    @Test
    void progressWithZeroTotal() {
        ProgressTracker tracker = new ProgressTracker(0);
        
        assertEquals(0.0, tracker.getProgress());
        
        tracker.update(100);
        assertEquals(0.0, tracker.getProgress());
    }

    @Test
    void callbackNotification() {
        ProgressTracker tracker = new ProgressTracker(1000);
        AtomicReference<ProgressEvent> receivedEvent = new AtomicReference<>();
        
        tracker.addCallback(new ProgressCallback() {
            @Override
            public void onProgress(ProgressEvent event) {
                receivedEvent.set(event);
            }

            @Override
            public void onComplete(org.aurora.launcher.download.core.DownloadResult result) {
            }

            @Override
            public void onError(Exception error) {
            }
        });
        
        tracker.update(200);
        
        assertNotNull(receivedEvent.get());
        assertEquals(200, receivedEvent.get().getDownloadedBytes());
        assertEquals(1000, receivedEvent.get().getTotalBytes());
        assertEquals(0.2, receivedEvent.get().getProgress(), 0.001);
    }

    @Test
    void speedCalculation() throws InterruptedException {
        ProgressTracker tracker = new ProgressTracker(10000);
        
        tracker.update(5000);
        Thread.sleep(100);
        long speed = tracker.getSpeed();
        
        assertTrue(speed >= 0);
    }

    @Test
    void estimatedTimeRemaining() {
        ProgressTracker tracker = new ProgressTracker(10000);
        
        Duration remaining = tracker.getEstimatedTimeRemaining();
        
        assertNotNull(remaining);
    }

    @Test
    void reset() {
        ProgressTracker tracker = new ProgressTracker(1000);
        
        tracker.update(500);
        assertEquals(500, tracker.getDownloadedBytes());
        
        tracker.reset();
        assertEquals(0, tracker.getDownloadedBytes());
    }
}