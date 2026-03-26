package org.aurora.launcher.modpack.share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShareCodeParser {
    
    private static final Logger logger = LoggerFactory.getLogger(ShareCodeParser.class);
    private static final Pattern CODE_PATTERN = Pattern.compile(
            "^AURORA-([A-Z2-9]{4})-([A-Z2-9]{4})-([A-Z2-9]{4})$");
    
    public ShareCode parse(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Share code cannot be null or empty");
        }
        
        String normalizedCode = code.toUpperCase().trim();
        
        Matcher matcher = CODE_PATTERN.matcher(normalizedCode);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid share code format: " + code);
        }
        
        ShareCode shareCode = new ShareCode();
        shareCode.setCode(normalizedCode);
        
        return shareCode;
    }
    
    public ShareCode parseFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        
        String code = extractCodeFromUrl(url);
        if (code == null) {
            throw new IllegalArgumentException("No valid share code found in URL: " + url);
        }
        
        return parse(code);
    }
    
    private String extractCodeFromUrl(String url) {
        int lastSegment = url.lastIndexOf('/');
        if (lastSegment >= 0 && lastSegment < url.length() - 1) {
            String potentialCode = url.substring(lastSegment + 1);
            if (ShareCodeGenerator.isValidCodeFormat(potentialCode)) {
                return potentialCode;
            }
        }
        
        String[] parts = url.split("[?&]");
        for (String part : parts) {
            if (part.startsWith("code=")) {
                String code = part.substring(5);
                if (ShareCodeGenerator.isValidCodeFormat(code)) {
                    return code;
                }
            }
        }
        
        return null;
    }
    
    public boolean isValid(String code) {
        try {
            parse(code);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public ShareCode decode(String encodedData) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encodedData);
            String json = new String(decoded, "UTF-8");
            
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .registerTypeAdapter(Instant.class, 
                            new com.google.gson.JsonDeserializer<Instant>() {
                                @Override
                                public Instant deserialize(com.google.gson.JsonElement json, 
                                        java.lang.reflect.Type typeOfT, 
                                        com.google.gson.JsonDeserializationContext context) {
                                    return Instant.parse(json.getAsString());
                                }
                            })
                    .create();
            
            return gson.fromJson(json, ShareCode.class);
        } catch (Exception e) {
            logger.error("Failed to decode share code data: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to decode share code data", e);
        }
    }
    
    public String encode(ShareCode shareCode) {
        try {
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .registerTypeAdapter(Instant.class, 
                            new com.google.gson.JsonSerializer<Instant>() {
                                @Override
                                public com.google.gson.JsonElement serialize(Instant src, 
                                        java.lang.reflect.Type typeOfSrc, 
                                        com.google.gson.JsonSerializationContext context) {
                                    return new com.google.gson.JsonPrimitive(src.toString());
                                }
                            })
                    .create();
            
            String json = gson.toJson(shareCode);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes("UTF-8"));
        } catch (Exception e) {
            logger.error("Failed to encode share code: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to encode share code", e);
        }
    }
}