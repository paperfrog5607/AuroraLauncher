package org.aurora.launcher.api.unified;

import java.util.ArrayList;
import java.util.List;

public class SearchFilter {
    
    private List<String> gameVersions = new ArrayList<>();
    private List<String> loaders = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private int minDownloads = 0;
    private int maxDownloads = Integer.MAX_VALUE;
    
    public SearchFilter() {
    }
    
    public SearchFilter gameVersion(String version) {
        this.gameVersions.add(version);
        return this;
    }
    
    public SearchFilter gameVersions(List<String> versions) {
        this.gameVersions.addAll(versions);
        return this;
    }
    
    public SearchFilter loader(String loader) {
        this.loaders.add(loader);
        return this;
    }
    
    public SearchFilter loaders(List<String> loaders) {
        this.loaders.addAll(loaders);
        return this;
    }
    
    public SearchFilter category(String category) {
        this.categories.add(category);
        return this;
    }
    
    public SearchFilter categories(List<String> categories) {
        this.categories.addAll(categories);
        return this;
    }
    
    public SearchFilter minDownloads(int minDownloads) {
        this.minDownloads = minDownloads;
        return this;
    }
    
    public SearchFilter maxDownloads(int maxDownloads) {
        this.maxDownloads = maxDownloads;
        return this;
    }
    
    public List<String> getGameVersions() {
        return gameVersions;
    }
    
    public List<String> getLoaders() {
        return loaders;
    }
    
    public List<String> getCategories() {
        return categories;
    }
    
    public int getMinDownloads() {
        return minDownloads;
    }
    
    public int getMaxDownloads() {
        return maxDownloads;
    }
    
    public boolean hasVersionFilter() {
        return !gameVersions.isEmpty();
    }
    
    public boolean hasLoaderFilter() {
        return !loaders.isEmpty();
    }
    
    public boolean hasCategoryFilter() {
        return !categories.isEmpty();
    }
    
    public boolean hasDownloadFilter() {
        return minDownloads > 0 || maxDownloads < Integer.MAX_VALUE;
    }
    
    public boolean isEmpty() {
        return gameVersions.isEmpty() && loaders.isEmpty() && 
               categories.isEmpty() && !hasDownloadFilter();
    }
    
    public static SearchFilter none() {
        return new SearchFilter();
    }
    
    public static SearchFilter ofVersion(String version) {
        return new SearchFilter().gameVersion(version);
    }
    
    public static SearchFilter ofLoader(String loader) {
        return new SearchFilter().loader(loader);
    }
}
