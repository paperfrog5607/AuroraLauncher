package org.aurora.launcher.ui.service;

import org.aurora.launcher.api.curseforge.CurseForgeClient;
import org.aurora.launcher.api.curseforge.CurseForgeMod;
import org.aurora.launcher.api.modrinth.ModrinthClient;
import org.aurora.launcher.api.modrinth.ModrinthProject;
import org.aurora.launcher.api.unified.UnifiedMod;
import org.aurora.launcher.config.ApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class UnifiedSearchService {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedSearchService.class);

    private final ModrinthClient modrinthClient;
    private CurseForgeClient curseForgeClient;
    private final EnhancedSearchService enhancedSearch;
    private final AtomicReference<List<UnifiedMod>> cachedProjects = new AtomicReference<>(new ArrayList<>());

    private final ConcurrentHashMap<String, List<UnifiedMod>> slugIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<UnifiedMod>> titleIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<UnifiedMod>> prefixIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<UnifiedMod>> containsIndex = new ConcurrentHashMap<>();

    private volatile boolean indexBuilt = false;

    public UnifiedSearchService() {
        this.modrinthClient = new ModrinthClient();
        this.enhancedSearch = new EnhancedSearchService();

        String cfApiKey = ApiConfig.getInstance().getCurseForgeApiKey();
        if (cfApiKey != null && !cfApiKey.isEmpty()) {
            this.curseForgeClient = new CurseForgeClient(cfApiKey);
            logger.info("CurseForge client initialized");
        } else {
            logger.warn("CurseForge API key not configured");
        }
    }

    public boolean isCurseForgeEnabled() {
        return curseForgeClient != null;
    }
    
    public CompletableFuture<List<UnifiedMod>> search(String query, String gameVersion, String projectType) {
        return search(query, gameVersion, projectType, SearchSource.BOTH);
    }
    
    public CompletableFuture<List<UnifiedMod>> search(String query, String gameVersion, String projectType, SearchSource source) {
        boolean searchMr = (source == SearchSource.BOTH || source == SearchSource.MODRINTH);
        boolean searchCf = (source == SearchSource.BOTH || source == SearchSource.CURSEFORGE);
        return search(query, gameVersion, projectType, searchMr, searchCf);
    }
    
    public CompletableFuture<List<UnifiedMod>> search(String query, String gameVersion, String projectType, boolean searchModrinth, boolean searchCurseforge) {
        boolean isPopular = query == null || query.trim().isEmpty();
        
        if (isPopular) {
            return searchPopular(projectType, searchModrinth, searchCurseforge);
        }
        
        String searchQuery = query.trim();
        
        CompletableFuture<UnifiedMod> directLookupFuture = CompletableFuture.completedFuture((UnifiedMod) null);
        if (searchModrinth) {
            directLookupFuture = tryDirectProjectLookup(searchQuery, projectType);
        }
        
        List<CompletableFuture<List<UnifiedMod>>> futures = new ArrayList<>();
        
        if (searchModrinth) {
            futures.add(searchModrinth(searchQuery, projectType));
        }
        
        if (searchCurseforge && curseForgeClient != null) {
            futures.add(searchCurseForge(searchQuery, projectType));
        }
        
        if (futures.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenCombine(directLookupFuture, (apiResults, directMod) -> {
                ConcurrentHashMap<String, UnifiedMod> mergedMods = new ConcurrentHashMap<>();
                
                if (directMod != null) {
                    String key = normalizeName(directMod.getName());
                    mergedMods.put(key, directMod);
                }
                
                for (CompletableFuture<List<UnifiedMod>> future : futures) {
                    List<UnifiedMod> results = future.join();
                    if (results != null) {
                        for (UnifiedMod mod : results) {
                            String key = normalizeName(mod.getName());
                            UnifiedMod existing = mergedMods.get(key);
                            if (existing == null) {
                                mergedMods.put(key, mod);
                            } else {
                                existing.setDownloads(existing.getDownloads() + mod.getDownloads());
                                existing.setSource(existing.getSource() + "+" + mod.getSource());
                            }
                        }
                    }
                }

                List<UnifiedMod> allResults = new ArrayList<>(mergedMods.values());
                allResults.sort((a, b) -> Long.compare(b.getDownloads(), a.getDownloads()));

                buildIndex(allResults);
                cachedProjects.set(allResults);

                List<UnifiedMod> enhanced = enhancedSearchMods(allResults, searchQuery, gameVersion);
                logger.info("Unified search '{}' returned {} results", searchQuery, enhanced.size());
                return enhanced;
            });
    }
    
    private CompletableFuture<UnifiedMod> tryDirectProjectLookup(String query, String projectType) {
        String rawQuery = query.trim();
        if (rawQuery.length() < 2) {
            return CompletableFuture.completedFuture(null);
        }
        
        logger.info("Direct project lookup for: '{}'", rawQuery);
        
        return modrinthClient.getProject(rawQuery)
            .handle((project, ex) -> {
                if (ex != null || project == null) {
                    logger.info("Direct lookup for '{}' failed or not found: {}", rawQuery, ex != null ? ex.getMessage() : "project null");
                    return null;
                }
                logger.info("Direct lookup found: {} (slug: {})", project.getName(), project.getSlug());
                return toUnifiedMod(project);
            });
    }
    
    public enum SearchSource {
        MODRINTH,
        CURSEFORGE,
        BOTH
    }
    
    private CompletableFuture<List<UnifiedMod>> searchPopular(String projectType, SearchSource source) {
        boolean searchMr = (source == SearchSource.BOTH || source == SearchSource.MODRINTH);
        boolean searchCf = (source == SearchSource.BOTH || source == SearchSource.CURSEFORGE);
        return searchPopular(projectType, searchMr, searchCf);
    }
    
    private CompletableFuture<List<UnifiedMod>> searchPopular(String projectType, boolean searchModrinth, boolean searchCurseforge) {
        CompletableFuture<List<UnifiedMod>> mrFuture;
        CompletableFuture<List<UnifiedMod>> cfFuture;
        
        logger.info("searchPopular: Modrinth={}, CurseForge={}", searchModrinth, searchCurseforge);
        
        if (searchModrinth) {
            mrFuture = modrinthClient.search("", null, projectType, 0, 100, "downloads")
                .handle((result, ex) -> {
                    if (ex != null || result == null || result.getHits() == null) {
                        logger.warn("Modrinth search failed or returned null");
                        return new ArrayList<UnifiedMod>();
                    }
                    List<UnifiedMod> mods = result.getHits().stream()
                        .map(this::toUnifiedMod)
                        .collect(Collectors.toList());
                    logger.info("Modrinth returned {} mods", mods.size());
                    return mods;
                });
        } else {
            mrFuture = CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        if (searchCurseforge && curseForgeClient != null) {
            cfFuture = curseForgeClient.searchMods("", null, 0, 50)
                .handle((result, ex) -> {
                    if (ex != null || result == null || result.getData() == null) {
                        logger.warn("CurseForge search failed or returned null, ex={}", ex != null ? ex.getMessage() : "none");
                        return new ArrayList<UnifiedMod>();
                    }
                    List<UnifiedMod> mods = result.getData().stream()
                        .map(this::toUnifiedMod)
                        .collect(Collectors.toList());
                    logger.info("CurseForge returned {} mods", mods.size());
                    return mods;
                });
        } else {
            cfFuture = CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        return mrFuture.thenCombine(cfFuture, (mrMods, cfMods) -> {
            logger.info("Combining results: Modrinth={}, CurseForge={}", mrMods.size(), cfMods.size());
            
            ConcurrentHashMap<String, UnifiedMod> mergedMods = new ConcurrentHashMap<>();
            
            for (UnifiedMod mod : mrMods) {
                String key = normalizeName(mod.getName());
                UnifiedMod existing = mergedMods.get(key);
                if (existing == null) {
                    mergedMods.put(key, mod);
                } else {
                    existing.setDownloads(existing.getDownloads() + mod.getDownloads());
                    existing.setSource(existing.getSource() + "+" + mod.getSource());
                }
            }
            
            for (UnifiedMod mod : cfMods) {
                String key = normalizeName(mod.getName());
                UnifiedMod existing = mergedMods.get(key);
                if (existing == null) {
                    mergedMods.put(key, mod);
                } else {
                    existing.setDownloads(existing.getDownloads() + mod.getDownloads());
                    existing.setSource(existing.getSource() + "+" + mod.getSource());
                }
            }
            
            List<UnifiedMod> allResults = new ArrayList<>(mergedMods.values());
            allResults.sort((a, b) -> Long.compare(b.getDownloads(), a.getDownloads()));
            
            logger.info("Merged total: {} unique mods", allResults.size());
            
            cachedProjects.set(allResults);
            return allResults;
        });
    }
    
    private String normalizeName(String name) {
        if (name == null) return "";
        return name.toLowerCase().trim();
    }

    private CompletableFuture<List<UnifiedMod>> searchModrinth(String query, String projectType) {
        return modrinthClient.search(query, null, projectType, 0, 500, "relevance")
            .handle((result, ex) -> {
                if (ex != null || result == null || result.getHits() == null) {
                    return new ArrayList<UnifiedMod>();
                }
                return result.getHits().stream()
                    .map(this::toUnifiedMod)
                    .collect(Collectors.toList());
            });
    }

    private CompletableFuture<List<UnifiedMod>> searchCurseForge(String query, String projectType) {
        if (curseForgeClient == null) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        return curseForgeClient.searchMods(query, null, 0, 50)
            .handle((result, ex) -> {
                if (ex != null || result == null || result.getData() == null) {
                    return new ArrayList<UnifiedMod>();
                }
                return result.getData().stream()
                    .map(this::toUnifiedMod)
                    .collect(Collectors.toList());
            });
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

    private void buildIndex(List<UnifiedMod> mods) {
        slugIndex.clear();
        titleIndex.clear();
        prefixIndex.clear();
        containsIndex.clear();

        for (UnifiedMod mod : mods) {
            String slug = normalize(mod.getSlug());
            String title = normalize(mod.getName());
            String titleLower = mod.getName() != null ? mod.getName().toLowerCase() : "";

            if (slug != null && !slug.isEmpty()) {
                slugIndex.computeIfAbsent(slug, k -> new ArrayList<>()).add(mod);
            }

            if (title != null && !title.isEmpty()) {
                titleIndex.computeIfAbsent(title, k -> new ArrayList<>()).add(mod);
            }

            for (int i = 1; i <= Math.min(slug.length(), 8); i++) {
                String prefix = slug.substring(0, i);
                prefixIndex.computeIfAbsent(prefix, k -> new ArrayList<>()).add(mod);
            }

            for (int i = 1; i <= Math.min(title.length(), 8); i++) {
                String prefix = title.substring(0, i);
                prefixIndex.computeIfAbsent(prefix, k -> new ArrayList<>()).add(mod);
            }

            String[] words = titleLower.split("[\\s_-]+");
            for (String word : words) {
                if (word.length() >= 3) {
                    containsIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(mod);
                }
            }
        }

        indexBuilt = true;
        logger.info("Search index built: {} slug, {} title, {} prefix, {} contains entries",
            slugIndex.size(), titleIndex.size(), prefixIndex.size(), containsIndex.size());
    }

    private List<UnifiedMod> enhancedSearchMods(List<UnifiedMod> mods, String query, String gameVersion) {
        if (query == null || query.trim().isEmpty()) {
            return mods;
        }

        String normalized = normalize(query.trim());
        String rawQuery = query.trim().toLowerCase();
        logger.info("Enhanced search for '{}' (normalized: '{}', raw: '{}'), total mods: {}", query, normalized, rawQuery, mods.size());
        
        logger.info("  Checking if any mod slug contains '{}':", rawQuery);
        for (UnifiedMod m : mods) {
            String modSlug = m.getSlug() != null ? m.getSlug().toLowerCase() : "";
            if (modSlug.contains(rawQuery)) {
                logger.info("    FOUND: {} (slug: {})", m.getName(), m.getSlug());
            }
        }

        List<Candidate> candidates = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();

        List<UnifiedMod> exactSlug = slugIndex.get(normalized);
        if (exactSlug != null && !exactSlug.isEmpty()) {
            logger.info("  Exact slug match: {} mods", exactSlug.size());
            for (UnifiedMod m : exactSlug) {
                if (!seen.contains(m.getId())) {
                    candidates.add(new Candidate(m, 1000, MatchType.EXACT_SLUG));
                    seen.add(m.getId());
                }
            }
        }

        List<UnifiedMod> exactTitle = titleIndex.get(normalized);
        if (exactTitle != null && !exactTitle.isEmpty()) {
            logger.info("  Exact title match: {} mods", exactTitle.size());
            for (UnifiedMod m : exactTitle) {
                if (!seen.contains(m.getId())) {
                    candidates.add(new Candidate(m, 950, MatchType.EXACT_TITLE));
                    seen.add(m.getId());
                }
            }
        }

        int prefixMatches = 0;
        for (String key : prefixIndex.keySet()) {
            if (key.startsWith(normalized) && !normalized.equals(key)) {
                prefixMatches++;
                for (UnifiedMod m : prefixIndex.get(key)) {
                    if (!seen.contains(m.getId())) {
                        candidates.add(new Candidate(m, 800, MatchType.PREFIX));
                        seen.add(m.getId());
                    }
                }
            }
        }
        if (prefixMatches > 0) {
            logger.info("  Prefix matches: {} keys matched", prefixMatches);
        }

        int containsMatches = 0;
        for (String key : containsIndex.keySet()) {
            if (key.contains(normalized)) {
                containsMatches++;
                for (UnifiedMod m : containsIndex.get(key)) {
                    if (!seen.contains(m.getId())) {
                        candidates.add(new Candidate(m, 600, MatchType.CONTAINS_WORD));
                        seen.add(m.getId());
                    }
                }
            }
        }
        if (containsMatches > 0) {
            logger.info("  Contains matches: {} keys matched", containsMatches);
        }

        for (UnifiedMod m : mods) {
            if (seen.contains(m.getId())) continue;

            String slug = normalize(m.getSlug());
            String title = normalize(m.getName());

            if (slug.contains(normalized) || title.contains(normalized)) {
                int score = slug.contains(normalized) ? 500 : 450;
                candidates.add(new Candidate(m, score, MatchType.CONTAINS));
                seen.add(m.getId());
                continue;
            }

            if (normalized.length() >= 3) {
                int editDistSlug = levenshteinDistance(normalized, slug);
                int editDistTitle = levenshteinDistance(normalized, title);
                int minDist = Math.min(editDistSlug, editDistTitle);
                if (minDist <= 2) {
                    int score = 300 - minDist * 100;
                    candidates.add(new Candidate(m, score, MatchType.SPELLING));
                    seen.add(m.getId());
                }
            }
        }

        candidates.sort((a, b) -> {
            int cmp = Integer.compare(b.score, a.score);
            if (cmp != 0) return cmp;
            return Long.compare(b.mod.getDownloads(), a.mod.getDownloads());
        });

        logger.info("Enhanced search returned {} candidates, first: {}", candidates.size(), 
            candidates.isEmpty() ? "none" : candidates.get(0).mod.getName());
        return candidates.stream()
            .map(c -> c.mod)
            .collect(Collectors.toList());
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        if (len1 > len2) {
            String tmp = s1; s1 = s2; s2 = tmp;
            len1 = len2;
        }

        int[] prev = new int[len1 + 1];
        int[] curr = new int[len1 + 1];

        for (int i = 0; i <= len1; i++) prev[i] = i;

        for (int j = 1; j <= len2; j++) {
            curr[0] = j;
            for (int i = 1; i <= len1; i++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                curr[i] = Math.min(Math.min(curr[i - 1] + 1, prev[i] + 1), prev[i - 1] + cost);
            }
            int[] tmp = prev; prev = curr; curr = tmp;
        }

        return prev[len1];
    }

    public void shutdown() {
        modrinthClient.close();
    }

    private enum MatchType {
        EXACT_SLUG, EXACT_TITLE, PREFIX, CONTAINS_WORD, CONTAINS, SPELLING
    }

    private static class Candidate {
        UnifiedMod mod;
        int score;
        MatchType type;

        Candidate(UnifiedMod mod, int score, MatchType type) {
            this.mod = mod;
            this.score = score;
            this.type = type;
        }
    }
}