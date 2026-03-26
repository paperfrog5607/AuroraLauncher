package org.aurora.launcher.dev.kubejs.recipe;

import org.aurora.launcher.dev.template.KubeJsCategory;
import org.aurora.launcher.dev.template.KubeJsTemplate;

public class RecipeTemplate extends KubeJsTemplate {
    private RecipeType recipeType;

    public RecipeTemplate() {
        setCategory(KubeJsCategory.RECIPE);
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(RecipeType recipeType) {
        this.recipeType = recipeType;
    }
}