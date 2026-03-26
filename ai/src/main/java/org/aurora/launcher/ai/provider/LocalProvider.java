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

public class LocalProvider extends AiProvider {
    
    private static final String DEFAULT_BASE_URL = "http://localhost:11434/v1/";
    private static final String DEFAULT_MODEL = "llama2";
    private static final MediaType JSON = MediaType.parse("application/json");
    private final Gson gson = new Gson();
    
    public LocalProvider(AiConfig config) {
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
        return "local";
    }
    
    @Override
    public boolean isAvailable() {
        return config.getBaseUrl() != null && !config.getBaseUrl().isEmpty();
    }
    
    @Override
    protected Request buildRequest(List<ChatMessage> messages, AiOptions options) {
        JsonObject body = new JsonObject();
        body.addProperty("model", config.getModel());
        body.add("messages", buildMessagesArray(messages));
        
        if (options != null) {
            if (options.getTemperature() != null) {
                body.addProperty("temperature", options.getTemperature());
            }
            if (options.getMaxTokens() != null) {
                body.addProperty("max_tokens", options.getMaxTokens());
            }
            if (options.getTopP() != null) {
                body.addProperty("top_p", options.getTopP());
            }
        }
        
        String url = config.getBaseUrl();
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "chat/completions";
        
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), JSON));
        
        if (config.getApiKey() != null && !config.getApiKey().isEmpty()) {
            builder.header("Authorization", "Bearer " + config.getApiKey());
        }
        
        return builder.build();
    }
    
    @Override
    protected AiResponse parseResponse(String responseBody) {
        JsonObject json = gson.fromJson(responseBody, JsonObject.class);
        
        String content = "";
        if (json.has("choices")) {
            JsonObject choice = json.getAsJsonArray("choices").get(0).getAsJsonObject();
            JsonObject message = choice.getAsJsonObject("message");
            content = message.get("content").getAsString();
        } else if (json.has("response")) {
            content = json.get("response").getAsString();
        }
        
        int promptTokens = 0;
        int completionTokens = 0;
        int totalTokens = 0;
        
        if (json.has("usage")) {
            JsonObject usage = json.getAsJsonObject("usage");
            promptTokens = usage.has("prompt_tokens") ? usage.get("prompt_tokens").getAsInt() : 0;
            completionTokens = usage.has("completion_tokens") ? usage.get("completion_tokens").getAsInt() : 0;
            totalTokens = usage.has("total_tokens") ? usage.get("total_tokens").getAsInt() : 0;
        }
        
        String model = json.has("model") ? json.get("model").getAsString() : config.getModel();
        
        return new AiResponse(content, promptTokens, completionTokens, totalTokens, model, null, AiResponse.FinishReason.STOP);
    }
    
    @Override
    public CompletableFuture<AiResponse> complete(String prompt, AiOptions options) {
        List<ChatMessage> messages = Collections.singletonList(ChatMessage.user(prompt));
        return chat(messages, options);
    }
    
    private JsonArray buildMessagesArray(List<ChatMessage> messages) {
        JsonArray array = new JsonArray();
        for (ChatMessage msg : messages) {
            JsonObject message = new JsonObject();
            message.addProperty("role", msg.getRole().name().toLowerCase());
            message.addProperty("content", msg.getContent());
            array.add(message);
        }
        return array;
    }
}