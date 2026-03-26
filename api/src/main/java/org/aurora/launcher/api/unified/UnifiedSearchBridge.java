package org.aurora.launcher.api.unified;

import org.aurora.launcher.api.curseforge.CurseForgeClient;
import org.aurora.launcher.api.curseforge.CurseForgeMod;
import org.aurora.launcher.api.modrinth.ModrinthClient;
import org.aurora.launcher.api.modrinth.ModrinthProject;
import org.aurora.launcher.api.modrinth.ModrinthSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UnifiedSearchBridge {
    
    private static final Logger logger = LoggerFactory.getLogger(UnifiedSearchBridge.class);
    
    private final ModrinthClient modrinthClient;
    private final CurseForgeClient curseForgeClient;
    private final SearchEngine searchEngine;
    
    private final Map<String, List<UnifiedMod>> modrinthCache = new ConcurrentHashMap<>();
    private final Map<String, List<UnifiedMod>> curseForgeCache = new ConcurrentHashMap<>();
    private final Map<String, UnifiedMod> directLookupCache = new ConcurrentHashMap<>();
    
    private static final long CACHE_TTL_MS = 5 * 60 * 1000;
    private long lastCacheTime = 0;
    
    public UnifiedSearchBridge(ModrinthClient modrinthClient, CurseForgeClient curseForgeClient) {
        this.modrinthClient = modrinthClient;
        this.curseForgeClient = curseForgeClient;
        this.searchEngine = new SearchEngine();
    }
    
    public CompletableFuture<UnifiedSearchResponse> search(UnifiedSearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        if (request.isPopularSearch()) {
            return searchPopular(request);
        }
        
        String query = request.getQuery().trim();
        List<CompletableFuture<List<UnifiedMod>>> futures = new ArrayList<>();
        
        if (request.getSource() == UnifiedSearchRequest.Source.MODRINTH || 
            request.getSource() == UnifiedSearchRequest.Source.BOTH) {
            futures.add(searchModrinth(query, request));
        }
        
        if (request.getSource() == UnifiedSearchRequest.Source.CURSEFORGE || 
            request.getSource() == UnifiedSearchRequest.Source.BOTH) {
            futures.add(searchCurseForge(query, request));
        }
        
        if (request.isEnableDirectLookup() && request.getBoostSlugs().isEmpty()) {
            futures.add(directLookup(query));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                UnifiedSearchResponse response = new UnifiedSearchResponse();
                
                Set<String> seenIds = new HashSet<>();
                
                List<UnifiedMod> allMods = new ArrayList<>();
                
                if (request.isEnableDirectLookup()) {
                    for (String slug : request.getBoostSlugs()) {
                        UnifiedMod direct = directLookupCache.get(slug.toLowerCase());
                        if (direct != null && !seenIds.contains(direct.getId())) {
                            response.addResult(direct);
                            seenIds.add(direct.getId());
                            allMods.add(direct);
                        }
                    }
                }
                
                for (CompletableFuture<List<UnifiedMod>> future : futures) {
                    List<UnifiedMod> results = future.join();
                    if (results != null) {
                        for (UnifiedMod mod : results) {
                            if (!seenIds.contains(mod.getId())) {
                                allMods.add(mod);
                                seenIds.add(mod.getId());
                            }
                        }
                    }
                }
                
                List<UnifiedMod> filtered = applyFilter(allMods, request.getFilter());
                
                searchEngine.buildIndex(filtered);
                List<SearchEngine.SearchResult> searchResults = searchEngine.search(query, filtered);
                
                int exactCount = 0;
                for (SearchEngine.SearchResult sr : searchResults) {
                    if (sr.score >= 600) {
                        exactCount++;
                    }
                }
                
                List<UnifiedMod> sorted = new ArrayList<>();
                for (SearchEngine.SearchResult sr : searchResults) {
                    sorted.add(sr.mod);
                }
                
                response = new UnifiedSearchResponse(sorted);
                response.setTotalHits(sorted.size());
                response.setHasMore(sorted.size() > request.getOffset() + request.getLimit());
                
                logger.info("Search '{}': {} exact matches, {} total results", query, exactCount, sorted.size());
                
                response.setOffset(request.getOffset());
                response.setLimit(request.getLimit());
                response.setSearchTimeMs(System.currentTimeMillis() - startTime);
                
                logger.info("UnifiedSearch '{}' -> {} results ({}ms)", query, response.getResults().size(), response.getSearchTimeMs());
                return response;
            });
    }
    
    private CompletableFuture<UnifiedSearchResponse> searchPopular(UnifiedSearchRequest request) {
        long startTime = System.currentTimeMillis();
        int popularLimit = UnifiedSearchRequest.POPULAR_LIMIT;
        List<CompletableFuture<List<UnifiedMod>>> futures = new ArrayList<>();
        
        if (request.getSource() == UnifiedSearchRequest.Source.MODRINTH || 
            request.getSource() == UnifiedSearchRequest.Source.BOTH) {
            futures.add(searchModrinthPopular(popularLimit));
        }
        
        if (request.getSource() == UnifiedSearchRequest.Source.CURSEFORGE || 
            request.getSource() == UnifiedSearchRequest.Source.BOTH) {
            futures.add(searchCurseForgePopular(popularLimit));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                UnifiedSearchResponse response = new UnifiedSearchResponse();
                List<UnifiedMod> allResults = new ArrayList<>();
                
                for (CompletableFuture<List<UnifiedMod>> future : futures) {
                    List<UnifiedMod> results = future.join();
                    if (results != null) {
                        allResults.addAll(results);
                    }
                }
                
                List<UnifiedMod> merged = mergeModsByName(allResults);
                List<UnifiedMod> sorted = sortResults(merged, UnifiedSearchRequest.SortBy.DOWNLOADS, null);
                List<UnifiedMod> limited = limitResults(sorted, 0, popularLimit);
                
                response = new UnifiedSearchResponse(limited);
                response.setTotalHits(sorted.size());
                response.setOffset(0);
                response.setLimit(popularLimit);
                response.setHasMore(sorted.size() > popularLimit);
                response.setSearchTimeMs(System.currentTimeMillis() - startTime);
                
                logger.info("Popular search -> {} results ({}ms)", response.getResults().size(), response.getSearchTimeMs());
                return response;
            });
    }
    
    private CompletableFuture<List<UnifiedMod>> searchModrinth(String query, UnifiedSearchRequest request) {
        String cacheKey = "mr:" + query + ":" + request.getSortBy().getApiValue();
        
        if (!modrinthCache.isEmpty() && System.currentTimeMillis() - lastCacheTime < CACHE_TTL_MS) {
            List<UnifiedMod> cached = modrinthCache.get(cacheKey);
            if (cached != null) {
                return CompletableFuture.completedFuture(cached);
            }
        }
        
        return modrinthClient.search(query, null, "mod", 0, UnifiedSearchRequest.SEARCH_LIMIT, request.getSortBy().getApiValue())
            .handle((result, ex) -> {
                if (ex != null || result == null || result.getHits() == null) {
                    return new ArrayList<UnifiedMod>();
                }
                List<UnifiedMod> mods = result.getHits().stream()
                    .map(this::toUnifiedMod)
                    .collect(Collectors.toList());
                modrinthCache.put(cacheKey, mods);
                return mods;
            });
    }
    
    private CompletableFuture<List<UnifiedMod>> searchModrinthPopular(int limit) {
        return modrinthClient.search("", null, "mod", 0, limit, "downloads")
            .handle((result, ex) -> {
                if (ex != null || result == null || result.getHits() == null) {
                    return new ArrayList<UnifiedMod>();
                }
                return result.getHits().stream()
                    .map(this::toUnifiedMod)
                    .collect(Collectors.toList());
            });
    }
    
    private CompletableFuture<List<UnifiedMod>> searchCurseForge(String query, UnifiedSearchRequest request) {
        if (curseForgeClient == null) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        String cacheKey = "cf:" + query;
        
        if (!curseForgeCache.isEmpty() && System.currentTimeMillis() - lastCacheTime < CACHE_TTL_MS) {
            List<UnifiedMod> cached = curseForgeCache.get(cacheKey);
            if (cached != null) {
                return CompletableFuture.completedFuture(cached);
            }
        }
        
        return curseForgeClient.searchMods(query, null, 0, UnifiedSearchRequest.SEARCH_LIMIT)
            .handle((result, ex) -> {
                if (ex != null || result == null || result.getData() == null) {
                    return new ArrayList<UnifiedMod>();
                }
                List<UnifiedMod> mods = result.getData().stream()
                    .map(this::toUnifiedMod)
                    .collect(Collectors.toList());
                curseForgeCache.put(cacheKey, mods);
                return mods;
            });
    }
    
    private CompletableFuture<List<UnifiedMod>> searchCurseForgePopular(int limit) {
        if (curseForgeClient == null) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        return curseForgeClient.searchMods("", null, 0, limit)
            .handle((result, ex) -> {
                if (ex != null || result == null || result.getData() == null) {
                    return new ArrayList<UnifiedMod>();
                }
                return result.getData().stream()
                    .map(this::toUnifiedMod)
                    .collect(Collectors.toList());
            });
    }
    
    private CompletableFuture<List<UnifiedMod>> directLookup(String query) {
        String normalized = query.toLowerCase().trim();
        
        return modrinthClient.getProject(normalized)
            .handle((project, ex) -> {
                if (ex != null || project == null) {
                    return null;
                }
                UnifiedMod mod = toUnifiedMod(project);
                directLookupCache.put(normalized, mod);
                return mod;
            })
            .thenApply(mod -> {
                List<UnifiedMod> result = new ArrayList<>();
                if (mod != null) {
                    result.add(mod);
                }
                return result;
            });
    }
    
    private List<UnifiedMod> applyFilter(List<UnifiedMod> mods, SearchFilter filter) {
        if (filter == null || filter.isEmpty()) {
            return mods;
        }
        
        return mods.stream()
            .filter(mod -> {
                if (filter.hasVersionFilter()) {
                    boolean hasVersion = mod.getGameVersions().stream()
                        .anyMatch(filter.getGameVersions()::contains);
                    if (!hasVersion) return false;
                }
                
                if (filter.hasLoaderFilter()) {
                    boolean hasLoader = mod.getLoaders().stream()
                        .anyMatch(l -> filter.getLoaders().stream()
                            .anyMatch(fl -> l.toLowerCase().contains(fl.toLowerCase())));
                    if (!hasLoader) return false;
                }
                
                if (filter.hasDownloadFilter()) {
                    if (mod.getDownloads() < filter.getMinDownloads()) return false;
                    if (mod.getDownloads() > filter.getMaxDownloads()) return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
    
    private List<UnifiedMod> sortResults(List<UnifiedMod> mods, UnifiedSearchRequest.SortBy sortBy, String query) {
        if (mods == null || mods.isEmpty()) {
            return mods;
        }
        
        Comparator<UnifiedMod> comparator;
        switch (sortBy) {
            case DOWNLOADS:
                comparator = (a, b) -> Long.compare(b.getDownloads(), a.getDownloads());
                break;
            case UPDATED:
            case NEWEST:
                comparator = (a, b) -> 0;
                break;
            case NAME:
                comparator = (a, b) -> a.getName().compareToIgnoreCase(b.getName());
                break;
            case RELEVANCE:
            default:
                final String searchQuery = query;
                comparator = (a, b) -> {
                    if (searchQuery == null || searchQuery.isEmpty()) {
                        return Long.compare(b.getDownloads(), a.getDownloads());
                    }
                    String q = searchQuery.toLowerCase();
                    int scoreA = calculateRelevanceScore(a, q);
                    int scoreB = calculateRelevanceScore(b, q);
                    return Integer.compare(scoreB, scoreA);
                };
                break;
        }
        
        return mods.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    }
    
    private int calculateRelevanceScore(UnifiedMod mod, String query) {
        int score = 0;
        String slug = mod.getSlug() != null ? mod.getSlug().toLowerCase() : "";
        String name = mod.getName() != null ? mod.getName().toLowerCase() : "";
        String q = query.toLowerCase();
        
        if (slug.equals(q)) {
            score += 1000;
        } else if (slug.startsWith(q)) {
            score += 800;
        } else if (slug.contains(q)) {
            score += 500;
        }
        
        if (name.equals(q)) {
            score += 950;
        } else if (name.startsWith(q)) {
            score += 750;
        } else if (name.contains(q)) {
            score += 450;
        }
        
        score += Math.min(mod.getDownloads() / 100000, 100);
        
        return score;
    }
    
    private List<UnifiedMod> limitResults(List<UnifiedMod> mods, int offset, int limit) {
        if (mods == null) {
            return new ArrayList<>();
        }
        if (offset >= mods.size()) {
            return new ArrayList<>();
        }
        int end = Math.min(offset + limit, mods.size());
        return new ArrayList<>(mods.subList(offset, end));
    }
    
    private UnifiedMod toUnifiedMod(ModrinthProject project) {
        UnifiedMod mod = new UnifiedMod();
        mod.setId(project.getId());
        mod.setSource("modrinth");
        mod.setName(project.getName());
        mod.setSlug(project.getSlug());
        mod.setDescription(project.getDescription());
        mod.setIconUrl(project.getIconUrl());
        mod.setDownloads(project.getDownloads());
        mod.setCategories(project.getCategories());
        mod.setGameVersions(project.getGameVersions());
        mod.setLoaders(project.getLoaders());
        mod.setPageUrl("https://modrinth.com/mod/" + project.getSlug());
        return mod;
    }
    
    private UnifiedMod toUnifiedMod(CurseForgeMod cfMod) {
        UnifiedMod mod = new UnifiedMod();
        mod.setId(String.valueOf(cfMod.getId()));
        mod.setSource("curseforge");
        mod.setName(cfMod.getName());
        mod.setSlug(cfMod.getSlug());
        mod.setDescription(cfMod.getSummary());
        mod.setIconUrl(cfMod.getLogoUrl());
        mod.setDownloads(cfMod.getDownloadCount());
        mod.setPageUrl(cfMod.getWebsiteUrl());
        if (cfMod.getCategories() != null) {
            mod.setCategories(cfMod.getCategories().stream()
                .map(c -> c.getName())
                .collect(Collectors.toList()));
        }
        return mod;
    }
    
    private List<UnifiedMod> mergeModsByName(List<UnifiedMod> mods) {
        Map<String, UnifiedMod> mergedMap = new LinkedHashMap<>();
        
        for (UnifiedMod mod : mods) {
            if (mod == null || mod.getName() == null) {
                continue;
            }
            
            String normalizedName = mod.getName().toLowerCase().trim();
            
            if (mergedMap.containsKey(normalizedName)) {
                UnifiedMod existing = mergedMap.get(normalizedName);
                existing.addSourceDownload(mod.getSource(), mod.getDownloads());
                if (existing.getIconUrl() == null && mod.getIconUrl() != null) {
                    existing.setIconUrl(mod.getIconUrl());
                }
                if (existing.getSlug() == null && mod.getSlug() != null) {
                    existing.setSlug(mod.getSlug());
                }
                if (existing.getPageUrl() == null && mod.getPageUrl() != null) {
                    existing.setPageUrl(mod.getPageUrl());
                }
            } else {
                mergedMap.put(normalizedName, mod);
            }
        }
        
        return new ArrayList<>(mergedMap.values());
    }
    
    public void clearCache() {
        modrinthCache.clear();
        curseForgeCache.clear();
        directLookupCache.clear();
        lastCacheTime = 0;
        logger.info("Search cache cleared");
    }
    
    public void close() {
        clearCache();
        modrinthClient.close();
    }
}
