package org.aurora.launcher.ai.review;

public class CodeIssue {
    
    private IssueSeverity severity;
    private String line;
    private String description;
    private String suggestion;
    
    public CodeIssue() {
    }
    
    public CodeIssue(IssueSeverity severity, String description) {
        this.severity = severity;
        this.description = description;
    }
    
    public IssueSeverity getSeverity() {
        return severity;
    }
    
    public void setSeverity(IssueSeverity severity) {
        this.severity = severity;
    }
    
    public String getLine() {
        return line;
    }
    
    public void setLine(String line) {
        this.line = line;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSuggestion() {
        return suggestion;
    }
    
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}