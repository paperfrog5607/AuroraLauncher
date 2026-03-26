package org.aurora.launcher.dev.datapack.tag;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TagBuilderTest {

    @Test
    void buildSimpleTag() {
        TagBuilder builder = new TagBuilder();
        builder.add("minecraft:stone");
        builder.add("minecraft:dirt");
        
        com.google.gson.JsonElement json = builder.build();
        
        assertTrue(json.isJsonObject());
        assertEquals(2, json.getAsJsonObject().getAsJsonArray("values").size());
    }

    @Test
    void buildWithReplace() {
        TagBuilder builder = new TagBuilder();
        builder.setReplace(true);
        builder.add("minecraft:stone");
        
        com.google.gson.JsonElement json = builder.build();
        
        assertTrue(json.getAsJsonObject().get("replace").getAsBoolean());
    }

    @Test
    void addAll() {
        TagBuilder builder = new TagBuilder();
        builder.addAll(java.util.Arrays.asList("minecraft:stone", "minecraft:dirt", "minecraft:grass_block"));
        
        com.google.gson.JsonElement json = builder.build();
        
        assertEquals(3, json.getAsJsonObject().getAsJsonArray("values").size());
    }
}