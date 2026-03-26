package org.aurora.launcher.ui.controller.download;

import org.aurora.launcher.api.unified.UnifiedMod;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModController extends BaseResourceController {
    
    @Override
    protected CompletableFuture<List<UnifiedMod>> doSearch(String query) {
        if (unifiedSearchService != null) {
            boolean isPopular = query == null || query.trim().isEmpty();
            if (isPopular && !showPopular) {
                return CompletableFuture.completedFuture(java.util.Collections.emptyList());
            }
            return unifiedSearchService.search(query, null, "mod", searchModrinth, searchCurseforge);
        }
        return searchService.searchMods(query, null)
            .thenApply(list -> list.stream()
                .map(this::toUnifiedMod)
                .collect(java.util.stream.Collectors.toList()));
    }
    
    private UnifiedMod toUnifiedMod(org.aurora.launcher.api.modrinth.ModrinthProject p) {
        UnifiedMod mod = new UnifiedMod();
        mod.setId(p.getId());
        mod.setSource("modrinth");
        mod.setName(p.getName());
        mod.setSlug(p.getSlug());
        mod.setDescription(p.getDescription());
        mod.setIconUrl(p.getIconUrl());
        mod.setDownloads(p.getDownloads());
        mod.setCategories(p.getCategories());
        mod.setGameVersions(p.getGameVersions());
        mod.setLoaders(p.getLoaders());
        mod.setPageUrl("https://modrinth.com/mod/" + p.getSlug());
        return mod;
    }
}