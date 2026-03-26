package org.aurora.launcher.download.validation;

import org.aurora.launcher.download.core.DownloadRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class SizeValidatorTest {

    @TempDir
    Path tempDir;

    @Test
    void validateCorrectSize() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, new byte[100]);
        
        DownloadRequest request = new DownloadRequest();
        request.setExpectedSize(100);
        
        SizeValidator validator = new SizeValidator();
        assertTrue(validator.validate(file, request));
        assertNull(validator.getValidationError());
    }

    @Test
    void validateWrongSize() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, new byte[100]);
        
        DownloadRequest request = new DownloadRequest();
        request.setExpectedSize(200);
        
        SizeValidator validator = new SizeValidator();
        assertFalse(validator.validate(file, request));
        assertNotNull(validator.getValidationError());
    }

    @Test
    void validateNoExpectedSize() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, new byte[100]);
        
        DownloadRequest request = new DownloadRequest();
        
        SizeValidator validator = new SizeValidator();
        assertTrue(validator.validate(file, request));
    }

    @Test
    void validateZeroExpectedSize() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, new byte[100]);
        
        DownloadRequest request = new DownloadRequest();
        request.setExpectedSize(0);
        
        SizeValidator validator = new SizeValidator();
        assertTrue(validator.validate(file, request));
    }
}