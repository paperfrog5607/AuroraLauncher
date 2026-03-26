package org.aurora.launcher.mod.security;

import org.aurora.launcher.mod.scanner.ModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModSecurityScanner {
    
    public CompletableFuture<SecurityReport> scan(ModInfo mod) {
        return CompletableFuture.supplyAsync(() -> {
            SecurityReport report = new SecurityReport();
            report.setModId(mod.getId());
            
            scanBasicInfo(mod, report);
            analyzePermissions(mod, report);
            detectSuspiciousPatterns(mod, report);
            
            report.calculateOverallRisk();
            
            return report;
        });
    }
    
    private void scanBasicInfo(ModInfo mod, SecurityReport report) {
        if (mod.getId() == null || mod.getId().isEmpty()) {
            report.addIssue(new SecurityIssue(
                    SecurityIssue.IssueType.SUSPICIOUS_PATTERN,
                    "Mod ID is missing",
                    RiskLevel.MEDIUM
            ));
        }
        
        if (mod.getName() == null || mod.getName().isEmpty()) {
            report.addIssue(new SecurityIssue(
                    SecurityIssue.IssueType.SUSPICIOUS_PATTERN,
                    "Mod name is missing",
                    RiskLevel.LOW
            ));
        }
        
        if (mod.getVersion() == null || mod.getVersion().isEmpty()) {
            report.addIssue(new SecurityIssue(
                    SecurityIssue.IssueType.SUSPICIOUS_PATTERN,
                    "Mod version is missing",
                    RiskLevel.LOW
            ));
        }
    }
    
    private void analyzePermissions(ModInfo mod, SecurityReport report) {
        String description = mod.getDescription();
        if (description != null) {
            checkForNetworkKeywords(description, report);
            checkForFileKeywords(description, report);
        }
        
        String homepage = mod.getHomepage();
        if (homepage != null && !homepage.isEmpty()) {
            report.addPermission(new PermissionRequest(
                    PermissionRequest.PermissionType.NETWORK_ACCESS,
                    "Homepage: " + homepage
            ));
        }
    }
    
    private void checkForNetworkKeywords(String text, SecurityReport report) {
        String[] networkKeywords = {"http://", "https://", "websocket", "socket", "network"};
        for (String keyword : networkKeywords) {
            if (text.toLowerCase().contains(keyword.toLowerCase())) {
                report.addPermission(new PermissionRequest(
                        PermissionRequest.PermissionType.NETWORK_ACCESS,
                        "Potential network access detected"
                ));
                break;
            }
        }
    }
    
    private void checkForFileKeywords(String text, SecurityReport report) {
        String[] fileKeywords = {"file", "save", "delete", "write", "read"};
        for (String keyword : fileKeywords) {
            if (text.toLowerCase().contains(keyword.toLowerCase())) {
                report.addPermission(new PermissionRequest(
                        PermissionRequest.PermissionType.FILE_ACCESS,
                        "Potential file access detected"
                ));
                break;
            }
        }
    }
    
    private void detectSuspiciousPatterns(ModInfo mod, SecurityReport report) {
        String id = mod.getId();
        if (id != null) {
            if (id.toLowerCase().contains("hack") || id.toLowerCase().contains("cheat")) {
                report.addIssue(new SecurityIssue(
                        SecurityIssue.IssueType.SUSPICIOUS_PATTERN,
                        "Suspicious mod ID pattern",
                        RiskLevel.HIGH
                ));
            }
        }
    }
}