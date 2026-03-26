package org.aurora.launcher.download.core;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DownloadRequestTest {

    @Test
    void defaultValues() {
        DownloadRequest request = new DownloadRequest();
        assertNull(request.getId());
        assertNull(request.getUrl());
        assertNull(request.getTargetPath());
        assertNull(request.getExpectedSha1());
        assertEquals(0, request.getExpectedSize());
        assertEquals(3, request.getMaxRetries());
        assertEquals(4, request.getChunkCount());
        assertTrue(request.isSupportResume());
        assertNull(request.getProxy());
        assertNotNull(request.getHeaders());
        assertTrue(request.getHeaders().isEmpty());
        assertEquals(30000, request.getTimeout());
        assertEquals(0, request.getPriority());
    }

    @Test
    void settersAndGetters() {
        DownloadRequest request = new DownloadRequest();
        Path target = Paths.get("/tmp/file.zip");
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "AuroraLauncher");

        request.setId("test-id");
        request.setUrl("https://example.com/file.zip");
        request.setTargetPath(target);
        request.setExpectedSha1("abc123");
        request.setExpectedSize(1024);
        request.setMaxRetries(5);
        request.setChunkCount(8);
        request.setSupportResume(false);
        request.setHeaders(headers);
        request.setTimeout(60000);
        request.setPriority(10);

        assertEquals("test-id", request.getId());
        assertEquals("https://example.com/file.zip", request.getUrl());
        assertEquals(target, request.getTargetPath());
        assertEquals("abc123", request.getExpectedSha1());
        assertEquals(1024, request.getExpectedSize());
        assertEquals(5, request.getMaxRetries());
        assertEquals(8, request.getChunkCount());
        assertFalse(request.isSupportResume());
        assertEquals(headers, request.getHeaders());
        assertEquals(60000, request.getTimeout());
        assertEquals(10, request.getPriority());
    }
}