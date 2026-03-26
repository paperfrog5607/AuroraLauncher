package org.aurora.launcher.ai.core;

import okhttp3.*;
import org.aurora.launcher.core.net.HttpClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AiProvider implements AiService {
    
    protected final AiConfig config;
    protected final HttpClient httpClient;
    
    protected AiProvider(AiConfig config) {
        this.config = config;
        this.httpClient = new HttpClient();
    }
    
    protected AiProvider(AiConfig config, HttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }
    
    public AiConfig getConfig() {
        return config;
    }
    
    @Override
    public int getMaxTokens() {
        return config.getMaxTokens();
    }
    
    @Override
    public boolean isAvailable() {
        return config.getApiKey() != null && !config.getApiKey().isEmpty();
    }
    
    @Override
    public CompletableFuture<AiResponse> chat(List<ChatMessage> messages, AiOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = buildRequest(messages, options);
                try (okhttp3.Response response = executeRequest(request)) {
                    if (!response.isSuccessful()) {
                        throw new RuntimeException("AI request failed: " + response.code());
                    }
                    ResponseBody body = response.body();
                    if (body == null) {
                        throw new RuntimeException("Empty response body");
                    }
                    return parseResponse(body.string());
                }
            } catch (IOException e) {
                throw new RuntimeException("AI request failed", e);
            }
        });
    }
    
    protected okhttp3.Response executeRequest(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(config.getTimeout())
                .readTimeout(config.getTimeout())
                .writeTimeout(config.getTimeout())
                .build();
        return client.newCall(request).execute();
    }
    
    protected abstract Request buildRequest(List<ChatMessage> messages, AiOptions options);
    
    protected abstract AiResponse parseResponse(String responseBody);
}