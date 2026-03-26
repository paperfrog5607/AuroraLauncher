package org.aurora.launcher.launcher.library;

import org.aurora.launcher.launcher.version.Library;
import org.aurora.launcher.launcher.version.LibraryArtifact;
import org.aurora.launcher.launcher.version.LibraryDownloads;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public class LibraryValidator {

    public boolean validate(Library library, Path libraryPath) {
        if (!library.isAllowedOnCurrentPlatform()) {
            return true;
        }
        
        if (!Files.exists(libraryPath)) {
            return false;
        }
        
        LibraryDownloads downloads = library.getDownloads();
        if (downloads == null) return true;
        
        LibraryArtifact artifact = downloads.getArtifact();
        if (artifact == null) return true;
        
        String expectedSha1 = artifact.getSha1();
        if (expectedSha1 == null || expectedSha1.isEmpty()) {
            return true;
        }
        
        try {
            String actualSha1 = calculateSha1(libraryPath);
            return expectedSha1.equalsIgnoreCase(actualSha1);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateSize(Library library, Path libraryPath) {
        LibraryDownloads downloads = library.getDownloads();
        if (downloads == null) return true;
        
        LibraryArtifact artifact = downloads.getArtifact();
        if (artifact == null) return true;
        
        long expectedSize = artifact.getSize();
        if (expectedSize <= 0) {
            return true;
        }
        
        try {
            long actualSize = Files.size(libraryPath);
            return actualSize == expectedSize;
        } catch (Exception e) {
            return false;
        }
    }

    private String calculateSha1(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
        }
        
        byte[] hash = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}