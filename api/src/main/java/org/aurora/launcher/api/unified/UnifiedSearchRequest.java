package org.aurora.launcher.api.unified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnifiedSearchRequest {
    
    public static final int DEFAULT_LIMIT = 200;
    public static final int SEARCH_LIMIT = 200;
    public static final int POPULAR_LIMIT = 1000;
    
    public enum Source {
        MODRINTH("modrinth"),
        CURSEFORGE("curseforge"),
        BOTH("both");
        
        private final String value;
        
        Source(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    public enum SortBy {
        RELEVANCE("relevance", 0),
        DOWNLOADS("downloads", 1),
        UPDATED("updated", 2),
        NEWEST("newest", 3),
        NAME("name", 4);
        
        private final String apiValue;
        private final int priority;
        
        SortBy(String apiValue, int priority) {
            this.apiValue = apiValue;
            this.priority = priority;
        }
        
        public String getApiValue() {
            return apiValue;
        }
        
        public int getPriority() {
            return priority;
        }
    }
    
    private String query;
    private Source source = Source.BOTH;
    private SortBy sortBy = SortBy.RELEVANCE;
    private SearchFilter filter = new SearchFilter();
    private int offset = 0;
    private int limit = DEFAULT_LIMIT;
    private boolean enableDirectLookup = true;
    private boolean enableFuzzySearch = true;
    private List<String> boostSlugs = new ArrayList<>();
    
    public UnifiedSearchRequest() {
    }
    
    public UnifiedSearchRequest(String query) {
        this.query = query;
    }
    
    public static UnifiedSearchRequest of(String query) {
        return new UnifiedSearchRequest(query);
    }
    
    public UnifiedSearchRequest query(String query) {
        this.query = query;
        return this;
    }
    
    public UnifiedSearchRequest source(Source source) {
        this.source = source;
        return this;
    }
    
    public UnifiedSearchRequest source(String source) {
        this.source = Source.valueOf(source.toUpperCase());
        return this;
    }
    
    public UnifiedSearchRequest sortBy(SortBy sortBy) {
        this.sortBy = sortBy;
        return this;
    }
    
    public UnifiedSearchRequest filter(SearchFilter filter) {
        this.filter = filter;
        return this;
    }
    
    public UnifiedSearchRequest offset(int offset) {
        this.offset = offset;
        return this;
    }
    
    public UnifiedSearchRequest limit(int limit) {
        this.limit = limit;
        return this;
    }
    
    public UnifiedSearchRequest enableDirectLookup(boolean enable) {
        this.enableDirectLookup = enable;
        return this;
    }
    
    public UnifiedSearchRequest enableFuzzySearch(boolean enable) {
        this.enableFuzzySearch = enable;
        return this;
    }
    
    public UnifiedSearchRequest boostSlugs(String... slugs) {
        this.boostSlugs.addAll(Arrays.asList(slugs));
        return this;
    }
    
    public UnifiedSearchRequest gameVersion(String version) {
        this.filter.gameVersion(version);
        return this;
    }
    
    public UnifiedSearchRequest loader(String loader) {
        this.filter.loader(loader);
        return this;
    }
    
    public String getQuery() {
        return query;
    }
    
    public Source getSource() {
        return source;
    }
    
    public SortBy getSortBy() {
        return sortBy;
    }
    
    public SearchFilter getFilter() {
        return filter;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public boolean isEnableDirectLookup() {
        return enableDirectLookup;
    }
    
    public boolean isEnableFuzzySearch() {
        return enableFuzzySearch;
    }
    
    public List<String> getBoostSlugs() {
        return boostSlugs;
    }
    
    public boolean isPopularSearch() {
        return query == null || query.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "UnifiedSearchRequest{" +
                "query='" + query + '\'' +
                ", source=" + source +
                ", sortBy=" + sortBy +
                ", filter=" + filter +
                ", offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
