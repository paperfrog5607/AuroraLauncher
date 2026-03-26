package org.aurora.launcher.api.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.aurora.launcher.api.cache.ApiCache;
import org.aurora.launcher.core.net.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ApiClient {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final HttpClient httpClient;
    protected final ApiCache cache;
    protected final RateLimiter rateLimiter;
    protected final String baseUrl;
    protected final Gson gson;
    
    protected ApiClient(String baseUrl) {
        this(baseUrl, new ApiCache(), new RateLimiter(300, Duration.ofMinutes(1)));
    }
    
    protected ApiClient(String baseUrl, ApiCache cache, RateLimiter rateLimiter) {
        this.baseUrl = baseUrl;
        this.cache = cache;
        this.rateLimiter = rateLimiter;
        this.httpClient = new HttpClient();
        this.gson = new Gson();
    }
    
    protected <T> CompletableFuture<T> get(String endpoint, Type type) {
        return get(endpoint, type, Duration.ofHours(1));
    }
    
    protected <T> CompletableFuture<T> get(String endpoint, Type type, Duration cacheTtl) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = cache.generateKey(endpoint, null);
            
            java.util.Optional<T> cached = cache.get(cacheKey, type);
            if (cached.isPresent()) {
                logger.debug("Cache hit for {}", endpoint);
                return cached.get();
            }
            
            rateLimiter.acquire();
            
            try {
                String url = buildUrl(endpoint);
                logger.debug("GET {}", url);
                
                String response = httpClient.get(url, getHeaders());
                logger.debug("Response length: {} chars", response.length());
                
                T result = gson.fromJson(response, type);
                
                logger.debug("API response preview: {}", response.substring(0, Math.min(200, response.length())));
                
                cache.put(cacheKey, result, cacheTtl);
                
                return result;
            } catch (IOException e) {
                logger.error("API request failed for {}: {} - {}", endpoint, e.getClass().getSimpleName(), e.getMessage());
                throw new ApiException("API request failed: " + endpoint, e);
            }
        });
    }
    
    protected <T> CompletableFuture<T> post(String endpoint, Object body, Type type) {
        return CompletableFuture.supplyAsync(() -> {
            rateLimiter.acquire();
            
            try {
                String url = buildUrl(endpoint);
                String jsonBody = gson.toJson(body);
                
                logger.debug("POST {}", url);
                
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                        .build();
                
                Request request = new Request.Builder()
                        .url(url)
                        .headers(Headers.of(getHeaders()))
                        .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                        .build();
                
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new ApiException("API request failed: " + response.code());
                    }
                    
                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        throw new ApiException("Empty response body");
                    }
                    
                    return gson.fromJson(responseBody.string(), type);
                }
            } catch (IOException e) {
                throw new ApiException("API request failed: " + endpoint, e);
            }
        });
    }
    
    protected String buildUrl(String endpoint) {
        if (endpoint.startsWith("http")) {
            return endpoint;
        }
        return baseUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
    }
    
    protected Map<String, String> getHeaders() {
        return new java.util.HashMap<>();
    }
    
    public ApiCache getCache() {
        return cache;
    }
    
    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }
    
    public void close() {
        httpClient.close();
    }
}