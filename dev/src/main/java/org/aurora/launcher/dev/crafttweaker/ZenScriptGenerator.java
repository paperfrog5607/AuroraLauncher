package org.aurora.launcher.dev.crafttweaker;

import org.aurora.launcher.dev.kubejs.recipe.RecipeType;

public class ZenScriptGenerator {

    public String generateRecipe(RecipeConfig config) {
        StringBuilder sb = new StringBuilder();
        
        switch (config.getType()) {
            case SHAPED:
                sb.append("craftingTable.addShaped(\"").append(config.getId()).append("\", \n");
                sb.append("  <item:").append(config.getOutput()).append(">, \n");
                sb.append("  [\n");
                if (config.getPattern() != null) {
                    for (String row : config.getPattern()) {
                        sb.append("    [").append(buildIngredientRow(row)).append("],\n");
                    }
                }
                sb.append("  ]\n");
                sb.append(");\n");
                break;
                
            case SHAPELESS:
                sb.append("craftingTable.addShapeless(\"").append(config.getId()).append("\", \n");
                sb.append("  <item:").append(config.getOutput()).append(">, \n");
                sb.append("  [").append(buildIngredientList(config.getInputs())).append("]\n");
                sb.append(");\n");
                break;
                
            case SMELTING:
                sb.append("furnace.addRecipe(\"").append(config.getId()).append("\", \n");
                sb.append("  <item:").append(config.getInputs().isEmpty() ? "air" : config.getInputs().get(0)).append(">, \n");
                sb.append("  <item:").append(config.getOutput()).append(">, \n");
                sb.append("  ").append(config.getXp()).append(", \n");
                sb.append("  ").append(config.getTime()).append("\n");
                sb.append(");\n");
                break;
                
            case BLASTING:
                sb.append("blastFurnace.addRecipe(\"").append(config.getId()).append("\", \n");
                sb.append("  <item:").append(config.getInputs().isEmpty() ? "air" : config.getInputs().get(0)).append(">, \n");
                sb.append("  <item:").append(config.getOutput()).append(">, \n");
                sb.append("  ").append(config.getXp()).append(", \n");
                sb.append("  ").append(config.getTime()).append("\n");
                sb.append(");\n");
                break;
                
            case SMOKING:
                sb.append("smoker.addRecipe(\"").append(config.getId()).append("\", \n");
                sb.append("  <item:").append(config.getInputs().isEmpty() ? "air" : config.getInputs().get(0)).append(">, \n");
                sb.append("  <item:").append(config.getOutput()).append(">, \n");
                sb.append("  ").append(config.getXp()).append(", \n");
                sb.append("  ").append(config.getTime()).append("\n");
                sb.append(");\n");
                break;
                
            default:
                sb.append("// Unsupported recipe type: ").append(config.getType()).append("\n");
        }
        
        return sb.toString();
    }

    private String buildIngredientRow(String row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);
            if (c == ' ') {
                sb.append("<item:air>");
            } else {
                sb.append("<item:").append(c).append(">");
            }
            if (i < row.length() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    private String buildIngredientList(java.util.List<String> inputs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputs.size(); i++) {
            sb.append("<item:").append(inputs.get(i)).append(">");
            if (i < inputs.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}