package org.aurora.launcher.dev.kubejs.block;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlockBuilderTest {

    @Test
    void buildSimpleBlock() {
        BlockBuilder builder = new BlockBuilder();
        builder.setId("test_mod:test_block");
        builder.setDisplayName("Test Block");
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains("event.create('test_mod:test_block')"));
        assertTrue(result.contains(".displayName('Test Block')"));
    }

    @Test
    void buildBlockWithMaterial() {
        BlockBuilder builder = new BlockBuilder();
        builder.setId("test_mod:stone_block");
        builder.setMaterial("metal");
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains(".material('metal')"));
    }

    @Test
    void buildBlockWithHardness() {
        BlockBuilder builder = new BlockBuilder();
        builder.setId("test_mod:hard_block");
        builder.setHardness(5.0f);
        builder.setResistance(10.0f);
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains(".hardness(5.0)"));
        assertTrue(result.contains(".resistance(10.0)"));
    }

    @Test
    void buildBlockWithHarvestTool() {
        BlockBuilder builder = new BlockBuilder();
        builder.setId("test_mod:mineable_block");
        builder.setHarvestTool("pickaxe");
        builder.setHarvestLevel(2);
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains(".harvestTool('pickaxe', 2)"));
    }

    @Test
    void buildBlockWithTileEntity() {
        BlockBuilder builder = new BlockBuilder();
        builder.setId("test_mod:tile_block");
        builder.setHasTileEntity(true);
        
        String result = builder.buildKubeJs();
        
        assertTrue(result.contains(".blockEntity(true)"));
    }
}