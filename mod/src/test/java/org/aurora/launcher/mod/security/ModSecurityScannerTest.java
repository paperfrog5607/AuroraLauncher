package org.aurora.launcher.mod.security;

import org.aurora.launcher.mod.scanner.ModInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModSecurityScannerTest {

    @Test
    void shouldCreateScanner() {
        ModSecurityScanner scanner = new ModSecurityScanner();
        
        assertNotNull(scanner);
    }

    @Test
    void shouldScanMod() throws Exception {
        ModSecurityScanner scanner = new ModSecurityScanner();
        
        ModInfo mod = new ModInfo();
        mod.setId("test-mod");
        mod.setName("Test Mod");
        mod.setVersion("1.0.0");
        
        SecurityReport report = scanner.scan(mod).get();
        
        assertNotNull(report);
        assertEquals("test-mod", report.getModId());
    }

    @Test
    void shouldDetectMissingId() throws Exception {
        ModSecurityScanner scanner = new ModSecurityScanner();
        
        ModInfo mod = new ModInfo();
        
        SecurityReport report = scanner.scan(mod).get();
        
        assertFalse(report.getIssues().isEmpty());
    }
}