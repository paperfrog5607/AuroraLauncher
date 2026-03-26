package org.aurora.launcher.ai.review;

import java.util.ArrayList;
import java.util.List;

public class ReviewResult {
    
    private int score;
    private List<CodeIssue> issues;
    private List<String> suggestions;
    private String summary;
    
    public ReviewResult() {
        this.issues = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.score = 100;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = Math.max(0, Math.min(100, score));
    }
    
    public List<CodeIssue> getIssues() {
        return issues;
    }
    
    public void addIssue(CodeIssue issue) {
        issues.add(issue);
        adjustScore(issue);
    }
    
    private void adjustScore(CodeIssue issue) {
        int deduction = 0;
        switch (issue.getSeverity()) {
            case ERROR:
                deduction = 15;
                break;
            case WARNING:
                deduction = 5;
                break;
            case INFO:
                deduction = 1;
                break;
        }
        score = Math.max(0, score - deduction);
    }
    
    public List<String> getSuggestions() {
        return suggestions;
    }
    
    public void addSuggestion(String suggestion) {
        suggestions.add(suggestion);
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
}