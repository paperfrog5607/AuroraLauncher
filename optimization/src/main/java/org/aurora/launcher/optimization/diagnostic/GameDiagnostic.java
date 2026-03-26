package org.aurora.launcher.optimization.diagnostic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 游戏诊断器
 * 分析崩溃报告、日志、性能问题
 */
public class GameDiagnostic {

    private static final Logger logger = LoggerFactory.getLogger(GameDiagnostic.class);

    private static GameDiagnostic instance;

    private final List<CrashReport> crashHistory;
    private final SimpleDateFormat dateFormat;

    private GameDiagnostic() {
        this.crashHistory = new CopyOnWriteArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    }

    public static synchronized GameDiagnostic getInstance() {
        if (instance == null) {
            instance = new GameDiagnostic();
        }
        return instance;
    }

    /**
     * 诊断问题
     */
    public DiagnosticResult diagnose(File gameDirectory) {
        DiagnosticResult result = new DiagnosticResult();
        
        if (gameDirectory == null || !gameDirectory.exists()) {
            result.addIssue(IssueSeverity.ERROR, "Game directory not found");
            return result;
        }
        
        // 检查崩溃报告
        checkCrashReports(gameDirectory, result);
        
        // 检查日志
        checkLogs(gameDirectory, result);
        
        // 检查Java环境
        checkJavaEnvironment(result);
        
        // 检查内存
        checkMemoryConfiguration(result);
        
        // 检查显卡驱动
        checkGraphicsDrivers(result);
        
        return result;
    }

    private void checkCrashReports(File gameDir, DiagnosticResult result) {
        File crashFolder = new File(gameDir, "crash-reports");
        if (!crashFolder.exists()) {
            return;
        }
        
        File[] reports = crashFolder.listFiles((dir, name) -> name.startsWith("crash-"));
        if (reports != null && reports.length > 0) {
            Arrays.sort(reports, Comparator.comparingLong(File::lastModified).reversed());
            
            for (int i = 0; i < Math.min(3, reports.length); i++) {
                CrashReport report = parseCrashReport(reports[i]);
                if (report != null) {
                    crashHistory.add(report);
                    result.addCrashReport(report);
                }
            }
            
            if (reports.length > 3) {
                result.addIssue(IssueSeverity.WARNING, 
                    "Multiple crash reports found (" + reports.length + "), recent 3 analyzed");
            }
        }
    }

    private CrashReport parseCrashReport(File file) {
        CrashReport report = new CrashReport();
        report.setFileName(file.getName());
        report.setTimestamp(file.lastModified());
        
        try {
            String content = Files.readString(file.toPath());
            report.setContent(content);
            
            // 提取崩溃原因
            Pattern causePattern = Pattern.compile("Exception in thread \"[^\"]+\" ([^\n]+)");
            Matcher causeMatcher = causePattern.matcher(content);
            if (causeMatcher.find()) {
                report.setCause(causeMatcher.group(1));
            }
            
            // 提取游戏版本
            Pattern versionPattern = Pattern.compile("Minecraft Version: ([^\n]+)");
            Matcher versionMatcher = versionPattern.matcher(content);
            if (versionMatcher.find()) {
                report.setMinecraftVersion(versionMatcher.group(1));
            }
            
            // 提取崩溃类型
            if (content.contains("OutOfMemoryError")) {
                report.setType(CrashType.OUT_OF_MEMORY);
                report.addSuggestion("Allocate more memory to the game");
                report.addSuggestion("Remove some mods to reduce memory usage");
            } else if (content.contains("LinkageError") || content.contains("NoClassDefFoundError")) {
                report.setType(CrashType.MOD_CONFLICT);
                report.addSuggestion("Check for mod compatibility issues");
                report.addSuggestion("Update all mods to latest versions");
            } else if (content.contains("Render Thread")) {
                report.setType(CrashType.GRAPHICS);
                report.addSuggestion("Update graphics drivers");
                report.addSuggestion("Disable shader mods");
            } else {
                report.setType(CrashType.UNKNOWN);
            }
            
        } catch (IOException e) {
            logger.error("Failed to parse crash report: {}", file.getName(), e);
        }
        
        return report;
    }

    private void checkLogs(File gameDir, DiagnosticResult result) {
        File logsDir = new File(gameDir, "logs");
        if (!logsDir.exists()) {
            return;
        }
        
        File[] logs = logsDir.listFiles((dir, name) -> name.endsWith(".log"));
        if (logs != null && logs.length > 0) {
            Arrays.sort(logs, Comparator.comparingLong(File::lastModified).reversed());
            
            try {
                String recentLog = Files.readString(logs[0].toPath());
                
                if (recentLog.contains("java.lang.OutOfMemoryError")) {
                    result.addIssue(IssueSeverity.ERROR, "Out of memory error in recent logs");
                }
                
                if (recentLog.contains("Shaders")) {
                    result.addIssue(IssueSeverity.WARNING, "Shader-related errors detected");
                }
                
                long warnCount = countOccurrences(recentLog, "[WARN]");
                long errorCount = countOccurrences(recentLog, "[ERROR]");
                
                if (errorCount > 10) {
                    result.addIssue(IssueSeverity.WARNING, 
                        "High number of errors in logs: " + errorCount);
                }
                
            } catch (IOException e) {
                logger.error("Failed to read log file", e);
            }
        }
    }

    private void checkJavaEnvironment(DiagnosticResult result) {
        String javaVersion = System.getProperty("java.version");
        result.setJavaVersion(javaVersion);
        
        if (javaVersion.startsWith("1.8") || javaVersion.startsWith("8")) {
            result.addIssue(IssueSeverity.INFO, 
                "Using Java 8 - consider upgrading to Java 17+ for better performance");
        }
        
        String javaVendor = System.getProperty("java.vendor");
        if (javaVendor.contains("Oracle") || javaVendor.contains("AdoptOpenJDK")) {
            result.addIssue(IssueSeverity.INFO, "Java vendor: " + javaVendor);
        }
    }

    private void checkMemoryConfiguration(DiagnosticResult result) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        result.setMaxMemory(maxMemory);
        result.setAllocatedMemory(totalMemory);
        
        if (maxMemory < 2L * 1024 * 1024 * 1024) {
            result.addIssue(IssueSeverity.WARNING, 
                "Maximum heap is less than 2GB - may cause performance issues");
        }
        
        if (maxMemory > 8L * 1024 * 1024 * 1024) {
            result.addIssue(IssueSeverity.INFO, 
                "Large memory allocation (" + (maxMemory / 1024 / 1024 / 1024) + "GB)");
        }
    }

    private void checkGraphicsDrivers(DiagnosticResult result) {
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows")) {
            result.addIssue(IssueSeverity.INFO, 
                "Checking graphics drivers on Windows...");
        }
    }

    private long countOccurrences(String text, String pattern) {
        return Pattern.compile(Pattern.quote(pattern))
                .matcher(text)
                .results()
                .count();
    }

    /**
     * 生成诊断报告
     */
    public String generateReport(File gameDirectory) {
        DiagnosticResult result = diagnose(gameDirectory);
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== Aurora Diagnostic Report ===\n\n");
        sb.append("Generated: ").append(new Date()).append("\n\n");
        
        sb.append("Issues Found: ").append(result.getIssues().size()).append("\n");
        for (Issue issue : result.getIssues()) {
            sb.append("  [").append(issue.getSeverity()).append("] ").append(issue.getMessage()).append("\n");
        }
        
        if (!result.getCrashReports().isEmpty()) {
            sb.append("\nCrash Reports:\n");
            for (CrashReport crash : result.getCrashReports()) {
                sb.append("  - ").append(crash.getFileName());
                if (crash.getCause() != null) {
                    sb.append(": ").append(crash.getCause());
                }
                sb.append("\n");
            }
        }
        
        sb.append("\nSuggestions:\n");
        Set<String> suggestions = new LinkedHashSet<>();
        for (CrashReport crash : result.getCrashReports()) {
            suggestions.addAll(crash.getSuggestions());
        }
        for (String suggestion : suggestions) {
            sb.append("  • ").append(suggestion).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * 导出诊断报告
     */
    public void exportReport(File gameDirectory, File output) throws IOException {
        String report = generateReport(gameDirectory);
        Files.writeString(output.toPath(), report);
        logger.info("Diagnostic report exported to: {}", output.getAbsolutePath());
    }

    public List<CrashReport> getCrashHistory() {
        return new ArrayList<>(crashHistory);
    }

    public enum IssueSeverity { INFO, WARNING, ERROR }

    public enum CrashType { 
        OUT_OF_MEMORY, 
        MOD_CONFLICT, 
        GRAPHICS, 
        UNKNOWN 
    }

    public static class Issue {
        private IssueSeverity severity;
        private String message;
        
        public Issue(IssueSeverity severity, String message) {
            this.severity = severity;
            this.message = message;
        }
        
        public IssueSeverity getSeverity() { return severity; }
        public String getMessage() { return message; }
    }

    public static class CrashReport {
        private String fileName;
        private long timestamp;
        private String content;
        private String cause;
        private String minecraftVersion;
        private CrashType type;
        private List<String> suggestions = new ArrayList<>();
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCause() { return cause; }
        public void setCause(String cause) { this.cause = cause; }
        public String getMinecraftVersion() { return minecraftVersion; }
        public void setMinecraftVersion(String minecraftVersion) { this.minecraftVersion = minecraftVersion; }
        public CrashType getType() { return type; }
        public void setType(CrashType type) { this.type = type; }
        public List<String> getSuggestions() { return suggestions; }
        public void addSuggestion(String suggestion) { suggestions.add(suggestion); }
    }

    public static class DiagnosticResult {
        private List<Issue> issues = new ArrayList<>();
        private List<CrashReport> crashReports = new ArrayList<>();
        private String javaVersion;
        private long maxMemory;
        private long allocatedMemory;
        
        public void addIssue(IssueSeverity severity, String message) {
            issues.add(new Issue(severity, message));
        }
        
        public void addCrashReport(CrashReport report) {
            crashReports.add(report);
        }
        
        public List<Issue> getIssues() { return issues; }
        public List<CrashReport> getCrashReports() { return crashReports; }
        public String getJavaVersion() { return javaVersion; }
        public void setJavaVersion(String javaVersion) { this.javaVersion = javaVersion; }
        public long getMaxMemory() { return maxMemory; }
        public void setMaxMemory(long maxMemory) { this.maxMemory = maxMemory; }
        public long getAllocatedMemory() { return allocatedMemory; }
        public void setAllocatedMemory(long allocatedMemory) { this.allocatedMemory = allocatedMemory; }
    }
}
