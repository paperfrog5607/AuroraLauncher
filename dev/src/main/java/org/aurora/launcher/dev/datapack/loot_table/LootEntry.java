package org.aurora.launcher.dev.datapack.loot_table;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LootEntry {
    private String type = "minecraft:item";
    private String name;
    private int weight = 1;

    public LootEntry() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.addProperty("name", name);
        if (weight != 1) {
            json.addProperty("weight", weight);
        }
        return json;
    }
}