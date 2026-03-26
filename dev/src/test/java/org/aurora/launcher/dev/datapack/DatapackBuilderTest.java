package org.aurora.launcher.dev.datapack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class DatapackBuilderTest {

    @TempDir
    Path tempDir;

    @Test
    void createDatapack() {
        DatapackBuilder builder = new DatapackBuilder();
        builder.setName("test_pack");
        builder.setDescription("Test datapack");
        builder.setPackFormat(26);
        
        assertEquals("test_pack", builder.getName());
        assertEquals("Test datapack", builder.getDescription());
        assertEquals(26, builder.getPackFormat());
    }

    @Test
    void buildEmptyDatapack() throws Exception {
        DatapackBuilder builder = new DatapackBuilder();
        builder.setName("empty_pack");
        builder.setDescription("Empty test pack");
        builder.setPackFormat(26);
        
        Path packDir = builder.build(tempDir);
        
        assertTrue(java.nio.file.Files.exists(packDir));
        assertTrue(java.nio.file.Files.exists(packDir.resolve("pack.mcmeta")));
    }

    @Test
    void buildWithFunction() throws Exception {
        DatapackBuilder builder = new DatapackBuilder();
        builder.setName("func_pack");
        builder.setDescription("Pack with function");
        builder.setPackFormat(26);
        
        builder.function("minecraft:test", "say Hello World");
        
        Path packDir = builder.build(tempDir);
        
        Path funcPath = packDir.resolve("data").resolve("minecraft").resolve("functions").resolve("test.mcfunction");
        assertTrue(java.nio.file.Files.exists(funcPath));
    }

    @Test
    void buildWithLootTable() throws Exception {
        DatapackBuilder builder = new DatapackBuilder();
        builder.setName("loot_pack");
        builder.setPackFormat(26);
        
        org.aurora.launcher.dev.datapack.loot_table.LootTableBuilder lootBuilder = 
            new org.aurora.launcher.dev.datapack.loot_table.LootTableBuilder();
        builder.lootTable("minecraft:entities/test", lootBuilder);
        
        Path packDir = builder.build(tempDir);
        
        Path lootPath = packDir.resolve("data").resolve("minecraft").resolve("loot_tables").resolve("entities").resolve("test.json");
        assertTrue(java.nio.file.Files.exists(lootPath));
    }

    @Test
    void buildWithTag() throws Exception {
        DatapackBuilder builder = new DatapackBuilder();
        builder.setName("tag_pack");
        builder.setPackFormat(26);
        
        org.aurora.launcher.dev.datapack.tag.TagBuilder tagBuilder = 
            new org.aurora.launcher.dev.datapack.tag.TagBuilder();
        tagBuilder.add("minecraft:stone");
        tagBuilder.add("minecraft:dirt");
        builder.tag("minecraft:blocks/test", tagBuilder);
        
        Path packDir = builder.build(tempDir);
        
        Path tagPath = packDir.resolve("data").resolve("minecraft").resolve("tags").resolve("blocks").resolve("test.json");
        assertTrue(java.nio.file.Files.exists(tagPath));
    }
}