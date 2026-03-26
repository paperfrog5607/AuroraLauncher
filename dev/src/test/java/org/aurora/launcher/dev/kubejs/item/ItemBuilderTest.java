package org.aurora.launcher.dev.kubejs.item;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemBuilderTest {

    @Test
    void buildSimpleItem() {
        ItemBuilder builder = new ItemBuilder();
        builder.setId("test_mod:test_item");
        builder.setDisplayName("Test Item");
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains("event.create('test_mod:test_item')"));
        assertTrue(result.contains(".displayName('Test Item')"));
    }

    @Test
    void buildItemWithMaxStackSize() {
        ItemBuilder builder = new ItemBuilder();
        builder.setId("test_mod:stackable");
        builder.setMaxStackSize(16);
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains(".maxStackSize(16)"));
    }

    @Test
    void buildItemWithMaxDamage() {
        ItemBuilder builder = new ItemBuilder();
        builder.setId("test_mod:durable");
        builder.setMaxDamage(100);
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains(".maxDamage(100)"));
    }

    @Test
    void buildItemWithRarity() {
        ItemBuilder builder = new ItemBuilder();
        builder.setId("test_mod:rare_item");
        builder.setRarity("rare");
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains(".rarity('rare')"));
    }

    @Test
    void buildFoodItem() {
        ItemBuilder builder = new ItemBuilder();
        builder.setId("test_mod:food_item");
        FoodProperties food = new FoodProperties();
        food.setHunger(4);
        food.setSaturation(0.3f);
        builder.setFood(food);
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains(".food(food => {"));
        assertTrue(result.contains("food.hunger(4)"));
        assertTrue(result.contains("food.saturation(0.3)"));
    }

    @Test
    void buildItemWithTooltip() {
        ItemBuilder builder = new ItemBuilder();
        builder.setId("test_mod:tooltip_item");
        builder.addTooltip("Line 1");
        builder.addTooltip("Line 2");
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains(".tooltip('Line 1')"));
        assertTrue(result.contains(".tooltip('Line 2')"));
    }
}