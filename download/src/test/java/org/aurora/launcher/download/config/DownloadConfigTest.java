package org.aurora.launcher.download.config;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

class DownloadConfigTest {

    @Test
    void defaultValues() {
        DownloadConfig config = new DownloadConfig();
        assertEquals(4, config.getMaxConcurrent());
        assertEquals(3, config.getMaxRetries());
        assertEquals(4, config.getChunkCount());
        assertEquals(10000, config.getConnectTimeout());
        assertEquals(30000, config.getReadTimeout());
        assertTrue(config.isResumeEnabled());
        assertNull(config.getTempDir());
        assertNull(config.getProxy());
    }

    @Test
    void settersAndGetters() {
        DownloadConfig config = new DownloadConfig();
        Path tempDir = Paths.get("/tmp/downloads");
        ProxyConfig proxy = new ProxyConfig();
        
        config.setMaxConcurrent(8);
        config.setMaxRetries(5);
        config.setChunkCount(8);
        config.setConnectTimeout(5000);
        config.setReadTimeout(60000);
        config.setResumeEnabled(false);
        config.setTempDir(tempDir);
        config.setProxy(proxy);

        assertEquals(8, config.getMaxConcurrent());
        assertEquals(5, config.getMaxRetries());
        assertEquals(8, config.getChunkCount());
        assertEquals(5000, config.getConnectTimeout());
        assertEquals(60000, config.getReadTimeout());
        assertFalse(config.isResumeEnabled());
        assertEquals(tempDir, config.getTempDir());
        assertEquals(proxy, config.getProxy());
    }
}