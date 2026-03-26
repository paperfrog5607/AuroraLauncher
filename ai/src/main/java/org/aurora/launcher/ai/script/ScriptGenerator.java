package org.aurora.launcher.ai.script;

import org.aurora.launcher.ai.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptGenerator {
    
    private final AiProvider provider;
    private final Map<ScriptType, String> prompts;
    
    public ScriptGenerator(AiProvider provider) {
        this.provider = provider;
        this.prompts = new HashMap<>();
        initializePrompts();
    }
    
    private void initializePrompts() {
        prompts.put(ScriptType.KUBEJS_EVENT, 
                "Generate a KubeJS event script for Minecraft.\n" +
                "Description: {{description}}\n" +
                "Minecraft Version: {{mcVersion}}\n" +
                "Loader: {{loader}}\n" +
                "Installed Mods: {{installedMods}}\n\n" +
                "Output the JavaScript code only, wrapped in ```javascript code blocks.");
        
        prompts.put(ScriptType.KUBEJS_RECIPE,
                "Generate a KubeJS recipe script for Minecraft.\n" +
                "Description: {{description}}\n" +
                "Minecraft Version: {{mcVersion}}\n\n" +
                "Output the JavaScript code only, wrapped in ```javascript code blocks.");
        
        prompts.put(ScriptType.KUBEJS_ITEM,
                "Generate a KubeJS item registration script for Minecraft.\n" +
                "Description: {{description}}\n" +
                "Minecraft Version: {{mcVersion}}\n\n" +
                "Output the JavaScript code only, wrapped in ```javascript code blocks.");
        
        prompts.put(ScriptType.CRAFTTWEAKER_RECIPE,
                "Generate a CraftTweaker recipe script for Minecraft.\n" +
                "Description: {{description}}\n" +
                "Minecraft Version: {{mcVersion}}\n\n" +
                "Output the ZenScript code only, wrapped in ```zenscript code blocks.");
        
        prompts.put(ScriptType.DATAPACK_FUNCTION,
                "Generate a Minecraft datapack function.\n" +
                "Description: {{description}}\n" +
                "Minecraft Version: {{mcVersion}}\n\n" +
                "Output the mcfunction code only, wrapped in ```mcfunction code blocks.");
    }
    
    public CompletableFuture<GeneratedScript> generate(ScriptType type, String description, ScriptContext context) {
        String prompt = buildPrompt(type, description, context);
        
        AiOptions options = new AiOptions();
        options.setMaxTokens(2048);
        options.setTemperature(0.7);
        
        return provider.complete(prompt, options)
                .thenApply(response -> parseScript(response.getContent(), type));
    }
    
    private String buildPrompt(ScriptType type, String description, ScriptContext context) {
        String template = prompts.getOrDefault(type, prompts.get(ScriptType.KUBEJS_EVENT));
        
        String result = template
                .replace("{{description}}", description != null ? description : "")
                .replace("{{mcVersion}}", context != null && context.getMcVersion() != null ? context.getMcVersion() : "latest")
                .replace("{{loader}}", context != null && context.getLoader() != null ? context.getLoader() : "forge")
                .replace("{{installedMods}}", context != null ? formatMods(context.getInstalledMods()) : "none");
        
        return result;
    }
    
    private String formatMods(java.util.List<String> mods) {
        if (mods == null || mods.isEmpty()) {
            return "none";
        }
        return String.join(", ", mods);
    }
    
    private GeneratedScript parseScript(String content, ScriptType type) {
        GeneratedScript script = new GeneratedScript();
        script.setType(type);
        
        Pattern codeBlockPattern = Pattern.compile("```\\w*\\n([\\s\\S]*?)```");
        Matcher matcher = codeBlockPattern.matcher(content);
        
        if (matcher.find()) {
            script.setCode(matcher.group(1).trim());
        } else {
            script.setCode(content.trim());
        }
        
        script.setFileName(generateFileName(type));
        
        return script;
    }
    
    private String generateFileName(ScriptType type) {
        String extension;
        switch (type) {
            case KUBEJS_EVENT:
            case KUBEJS_RECIPE:
            case KUBEJS_ITEM:
                extension = ".js";
                break;
            case CRAFTTWEAKER_RECIPE:
            case CRAFTTWEAKER_EVENT:
                extension = ".zs";
                break;
            case DATAPACK_FUNCTION:
                extension = ".mcfunction";
                break;
            default:
                extension = ".txt";
        }
        return "generated_script" + extension;
    }
}