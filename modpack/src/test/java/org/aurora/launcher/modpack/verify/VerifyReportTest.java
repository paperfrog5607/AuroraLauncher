package org.aurora.launcher.modpack.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Paths;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class VerifyReportTest {
    
    private VerifyReport report;
    
    @BeforeEach
    void setUp() {
        report = new VerifyReport();
    }
    
    @Test
    void testReportCreation() {
        report.setInstanceId("instance-1");
        report.setFilesChecked(100);
        
        assertEquals("instance-1", report.getInstanceId());
        assertEquals(100, report.getFilesChecked());
        assertNotNull(report.getCheckTime());
    }
    
    @Test
    void testAddIssues() {
        VerifyReport.FileIssue missingFile = new VerifyReport.FileIssue(
                Paths.get("/missing/file.jar"), VerifyReport.IssueType.MISSING);
        report.addMissingFile(missingFile);
        
        VerifyReport.FileIssue corruptedFile = new VerifyReport.FileIssue(
                Paths.get("/corrupted/file.jar"), VerifyReport.IssueType.CORRUPTED);
        report.addCorruptedFile(corruptedFile);
        
        VerifyReport.DependencyIssue missingDep = new VerifyReport.DependencyIssue(
                "mod-id", "required-mod", VerifyReport.IssueType.MISSING);
        report.addMissingDependency(missingDep);
        
        VerifyReport.DependencyIssue conflictDep = new VerifyReport.DependencyIssue(
                "mod1", "mod2", VerifyReport.IssueType.CONFLICT);
        report.addConflictDependency(conflictDep);
        
        VerifyReport.ConfigIssue configIssue = new VerifyReport.ConfigIssue(
                Paths.get("/config/file.cfg"), "key", "Invalid value");
        report.addConfigIssue(configIssue);
        
        assertEquals(1, report.getMissingFiles().size());
        assertEquals(1, report.getCorruptedFiles().size());
        assertEquals(1, report.getMissingDependencies().size());
        assertEquals(1, report.getConflictDependencies().size());
        assertEquals(1, report.getConfigIssues().size());
    }
    
    @Test
    void testCalculateTotals() {
        report.addMissingFile(new VerifyReport.FileIssue(Paths.get("/file"), VerifyReport.IssueType.MISSING));
        report.addCorruptedFile(new VerifyReport.FileIssue(Paths.get("/file2"), VerifyReport.IssueType.CORRUPTED));
        report.addMissingDependency(new VerifyReport.DependencyIssue("a", "b", VerifyReport.IssueType.MISSING));
        
        report.calculateTotals();
        
        assertEquals(3, report.getIssuesFound());
        assertFalse(report.isPassed());
    }
    
    @Test
    void testPassedWhenNoIssues() {
        report.calculateTotals();
        
        assertEquals(0, report.getIssuesFound());
        assertTrue(report.isPassed());
    }
    
    @Test
    void testGetTotalIssues() {
        report.addMissingFile(new VerifyReport.FileIssue(Paths.get("/file"), VerifyReport.IssueType.MISSING));
        report.addCorruptedFile(new VerifyReport.FileIssue(Paths.get("/file2"), VerifyReport.IssueType.CORRUPTED));
        
        assertEquals(2, report.getTotalIssues());
    }
    
    @Test
    void testFileIssue() {
        VerifyReport.FileIssue issue = new VerifyReport.FileIssue(
                Paths.get("/path/to/file.jar"), VerifyReport.IssueType.CORRUPTED);
        issue.setExpectedHash("abc123");
        issue.setActualHash("def456");
        issue.setMessage("File hash mismatch");
        
        assertEquals(Paths.get("/path/to/file.jar"), issue.getPath());
        assertEquals(VerifyReport.IssueType.CORRUPTED, issue.getType());
        assertEquals("abc123", issue.getExpectedHash());
        assertEquals("def456", issue.getActualHash());
        assertEquals("File hash mismatch", issue.getMessage());
    }
    
    @Test
    void testDependencyIssue() {
        VerifyReport.DependencyIssue issue = new VerifyReport.DependencyIssue(
                "mod-a", "mod-b", VerifyReport.IssueType.VERSION_MISMATCH);
        issue.setModName("Mod A");
        issue.setRequiredVersion("1.0.0");
        issue.setMessage("Version mismatch");
        
        assertEquals("mod-a", issue.getModId());
        assertEquals("Mod A", issue.getModName());
        assertEquals("mod-b", issue.getRequiredMod());
        assertEquals("1.0.0", issue.getRequiredVersion());
        assertEquals(VerifyReport.IssueType.VERSION_MISMATCH, issue.getType());
    }
    
    @Test
    void testConfigIssue() {
        VerifyReport.ConfigIssue issue = new VerifyReport.ConfigIssue(
                Paths.get("/config/mod.cfg"), "someKey", "Invalid format");
        issue.setExpectedValue("true");
        issue.setActualValue("invalid");
        
        assertEquals(Paths.get("/config/mod.cfg"), issue.getConfigPath());
        assertEquals("someKey", issue.getKey());
        assertEquals("Invalid format", issue.getMessage());
        assertEquals("true", issue.getExpectedValue());
        assertEquals("invalid", issue.getActualValue());
    }
}