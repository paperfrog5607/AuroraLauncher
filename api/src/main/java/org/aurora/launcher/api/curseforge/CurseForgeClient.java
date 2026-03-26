package org.aurora.launcher.api.curseforge;

import org.aurora.launcher.api.cache.ApiCache;
import org.aurora.launcher.api.common.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CurseForgeClient extends ApiClient {
    
    private static final String BASE_URL = "https://api.curseforge.com/v1";
    private static final int MINECRAFT_GAME_ID = 432;
    private static final int MODS_CLASS_ID = 6;
    
    private final String apiKey;
    
    public CurseForgeClient(String apiKey) {
        super(BASE_URL, new ApiCache(), new RateLimiter(100, Duration.ofMinutes(1)));
        this.apiKey = apiKey;
    }
    
    public CurseForgeClient(String apiKey, ApiCache cache) {
        super(BASE_URL, cache, new RateLimiter(100, Duration.ofMinutes(1)));
        this.apiKey = apiKey;
    }
    
    @Override
    protected Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", apiKey);
        headers.put("Accept", "application/json");
        return headers;
    }
    
    public CompletableFuture<CurseForgeSearchResult> searchMods(String query) {
        return searchMods(query, null, 0, 20);
    }
    
    public CompletableFuture<CurseForgeSearchResult> searchMods(String query, String gameVersion, int index, int pageSize) {
        return searchMods(query, gameVersion, index, pageSize, 4);
    }
    
    public CompletableFuture<CurseForgeSearchResult> searchMods(String query, String gameVersion, int index, int pageSize, int sortField) {
        StringBuilder endpoint = new StringBuilder("/mods/search?gameId=");
        endpoint.append(MINECRAFT_GAME_ID);
        endpoint.append("&classId=").append(MODS_CLASS_ID);
        
        if (query != null && !query.isEmpty()) {
            endpoint.append("&searchFilter=").append(query);
        }
        
        if (gameVersion != null) {
            endpoint.append("&gameVersion=").append(gameVersion);
        }
        
        endpoint.append("&sortField=").append(sortField);
        endpoint.append("&index=").append(index);
        endpoint.append("&pageSize=").append(pageSize);
        
        logger.info("CurseForge search request: {}", endpoint);
        CompletableFuture<CurseForgeSearchResult> future = get(endpoint.toString(), CurseForgeSearchResult.class);
        
        future.thenAccept(result -> {
            if (result == null) {
                logger.error("CurseForge returned NULL result!");
            } else if (result.getData() == null) {
                logger.error("CurseForge returned NULL data!");
            } else {
                logger.info("CurseForge returned {} results, total: {}", 
                    result.getData().size(),
                    result.getPagination() != null ? result.getPagination().getTotalCount() : 0);
            }
        });
        
        return future;
    }
    
    public CompletableFuture<CurseForgeMod> getMod(int modId) {
        Type type = new com.google.gson.reflect.TypeToken<CurseForgeModResponse>(){}.getType();
        return get("/mods/" + modId, type)
                .thenApply(response -> {
                    if (response instanceof CurseForgeModResponse) {
                        return ((CurseForgeModResponse) response).getData();
                    }
                    return null;
                });
    }
    
    public CompletableFuture<List<CurseForgeFile>> getFiles(int modId) {
        Type type = new com.google.gson.reflect.TypeToken<CurseForgeFilesResponse>(){}.getType();
        return get("/mods/" + modId + "/files", type)
                .thenApply(response -> {
                    if (response instanceof CurseForgeFilesResponse) {
                        return ((CurseForgeFilesResponse) response).getData();
                    }
                    return null;
                });
    }
    
    public CompletableFuture<List<CurseForgeCategory>> getCategories() {
        Type type = new com.google.gson.reflect.TypeToken<CurseForgeCategoriesResponse>(){}.getType();
        return get("/categories?gameId=" + MINECRAFT_GAME_ID, type)
                .thenApply(response -> {
                    if (response instanceof CurseForgeCategoriesResponse) {
                        return ((CurseForgeCategoriesResponse) response).getData();
                    }
                    return null;
                });
    }
    
    public static class CurseForgeSearchResult {
        private List<CurseForgeMod> data;
        private Pagination pagination;
        
        public List<CurseForgeMod> getData() {
            return data;
        }
        
        public void setData(List<CurseForgeMod> data) {
            this.data = data;
        }
        
        public Pagination getPagination() {
            return pagination;
        }
        
        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }
    
    public static class Pagination {
        private int index;
        private int pageSize;
        private int resultCount;
        private int totalCount;
        
        public int getIndex() {
            return index;
        }
        
        public void setIndex(int index) {
            this.index = index;
        }
        
        public int getPageSize() {
            return pageSize;
        }
        
        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
        
        public int getResultCount() {
            return resultCount;
        }
        
        public void setResultCount(int resultCount) {
            this.resultCount = resultCount;
        }
        
        public int getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }
    
    private static class CurseForgeModResponse {
        private CurseForgeMod data;
        
        public CurseForgeMod getData() {
            return data;
        }
        
        public void setData(CurseForgeMod data) {
            this.data = data;
        }
    }
    
    private static class CurseForgeFilesResponse {
        private List<CurseForgeFile> data;
        
        public List<CurseForgeFile> getData() {
            return data;
        }
        
        public void setData(List<CurseForgeFile> data) {
            this.data = data;
        }
    }
    
    private static class CurseForgeCategoriesResponse {
        private List<CurseForgeCategory> data;
        
        public List<CurseForgeCategory> getData() {
            return data;
        }
        
        public void setData(List<CurseForgeCategory> data) {
            this.data = data;
        }
    }
}