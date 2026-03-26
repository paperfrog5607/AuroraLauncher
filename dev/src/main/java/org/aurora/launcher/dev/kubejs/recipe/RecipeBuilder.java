package org.aurora.launcher.dev.kubejs.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeBuilder {
    private RecipeType type;
    private String id;
    private String output;
    private List<String> inputs;
    private String[] pattern;
    private Map<Character, String> key;
    private float experience;
    private int cookingTime;

    public RecipeBuilder() {
        this.inputs = new ArrayList<>();
        this.key = new HashMap<>();
        this.cookingTime = 200;
    }

    public RecipeType getType() {
        return type;
    }

    public void setType(RecipeType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public void addInput(String input) {
        this.inputs.add(input);
    }

    public void setPattern(String... rows) {
        this.pattern = rows;
    }

    public Map<Character, String> getKey() {
        return key;
    }

    public void addKey(char character, String item) {
        this.key.put(character, item);
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String buildKubeJs() {
        StringBuilder sb = new StringBuilder();
        sb.append("ServerEvents.recipes(event => {\n");
        
        switch (type) {
            case SHAPED:
                buildShaped(sb);
                break;
            case SHAPELESS:
                buildShapeless(sb);
                break;
            case SMELTING:
                buildSmelting(sb, "smelting");
                break;
            case BLASTING:
                buildSmelting(sb, "blasting");
                break;
            case SMOKING:
                buildSmelting(sb, "smoking");
                break;
            case CAMPFIRE_COOKING:
                buildSmelting(sb, "campfireCooking");
                break;
            case STONECUTTING:
                buildStonecutting(sb);
                break;
            default:
                buildShaped(sb);
        }
        
        sb.append("})");
        return sb.toString();
    }

    private void buildShaped(StringBuilder sb) {
        sb.append("  event.shaped('").append(output).append("', [\n");
        if (pattern != null) {
            for (int i = 0; i < pattern.length; i++) {
                sb.append("    '").append(pattern[i]).append("'");
                if (i < pattern.length - 1) sb.append(",");
                sb.append("\n");
            }
        }
        sb.append("  ], {\n");
        for (Map.Entry<Character, String> entry : key.entrySet()) {
            sb.append("    ").append(entry.getKey()).append(": '").append(entry.getValue()).append("',\n");
        }
        sb.append("  })\n");
    }

    private void buildShapeless(StringBuilder sb) {
        sb.append("  event.shapeless('").append(output).append("', [\n");
        for (int i = 0; i < inputs.size(); i++) {
            sb.append("    '").append(inputs.get(i)).append("'");
            if (i < inputs.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ])\n");
    }

    private void buildSmelting(StringBuilder sb, String method) {
        String inputItem = inputs.isEmpty() ? "minecraft:air" : inputs.get(0);
        sb.append("  event.").append(method).append("('").append(output).append("', '").append(inputItem).append("')\n");
        if (experience > 0) {
            sb.append("    .xp(").append(experience).append(")\n");
        }
        if (cookingTime != 200) {
            sb.append("    .cookingTime(").append(cookingTime).append(")\n");
        }
    }

    private void buildStonecutting(StringBuilder sb) {
        String inputItem = inputs.isEmpty() ? "minecraft:air" : inputs.get(0);
        sb.append("  event.stonecutting('").append(output).append("', '").append(inputItem).append("')\n");
    }
}