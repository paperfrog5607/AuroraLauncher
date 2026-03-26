package org.aurora.launcher.config.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ConfigTemplateManagerTest {
    
    @TempDir
    Path tempDir;
    
    private ConfigTemplateManager manager;
    
    @BeforeEach
    void setUp() {
        manager = ConfigTemplateManager.getInstance();
        manager.setTemplateDir(tempDir);
    }
    
    @Test
    void getInstance_returnsSameInstance() {
        ConfigTemplateManager instance1 = ConfigTemplateManager.getInstance();
        ConfigTemplateManager instance2 = ConfigTemplateManager.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    void saveTemplate_and_getTemplate() throws Exception {
        ConfigTemplate template = new ConfigTemplate();
        template.setId("test");
        template.setName("Test Template");
        
        manager.saveTemplate(template);
        
        assertNotNull(manager.getTemplate("test"));
        assertEquals("Test Template", manager.getTemplate("test").getName());
    }
    
    @Test
    void getTemplates_returnsAllTemplates() throws Exception {
        ConfigTemplate template1 = new ConfigTemplate();
        template1.setId("test1");
        template1.setName("Test 1");
        
        ConfigTemplate template2 = new ConfigTemplate();
        template2.setId("test2");
        template2.setName("Test 2");
        
        manager.saveTemplate(template1);
        manager.saveTemplate(template2);
        
        List<ConfigTemplate> templates = manager.getTemplates();
        
        assertTrue(templates.size() >= 2);
    }
    
    @Test
    void deleteTemplate_removesTemplate() throws Exception {
        ConfigTemplate template = new ConfigTemplate();
        template.setId("to-delete");
        template.setName("To Delete");
        
        manager.saveTemplate(template);
        manager.deleteTemplate("to-delete");
        
        assertNull(manager.getTemplate("to-delete"));
    }
    
    @Test
    void exportTemplate_createsFile() throws Exception {
        ConfigTemplate template = new ConfigTemplate();
        template.setId("export-test");
        template.setName("Export Test");
        manager.saveTemplate(template);
        
        Path exportPath = tempDir.resolve("exported.json");
        manager.exportTemplate("export-test", exportPath);
        
        assertTrue(Files.exists(exportPath));
    }
    
    @Test
    void getTemplatesForVersion_filtersByVersion() throws Exception {
        ConfigTemplate template1 = new ConfigTemplate();
        template1.setId("version-test");
        template1.setName("Version Test");
        template1.setGameVersion("1.20.1");
        
        ConfigTemplate template2 = new ConfigTemplate();
        template2.setId("version-test2");
        template2.setName("Version Test 2");
        template2.setGameVersion("1.19.4");
        
        manager.saveTemplate(template1);
        manager.saveTemplate(template2);
        
        List<ConfigTemplate> filtered = manager.getTemplatesForVersion("1.20.1");
        
        assertTrue(filtered.stream().anyMatch(t -> "version-test".equals(t.getId())));
        assertFalse(filtered.stream().anyMatch(t -> "version-test2".equals(t.getId())));
    }
    
    @Test
    void getTemplatesForLoader_filtersByLoader() throws Exception {
        ConfigTemplate template1 = new ConfigTemplate();
        template1.setId("loader-test");
        template1.setName("Loader Test");
        template1.setLoaderType("fabric");
        
        ConfigTemplate template2 = new ConfigTemplate();
        template2.setId("loader-test2");
        template2.setName("Loader Test 2");
        template2.setLoaderType("forge");
        
        manager.saveTemplate(template1);
        manager.saveTemplate(template2);
        
        List<ConfigTemplate> filtered = manager.getTemplatesForLoader("fabric");
        
        assertTrue(filtered.stream().anyMatch(t -> "loader-test".equals(t.getId())));
        assertFalse(filtered.stream().anyMatch(t -> "loader-test2".equals(t.getId())));
    }
}