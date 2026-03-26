package org.aurora.launcher.ai.script;

import java.nio.file.Path;

public class GeneratedScript {
    
    private ScriptType type;
    private String code;
    private String explanation;
    private String fileName;
    private Path suggestedPath;
    
    public GeneratedScript() {
    }
    
    public GeneratedScript(ScriptType type, String code) {
        this.type = type;
        this.code = code;
    }
    
    public ScriptType getType() {
        return type;
    }
    
    public void setType(ScriptType type) {
        this.type = type;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Path getSuggestedPath() {
        return suggestedPath;
    }
    
    public void setSuggestedPath(Path suggestedPath) {
        this.suggestedPath = suggestedPath;
    }
}