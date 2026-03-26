package org.aurora.launcher.dev.kubejs;

import org.aurora.launcher.dev.template.KubeJsCategory;
import org.aurora.launcher.dev.template.KubeJsTemplate;
import org.aurora.launcher.dev.template.TemplateManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class KubeJsServiceTest {

    private KubeJsService service;

    @BeforeEach
    void setUp() {
        TemplateManager manager = new TemplateManager();
        service = new KubeJsService(manager);
    }

    @Test
    void getTemplates() {
        KubeJsTemplate template = new KubeJsTemplate();
        template.setId("test-event");
        template.setCategory(KubeJsCategory.EVENT);
        service.getTemplateManager().addTemplate(template);
        
        List<KubeJsTemplate> templates = service.getTemplates(KubeJsCategory.EVENT);
        
        assertEquals(1, templates.size());
        assertEquals("test-event", templates.get(0).getId());
    }

    @Test
    void generate() {
        KubeJsTemplate template = new KubeJsTemplate();
        template.setId("test-template");
        template.setTemplate("ServerEvents.loaded(event => {\n  console.info('Hello ${name}!');\n});");
        
        Map<String, Object> params = new HashMap<>();
        params.put("name", "World");
        
        String result = service.generate(template, params);
        
        assertTrue(result.contains("Hello World!"));
    }

    @Test
    void export() throws Exception {
        String code = "console.info('test');";
        Path tempDir = java.nio.file.Files.createTempDirectory("kubejs-test");
        
        Path exported = service.export(code, "test.js", tempDir);
        
        assertTrue(java.nio.file.Files.exists(exported));
        assertEquals("test.js", exported.getFileName().toString());
        
        java.nio.file.Files.deleteIfExists(exported);
        java.nio.file.Files.deleteIfExists(tempDir);
    }

    @Test
    void getTemplateManager() {
        assertNotNull(service.getTemplateManager());
    }
}