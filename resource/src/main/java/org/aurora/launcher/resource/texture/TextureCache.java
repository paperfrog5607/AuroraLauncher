package org.aurora.launcher.resource.texture;

import java.awt.Image;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TextureCache {
    
    private final Map<String, Image> cache;
    private final int maxSize;
    
    public TextureCache() {
        this(100);
    }
    
    public TextureCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<String, Image>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Image> eldest) {
                return size() > maxSize;
            }
        };
    }
    
    public Image get(Path filePath, String texturePath) {
        String key = createKey(filePath, texturePath);
        return cache.get(key);
    }
    
    public void put(Path filePath, String texturePath, Image image) {
        String key = createKey(filePath, texturePath);
        synchronized (cache) {
            cache.put(key, image);
        }
    }
    
    public CompletableFuture<Image> getOrLoad(Path filePath, String texturePath) {
        Image cached = get(filePath, texturePath);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        
        return new TextureExtractor().extractSingle(filePath, texturePath)
                .thenApply(image -> {
                    if (image != null) {
                        put(filePath, texturePath, image);
                    }
                    return image;
                });
    }
    
    public boolean contains(Path filePath, String texturePath) {
        String key = createKey(filePath, texturePath);
        synchronized (cache) {
            return cache.containsKey(key);
        }
    }
    
    public void remove(Path filePath, String texturePath) {
        String key = createKey(filePath, texturePath);
        synchronized (cache) {
            cache.remove(key);
        }
    }
    
    public void clear() {
        synchronized (cache) {
            cache.clear();
        }
    }
    
    public int size() {
        synchronized (cache) {
            return cache.size();
        }
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    private String createKey(Path filePath, String texturePath) {
        return filePath.toString() + ":" + texturePath;
    }
}