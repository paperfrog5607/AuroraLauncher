package org.aurora.launcher.download.validation;

import org.aurora.launcher.download.core.DownloadRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SizeValidator implements FileValidator {
    private String validationError;

    @Override
    public boolean validate(Path file, DownloadRequest request) {
        validationError = null;
        
        if (request.getExpectedSize() <= 0) {
            return true;
        }
        
        try {
            long actualSize = Files.size(file);
            if (actualSize != request.getExpectedSize()) {
                validationError = "Size mismatch: expected " + request.getExpectedSize() 
                    + " bytes but got " + actualSize + " bytes";
                return false;
            }
            return true;
        } catch (IOException e) {
            validationError = "Failed to read file size: " + e.getMessage();
            return false;
        }
    }

    @Override
    public String getValidationError() {
        return validationError;
    }
}