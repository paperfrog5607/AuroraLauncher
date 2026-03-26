package org.aurora.launcher.dev.kubejs.recipe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecipeBuilderTest {

    @Test
    void buildShapedRecipe() {
        RecipeBuilder builder = new RecipeBuilder();
        builder.setType(RecipeType.SHAPED);
        builder.setId("test_recipe");
        builder.setOutput("minecraft:diamond");
        builder.setPattern("AAA", "BBB", "CCC");
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains("event.shaped"));
        assertTrue(result.contains("minecraft:diamond"));
        assertTrue(result.contains("AAA"));
        assertTrue(result.contains("BBB"));
        assertTrue(result.contains("CCC"));
    }

    @Test
    void buildShapelessRecipe() {
        RecipeBuilder builder = new RecipeBuilder();
        builder.setType(RecipeType.SHAPELESS);
        builder.setId("shapeless_test");
        builder.setOutput("minecraft:emerald");
        builder.addInput("minecraft:diamond");
        builder.addInput("minecraft:gold_ingot");
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains("event.shapeless"));
        assertTrue(result.contains("minecraft:emerald"));
        assertTrue(result.contains("minecraft:diamond"));
        assertTrue(result.contains("minecraft:gold_ingot"));
    }

    @Test
    void buildSmeltingRecipe() {
        RecipeBuilder builder = new RecipeBuilder();
        builder.setType(RecipeType.SMELTING);
        builder.setId("smelting_test");
        builder.setOutput("minecraft:iron_ingot");
        builder.addInput("minecraft:raw_iron");
        builder.setExperience(0.7f);
        builder.setCookingTime(100);
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains("event.smelting"));
        assertTrue(result.contains("minecraft:iron_ingot"));
        assertTrue(result.contains("minecraft:raw_iron"));
        assertTrue(result.contains("0.7"));
        assertTrue(result.contains("100"));
    }

    @Test
    void buildBlastingRecipe() {
        RecipeBuilder builder = new RecipeBuilder();
        builder.setType(RecipeType.BLASTING);
        builder.setOutput("minecraft:iron_ingot");
        builder.addInput("minecraft:raw_iron");
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains("event.blasting"));
    }

    @Test
    void buildWithKey() {
        RecipeBuilder builder = new RecipeBuilder();
        builder.setType(RecipeType.SHAPED);
        builder.setOutput("minecraft:diamond_sword");
        builder.setPattern(" D ", " D ", " S ");
        builder.addKey('D', "minecraft:diamond");
        builder.addKey('S', "minecraft:stick");
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains("D: 'minecraft:diamond'"));
        assertTrue(result.contains("S: 'minecraft:stick'"));
    }
}