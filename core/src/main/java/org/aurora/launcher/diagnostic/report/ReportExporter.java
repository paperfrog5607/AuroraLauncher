package org.aurora.launcher.diagnostic.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReportExporter {

    public void exportAsText(DiagnosticReport report, Path target) throws IOException {
        if (report == null || target == null) {
            throw new IllegalArgumentException("Report and target path cannot be null");
        }
        
        String content = report.exportAsText();
        Files.createDirectories(target.getParent());
        Files.write(target, content.getBytes());
    }

    public void exportAsMarkdown(DiagnosticReport report, Path target) throws IOException {
        if (report == null || target == null) {
            throw new IllegalArgumentException("Report and target path cannot be null");
        }
        
        String content = report.exportAsMarkdown();
        Files.createDirectories(target.getParent());
        Files.write(target, content.getBytes());
    }

    public void exportAsJson(DiagnosticReport report, Path target) throws IOException {
        if (report == null || target == null) {
            throw new IllegalArgumentException("Report and target path cannot be null");
        }
        
        byte[] content = report.exportAsJson();
        Files.createDirectories(target.getParent());
        Files.write(target, content);
    }

    public void export(DiagnosticReport report, Path target, Format format) throws IOException {
        if (format == null) {
            format = Format.TEXT;
        }
        
        switch (format) {
            case MARKDOWN:
                exportAsMarkdown(report, target);
                break;
            case JSON:
                exportAsJson(report, target);
                break;
            case TEXT:
            default:
                exportAsText(report, target);
                break;
        }
    }

    public enum Format {
        TEXT,
        MARKDOWN,
        JSON
    }
}