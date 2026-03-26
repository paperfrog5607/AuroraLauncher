package org.aurora.launcher.api.unified;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnifiedSearchResponse {
    
    private List<UnifiedMod> results = new ArrayList<>();
    private Map<String, List<UnifiedMod>> resultsBySource = new HashMap<>();
    private int totalHits = 0;
    private int offset = 0;
    private int limit = 0;
    private long searchTimeMs = 0;
    private boolean hasMore = false;
    private List<String> warnings = new ArrayList<>();
    
    public UnifiedSearchResponse() {
    }
    
    public UnifiedSearchResponse(List<UnifiedMod> results) {
        this.results = results;
    }
    
    public void addResult(UnifiedMod mod) {
        this.results.add(mod);
        String source = mod.getSource();
        resultsBySource.computeIfAbsent(source, k -> new ArrayList<>()).add(mod);
    }
    
    public void addResults(List<UnifiedMod> mods) {
        for (UnifiedMod mod : mods) {
            addResult(mod);
        }
    }
    
    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public void setSearchTimeMs(long searchTimeMs) {
        this.searchTimeMs = searchTimeMs;
    }
    
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
    
    public void addWarning(String warning) {
        this.warnings.add(warning);
    }
    
    public List<UnifiedMod> getResults() {
        return results;
    }
    
    public Map<String, List<UnifiedMod>> getResultsBySource() {
        return resultsBySource;
    }
    
    public List<UnifiedMod> getResultsBySource(String source) {
        return resultsBySource.getOrDefault(source, new ArrayList<>());
    }
    
    public int getTotalHits() {
        return totalHits;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getSearchTimeMs() {
        return searchTimeMs;
    }
    
    public boolean isHasMore() {
        return hasMore;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public int getModrinthCount() {
        return getResultsBySource("modrinth").size();
    }
    
    public int getCurseForgeCount() {
        return getResultsBySource("curseforge").size();
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    @Override
    public String toString() {
        return "UnifiedSearchResponse{" +
                "totalHits=" + totalHits +
                ", returned=" + results.size() +
                ", modrinth=" + getModrinthCount() +
                ", curseforge=" + getCurseForgeCount() +
                ", searchTimeMs=" + searchTimeMs +
                '}';
    }
}
