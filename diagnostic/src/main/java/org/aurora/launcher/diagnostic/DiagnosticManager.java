package org.aurora.launcher.diagnostic;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiagnosticManager {

    private static DiagnosticManager instance;
    private final CopyOnWriteArrayList<DiagnosticReport> reportHistory;

    private DiagnosticManager() {
        this.reportHistory = new CopyOnWriteArrayList<>();
    }

    public static synchronized DiagnosticManager getInstance() {
        if (instance == null) {
            instance = new DiagnosticManager();
        }
        return instance;
    }

    public DiagnosticReport runDiagnostics(File targetPath) {
        DiagnosticReport report = new DiagnosticReport();
        report.setTimestamp(System.currentTimeMillis());
        report.setTargetPath(targetPath.getAbsolutePath());

        checkFileSystem(targetPath, report);
        checkPermissions(targetPath, report);
        checkDiskSpace(targetPath, report);
        checkEnvironment(report);

        reportHistory.add(report);
        return report;
    }

    private void checkFileSystem(File path, DiagnosticReport report) {
        if (!path.exists()) {
            report.addIssue(IssueLevel.ERROR, "Path does not exist: " + path);
            return;
        }
        
        if (!path.canRead()) {
            report.addIssue(IssueLevel.WARNING, "Cannot read path: " + path);
        }
    }

    private void checkPermissions(File path, DiagnosticReport report) {
        if (path.canWrite()) {
            report.addInfo("Write permission granted");
        } else {
            report.addIssue(IssueLevel.WARNING, "No write permission");
        }
    }

    private void checkDiskSpace(File path, DiagnosticReport report) {
        File[] roots = File.listRoots();
        for (File root : roots) {
            long free = root.getFreeSpace();
            long total = root.getTotalSpace();
            if (free < total * 0.1) {
                report.addIssue(IssueLevel.WARNING, 
                    "Low disk space on " + root + ": " + (free / 1024 / 1024 / 1024) + "GB free");
            }
        }
    }

    private void checkEnvironment(DiagnosticReport report) {
        String javaVersion = System.getProperty("java.version");
        report.addInfo("Java version: " + javaVersion);
        
        String os = System.getProperty("os.name");
        report.addInfo("OS: " + os);
        
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        report.addInfo("Max heap: " + maxMemory + "MB");
    }

    public CopyOnWriteArrayList<DiagnosticReport> getReportHistory() {
        return reportHistory;
    }

    public static class DiagnosticReport {
        private long timestamp;
        private String targetPath;
        private CopyOnWriteArrayList<String> infos;
        private CopyOnWriteArrayList<Issue> issues;

        public DiagnosticReport() {
            this.infos = new CopyOnWriteArrayList<>();
            this.issues = new CopyOnWriteArrayList<>();
        }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getTargetPath() { return targetPath; }
        public void setTargetPath(String targetPath) { this.targetPath = targetPath; }
        public CopyOnWriteArrayList<String> getInfos() { return infos; }
        public CopyOnWriteArrayList<Issue> getIssues() { return issues; }

        public void addInfo(String info) { infos.add(info); }
        public void addIssue(IssueLevel level, String message) { 
            issues.add(new Issue(level, message)); 
        }

        public boolean hasErrors() {
            return issues.stream().anyMatch(i -> i.level == IssueLevel.ERROR);
        }
    }

    public enum IssueLevel { INFO, WARNING, ERROR }

    public static class Issue {
        public IssueLevel level;
        public String message;
        
        public Issue(IssueLevel level, String message) {
            this.level = level;
            this.message = message;
        }
    }
}