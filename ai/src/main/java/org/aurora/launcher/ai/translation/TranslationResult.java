package org.aurora.launcher.ai.translation;

public class TranslationResult {
    
    private final String translatedText;
    private final LanguagePair languagePair;
    private final int tokensUsed;
    
    public TranslationResult(String translatedText, LanguagePair languagePair, int tokensUsed) {
        this.translatedText = translatedText;
        this.languagePair = languagePair;
        this.tokensUsed = tokensUsed;
    }
    
    public String getTranslatedText() {
        return translatedText;
    }
    
    public LanguagePair getLanguagePair() {
        return languagePair;
    }
    
    public int getTokensUsed() {
        return tokensUsed;
    }
}