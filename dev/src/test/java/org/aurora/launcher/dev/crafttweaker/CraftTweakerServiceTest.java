package org.aurora.launcher.dev.crafttweaker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CraftTweakerServiceTest {

    private CraftTweakerService service;

    @BeforeEach
    void setUp() {
        service = new CraftTweakerService();
    }

    @Test
    void getTemplates() {
        List<ZenScriptTemplate> templates = service.getTemplates();
        assertNotNull(templates);
    }

    @Test
    void generate() {
        ZenScriptTemplate template = new ZenScriptTemplate();
        template.setId("test");
        template.setTemplate("print('${message}');");
        
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("message", "Hello World");
        
        String result = service.generate(template, params);
        
        assertEquals("print('Hello World');", result);
    }
}