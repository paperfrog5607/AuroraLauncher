package org.aurora.launcher.ai.review;

import org.aurora.launcher.ai.core.*;
import org.aurora.launcher.ai.script.GeneratedScript;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeReviewer {
    
    private final AiProvider provider;
    
    public CodeReviewer(AiProvider provider) {
        this.provider = provider;
    }
    
    public CompletableFuture<ReviewResult> review(String code, String language) {
        String prompt = buildPrompt(code, language);
        
        AiOptions options = new AiOptions();
        options.setMaxTokens(2048);
        options.setTemperature(0.3);
        
        return provider.complete(prompt, options)
                .thenApply(this::parseResult);
    }
    
    public CompletableFuture<ReviewResult> reviewScript(GeneratedScript script) {
        String language = getLanguage(script.getType());
        return review(script.getCode(), language);
    }
    
    private String buildPrompt(String code, String language) {
        return String.format(
                "Review the following %s code for:\n" +
                "1. Bugs and errors\n" +
                "2. Performance issues\n" +
                "3. Best practices\n" +
                "4. Security concerns\n\n" +
                "Output format:\n" +
                "Score: [0-100]\n" +
                "Issues: [ERROR|WARNING|INFO]: description\n" +
                "Suggestions: - suggestion\n" +
                "Summary: brief summary\n\n" +
                "```%s\n%s\n```",
                language, language, code
        );
    }
    
    private ReviewResult parseResult(AiResponse response) {
        ReviewResult result = new ReviewResult();
        String content = response.getContent();
        
        Pattern scorePattern = Pattern.compile("Score:\\s*(\\d+)");
        Matcher scoreMatcher = scorePattern.matcher(content);
        if (scoreMatcher.find()) {
            result.setScore(Integer.parseInt(scoreMatcher.group(1)));
        }
        
        Pattern issuePattern = Pattern.compile("(ERROR|WARNING|INFO):\\s*(.+)");
        Matcher issueMatcher = issuePattern.matcher(content);
        while (issueMatcher.find()) {
            IssueSeverity severity = IssueSeverity.valueOf(issueMatcher.group(1));
            String description = issueMatcher.group(2).trim();
            result.addIssue(new CodeIssue(severity, description));
        }
        
        Pattern suggestionPattern = Pattern.compile("-\\s*(.+)");
        Matcher suggestionMatcher = suggestionPattern.matcher(content);
        while (suggestionMatcher.find()) {
            result.addSuggestion(suggestionMatcher.group(1).trim());
        }
        
        Pattern summaryPattern = Pattern.compile("Summary:\\s*(.+)");
        Matcher summaryMatcher = summaryPattern.matcher(content);
        if (summaryMatcher.find()) {
            result.setSummary(summaryMatcher.group(1).trim());
        }
        
        return result;
    }
    
    private String getLanguage(org.aurora.launcher.ai.script.ScriptType type) {
        switch (type) {
            case KUBEJS_EVENT:
            case KUBEJS_RECIPE:
            case KUBEJS_ITEM:
                return "javascript";
            case CRAFTTWEAKER_RECIPE:
            case CRAFTTWEAKER_EVENT:
                return "zenscript";
            case DATAPACK_FUNCTION:
                return "mcfunction";
            default:
                return "text";
        }
    }
}