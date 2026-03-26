package org.aurora.launcher.config.parser;

import com.google.gson.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {
    
    private JsonParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new JsonParser();
    }
    
    @Test
    void getSupportedExtensions_returnsJson() {
        String[] extensions = parser.getSupportedExtensions();
        
        assertArrayEquals(new String[]{"json"}, extensions);
    }
    
    @Test
    void parse_validJson_returnsMap() throws Exception {
        String content = "{\"server\":{\"port\":25565},\"maxPlayers\":100}";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        Map<String, Object> result = parser.parse(input);
        
        assertEquals(2, result.size());
        assertTrue(result.get("server") instanceof Map);
        assertTrue(result.get("maxPlayers") instanceof Number);
        assertEquals(100, ((Number) result.get("maxPlayers")).intValue());
    }
    
    @Test
    void parse_jsonArray_throwsException() {
        String content = "[1,2,3]";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        assertThrows(ConfigParseException.class, () -> parser.parse(input));
    }
    
    @Test
    void write_validMap_producesJson() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> server = new HashMap<>();
        server.put("port", 25565);
        config.put("server", server);
        config.put("maxPlayers", 100);
        
        parser.write(output, config);
        
        String result = output.toString();
        assertTrue(result.contains("\"server\""));
        assertTrue(result.contains("\"port\""));
        assertTrue(result.contains("25565"));
        assertTrue(result.contains("\"maxPlayers\""));
        assertTrue(result.contains("100"));
    }
    
    @Test
    void parseObject_validJson_returnsJsonObject() throws Exception {
        String content = "{\"name\":\"test\"}";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        JsonObject result = parser.parseObject(input);
        
        assertEquals("test", result.get("name").getAsString());
    }
    
    @Test
    void parseArray_validJson_returnsJsonArray() throws Exception {
        String content = "[1,2,3]";
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
        
        JsonArray result = parser.parseArray(input);
        
        assertEquals(3, result.size());
    }
}