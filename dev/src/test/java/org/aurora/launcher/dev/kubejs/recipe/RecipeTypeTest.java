package org.aurora.launcher.dev.kubejs.recipe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecipeTypeTest {

    @Test
    void recipeTypeEnum() {
        assertEquals(9, RecipeType.values().length);
        assertEquals(RecipeType.SHAPED, RecipeType.valueOf("SHAPED"));
        assertEquals(RecipeType.SHAPELESS, RecipeType.valueOf("SHAPELESS"));
        assertEquals(RecipeType.SMELTING, RecipeType.valueOf("SMELTING"));
        assertEquals(RecipeType.BLASTING, RecipeType.valueOf("BLASTING"));
        assertEquals(RecipeType.SMOKING, RecipeType.valueOf("SMOKING"));
        assertEquals(RecipeType.CAMPFIRE_COOKING, RecipeType.valueOf("CAMPFIRE_COOKING"));
        assertEquals(RecipeType.STONECUTTING, RecipeType.valueOf("STONECUTTING"));
        assertEquals(RecipeType.SMITHING, RecipeType.valueOf("SMITHING"));
        assertEquals(RecipeType.BREWING, RecipeType.valueOf("BREWING"));
    }
}