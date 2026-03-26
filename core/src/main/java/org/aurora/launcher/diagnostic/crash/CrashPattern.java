package org.aurora.launcher.diagnostic.crash;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CrashPattern {
    private String id;
    private String name;
    private Pattern regex;
    private CrashType type;
    private String description;
    private List<String> keywords;
    private List<CrashSolution> solutions;
    private int priority;

    public CrashPattern() {
        this.keywords = new ArrayList<>();
        this.solutions = new ArrayList<>();
        this.priority = 0;
    }

    public CrashPattern(String id, CrashType type, String... keywords) {
        this.id = id;
        this.type = type;
        this.keywords = new ArrayList<>();
        for (String kw : keywords) {
            this.keywords.add(kw);
        }
        this.solutions = new ArrayList<>();
        this.priority = 0;
    }

    public boolean matches(String crashLog) {
        if (crashLog == null || crashLog.isEmpty()) {
            return false;
        }
        
        String lowerLog = crashLog.toLowerCase();
        
        if (regex != null && regex.matcher(crashLog).find()) {
            return true;
        }
        
        if (keywords != null && !keywords.isEmpty()) {
            for (String keyword : keywords) {
                if (!lowerLog.contains(keyword.toLowerCase())) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pattern getRegex() {
        return regex;
    }

    public void setRegex(Pattern regex) {
        this.regex = regex;
    }

    public void setRegex(String regexStr) {
        if (regexStr != null && !regexStr.isEmpty()) {
            this.regex = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        }
    }

    public CrashType getType() {
        return type;
    }

    public void setType(CrashType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords != null ? keywords : new ArrayList<>();
    }

    public void addKeyword(String keyword) {
        if (keyword != null && !keywords.contains(keyword)) {
            keywords.add(keyword);
        }
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}