package org.aurora.launcher.diagnostic.crash;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrashAnalyzer {
    private final List<CrashPattern> patterns;

    public CrashAnalyzer() {
        this.patterns = new ArrayList<>();
        loadDefaultPatterns();
    }

    public CrashAnalyzer(List<CrashPattern> patterns) {
        this.patterns = patterns != null ? patterns : new ArrayList<>();
    }

    public CrashReport analyze(String crashLog) {
        if (crashLog == null || crashLog.isEmpty()) {
            return createEmptyReport();
        }

        CrashReport report = new CrashReport();
        report.setCrashId(UUID.randomUUID().toString());
        report.setAnalysisTime(Instant.now());

        extractExceptionInfo(crashLog, report);
        extractSuspectedMods(crashLog, report);
        extractStackTrace(crashLog, report);

        CrashPattern matchedPattern = findMatchingPattern(crashLog);
        if (matchedPattern != null) {
            report.setType(matchedPattern.getType());
            report.setSummary(matchedPattern.getName());
            report.setDetailedDescription(matchedPattern.getDescription());
            report.setSolutions(new ArrayList<>(matchedPattern.getSolutions()));
            report.setConfidence(Confidence.HIGH);
        } else {
            analyzeByKeywords(crashLog, report);
        }

        return report;
    }

    private void loadDefaultPatterns() {
        addOutOfMemoryPattern();
        addJavaVersionPattern();
        addModConflictPattern();
        addDriverIssuePattern();
        addCorruptedFilePattern();
    }

    private void addOutOfMemoryPattern() {
        CrashPattern pattern = new CrashPattern("oom", CrashType.OUT_OF_MEMORY, 
            "OutOfMemoryError", "java.lang.OutOfMemoryError");
        pattern.setName("Out of Memory Error");
        pattern.setDescription("The game ran out of allocated memory.");
        pattern.setPriority(100);
        
        CrashSolution solution = new CrashSolution(
            "Allocate more memory",
            "Increase the allocated memory in launcher settings.",
            SolutionType.ALLOCATE_MEMORY
        );
        solution.setAction("Settings > Memory > Increase max memory to at least 4GB");
        pattern.addSolution(solution);
        
        patterns.add(pattern);
    }

    private void addJavaVersionPattern() {
        CrashPattern pattern = new CrashPattern("java-version", CrashType.JAVA_VERSION,
            "unsupported class version", "unsupported major.minor version");
        pattern.setName("Java Version Incompatibility");
        pattern.setDescription("The game requires a different Java version.");
        pattern.setPriority(90);
        
        CrashSolution solution = new CrashSolution(
            "Update Java",
            "Install the correct Java version for this Minecraft version.",
            SolutionType.UPDATE_JAVA
        );
        solution.setAction("Install Java 17 or higher for Minecraft 1.18+");
        pattern.addSolution(solution);
        
        patterns.add(pattern);
    }

    private void addModConflictPattern() {
        CrashPattern pattern = new CrashPattern("mod-conflict", CrashType.MOD_CONFLICT,
            "duplicate", "already registered", "conflict");
        pattern.setName("Mod Conflict Detected");
        pattern.setDescription("Two or more mods are conflicting with each other.");
        pattern.setPriority(80);
        
        CrashSolution solution = new CrashSolution(
            "Remove conflicting mod",
            "Identify and remove the conflicting mod from the mods folder.",
            SolutionType.REMOVE_MOD
        );
        pattern.addSolution(solution);
        
        patterns.add(pattern);
    }

    private void addDriverIssuePattern() {
        CrashPattern pattern = new CrashPattern("driver", CrashType.DRIVER_ISSUE,
            "driver", "gpu", "opengl", "graphics");
        pattern.setName("Graphics Driver Issue");
        pattern.setDescription("There is an issue with your graphics driver.");
        pattern.setPriority(70);
        
        CrashSolution solution = new CrashSolution(
            "Update graphics driver",
            "Update your graphics card driver to the latest version.",
            SolutionType.UPDATE_DRIVER
        );
        solution.setAction("Download and install the latest driver from your GPU manufacturer's website");
        pattern.addSolution(solution);
        
        patterns.add(pattern);
    }

    private void addCorruptedFilePattern() {
        CrashPattern pattern = new CrashPattern("corrupted", CrashType.CORRUPTED_FILE,
            "corrupted", "invalid", "malformed", "zip file");
        pattern.setName("Corrupted Game File");
        pattern.setDescription("One or more game files are corrupted.");
        pattern.setPriority(60);
        
        CrashSolution solution = new CrashSolution(
            "Reinstall game",
            "Delete the game instance and reinstall it.",
            SolutionType.REINSTALL
        );
        pattern.addSolution(solution);
        
        patterns.add(pattern);
    }

    private CrashPattern findMatchingPattern(String crashLog) {
        return patterns.stream()
            .filter(p -> p.matches(crashLog))
            .max(Comparator.comparingInt(CrashPattern::getPriority))
            .orElse(null);
    }

    private void analyzeByKeywords(String crashLog, CrashReport report) {
        String lowerLog = crashLog.toLowerCase();
        
        if (lowerLog.contains("outofmemory") || lowerLog.contains("heap space")) {
            report.setType(CrashType.OUT_OF_MEMORY);
            report.setSummary("Out of memory detected");
            report.setConfidence(Confidence.MEDIUM);
        } else if (lowerLog.contains("unsupported class version") || 
                   lowerLog.contains("unsupported major.minor")) {
            report.setType(CrashType.JAVA_VERSION);
            report.setSummary("Java version incompatibility");
            report.setConfidence(Confidence.MEDIUM);
        }
    }

    private void extractExceptionInfo(String crashLog, CrashReport report) {
        Pattern exceptionPattern = Pattern.compile(
            "(java\\.[a-z.]+\\.[A-Z][a-zA-Z]*(?:Exception|Error))",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = exceptionPattern.matcher(crashLog);
        if (matcher.find()) {
            report.setExceptionType(matcher.group(1));
        }
    }

    private void extractSuspectedMods(String crashLog, CrashReport report) {
        Pattern modPattern = Pattern.compile("\\[([a-z][a-z0-9_-]{2,})\\]", 
            Pattern.CASE_INSENSITIVE);
        Matcher matcher = modPattern.matcher(crashLog);
        int count = 0;
        while (matcher.find() && count < 10) {
            String modId = matcher.group(1).toLowerCase();
            if (!isCommonWord(modId)) {
                report.addSuspectedMod(modId);
                count++;
            }
        }
    }

    private void extractStackTrace(String crashLog, CrashReport report) {
        Pattern stackPattern = Pattern.compile(
            "at\\s+[a-zA-Z0-9_.]+\\([^)]+\\)",
            Pattern.MULTILINE
        );
        Matcher matcher = stackPattern.matcher(crashLog);
        StringBuilder sb = new StringBuilder();
        int lines = 0;
        while (matcher.find() && lines < 20) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(matcher.group());
            lines++;
        }
        if (sb.length() > 0) {
            report.setStackTrace(sb.toString());
        }
    }

    private boolean isCommonWord(String word) {
        String[] common = {"main", "thread", "info", "warn", "error", "debug", "trace", 
                          "server", "client", "player", "world", "chunk", "entity"};
        for (String c : common) {
            if (c.equalsIgnoreCase(word)) return true;
        }
        return false;
    }

    private CrashReport createEmptyReport() {
        CrashReport report = new CrashReport();
        report.setCrashId(UUID.randomUUID().toString());
        report.setSummary("Empty crash log");
        report.setType(CrashType.UNKNOWN);
        return report;
    }

    public void addPattern(CrashPattern pattern) {
        if (pattern != null) {
            patterns.add(pattern);
        }
    }

    public List<CrashPattern> getPatterns() {
        return new ArrayList<>(patterns);
    }
}