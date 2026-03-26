package org.aurora.launcher.ai.provider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.aurora.launcher.ai.core.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class OpenAIProviderTest {

    private MockWebServer mockWebServer;
    private Gson gson = new Gson();

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnCorrectName() {
        AiConfig config = new AiConfig();
        OpenAIProvider provider = new OpenAIProvider(config);
        
        assertEquals("openai", provider.getName());
    }

    @Test
    void shouldBuildCorrectRequest() throws Exception {
        String baseUrl = mockWebServer.url("/v1/").toString();
        AiConfig config = AiConfig.builder()
                .apiKey("test-api-key")
                .baseUrl(baseUrl)
                .model("gpt-4")
                .build();
        
        OpenAIProvider provider = new OpenAIProvider(config);
        
        mockWebServer.enqueue(new MockResponse()
                .setBody(createSuccessResponse("Hello!"))
                .addHeader("Content-Type", "application/json"));
        
        List<ChatMessage> messages = Arrays.asList(
            ChatMessage.system("You are helpful"),
            ChatMessage.user("Hi")
        );
        
        provider.chat(messages, new AiOptions()).get(5, TimeUnit.SECONDS);
        
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/v1/chat/completions", request.getPath());
        assertEquals("Bearer test-api-key", request.getHeader("Authorization"));
        
        JsonObject body = gson.fromJson(request.getBody().readUtf8(), JsonObject.class);
        assertEquals("gpt-4", body.get("model").getAsString());
        assertEquals(2, body.getAsJsonArray("messages").size());
    }

    @Test
    void shouldParseResponseCorrectly() throws Exception {
        String baseUrl = mockWebServer.url("/v1/").toString();
        AiConfig config = AiConfig.builder()
                .apiKey("test-key")
                .baseUrl(baseUrl)
                .model("gpt-4")
                .build();
        
        OpenAIProvider provider = new OpenAIProvider(config);
        
        mockWebServer.enqueue(new MockResponse()
                .setBody(createSuccessResponse("Hello, how can I help?"))
                .addHeader("Content-Type", "application/json"));
        
        List<ChatMessage> messages = Arrays.asList(ChatMessage.user("Hi"));
        AiResponse response = provider.chat(messages, new AiOptions()).get(5, TimeUnit.SECONDS);
        
        assertEquals("Hello, how can I help?", response.getContent());
        assertEquals("gpt-4", response.getModel());
        assertEquals(AiResponse.FinishReason.STOP, response.getFinishReason());
    }

    @Test
    void shouldIncludeOptionsInRequest() throws Exception {
        String baseUrl = mockWebServer.url("/v1/").toString();
        AiConfig config = AiConfig.builder()
                .apiKey("test-key")
                .baseUrl(baseUrl)
                .model("gpt-4")
                .build();
        
        OpenAIProvider provider = new OpenAIProvider(config);
        
        mockWebServer.enqueue(new MockResponse()
                .setBody(createSuccessResponse("Response"))
                .addHeader("Content-Type", "application/json"));
        
        AiOptions options = AiOptions.builder()
                .temperature(0.5)
                .maxTokens(100)
                .topP(0.9)
                .build();
        
        provider.chat(Arrays.asList(ChatMessage.user("Hi")), options).get(5, TimeUnit.SECONDS);
        
        RecordedRequest request = mockWebServer.takeRequest();
        JsonObject body = gson.fromJson(request.getBody().readUtf8(), JsonObject.class);
        
        assertEquals(0.5, body.get("temperature").getAsDouble(), 0.001);
        assertEquals(100, body.get("max_tokens").getAsInt());
        assertEquals(0.9, body.get("top_p").getAsDouble(), 0.001);
    }

    @Test
    void shouldHandleCompleteMethod() throws Exception {
        String baseUrl = mockWebServer.url("/v1/").toString();
        AiConfig config = AiConfig.builder()
                .apiKey("test-key")
                .baseUrl(baseUrl)
                .model("gpt-4")
                .build();
        
        OpenAIProvider provider = new OpenAIProvider(config);
        
        mockWebServer.enqueue(new MockResponse()
                .setBody(createSuccessResponse("Completed text"))
                .addHeader("Content-Type", "application/json"));
        
        AiResponse response = provider.complete("Once upon a time", new AiOptions())
                .get(5, TimeUnit.SECONDS);
        
        assertEquals("Completed text", response.getContent());
        
        RecordedRequest request = mockWebServer.takeRequest();
        JsonObject body = gson.fromJson(request.getBody().readUtf8(), JsonObject.class);
        assertEquals(1, body.getAsJsonArray("messages").size());
    }

    @Test
    void shouldHandleError() throws Exception {
        String baseUrl = mockWebServer.url("/v1/").toString();
        AiConfig config = AiConfig.builder()
                .apiKey("test-key")
                .baseUrl(baseUrl)
                .model("gpt-4")
                .build();
        
        OpenAIProvider provider = new OpenAIProvider(config);
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\": {\"message\": \"Invalid API key\"}}")
                .addHeader("Content-Type", "application/json"));
        
        assertThrows(Exception.class, () -> {
            provider.chat(Arrays.asList(ChatMessage.user("Hi")), new AiOptions())
                    .get(5, TimeUnit.SECONDS);
        });
    }

    private String createSuccessResponse(String content) {
        JsonObject response = new JsonObject();
        response.addProperty("id", "chatcmpl-test");
        response.addProperty("object", "chat.completion");
        response.addProperty("created", System.currentTimeMillis() / 1000);
        response.addProperty("model", "gpt-4");
        
        JsonObject choice = new JsonObject();
        choice.addProperty("index", 0);
        JsonObject message = new JsonObject();
        message.addProperty("role", "assistant");
        message.addProperty("content", content);
        choice.add("message", message);
        choice.addProperty("finish_reason", "stop");
        response.add("choices", gson.toJsonTree(Arrays.asList(choice)));
        
        JsonObject usage = new JsonObject();
        usage.addProperty("prompt_tokens", 10);
        usage.addProperty("completion_tokens", 5);
        usage.addProperty("total_tokens", 15);
        response.add("usage", usage);
        
        return gson.toJson(response);
    }
}