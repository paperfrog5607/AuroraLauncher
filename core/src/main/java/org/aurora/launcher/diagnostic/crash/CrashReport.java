package org.aurora.launcher.diagnostic.crash;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CrashReport {
    private String crashId;
    private CrashType type;
    private String summary;
    private String detailedDescription;
    private List<String> suspectedMods;
    private String exceptionType;
    private String stackTrace;
    private List<CrashSolution> solutions;
    private Instant analysisTime;
    private Confidence confidence;

    public CrashReport() {
        this.suspectedMods = new ArrayList<>();
        this.solutions = new ArrayList<>();
        this.analysisTime = Instant.now();
        this.type = CrashType.UNKNOWN;
        this.confidence = Confidence.LOW;
    }

    public String getCrashId() {
        return crashId;
    }

    public void setCrashId(String crashId) {
        this.crashId = crashId;
    }

    public CrashType getType() {
        return type;
    }

    public void setType(CrashType type) {
        this.type = type;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public List<String> getSuspectedMods() {
        return suspectedMods;
    }

    public void setSuspectedMods(List<String> suspectedMods) {
        this.suspectedMods = suspectedMods != null ? suspectedMods : new ArrayList<>();
    }

    public void addSuspectedMod(String modId) {
        if (modId != null && !suspectedMods.contains(modId)) {
            suspectedMods.add(modId);
        }
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public List<CrashSolution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<CrashSolution> solutions) {
        this.solutions = solutions != null ? solutions : new ArrayList<>();
    }

    public void addSolution(CrashSolution solution) {
        if (solution != null) {
            solutions.add(solution);
        }
    }

    public Instant getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(Instant analysisTime) {
        this.analysisTime = analysisTime;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

    public boolean hasSolution() {
        return !solutions.isEmpty();
    }
}