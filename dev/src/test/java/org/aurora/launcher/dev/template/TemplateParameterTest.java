package org.aurora.launcher.dev.template;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TemplateParameterTest {

    @Test
    void createStringParameter() {
        TemplateParameter param = new TemplateParameter();
        param.setName("output");
        param.setLabel("Output Item");
        param.setDescription("The output item ID");
        param.setType(ParameterType.STRING);
        param.setDefaultValue("minecraft:diamond");
        
        assertEquals("output", param.getName());
        assertEquals("Output Item", param.getLabel());
        assertEquals("The output item ID", param.getDescription());
        assertEquals(ParameterType.STRING, param.getType());
        assertEquals("minecraft:diamond", param.getDefaultValue());
    }

    @Test
    void createSelectParameter() {
        TemplateParameter param = new TemplateParameter();
        param.setName("rarity");
        param.setType(ParameterType.SELECT);
        param.setOptions(java.util.Arrays.asList("common", "uncommon", "rare", "epic"));
        
        assertEquals(4, param.getOptions().size());
        assertTrue(param.getOptions().contains("rare"));
    }

    @Test
    void parameterTypeEnum() {
        assertEquals(7, ParameterType.values().length);
        assertEquals(ParameterType.STRING, ParameterType.valueOf("STRING"));
        assertEquals(ParameterType.NUMBER, ParameterType.valueOf("NUMBER"));
        assertEquals(ParameterType.BOOLEAN, ParameterType.valueOf("BOOLEAN"));
        assertEquals(ParameterType.ITEM_ID, ParameterType.valueOf("ITEM_ID"));
        assertEquals(ParameterType.BLOCK_ID, ParameterType.valueOf("BLOCK_ID"));
        assertEquals(ParameterType.FLUID_ID, ParameterType.valueOf("FLUID_ID"));
        assertEquals(ParameterType.SELECT, ParameterType.valueOf("SELECT"));
    }
}