package org.aurora.launcher.diagnostic.report;

import org.aurora.launcher.diagnostic.conflict.ConflictReport;
import org.aurora.launcher.diagnostic.crash.CrashReport;
import org.aurora.launcher.diagnostic.log.JavaInfo;
import org.aurora.launcher.diagnostic.log.LogAnalysisResult;
import org.aurora.launcher.diagnostic.performance.PerformanceReport;

import java.util.List;
import java.util.Optional;

public class ReportBuilder {
    private DiagnosticReport report;

    public ReportBuilder() {
        this.report = new DiagnosticReport();
    }

    public static ReportBuilder create() {
        return new ReportBuilder();
    }

    public ReportBuilder instanceId(String instanceId) {
        report.setInstanceId(instanceId);
        return this;
    }

    public ReportBuilder systemInfo(SystemInfo systemInfo) {
        report.setSystemInfo(systemInfo);
        return this;
    }

    public ReportBuilder javaInfo(JavaInfo javaInfo) {
        report.setJavaInfo(javaInfo);
        return this;
    }

    public ReportBuilder minecraftInfo(MinecraftInfo minecraftInfo) {
        report.setMinecraftInfo(minecraftInfo);
        return this;
    }

    public ReportBuilder mods(List<String> mods) {
        report.setMods(mods);
        return this;
    }

    public ReportBuilder crashReport(CrashReport crash) {
        report.setCrashReport(Optional.ofNullable(crash));
        return this;
    }

    public ReportBuilder logAnalysis(LogAnalysisResult logAnalysis) {
        report.setLogAnalysis(Optional.ofNullable(logAnalysis));
        return this;
    }

    public ReportBuilder performance(PerformanceReport performance) {
        report.setPerformance(Optional.ofNullable(performance));
        return this;
    }

    public ReportBuilder conflicts(ConflictReport conflicts) {
        report.setConflicts(Optional.ofNullable(conflicts));
        return this;
    }

    public ReportBuilder addRecommendation(Recommendation recommendation) {
        report.addRecommendation(recommendation);
        return this;
    }

    public DiagnosticReport build() {
        generateRecommendations();
        return report;
    }

    private void generateRecommendations() {
        if (report.getCrashReport().isPresent()) {
            CrashReport crash = report.getCrashReport().get();
            if (crash.hasSolution()) {
                Recommendation rec = new Recommendation(
                    "Resolve crash issue",
                    crash.getSummary(),
                    Recommendation.Priority.HIGH
                );
                report.addRecommendation(rec);
            }
        }
        
        if (report.getConflicts().isPresent()) {
            ConflictReport conflicts = report.getConflicts().get();
            if (conflicts.hasCriticalConflicts()) {
                Recommendation rec = new Recommendation(
                    "Resolve mod conflicts",
                    "Critical mod conflicts detected. Game may not work properly.",
                    Recommendation.Priority.HIGH
                );
                report.addRecommendation(rec);
            }
        }
        
        if (report.getPerformance().isPresent()) {
            PerformanceReport perf = report.getPerformance().get();
            if (perf.hasCriticalIssues()) {
                Recommendation rec = new Recommendation(
                    "Performance issues detected",
                    "Consider allocating more memory or reducing graphics settings.",
                    Recommendation.Priority.MEDIUM
                );
                report.addRecommendation(rec);
            }
        }
        
        if (report.getJavaInfo() != null) {
            int javaVersion = report.getJavaInfo().getMajorVersion();
            if (javaVersion < 17) {
                Recommendation rec = new Recommendation(
                    "Update Java",
                    "Java 17+ is recommended for modern Minecraft versions.",
                    Recommendation.Priority.MEDIUM
                );
                report.addRecommendation(rec);
            }
        }
    }
}