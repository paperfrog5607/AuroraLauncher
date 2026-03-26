package org.aurora.launcher.config.template;

import org.aurora.launcher.config.editor.ConfigEditor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigTemplateTest {
    
    @TempDir
    Path tempDir;
    
    private ConfigTemplate template;
    
    @BeforeEach
    void setUp() {
        template = new ConfigTemplate();
        template.setId("test-template");
        template.setName("Test Template");
        template.setDescription("A test template");
    }
    
    @Test
    void apply_setsDefaults() throws Exception {
        template.getDefaults().put("server-port", 25565);
        template.getDefaults().put("max-players", 20);
        
        Path configPath = tempDir.resolve("test.properties");
        template.apply(configPath);
        
        ConfigEditor editor = ConfigEditor.load(configPath);
        assertEquals("25565", editor.get("server-port"));
        assertEquals("20", editor.get("max-players"));
    }
    
    @Test
    void apply_withRules_appliesRules() throws Exception {
        template.addRule(new TemplateRule("difficulty", "hard"));
        template.addRule(new TemplateRule("spawn-protection", 16, TemplateRule.RuleCondition.IF_MISSING));
        
        Path configPath = tempDir.resolve("test.properties");
        template.apply(configPath);
        
        ConfigEditor editor = ConfigEditor.load(configPath);
        assertEquals("hard", editor.get("difficulty"));
        assertEquals("16", editor.get("spawn-protection"));
    }
    
    @Test
    void applyWithOverrides_appliesOverrides() throws Exception {
        template.getDefaults().put("server-port", 25565);
        
        Path configPath = tempDir.resolve("test.properties");
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("server-port", 25566);
        template.applyWithOverrides(configPath, overrides);
        
        ConfigEditor editor = ConfigEditor.load(configPath);
        assertEquals("25566", editor.get("server-port"));
    }
    
    @Test
    void setters_updateFields() {
        template.setGameVersion("1.20.1");
        template.setLoaderType("fabric");
        template.setLoaderVersion("0.15.0");
        
        assertEquals("1.20.1", template.getGameVersion());
        assertEquals("fabric", template.getLoaderType());
        assertEquals("0.15.0", template.getLoaderVersion());
    }
}