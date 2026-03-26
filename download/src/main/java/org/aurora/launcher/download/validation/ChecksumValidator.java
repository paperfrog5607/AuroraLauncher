package org.aurora.launcher.download.validation;

import org.aurora.launcher.download.core.DownloadRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumValidator implements FileValidator {
    private String validationError;

    @Override
    public boolean validate(Path file, DownloadRequest request) {
        validationError = null;
        
        if (request.getExpectedSha1() == null || request.getExpectedSha1().isEmpty()) {
            return true;
        }
        
        try {
            String actualSha1 = calculateSha1(file);
            if (!actualSha1.equalsIgnoreCase(request.getExpectedSha1())) {
                validationError = "Checksum mismatch: expected " + request.getExpectedSha1() 
                    + " but got " + actualSha1;
                return false;
            }
            return true;
        } catch (Exception e) {
            validationError = "Failed to calculate checksum: " + e.getMessage();
            return false;
        }
    }

    @Override
    public String getValidationError() {
        return validationError;
    }

    private String calculateSha1(Path file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }
        return bytesToHex(md.digest());
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}