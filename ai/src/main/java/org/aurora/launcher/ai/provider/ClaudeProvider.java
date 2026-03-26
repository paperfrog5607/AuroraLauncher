package org.aurora.launcher.ai.provider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.aurora.launcher.ai.core.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClaudeProvider extends AiProvider {
    
    private static final String DEFAULT_BASE_URL = "https://api.anthropic.com/v1/";
    private static final String DEFAULT_MODEL = "claude-3-opus-20240229";
    private static final MediaType JSON = MediaType.parse("application/json");
    private static final int DEFAULT_MAX_TOKENS = 4096;
    private final Gson gson = new Gson();
    
    public ClaudeProvider(AiConfig config) {
        super(config);
        if (config.getBaseUrl() == null) {
            config.setBaseUrl(DEFAULT_BASE_URL);
        }
        if (config.getModel() == null) {
            config.setModel(DEFAULT_MODEL);
        }
    }
    
    @Override
    public String getName() {
        return "claude";
    }
    
    @Override
    protected Request buildRequest(List<ChatMessage> messages, AiOptions options) {
        JsonObject body = new JsonObject();
        body.addProperty("model", config.getModel());
        
        int maxTokens = options != null && options.getMaxTokens() != null 
                ? options.getMaxTokens() 
                : config.getMaxTokens();
        body.addProperty("max_tokens", maxTokens);
        
        String systemPrompt = null;
        JsonArray msgArray = new JsonArray();
        
        for (ChatMessage msg : messages) {
            if (msg.getRole() == ChatMessage.Role.SYSTEM) {
                systemPrompt = msg.getContent();
            } else {
                JsonObject message = new JsonObject();
                message.addProperty("role", msg.getRole().name().toLowerCase());
                message.addProperty("content", msg.getContent());
                msgArray.add(message);
            }
        }
        
        if (systemPrompt != null) {
            body.addProperty("system", systemPrompt);
        }
        body.add("messages", msgArray);
        
        if (options != null) {
            if (options.getTemperature() != null) {
                body.addProperty("temperature", options.getTemperature());
            }
            if (options.getTopP() != null) {
                body.addProperty("top_p", options.getTopP());
            }
            if (options.getStopSequences() != null && !options.getStopSequences().isEmpty()) {
                body.add("stop_sequences", gson.toJsonTree(options.getStopSequences()));
            }
        }
        
        String url = config.getBaseUrl();
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "messages";
        
        return new Request.Builder()
                .url(url)
                .header("x-api-key", config.getApiKey())
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), JSON))
                .build();
    }
    
    @Override
    protected AiResponse parseResponse(String responseBody) {
        JsonObject json = gson.fromJson(responseBody, JsonObject.class);
        
        StringBuilder content = new StringBuilder();
        JsonArray contentArray = json.getAsJsonArray("content");
        for (int i = 0; i < contentArray.size(); i++) {
            JsonObject block = contentArray.get(i).getAsJsonObject();
            if ("text".equals(block.get("type").getAsString())) {
                content.append(block.get("text").getAsString());
            }
        }
        
        String finishReason = json.has("stop_reason") && !json.get("stop_reason").isJsonNull()
                ? json.get("stop_reason").getAsString()
                : "end_turn";
        
        int inputTokens = 0;
        int outputTokens = 0;
        
        if (json.has("usage")) {
            JsonObject usage = json.getAsJsonObject("usage");
            inputTokens = usage.has("input_tokens") ? usage.get("input_tokens").getAsInt() : 0;
            outputTokens = usage.has("output_tokens") ? usage.get("output_tokens").getAsInt() : 0;
        }
        
        String model = json.has("model") ? json.get("model").getAsString() : config.getModel();
        
        return new AiResponse(
                content.toString(),
                inputTokens,
                outputTokens,
                inputTokens + outputTokens,
                model,
                null,
                mapFinishReason(finishReason)
        );
    }
    
    @Override
    public CompletableFuture<AiResponse> complete(String prompt, AiOptions options) {
        List<ChatMessage> messages = Collections.singletonList(ChatMessage.user(prompt));
        return chat(messages, options);
    }
    
    private AiResponse.FinishReason mapFinishReason(String reason) {
        if (reason == null) {
            return AiResponse.FinishReason.STOP;
        }
        switch (reason) {
            case "end_turn":
            case "stop_sequence":
                return AiResponse.FinishReason.STOP;
            case "max_tokens":
                return AiResponse.FinishReason.LENGTH;
            default:
                return AiResponse.FinishReason.ERROR;
        }
    }
}