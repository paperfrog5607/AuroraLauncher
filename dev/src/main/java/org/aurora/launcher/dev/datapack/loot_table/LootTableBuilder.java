package org.aurora.launcher.dev.datapack.loot_table;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class LootTableBuilder {
    private String type = "minecraft:generic";
    private List<Pool> pools;

    public LootTableBuilder() {
        this.pools = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LootTableBuilder addPool(Pool pool) {
        pools.add(pool);
        return this;
    }

    public JsonElement build() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        
        JsonArray poolsArray = new JsonArray();
        for (Pool pool : pools) {
            poolsArray.add(pool.toJson());
        }
        json.add("pools", poolsArray);
        
        return json;
    }
}