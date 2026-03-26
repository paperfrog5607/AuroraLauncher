package org.aurora.launcher.config.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class PropertiesParserTest {
    
    private PropertiesParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new PropertiesParser();
    }
    
    @Test
    void getSupportedExtensions_returnsProperties() {
        String[] extensions = parser.getSupportedExtensions();
        
        assertArrayEquals(new String[]{"properties"}, extensions);
    }
    
    @Test
    void parse_validProperties_returnsMap() throws Exception {
        String content = "server.port=25565\nmax.players=100\n";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        Map<String, Object> result = parser.parse(input);
        
        assertEquals(2, result.size());
        assertEquals("25565", result.get("server.port"));
        assertEquals("100", result.get("max.players"));
    }
    
    @Test
    void parse_propertiesWithComments_ignoresComments() throws Exception {
        String content = "# This is a comment\nserver.port=25565\n";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        Map<String, Object> result = parser.parse(input);
        
        assertEquals(1, result.size());
        assertEquals("25565", result.get("server.port"));
    }
    
    @Test
    void write_validMap_producesProperties() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Map<String, Object> config = new HashMap<>();
        config.put("server.port", "25565");
        config.put("max.players", "100");
        
        parser.write(output, config);
        
        String result = output.toString();
        assertTrue(result.contains("server.port=25565"));
        assertTrue(result.contains("max.players=100"));
    }
}