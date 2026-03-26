package org.aurora.launcher.modpack.share;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ShareCodeTest {
    
    private ShareCode shareCode;
    
    @BeforeEach
    void setUp() {
        shareCode = new ShareCode();
    }
    
    @Test
    void testShareCodeCreation() {
        shareCode.setCode("AURORA-ABCD-EFGH-WXYZ");
        shareCode.setInstanceId("instance-1");
        shareCode.setInstanceName("Test Modpack");
        shareCode.setFormat("modrinth");
        shareCode.setDownloadUrl("https://example.com/modpack.mrpack");
        
        assertEquals("AURORA-ABCD-EFGH-WXYZ", shareCode.getCode());
        assertEquals("instance-1", shareCode.getInstanceId());
        assertEquals("Test Modpack", shareCode.getInstanceName());
        assertEquals("modrinth", shareCode.getFormat());
        assertEquals("https://example.com/modpack.mrpack", shareCode.getDownloadUrl());
    }
    
    @Test
    void testExpiration() {
        shareCode.setExpiresTime(Instant.now().plus(Duration.ofDays(7)));
        assertFalse(shareCode.isExpired());
        
        shareCode.setExpiresTime(Instant.now().minus(Duration.ofHours(1)));
        assertTrue(shareCode.isExpired());
    }
    
    @Test
    void testValidity() {
        shareCode.setCode("AURORA-ABCD-EFGH-WXYZ");
        shareCode.setDownloadUrl("https://example.com/modpack.mrpack");
        shareCode.setExpiresTime(Instant.now().plus(Duration.ofDays(7)));
        
        assertTrue(shareCode.isValid());
        
        shareCode.setDownloadUrl(null);
        assertFalse(shareCode.isValid());
    }
    
    @Test
    void testFormattedFileSize() {
        shareCode.setFileSize(512);
        assertEquals("512 B", shareCode.getFormattedFileSize());
        
        shareCode.setFileSize(2048);
        assertEquals("2.0 KB", shareCode.getFormattedFileSize());
        
        shareCode.setFileSize(1048576);
        assertEquals("1.0 MB", shareCode.getFormattedFileSize());
    }
}