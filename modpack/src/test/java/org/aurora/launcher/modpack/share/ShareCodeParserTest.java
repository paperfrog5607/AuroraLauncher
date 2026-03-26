package org.aurora.launcher.modpack.share;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class ShareCodeParserTest {
    
    private ShareCodeParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new ShareCodeParser();
    }
    
    @Test
    void testParseValidCode() {
        ShareCode code = parser.parse("AURORA-ABCD-EFGH-WXYZ");
        
        assertNotNull(code);
        assertEquals("AURORA-ABCD-EFGH-WXYZ", code.getCode());
    }
    
    @Test
    void testParseCodeWithLowerCase() {
        ShareCode code = parser.parse("aurora-abcd-efgh-wxyz");
        
        assertNotNull(code);
        assertEquals("AURORA-ABCD-EFGH-WXYZ", code.getCode());
    }
    
    @Test
    void testParseNullCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(null);
        });
    }
    
    @Test
    void testParseEmptyCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse("");
        });
    }
    
    @Test
    void testParseInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse("INVALID-CODE");
        });
    }
    
    @Test
    void testIsValid() {
        assertTrue(parser.isValid("AURORA-ABCD-EFGH-WXYZ"));
        assertTrue(parser.isValid("aurora-abcd-efgh-wxyz"));
        assertFalse(parser.isValid(null));
        assertFalse(parser.isValid(""));
        assertFalse(parser.isValid("INVALID-CODE"));
    }
    
    @Test
    void testParseFromUrl() {
        ShareCode code = parser.parseFromUrl("https://aurora.example.com/AURORA-ABCD-EFGH-WXYZ");
        
        assertNotNull(code);
        assertEquals("AURORA-ABCD-EFGH-WXYZ", code.getCode());
    }
    
    @Test
    void testParseFromUrlWithQuery() {
        ShareCode code = parser.parseFromUrl("https://aurora.example.com/share?code=AURORA-ABCD-EFGH-WXYZ");
        
        assertNotNull(code);
        assertEquals("AURORA-ABCD-EFGH-WXYZ", code.getCode());
    }
}