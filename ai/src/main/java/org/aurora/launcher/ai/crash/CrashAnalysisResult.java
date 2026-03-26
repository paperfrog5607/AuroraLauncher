package org.aurora.launcher.ai.crash;

import java.util.ArrayList;
import java.util.List;

public class CrashAnalysisResult {
    
    private String summary;
    private CrashType type;
    private List<String> suspectedMods;
    private String rootCause;
    private List<Solution> solutions;
    private int confidence;
    
    public static class Solution {
        private String description;
        private List<String> steps;
        private String command;
        
        public Solution(String description) {
            this.description = description;
            this.steps = new ArrayList<>();
        }
        
        public String getDescription() {
            return description;
        }
        
        public List<String> getSteps() {
            return steps;
        }
        
        public String getCommand() {
            return command;
        }
        
        public void addStep(String step) {
            steps.add(step);
        }
        
        public void setCommand(String command) {
            this.command = command;
        }
    }
    
    public CrashAnalysisResult() {
        this.suspectedMods = new ArrayList<>();
        this.solutions = new ArrayList<>();
        this.confidence = 0;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public CrashType getType() {
        return type;
    }
    
    public void setType(CrashType type) {
        this.type = type;
    }
    
    public List<String> getSuspectedMods() {
        return suspectedMods;
    }
    
    public void addSuspectedMod(String modId) {
        suspectedMods.add(modId);
    }
    
    public String getRootCause() {
        return rootCause;
    }
    
    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }
    
    public List<Solution> getSolutions() {
        return solutions;
    }
    
    public void addSolution(Solution solution) {
        solutions.add(solution);
    }
    
    public int getConfidence() {
        return confidence;
    }
    
    public void setConfidence(int confidence) {
        this.confidence = Math.max(0, Math.min(100, confidence));
    }
}