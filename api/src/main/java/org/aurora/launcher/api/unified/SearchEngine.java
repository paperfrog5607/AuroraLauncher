package org.aurora.launcher.api.unified;

import java.util.*;

class SearchEngine {
    
    private final Map<String, ModInfo> modIndex = new HashMap<>();
    
    private static class ModInfo {
        String id;
        String name;
        String slug;
        String normalizedName;
        String normalizedSlug;
        
        ModInfo(String id, String name, String slug) {
            this.id = id;
            this.name = name != null ? name : "";
            this.slug = slug != null ? slug : "";
            this.normalizedName = normalizeStatic(this.name);
            this.normalizedSlug = normalizeStatic(this.slug);
        }
    }
    
    void buildIndex(List<UnifiedMod> mods) {
        modIndex.clear();
        
        for (UnifiedMod mod : mods) {
            if (mod.getId() == null) continue;
            modIndex.put(mod.getId(), new ModInfo(mod.getId(), mod.getName(), mod.getSlug()));
        }
    }
    
    List<SearchResult> search(String query, List<UnifiedMod> allMods) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String normalizedQuery = normalize(query.trim());
        List<SearchResult> exactMatches = new ArrayList<>();
        List<SearchResult> fuzzyMatches = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        
        for (UnifiedMod mod : allMods) {
            if (mod.getId() == null || seen.contains(mod.getId())) continue;
            
            ModInfo info = modIndex.get(mod.getId());
            if (info == null) continue;
            
            int score = calculateScore(info, normalizedQuery, query);
            
            if (score > 0) {
                SearchResult result = new SearchResult(mod, score);
                seen.add(mod.getId());
                
                if (score >= 500) {
                    exactMatches.add(result);
                } else {
                    fuzzyMatches.add(result);
                }
            }
        }
        
        Collections.sort(exactMatches, (a, b) -> {
            int cmp = Integer.compare(b.score, a.score);
            if (cmp != 0) return cmp;
            return Long.compare(b.mod.getTotalDownloads(), a.mod.getTotalDownloads());
        });
        
        Collections.sort(fuzzyMatches, (a, b) -> {
            int cmp = Integer.compare(b.score, a.score);
            if (cmp != 0) return cmp;
            return Long.compare(b.mod.getTotalDownloads(), a.mod.getTotalDownloads());
        });
        
        List<SearchResult> results = new ArrayList<>(exactMatches);
        results.addAll(fuzzyMatches);
        
        return results;
    }
    
    private int calculateScore(ModInfo info, String normalizedQuery, String rawQuery) {
        String rawLower = rawQuery.toLowerCase();
        
        if (info.normalizedSlug.equals(normalizedQuery)) {
            return 1000;
        }
        
        if (info.normalizedName.equals(normalizedQuery)) {
            return 950;
        }
        
        if (info.normalizedSlug.startsWith(normalizedQuery)) {
            return 800;
        }
        
        if (info.normalizedName.startsWith(normalizedQuery)) {
            return 750;
        }
        
        if (info.normalizedSlug.contains(normalizedQuery)) {
            return 600;
        }
        
        if (info.normalizedName.contains(normalizedQuery)) {
            return 550;
        }
        
        for (String part : info.normalizedName.split("[\\s_-]+")) {
            if (part.startsWith(normalizedQuery)) {
                return 500;
            }
        }
        
        int editDistSlug = levenshteinDistance(normalizedQuery, info.normalizedSlug);
        int editDistName = levenshteinDistance(normalizedQuery, info.normalizedName);
        int minDist = Math.min(editDistSlug, editDistName);
        
        if (minDist <= 2) {
            return 400 - minDist * 100;
        }
        
        if (normalizedQuery.length() >= 3) {
            for (int i = 2; i <= normalizedQuery.length(); i++) {
                String prefix = normalizedQuery.substring(0, i);
                if (info.normalizedSlug.startsWith(prefix) || info.normalizedName.startsWith(prefix)) {
                    return 300 + i * 10;
                }
            }
        }
        
        return 0;
    }
    
    int levenshteinDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        
        int len1 = s1.length();
        int len2 = s2.length();
        
        if (len1 > len2) {
            String tmp = s1; s1 = s2; s2 = tmp;
            len1 = len2;
        }
        
        if (len1 == 0) return len2;
        
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
    
    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
    
    private static String normalizeStatic(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
    
    static class SearchResult {
        UnifiedMod mod;
        int score;
        
        SearchResult(UnifiedMod mod, int score) {
            this.mod = mod;
            this.score = score;
        }
    }
}
