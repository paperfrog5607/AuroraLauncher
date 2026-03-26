package org.aurora.launcher.core.util;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void isNullOrEmpty_nullString_returnsTrue() {
        assertTrue(StringUtils.isNullOrEmpty(null));
    }

    @Test
    void isNullOrEmpty_emptyString_returnsTrue() {
        assertTrue(StringUtils.isNullOrEmpty(""));
    }

    @Test
    void isNullOrEmpty_nonEmptyString_returnsFalse() {
        assertFalse(StringUtils.isNullOrEmpty("test"));
    }

    @Test
    void isNullOrBlank_nullString_returnsTrue() {
        assertTrue(StringUtils.isNullOrBlank(null));
    }

    @Test
    void isNullOrBlank_emptyString_returnsTrue() {
        assertTrue(StringUtils.isNullOrBlank(""));
    }

    @Test
    void isNullOrBlank_whitespaceOnly_returnsTrue() {
        assertTrue(StringUtils.isNullOrBlank("   "));
    }

    @Test
    void isNullOrBlank_nonBlankString_returnsFalse() {
        assertFalse(StringUtils.isNullOrBlank("test"));
    }

    @Test
    void truncate_stringShorterThanMax_returnsOriginal() {
        assertEquals("test", StringUtils.truncate("test", 10));
    }

    @Test
    void truncate_stringEqualToMax_returnsOriginal() {
        assertEquals("test", StringUtils.truncate("test", 4));
    }

    @Test
    void truncate_stringLongerThanMax_returnsTruncated() {
        assertEquals("test", StringUtils.truncate("testing", 4));
    }

    @Test
    void formatSize_bytes_returnsCorrectFormat() {
        assertEquals("100 B", StringUtils.formatSize(100));
    }

    @Test
    void formatSize_kilobytes_returnsCorrectFormat() {
        assertEquals("1.00 KB", StringUtils.formatSize(1024));
    }

    @Test
    void formatSize_megabytes_returnsCorrectFormat() {
        assertEquals("1.00 MB", StringUtils.formatSize(1024 * 1024));
    }

    @Test
    void formatSize_gigabytes_returnsCorrectFormat() {
        assertEquals("1.00 GB", StringUtils.formatSize(1024L * 1024 * 1024));
    }

    @Test
    void formatDuration_seconds_returnsCorrectFormat() {
        assertEquals("30s", StringUtils.formatDuration(Duration.ofSeconds(30)));
    }

    @Test
    void formatDuration_minutes_returnsCorrectFormat() {
        assertEquals("5m", StringUtils.formatDuration(Duration.ofMinutes(5)));
    }

    @Test
    void formatDuration_hours_returnsCorrectFormat() {
        assertEquals("2h", StringUtils.formatDuration(Duration.ofHours(2)));
    }

    @Test
    void formatDuration_complexDuration_returnsCorrectFormat() {
        assertEquals("1h 30m 45s", StringUtils.formatDuration(Duration.ofHours(1).plusMinutes(30).plusSeconds(45)));
    }
}