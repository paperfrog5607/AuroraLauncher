package org.aurora.launcher.dev.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class TemplateManagerTest {

    private TemplateManager manager;

    @BeforeEach
    void setUp() {
        manager = new TemplateManager();
    }

    @Test
    void addAndGetTemplate() {
        KubeJsTemplate template = new KubeJsTemplate();
        template.setId("test-template");
        template.setName("Test Template");
        template.setCategory(KubeJsCategory.RECIPE);
        
        manager.addTemplate(template);
        
        Optional<KubeJsTemplate> found = manager.getById("test-template");
        assertTrue(found.isPresent());
        assertEquals("Test Template", found.get().getName());
    }

    @Test
    void getByCategory() {
        KubeJsTemplate t1 = new KubeJsTemplate();
        t1.setId("recipe-1");
        t1.setCategory(KubeJsCategory.RECIPE);
        
        KubeJsTemplate t2 = new KubeJsTemplate();
        t2.setId("event-1");
        t2.setCategory(KubeJsCategory.EVENT);
        
        manager.addTemplate(t1);
        manager.addTemplate(t2);
        
        assertEquals(1, manager.getByCategory(KubeJsCategory.RECIPE).size());
        assertEquals(1, manager.getByCategory(KubeJsCategory.EVENT).size());
        assertEquals(0, manager.getByCategory(KubeJsCategory.ITEM).size());
    }

    @Test
    void getNonExistentTemplate() {
        Optional<KubeJsTemplate> found = manager.getById("non-existent");
        assertFalse(found.isPresent());
    }

    @Test
    void removeTemplate() {
        KubeJsTemplate template = new KubeJsTemplate();
        template.setId("remove-test");
        template.setCategory(KubeJsCategory.OTHER);
        
        manager.addTemplate(template);
        assertTrue(manager.getById("remove-test").isPresent());
        
        manager.removeTemplate("remove-test");
        assertFalse(manager.getById("remove-test").isPresent());
    }
}