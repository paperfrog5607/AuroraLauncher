package org.aurora.launcher.ui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModIntegrationRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ModIntegrationRegistry.class);
    private static ModIntegrationRegistry instance;
    
    private final Map<String, ModIntegration> integrations = new ConcurrentHashMap<>();
    private final List<RecipeTemplate> globalTemplates = new ArrayList<>();
    private ModDetectionService modDetection;
    
    private ModIntegrationRegistry() {
        modDetection = ModDetectionService.getInstance();
        registerGlobalTemplates();
    }
    
    public static ModIntegrationRegistry getInstance() {
        if (instance == null) {
            instance = new ModIntegrationRegistry();
        }
        return instance;
    }
    
    public void register(ModIntegration integration) {
        integrations.put(integration.getModId(), integration);
        logger.info("Registered mod integration: {}", integration.getModId());
    }
    
    public void register(String modId, ModIntegration integration) {
        integrations.put(modId, integration);
        logger.info("Registered mod integration: {} -> {}", modId, integration.getModId());
    }
    
    public ModIntegration get(String modId) {
        return integrations.get(modId);
    }
    
    public boolean isRegistered(String modId) {
        return integrations.containsKey(modId);
    }
    
    public List<ModIntegration> getAvailableIntegrations() {
        return integrations.values().stream()
            .filter(ModIntegration::isInstalled)
            .collect(Collectors.toList());
    }
    
    public List<ModIntegration> getAllIntegrations() {
        return new ArrayList<>(integrations.values());
    }
    
    public List<RecipeTemplate> getAvailableTemplates() {
        List<RecipeTemplate> result = new ArrayList<>(globalTemplates);
        
        for (ModIntegration integration : integrations.values()) {
            if (integration.isInstalled()) {
                result.addAll(integration.getRecipeTemplates());
            }
        }
        
        return result;
    }
    
    public List<RecipeTemplate> getGlobalTemplates() {
        return new ArrayList<>(globalTemplates);
    }
    
    public List<String> getGlobalCompletionHints() {
        List<String> hints = new ArrayList<>();
        hints.add("event.shaped(");
        hints.add("event.shapeless(");
        hints.add("event.remove(");
        hints.add("event.add(");
        hints.add("ServerEvents.recipes(");
        hints.add("ServerEvents.tick(");
        hints.add("PlayerEvents.loggedIn(");
        hints.add("#minecraft:");
        hints.add("#forge:");
        return hints;
    }
    
    public List<String> getCompletionHints(String modId) {
        ModIntegration integration = integrations.get(modId);
        if (integration != null && integration.isInstalled()) {
            return integration.getCompletionHints();
        }
        return Collections.emptyList();
    }
    
    private void registerGlobalTemplates() {
        globalTemplates.add(RecipeTemplate.shaped(
            "Shaped Recipe",
            "形状合成",
            """
            ServerEvents.recipes(event => {
                event.shaped('minecraft:diamond', [
                    'AAA',
                    'BBB',
                    'CCC'
                ], {
                    A: 'minecraft:coal',
                    B: 'minecraft:iron_ingot',
                    C: 'minecraft:gold_ingot'
                })
            })
            """,
            "Create a shaped crafting recipe",
            "创建形状合成配方"
        ));
        
        globalTemplates.add(RecipeTemplate.shapeless(
            "Shapeless Recipe",
            "无序合成",
            """
            ServerEvents.recipes(event => {
                event.shapeless('minecraft:coal', ['minecraft:charcoal'], 'minecraft:oak_planks')
            })
            """,
            "Create a shapeless crafting recipe",
            "创建无序合成配方"
        ));
        
        globalTemplates.add(new RecipeTemplate(
            "stonecutting",
            "Stonecutting",
            "切石",
            """
            ServerEvents.recipes(event => {
                event.stonecutting('minecraft:stone', 'minecraft:cobblestone')
            })
            """,
            "Create a stonecutting recipe",
            "创建切石配方",
            RecipeTemplate.Category.KUBEJS
        ));
        
        globalTemplates.add(new RecipeTemplate(
            "smithing",
            "Smithing",
            "锻造",
            """
            ServerEvents.recipes(event => {
                event.smithing('minecraft:diamond_sword', 'minecraft:diamond', 'minecraft:stick')
            })
            """,
            "Create a smithing recipe",
            "创建锻造配方",
            RecipeTemplate.Category.KUBEJS
        ));
        
        globalTemplates.add(new RecipeTemplate(
            "campfire",
            "Campfire Cooking",
            "营火烹饪",
            """
            ServerEvents.recipes(event => {
                event.campfire('minecraft:cooked_beef', 'minecraft:beef', 0)
            })
            """,
            "Create a campfire cooking recipe",
            "创建营火烹饪配方",
            RecipeTemplate.Category.KUBEJS
        ));
        
        logger.info("Registered {} global templates", globalTemplates.size());
    }
}
