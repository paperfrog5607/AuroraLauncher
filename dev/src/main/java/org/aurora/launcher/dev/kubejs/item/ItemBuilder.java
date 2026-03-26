package org.aurora.launcher.dev.kubejs.item;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private String id;
    private String displayName;
    private int maxStackSize = 64;
    private int maxDamage;
    private String rarity = "common";
    private List<String> tooltip;
    private FoodProperties food;
    private boolean glow;
    private String group;

    public ItemBuilder() {
        this.tooltip = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public List<String> getTooltip() {
        return tooltip;
    }

    public void addTooltip(String line) {
        this.tooltip.add(line);
    }

    public FoodProperties getFood() {
        return food;
    }

    public void setFood(FoodProperties food) {
        this.food = food;
    }

    public boolean isGlow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String buildKubeJs() {
        StringBuilder sb = new StringBuilder();
        sb.append("StartupEvents.registry('item', event => {\n");
        sb.append("  event.create('").append(id).append("')");
        
        if (displayName != null) {
            sb.append("\n    .displayName('").append(displayName).append("')");
        }
        if (maxStackSize != 64) {
            sb.append("\n    .maxStackSize(").append(maxStackSize).append(")");
        }
        if (maxDamage > 0) {
            sb.append("\n    .maxDamage(").append(maxDamage).append(")");
        }
        if (!"common".equals(rarity)) {
            sb.append("\n    .rarity('").append(rarity).append("')");
        }
        for (String line : tooltip) {
            sb.append("\n    .tooltip('").append(line).append("')");
        }
        if (food != null) {
            sb.append("\n    .food(food => {");
            sb.append("\n      food.hunger(").append(food.getHunger()).append(")");
            sb.append("\n      food.saturation(").append(food.getSaturation()).append(")");
            sb.append("\n    })");
        }
        if (glow) {
            sb.append("\n    .glow(true)");
        }
        
        sb.append("\n})");
        return sb.toString();
    }
}