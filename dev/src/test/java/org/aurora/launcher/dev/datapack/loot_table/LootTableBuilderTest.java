package org.aurora.launcher.dev.datapack.loot_table;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LootTableBuilderTest {

    @Test
    void buildSimpleLootTable() {
        LootTableBuilder builder = new LootTableBuilder();
        builder.setType("minecraft:entity");
        
        Pool pool = new Pool();
        pool.setRolls(1);
        LootEntry entry = new LootEntry();
        entry.setName("minecraft:diamond");
        pool.addEntry(entry);
        builder.addPool(pool);
        
        com.google.gson.JsonElement json = builder.build();
        
        assertTrue(json.isJsonObject());
        assertEquals("minecraft:entity", json.getAsJsonObject().get("type").getAsString());
        assertTrue(json.getAsJsonObject().has("pools"));
    }

    @Test
    void buildWithMultiplePools() {
        LootTableBuilder builder = new LootTableBuilder();
        
        Pool pool1 = new Pool();
        pool1.setRolls(1);
        LootEntry entry1 = new LootEntry();
        entry1.setName("minecraft:diamond");
        pool1.addEntry(entry1);
        
        Pool pool2 = new Pool();
        pool2.setRolls(2);
        LootEntry entry2 = new LootEntry();
        entry2.setName("minecraft:gold_ingot");
        pool2.addEntry(entry2);
        
        builder.addPool(pool1);
        builder.addPool(pool2);
        
        com.google.gson.JsonElement json = builder.build();
        
        assertEquals(2, json.getAsJsonObject().getAsJsonArray("pools").size());
    }
}