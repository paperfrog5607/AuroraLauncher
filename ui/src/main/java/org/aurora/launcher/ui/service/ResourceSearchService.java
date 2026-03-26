package org.aurora.launcher.ui.service;

import org.aurora.launcher.api.modrinth.ModrinthClient;
import org.aurora.launcher.api.modrinth.ModrinthProject;
import org.aurora.launcher.api.modrinth.ModrinthSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ResourceSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceSearchService.class);
    
    private final ModrinthClient modrinthClient;
    private final EnhancedSearchService enhancedSearch;
    private final AtomicReference<List<ModrinthProject>> cachedProjects = new AtomicReference<>(new ArrayList<>());
    
    public ResourceSearchService() {
        this.modrinthClient = new ModrinthClient();
        this.enhancedSearch = new EnhancedSearchService();
    }
    
    public CompletableFuture<List<ModrinthProject>> searchMods(String query, String gameVersion) {
        return search(query, gameVersion, "mod");
    }
    
    public CompletableFuture<List<ModrinthProject>> searchModpacks(String query, String gameVersion) {
        return search(query, gameVersion, "modpack");
    }
    
    public CompletableFuture<List<ModrinthProject>> searchResourcePacks(String query, String gameVersion) {
        return search(query, gameVersion, "resourcepack");
    }
    
    public CompletableFuture<List<ModrinthProject>> searchShaders(String query, String gameVersion) {
        return search(query, gameVersion, "shader");
    }
    
    private CompletableFuture<List<ModrinthProject>> search(String query, String gameVersion, String projectType) {
        boolean isPopular = query == null || query.trim().isEmpty();
        
        if (isPopular) {
            return modrinthClient.search("", null, projectType, 0, 500, "downloads")
                .handle((result, ex) -> {
                    if (ex != null) {
                        logger.error("Search failed", ex);
                        return new ArrayList<ModrinthProject>();
                    }
                    if (result == null || result.getHits() == null) {
                        return new ArrayList<ModrinthProject>();
                    }
                    List<ModrinthProject> hits = result.getHits();
                    enhancedSearch.cachePopular(hits);
                    return hits;
                });
        }
        
        String searchQuery = query.trim();
        
        return modrinthClient.getProject(searchQuery)
            .handle((directProject, ex) -> {
                List<ModrinthProject> results = new ArrayList<>();
                if (directProject != null && projectType.equals(directProject.getProjectType())) {
                    results.add(directProject);
                    logger.info("Direct project found: {}", directProject.getName());
                }
                return results;
            })
            .thenCompose(directResults -> {
                return modrinthClient.search(searchQuery, null, projectType, 0, 500, "relevance")
                    .handle((result, ex) -> {
                        if (ex != null) {
                            logger.error("Search failed", ex);
                            return new ArrayList<ModrinthProject>();
                        }
                        if (result == null || result.getHits() == null) {
                            return new ArrayList<ModrinthProject>();
                        }
                        return result.getHits();
                    })
                    .thenApply(hits -> {
                        for (ModrinthProject p : hits) {
                            if (!containsProject(directResults, p.getId())) {
                                directResults.add(p);
                            }
                        }
                        
                        enhancedSearch.buildIndex(directResults);
                        cachedProjects.set(directResults);
                        
                        List<ModrinthProject> enhanced = enhancedSearch.search(searchQuery, gameVersion, null);
                        logger.info("Enhanced search '{}' returned {} results, first: {}", 
                            searchQuery, enhanced.size(),
                            enhanced.isEmpty() ? "none" : enhanced.get(0).getName());
                        return enhanced;
                    });
            });
    }
    
    private boolean containsProject(List<ModrinthProject> list, String id) {
        if (id == null) return false;
        return list.stream().anyMatch(p -> id.equals(p.getId()));
    }
    
    public CompletableFuture<ModrinthProject> getProject(String id) {
        return modrinthClient.getProject(id)
            .exceptionally(e -> {
                logger.error("Failed to get project: {}", id, e);
                return null;
            });
    }
    
    public void shutdown() {
        modrinthClient.close();
    }
}