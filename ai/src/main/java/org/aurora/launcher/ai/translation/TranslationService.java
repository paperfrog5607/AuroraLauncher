package org.aurora.launcher.ai.translation;

import org.aurora.launcher.ai.core.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TranslationService {
    
    private final AiProvider provider;
    
    public TranslationService(AiProvider provider) {
        this.provider = provider;
    }
    
    public CompletableFuture<TranslationResult> translate(String text, LanguagePair pair) {
        String prompt = buildTranslationPrompt(text, pair);
        return provider.complete(prompt, new AiOptions())
                .thenApply(response -> new TranslationResult(
                        response.getContent(),
                        pair,
                        response.getTotalTokens()
                ));
    }
    
    public CompletableFuture<Map<String, String>> translateBatch(Map<String, String> texts, LanguagePair pair) {
        Map<String, String> results = new java.util.concurrent.ConcurrentHashMap<>();
        
        java.util.List<CompletableFuture<Void>> futures = new java.util.ArrayList<>();
        for (Map.Entry<String, String> entry : texts.entrySet()) {
            CompletableFuture<Void> future = translate(entry.getValue(), pair)
                    .thenAccept(result -> results.put(entry.getKey(), result.getTranslatedText()));
            futures.add(future);
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> results);
    }
    
    private String buildTranslationPrompt(String text, LanguagePair pair) {
        return String.format(
                "Translate the following text from %s to %s. Only output the translation, no explanations.\n\n%s",
                pair.getSource(), pair.getTarget(), text
        );
    }
}