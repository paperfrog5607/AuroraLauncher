package org.aurora.launcher.core.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String hashSHA256(byte[] data) {
        return hash(data, "SHA-256");
    }

    public static String hashSHA256(Path file) throws IOException {
        return hashFile(file, "SHA-256");
    }

    public static String hashMD5(byte[] data) {
        return hash(data, "MD5");
    }

    public static String hashMD5(Path file) throws IOException {
        return hashFile(file, "MD5");
    }

    public static boolean verifyChecksum(Path file, String expectedHash, String algorithm) {
        try {
            String actualHash;
            if ("SHA-256".equalsIgnoreCase(algorithm)) {
                actualHash = hashSHA256(file);
            } else if ("MD5".equalsIgnoreCase(algorithm)) {
                actualHash = hashMD5(file);
            } else {
                actualHash = hashFile(file, algorithm);
            }
            return expectedHash.equalsIgnoreCase(actualHash);
        } catch (IOException e) {
            return false;
        }
    }

    private static String hash(byte[] data, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(data);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not found: " + algorithm, e);
        }
    }

    private static String hashFile(Path file, String algorithm) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            try (InputStream is = Files.newInputStream(file)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    md.update(buffer, 0, read);
                }
            }
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not found: " + algorithm, e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}