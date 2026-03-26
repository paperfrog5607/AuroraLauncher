package org.aurora.launcher.dev.datapack.loot_table;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Pool {
    private int rolls = 1;
    private float bonusRolls;
    private List<LootEntry> entries;
    private List<Condition> conditions;

    public Pool() {
        this.entries = new ArrayList<>();
        this.conditions = new ArrayList<>();
    }

    public int getRolls() {
        return rolls;
    }

    public void setRolls(int rolls) {
        this.rolls = rolls;
    }

    public float getBonusRolls() {
        return bonusRolls;
    }

    public void setBonusRolls(float bonusRolls) {
        this.bonusRolls = bonusRolls;
    }

    public List<LootEntry> getEntries() {
        return entries;
    }

    public void addEntry(LootEntry entry) {
        this.entries.add(entry);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("rolls", rolls);
        
        if (bonusRolls > 0) {
            json.addProperty("bonus_rolls", bonusRolls);
        }
        
        JsonArray entriesArray = new JsonArray();
        for (LootEntry entry : entries) {
            entriesArray.add(entry.toJson());
        }
        json.add("entries", entriesArray);
        
        if (!conditions.isEmpty()) {
            JsonArray conditionsArray = new JsonArray();
            for (Condition condition : conditions) {
                conditionsArray.add(condition.toJson());
            }
            json.add("conditions", conditionsArray);
        }
        
        return json;
    }
}