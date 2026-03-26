package org.aurora.launcher.ui.service;

import org.aurora.launcher.api.modrinth.ModrinthProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EnhancedSearchService {
    private static final Logger logger = LoggerFactory.getLogger(EnhancedSearchService.class);
    
    private final Map<String, List<ModrinthProject>> slugIndex = new ConcurrentHashMap<>();
    private final Map<String, List<ModrinthProject>> titleIndex = new ConcurrentHashMap<>();
    private final Map<String, List<ModrinthProject>> prefixIndex = new ConcurrentHashMap<>();
    private final Map<String, List<ModrinthProject>> containsIndex = new ConcurrentHashMap<>();
    
    private volatile boolean indexBuilt = false;
    private final List<ModrinthProject> cachedPopular = new ArrayList<>();
    private final Map<String, ModrinthProject> projectCache = new ConcurrentHashMap<>();
    
    private static final int MAX_RESULTS = 1000;
    private static final int MAX_EDIT_DISTANCE = 2;
    
    public void buildIndex(List<ModrinthProject> projects) {
        if (projects == null || projects.isEmpty()) return;
        
        slugIndex.clear();
        titleIndex.clear();
        prefixIndex.clear();
        containsIndex.clear();
        projectCache.clear();
        
        for (ModrinthProject p : projects) {
            if (p == null) continue;
            String projectId = p.getId();
            if (projectId != null) {
                projectCache.put(projectId, p);
            }
            
            String slug = normalize(p.getSlug());
            String title = normalize(p.getName());
            String titleLower = p.getName() != null ? p.getName().toLowerCase() : "";
            
            if (slug != null && !slug.isEmpty()) {
                slugIndex.computeIfAbsent(slug, k -> new ArrayList<>()).add(p);
            }
            
            if (title != null && !title.isEmpty()) {
                titleIndex.computeIfAbsent(title, k -> new ArrayList<>()).add(p);
            }
            
            for (int i = 1; i <= Math.min(slug.length(), 8); i++) {
                String prefix = slug.substring(0, i);
                prefixIndex.computeIfAbsent(prefix, k -> new ArrayList<>()).add(p);
            }
            
            for (int i = 1; i <= Math.min(title.length(), 8); i++) {
                String prefix = title.substring(0, i);
                prefixIndex.computeIfAbsent(prefix, k -> new ArrayList<>()).add(p);
            }
            
            String[] words = titleLower.split("[\\s_-]+");
            for (String word : words) {
                if (word.length() >= 3) {
                    containsIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(p);
                }
            }
        }
        
        indexBuilt = true;
        logger.info("Search index built: {} slug, {} title, {} prefix, {} contains entries",
            slugIndex.size(), titleIndex.size(), prefixIndex.size(), containsIndex.size());
    }
    
    public void cachePopular(List<ModrinthProject> projects) {
        synchronized (cachedPopular) {
            cachedPopular.clear();
            if (projects != null) {
                cachedPopular.addAll(projects.stream()
                    .sorted(Comparator.comparingLong(ModrinthProject::getDownloads).reversed())
                    .limit(500)
                    .collect(Collectors.toList()));
            }
        }
    }
    
    public List<ModrinthProject> search(String query, String gameVersion, String loader) {
        if (query == null || query.trim().isEmpty()) {
            return getPopular();
        }
        
        String normalized = normalize(query.trim());
        
        List<Candidate> candidates = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        
        List<ModrinthProject> exactSlug = slugIndex.get(normalized);
        if (exactSlug != null) {
            for (ModrinthProject p : exactSlug) {
                if (!seen.contains(p.getId())) {
                    candidates.add(new Candidate(p, 1000, MatchType.EXACT_SLUG));
                    seen.add(p.getId());
                }
            }
        }
        
        List<ModrinthProject> exactTitle = titleIndex.get(normalized);
        if (exactTitle != null) {
            for (ModrinthProject p : exactTitle) {
                if (!seen.contains(p.getId())) {
                    candidates.add(new Candidate(p, 950, MatchType.EXACT_TITLE));
                    seen.add(p.getId());
                }
            }
        }
        
        for (String key : prefixIndex.keySet()) {
            if (key.startsWith(normalized) && !normalized.equals(key)) {
                for (ModrinthProject p : prefixIndex.get(key)) {
                    if (!seen.contains(p.getId())) {
                        candidates.add(new Candidate(p, 800, MatchType.PREFIX));
                        seen.add(p.getId());
                    }
                }
            }
        }
        
        for (String key : containsIndex.keySet()) {
            if (key.contains(normalized)) {
                for (ModrinthProject p : containsIndex.get(key)) {
                    if (!seen.contains(p.getId())) {
                        candidates.add(new Candidate(p, 600, MatchType.CONTAINS_WORD));
                        seen.add(p.getId());
                    }
                }
            }
        }
        
        for (ModrinthProject p : projectCache.values()) {
            if (seen.contains(p.getId())) continue;
            
            String slug = normalize(p.getSlug());
            String title = normalize(p.getName());
            
            if (slug.contains(normalized) || title.contains(normalized)) {
                int score = slug.contains(normalized) ? 500 : 450;
                candidates.add(new Candidate(p, score, MatchType.CONTAINS));
                seen.add(p.getId());
                continue;
            }
            
            if (normalized.length() >= 3) {
                int editDistSlug = levenshteinDistance(normalized, slug);
                int editDistTitle = levenshteinDistance(normalized, title);
                int minDist = Math.min(editDistSlug, editDistTitle);
                
                if (minDist <= MAX_EDIT_DISTANCE) {
                    int score = 300 - minDist * 100;
                    candidates.add(new Candidate(p, score, MatchType.SPELLING));
                    seen.add(p.getId());
                }
            }
        }
        
        candidates.sort((a, b) -> {
            int cmp = Integer.compare(b.score, a.score);
            if (cmp != 0) return cmp;
            return Long.compare(b.project.getDownloads(), a.project.getDownloads());
        });
        
        List<ModrinthProject> results = candidates.stream()
            .map(c -> c.project)
            .filter(p -> filterByEnvironment(p, gameVersion, loader))
            .limit(MAX_RESULTS)
            .collect(Collectors.toList());
        
        logger.info("Search '{}' -> {} results (exact={}, prefix={}, contains={}, spelling={})",
            query, results.size(),
            candidates.stream().filter(c -> c.type == MatchType.EXACT_SLUG || c.type == MatchType.EXACT_TITLE).count(),
            candidates.stream().filter(c -> c.type == MatchType.PREFIX).count(),
            candidates.stream().filter(c -> c.type == MatchType.CONTAINS || c.type == MatchType.CONTAINS_WORD).count(),
            candidates.stream().filter(c -> c.type == MatchType.SPELLING).count());
        
        return results;
    }
    
    private boolean filterByEnvironment(ModrinthProject p, String gameVersion, String loader) {
        if (gameVersion == null && loader == null) return true;
        
        if (p.getGameVersions() != null && gameVersion != null) {
            boolean versionMatch = p.getGameVersions().stream()
                .anyMatch(v -> v.contains(gameVersion) || gameVersion.contains(v));
            if (!versionMatch) return false;
        }
        
        if (p.getLoaders() != null && loader != null) {
            boolean loaderMatch = p.getLoaders().stream()
                .anyMatch(l -> l.equalsIgnoreCase(loader));
            if (!loaderMatch) return false;
        }
        
        return true;
    }
    
    private List<ModrinthProject> getPopular() {
        synchronized (cachedPopular) {
            return new ArrayList<>(cachedPopular);
        }
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
    
    private enum MatchType {
        EXACT_SLUG, EXACT_TITLE, PREFIX, CONTAINS_WORD, CONTAINS, SPELLING
    }
    
    private static class Candidate {
        ModrinthProject project;
        int score;
        MatchType type;
        
        Candidate(ModrinthProject project, int score, MatchType type) {
            this.project = project;
            this.score = score;
            this.type = type;
        }
    }
}