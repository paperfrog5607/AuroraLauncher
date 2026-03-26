package org.aurora.launcher.modpack.template;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {
    
    private Template template;
    
    @BeforeEach
    void setUp() {
        template = new Template();
    }
    
    @Test
    void testTemplateCreation() {
        template.setId("template-1");
        template.setName("Tech Template");
        template.setDescription("A tech-focused modpack template");
        template.setMinecraftVersion("1.20.4");
        template.setLoaderType("fabric");
        template.setLoaderVersion("0.15.7");
        template.setAuthor("Test Author");
        
        assertEquals("template-1", template.getId());
        assertEquals("Tech Template", template.getName());
        assertEquals("A tech-focused modpack template", template.getDescription());
        assertEquals("1.20.4", template.getMinecraftVersion());
        assertEquals("fabric", template.getLoaderType());
        assertEquals("0.15.7", template.getLoaderVersion());
        assertEquals("Test Author", template.getAuthor());
    }
    
    @Test
    void testTags() {
        template.addTag("tech");
        template.addTag("survival");
        
        assertEquals(2, template.getTags().size());
        assertTrue(template.getTags().contains("tech"));
        assertTrue(template.getTags().contains("survival"));
        
        template.addTag("tech");
        assertEquals(2, template.getTags().size());
    }
    
    @Test
    void testDefaultMods() {
        template.addDefaultMod("fabric-api");
        template.addDefaultMod("sodium");
        template.addDefaultMod("lithium");
        
        assertEquals(3, template.getDefaultMods().size());
        assertTrue(template.getDefaultMods().contains("fabric-api"));
        assertTrue(template.getDefaultMods().contains("sodium"));
    }
    
    @Test
    void testTimestamps() {
        Instant now = Instant.now();
        template.setCreatedTime(now);
        
        assertEquals(now, template.getCreatedTime());
    }
    
    @Test
    void testDefaultLoaderType() {
        assertEquals("vanilla", template.getLoaderType());
    }
}