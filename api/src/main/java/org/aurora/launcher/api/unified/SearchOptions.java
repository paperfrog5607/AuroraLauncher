package org.aurora.launcher.api.unified;

import java.util.ArrayList;
import java.util.List;

public class SearchOptions {
    
    private String query;
    private String gameVersion;
    private String loader;
    private String category;
    private String sortBy = "relevance";
    private int limit = 20;
    private int offset = 0;
    
    public SearchOptions() {
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public String getGameVersion() {
        return gameVersion;
    }
    
    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }
    
    public String getLoader() {
        return loader;
    }
    
    public void setLoader(String loader) {
        this.loader = loader;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final SearchOptions options = new SearchOptions();
        
        public Builder query(String query) {
            options.setQuery(query);
            return this;
        }
        
        public Builder gameVersion(String gameVersion) {
            options.setGameVersion(gameVersion);
            return this;
        }
        
        public Builder loader(String loader) {
            options.setLoader(loader);
            return this;
        }
        
        public Builder category(String category) {
            options.setCategory(category);
            return this;
        }
        
        public Builder sortBy(String sortBy) {
            options.setSortBy(sortBy);
            return this;
        }
        
        public Builder limit(int limit) {
            options.setLimit(limit);
            return this;
        }
        
        public Builder offset(int offset) {
            options.setOffset(offset);
            return this;
        }
        
        public SearchOptions build() {
            return options;
        }
    }
}