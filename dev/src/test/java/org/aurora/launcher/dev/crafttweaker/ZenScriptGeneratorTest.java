package org.aurora.launcher.dev.crafttweaker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ZenScriptGeneratorTest {

    @Test
    void generateShapedRecipe() {
        ZenScriptGenerator generator = new ZenScriptGenerator();
        RecipeConfig config = new RecipeConfig();
        config.setType(org.aurora.launcher.dev.kubejs.recipe.RecipeType.SHAPED);
        config.setId("shaped_test");
        config.setOutput("minecraft:diamond");
        config.setPattern(new String[]{"AAA", "BBB", "CCC"});
        
        String result = generator.generateRecipe(config);
        
        assertTrue(result.contains("craftingTable.addShaped"));
        assertTrue(result.contains("shaped_test"));
        assertTrue(result.contains("<item:minecraft:diamond>"));
    }

    @Test
    void generateShapelessRecipe() {
        ZenScriptGenerator generator = new ZenScriptGenerator();
        RecipeConfig config = new RecipeConfig();
        config.setType(org.aurora.launcher.dev.kubejs.recipe.RecipeType.SHAPELESS);
        config.setId("shapeless_test");
        config.setOutput("minecraft:emerald");
        config.addInput("minecraft:diamond");
        config.addInput("minecraft:gold_ingot");
        
        String result = generator.generateRecipe(config);
        
        assertTrue(result.contains("craftingTable.addShapeless"));
        assertTrue(result.contains("<item:minecraft:emerald>"));
        assertTrue(result.contains("<item:minecraft:diamond>"));
    }

    @Test
    void generateSmeltingRecipe() {
        ZenScriptGenerator generator = new ZenScriptGenerator();
        RecipeConfig config = new RecipeConfig();
        config.setType(org.aurora.launcher.dev.kubejs.recipe.RecipeType.SMELTING);
        config.setId("smelting_test");
        config.setOutput("minecraft:iron_ingot");
        config.addInput("minecraft:raw_iron");
        config.setXp(0.7f);
        config.setTime(200);
        
        String result = generator.generateRecipe(config);
        
        assertTrue(result.contains("furnace.addRecipe"));
        assertTrue(result.contains("<item:minecraft:iron_ingot>"));
        assertTrue(result.contains("<item:minecraft:raw_iron>"));
        assertTrue(result.contains("0.7"));
        assertTrue(result.contains("200"));
    }

    @Test
    void generateBlastingRecipe() {
        ZenScriptGenerator generator = new ZenScriptGenerator();
        RecipeConfig config = new RecipeConfig();
        config.setType(org.aurora.launcher.dev.kubejs.recipe.RecipeType.BLASTING);
        config.setId("blasting_test");
        config.setOutput("minecraft:iron_ingot");
        config.addInput("minecraft:raw_iron");
        
        String result = generator.generateRecipe(config);
        
        assertTrue(result.contains("blastFurnace.addRecipe"));
    }
}