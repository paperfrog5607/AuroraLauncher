package org.aurora.launcher.api.modrinth;

import org.aurora.launcher.api.cache.ApiCache;
import org.aurora.launcher.api.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModrinthClient extends ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ModrinthClient.class);
    
    private static final String BASE_URL = "https://api.modrinth.com/v2";
    
    public ModrinthClient() {
        super(BASE_URL, new ApiCache(), new RateLimiter(300, Duration.ofMinutes(1)));
    }
    
    public ModrinthClient(ApiCache cache) {
        super(BASE_URL, cache, new RateLimiter(300, Duration.ofMinutes(1)));
    }
    
    public CompletableFuture<ModrinthSearchResult> search(String query) {
        return search(query, null, null, 0, 20);
    }
    
    public CompletableFuture<ModrinthSearchResult> search(String query, String gameVersion, int offset, int limit) {
        return search(query, gameVersion, null, offset, limit);
    }
    
    public CompletableFuture<ModrinthSearchResult> search(String query, String gameVersion, String projectType, int offset, int limit) {
        return search(query, gameVersion, projectType, offset, limit, "relevance");
    }
    
    public CompletableFuture<ModrinthSearchResult> search(String query, String gameVersion, String projectType, int offset, int limit, String index) {
        StringBuilder endpoint = new StringBuilder("/search?q=");
        endpoint.append(query != null ? URLEncoder.encode(query, StandardCharsets.UTF_8) : "");
        
        StringBuilder facets = new StringBuilder();
        if (gameVersion != null) {
            if (facets.length() > 0) facets.append(",");
            facets.append("%5B%22versions:").append(gameVersion).append("%22%5D");
        }
        if (projectType != null) {
            if (facets.length() > 0) facets.append(",");
            facets.append("%5B%22project_type:").append(projectType).append("%22%5D");
        }
        if (facets.length() > 0) {
            endpoint.append("&facets=%5B").append(facets).append("%5D");
        }
        
        endpoint.append("&offset=").append(offset);
        endpoint.append("&limit=").append(limit);
        endpoint.append("&index=").append(index);
        
        logger.info("Modrinth search endpoint: {}", endpoint.toString());
        return get(endpoint.toString(), ModrinthSearchResult.class);
    }
    
    public CompletableFuture<ModrinthProject> getProject(String slugOrId) {
        return get("/project/" + slugOrId, ModrinthProject.class);
    }
    
    public CompletableFuture<List<ModrinthProject>> getProjects(List<String> ids) {
        String idsJson = gson.toJson(ids);
        return get("/projects?ids=" + idsJson, new com.google.gson.reflect.TypeToken<List<ModrinthProject>>(){}.getType());
    }
    
    public CompletableFuture<List<ModrinthVersion>> getVersions(String projectId) {
        Type type = new com.google.gson.reflect.TypeToken<List<ModrinthVersion>>(){}.getType();
        return get("/project/" + projectId + "/version", type);
    }
    
    public CompletableFuture<ModrinthVersion> getVersion(String versionId) {
        return get("/version/" + versionId, ModrinthVersion.class);
    }
    
    public CompletableFuture<ModrinthVersion> getVersionByHash(String hash, String algorithm) {
        return get("/version_file/" + hash + "?algorithm=" + algorithm, ModrinthVersion.class);
    }
    
    public CompletableFuture<List<ModrinthCategory>> getCategories() {
        Type type = new com.google.gson.reflect.TypeToken<List<ModrinthCategory>>(){}.getType();
        return get("/tag/category", type);
    }
}