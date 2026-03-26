package org.aurora.launcher.modpack.share;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class ShareCodeGeneratorTest {
    
    private ShareCodeGenerator generator;
    
    @BeforeEach
    void setUp() {
        generator = new ShareCodeGenerator();
    }
    
    @Test
    void testGenerateCode() {
        ShareCode shareCode = generator.generate(
                "instance-1", 
                "Test Modpack", 
                "https://example.com/modpack.mrpack", 
                1024
        );
        
        assertNotNull(shareCode);
        assertNotNull(shareCode.getCode());
        assertTrue(shareCode.getCode().startsWith("AURORA-"));
        assertEquals("instance-1", shareCode.getInstanceId());
        assertEquals("Test Modpack", shareCode.getInstanceName());
        assertEquals("https://example.com/modpack.mrpack", shareCode.getDownloadUrl());
        assertEquals(1024, shareCode.getFileSize());
    }
    
    @Test
    void testCodeFormat() {
        String code = generator.generate("id", "name", "url", 0).getCode();
        
        String[] parts = code.split("-");
        assertEquals(4, parts.length);
        assertEquals("AURORA", parts[0]);
        
        for (int i = 1; i < parts.length; i++) {
            assertEquals(4, parts[i].length());
        }
    }
    
    @Test
    void testIsValidCodeFormat() {
        assertTrue(ShareCodeGenerator.isValidCodeFormat("AURORA-ABCD-EFGH-WXYZ"));
        assertTrue(ShareCodeGenerator.isValidCodeFormat("AURORA-TEST-CODE-HERE"));
        
        assertFalse(ShareCodeGenerator.isValidCodeFormat(null));
        assertFalse(ShareCodeGenerator.isValidCodeFormat(""));
        assertFalse(ShareCodeGenerator.isValidCodeFormat("AURORA-ABC-123"));
        assertFalse(ShareCodeGenerator.isValidCodeFormat("NOTAURORA-ABCD-EFGH-WXYZ"));
        assertFalse(ShareCodeGenerator.isValidCodeFormat("AURORA-ABCD-EFGH"));
    }
    
    @Test
    void testDefaultSettings() {
        assertEquals(java.time.Duration.ofDays(7), generator.getDefaultExpiration());
        assertEquals("modrinth", generator.getDefaultFormat());
    }
    
    @Test
    void testCustomSettings() {
        generator.setDefaultExpiration(java.time.Duration.ofDays(30));
        generator.setDefaultFormat("curseforge");
        
        assertEquals(java.time.Duration.ofDays(30), generator.getDefaultExpiration());
        assertEquals("curseforge", generator.getDefaultFormat());
    }
}