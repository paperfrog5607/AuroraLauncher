package org.aurora.launcher.download.validation;

import org.aurora.launcher.download.core.DownloadRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ChecksumValidatorTest {

    @TempDir
    Path tempDir;

    @Test
    void validateCorrectChecksum() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "hello".getBytes());
        
        DownloadRequest request = new DownloadRequest();
        request.setExpectedSha1("aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d");
        
        ChecksumValidator validator = new ChecksumValidator();
        assertTrue(validator.validate(file, request));
        assertNull(validator.getValidationError());
    }

    @Test
    void validateWrongChecksum() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "hello".getBytes());
        
        DownloadRequest request = new DownloadRequest();
        request.setExpectedSha1("wrongchecksum");
        
        ChecksumValidator validator = new ChecksumValidator();
        assertFalse(validator.validate(file, request));
        assertNotNull(validator.getValidationError());
    }

    @Test
    void validateNoExpectedChecksum() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "hello".getBytes());
        
        DownloadRequest request = new DownloadRequest();
        
        ChecksumValidator validator = new ChecksumValidator();
        assertTrue(validator.validate(file, request));
    }

    @Test
    void validateNullChecksum() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, "hello".getBytes());
        
        DownloadRequest request = new DownloadRequest();
        request.setExpectedSha1(null);
        
        ChecksumValidator validator = new ChecksumValidator();
        assertTrue(validator.validate(file, request));
    }
}