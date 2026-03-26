package org.aurora.launcher.api.modrinth;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModrinthSearchResult {
    
    private List<ModrinthProject> hits;
    private int offset;
    private int limit;
    @SerializedName("total_hits")
    private int totalHits;
    
    public ModrinthSearchResult() {
        this.hits = new ArrayList<>();
    }
    
    public List<ModrinthProject> getHits() {
        return hits;
    }
    
    public void setHits(List<ModrinthProject> hits) {
        this.hits = hits != null ? hits : new ArrayList<>();
    }
    
    public int getOffset() {
        return offset;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public int getTotalHits() {
        return totalHits;
    }
    
    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }
}