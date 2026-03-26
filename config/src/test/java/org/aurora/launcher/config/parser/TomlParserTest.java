package org.aurora.launcher.config.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TomlParserTest {
    
    private TomlParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new TomlParser();
    }
    
    @Test
    void getSupportedExtensions_returnsToml() {
        String[] extensions = parser.getSupportedExtensions();
        
        assertArrayEquals(new String[]{"toml"}, extensions);
    }
    
    @Test
    void parse_validToml_returnsMap() throws Exception {
        String content = "[server]\nport = 25565\nmax_players = 100\n";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        Map<String, Object> result = parser.parse(input);
        
        assertTrue(result.containsKey("server"));
        @SuppressWarnings("unchecked")
        Map<String, Object> server = (Map<String, Object>) result.get("server");
        assertEquals(25565L, server.get("port"));
        assertEquals(100L, server.get("max_players"));
    }
    
    @Test
    void parse_simpleValues_returnsMap() throws Exception {
        String content = "title = \"Test\"\nvalue = 42\n";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        Map<String, Object> result = parser.parse(input);
        
        assertEquals("Test", result.get("title"));
        assertEquals(42L, result.get("value"));
    }
    
    @Test
    void write_validMap_producesToml() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> server = new HashMap<>();
        server.put("port", 25565L);
        config.put("server", server);
        config.put("maxPlayers", 100L);
        
        parser.write(output, config);
        
        String result = output.toString();
        assertTrue(result.contains("[server]"));
        assertTrue(result.contains("port"));
    }
}