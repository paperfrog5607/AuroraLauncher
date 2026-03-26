package org.aurora.launcher.modpack.share;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class ShareCodeGenerator {
    
    private static final String CODE_PREFIX = "AURORA";
    private static final int CODE_SEGMENTS = 3;
    private static final int SEGMENT_LENGTH = 4;
    
    private Duration defaultExpiration = Duration.ofDays(7);
    private String defaultFormat = "modrinth";
    
    public ShareCode generate(String instanceId, String instanceName, String downloadUrl, long fileSize) {
        ShareCode shareCode = new ShareCode();
        shareCode.setCode(generateCode());
        shareCode.setInstanceId(instanceId);
        shareCode.setInstanceName(instanceName);
        shareCode.setCreatedTime(Instant.now());
        shareCode.setExpiresTime(Instant.now().plus(defaultExpiration));
        shareCode.setFormat(defaultFormat);
        shareCode.setDownloadUrl(downloadUrl);
        shareCode.setFileSize(fileSize);
        return shareCode;
    }
    
    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_PREFIX);
        
        for (int i = 0; i < CODE_SEGMENTS; i++) {
            code.append("-");
            code.append(generateSegment());
        }
        
        return code.toString();
    }
    
    private String generateSegment() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder segment = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < SEGMENT_LENGTH; i++) {
            segment.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return segment.toString();
    }
    
    public Duration getDefaultExpiration() {
        return defaultExpiration;
    }
    
    public void setDefaultExpiration(Duration defaultExpiration) {
        this.defaultExpiration = defaultExpiration;
    }
    
    public String getDefaultFormat() {
        return defaultFormat;
    }
    
    public void setDefaultFormat(String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }
    
    public static boolean isValidCodeFormat(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        
        String[] parts = code.split("-");
        if (parts.length != CODE_SEGMENTS + 1) {
            return false;
        }
        
        if (!parts[0].equals(CODE_PREFIX)) {
            return false;
        }
        
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].length() != SEGMENT_LENGTH) {
                return false;
            }
            
            for (char c : parts[i].toCharArray()) {
                if (!Character.isLetterOrDigit(c)) {
                    return false;
                }
            }
        }
        
        return true;
    }
}