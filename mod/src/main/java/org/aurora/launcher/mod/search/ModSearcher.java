package org.aurora.launcher.mod.search;

import org.aurora.launcher.api.unified.UnifiedMod;
import org.aurora.launcher.api.unified.UnifiedSearch;
import org.aurora.launcher.api.unified.SearchOptions;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModSearcher {
    
    private final UnifiedSearch unifiedSearch;
    
    public ModSearcher() {
        this.unifiedSearch = new UnifiedSearch();
    }
    
    public ModSearcher(UnifiedSearch unifiedSearch) {
        this.unifiedSearch = unifiedSearch;
    }
    
    public CompletableFuture<List<ModSearchResult>> search(String query) {
        return search(query, new SearchOptions());
    }
    
    public CompletableFuture<List<ModSearchResult>> search(String query, SearchOptions options) {
        return unifiedSearch.search(query, options)
                .thenApply(mods -> {
                    return mods.stream()
                            .map(this::toSearchResult)
                            .collect(java.util.stream.Collectors.toList());
                });
    }
    
    private ModSearchResult toSearchResult(UnifiedMod mod) {
        ModSearchResult result = new ModSearchResult();
        result.setId(mod.getId());
        result.setSlug(mod.getSlug());
        result.setName(mod.getName());
        result.setDescription(mod.getDescription());
        result.setAuthor(mod.getAuthor());
        result.setIconUrl(mod.getIconUrl());
        result.setDownloads(mod.getDownloads());
        result.setSource(mod.getSource());
        result.setCategories(mod.getCategories());
        return result;
    }
}