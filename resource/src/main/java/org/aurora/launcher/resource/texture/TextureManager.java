package org.aurora.launcher.resource.texture;

import java.awt.Image;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TextureManager {
    
    private final TextureExtractor extractor;
    private final TextureCache cache;
    
    public TextureManager() {
        this.extractor = new TextureExtractor();
        this.cache = new TextureCache();
    }
    
    public TextureManager(int cacheSize) {
        this.extractor = new TextureExtractor();
        this.cache = new TextureCache(cacheSize);
    }
    
    public CompletableFuture<List<TexturePreview>> extractFromMod(Path modFile) {
        return extractor.extractFromMod(modFile);
    }
    
    public CompletableFuture<List<TexturePreview>> extractFromResourcePack(Path packPath) {
        return extractor.extractFromResourcePack(packPath);
    }
    
    public CompletableFuture<List<TexturePreview>> extractFromFolder(Path folderPath) {
        return extractor.extractFromFolder(folderPath);
    }
    
    public CompletableFuture<Image> extractSingle(Path filePath, String texturePath) {
        return extractor.extractSingle(filePath, texturePath);
    }
    
    public CompletableFuture<Image> getTexture(Path filePath, String texturePath) {
        return cache.getOrLoad(filePath, texturePath);
    }
    
    public Image getCachedTexture(Path filePath, String texturePath) {
        return cache.get(filePath, texturePath);
    }
    
    public void cacheTexture(Path filePath, String texturePath, Image image) {
        cache.put(filePath, texturePath, image);
    }
    
    public boolean isCached(Path filePath, String texturePath) {
        return cache.contains(filePath, texturePath);
    }
    
    public void removeFromCache(Path filePath, String texturePath) {
        cache.remove(filePath, texturePath);
    }
    
    public void clearCache() {
        cache.clear();
    }
    
    public int getCacheSize() {
        return cache.size();
    }
    
    public int getCacheMaxSize() {
        return cache.getMaxSize();
    }
}