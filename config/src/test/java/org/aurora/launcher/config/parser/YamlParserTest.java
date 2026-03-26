package org.aurora.launcher.config.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class YamlParserTest {
    
    private YamlParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }
    
    @Test
    void getSupportedExtensions_returnsYamlAndYml() {
        String[] extensions = parser.getSupportedExtensions();
        
        assertArrayEquals(new String[]{"yaml", "yml"}, extensions);
    }
    
    @Test
    void parse_validYaml_returnsMap() throws Exception {
        String content = "server:\n  port: 25565\nmaxPlayers: 100\n";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        Map<String, Object> result = parser.parse(input);
        
        assertTrue(result.containsKey("server"));
        @SuppressWarnings("unchecked")
        Map<String, Object> server = (Map<String, Object>) result.get("server");
        assertEquals(25565, server.get("port"));
        assertEquals(100, result.get("maxPlayers"));
    }
    
    @Test
    void parse_simpleValues_returnsMap() throws Exception {
        String content = "title: Test\nvalue: 42\n";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        Map<String, Object> result = parser.parse(input);
        
        assertEquals("Test", result.get("title"));
        assertEquals(42, result.get("value"));
    }
    
    @Test
    void parse_listValues_returnsList() throws Exception {
        String content = "items:\n  - item1\n  - item2\n";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        Map<String, Object> result = parser.parse(input);
        
        assertTrue(result.get("items") instanceof java.util.List);
    }
    
    @Test
    void write_validMap_producesYaml() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> server = new HashMap<>();
        server.put("port", 25565);
        config.put("server", server);
        config.put("maxPlayers", 100);
        
        parser.write(output, config);
        
        String result = output.toString();
        assertTrue(result.contains("server"));
        assertTrue(result.contains("port"));
        assertTrue(result.contains("25565"));
    }
}