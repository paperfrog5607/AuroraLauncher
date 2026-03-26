package org.aurora.launcher.dev.crafttweaker;

import org.aurora.launcher.dev.kubejs.recipe.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftTweakerService {
    private final List<ZenScriptTemplate> templates;

    public CraftTweakerService() {
        this.templates = new ArrayList<>();
        initializeBuiltInTemplates();
    }

    private void initializeBuiltInTemplates() {
        ZenScriptTemplate shaped = new ZenScriptTemplate();
        shaped.setId("shaped_recipe");
        shaped.setName("Shaped Recipe");
        shaped.setTemplate("craftingTable.addShaped(\"${id}\", <item:${output}>, [${pattern}]);");
        templates.add(shaped);
        
        ZenScriptTemplate shapeless = new ZenScriptTemplate();
        shapeless.setId("shapeless_recipe");
        shapeless.setName("Shapeless Recipe");
        shapeless.setTemplate("craftingTable.addShapeless(\"${id}\", <item:${output}>, [${inputs}]);");
        templates.add(shapeless);
    }

    public List<ZenScriptTemplate> getTemplates() {
        return templates;
    }

    public String generate(ZenScriptTemplate template, Map<String, Object> params) {
        return template.render(params);
    }
}