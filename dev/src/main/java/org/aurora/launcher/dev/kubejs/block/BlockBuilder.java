package org.aurora.launcher.dev.kubejs.block;

public class BlockBuilder {
    private String id;
    private String displayName;
    private String material = "stone";
    private float hardness = 1.0f;
    private float resistance = 1.0f;
    private String harvestTool;
    private int harvestLevel;
    private boolean hasTileEntity;

    public BlockBuilder() {
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

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public float getHardness() {
        return hardness;
    }

    public void setHardness(float hardness) {
        this.hardness = hardness;
    }

    public float getResistance() {
        return resistance;
    }

    public void setResistance(float resistance) {
        this.resistance = resistance;
    }

    public String getHarvestTool() {
        return harvestTool;
    }

    public void setHarvestTool(String harvestTool) {
        this.harvestTool = harvestTool;
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }

    public void setHarvestLevel(int harvestLevel) {
        this.harvestLevel = harvestLevel;
    }

    public boolean isHasTileEntity() {
        return hasTileEntity;
    }

    public void setHasTileEntity(boolean hasTileEntity) {
        this.hasTileEntity = hasTileEntity;
    }

    public String buildKubeJs() {
        StringBuilder sb = new StringBuilder();
        sb.append("StartupEvents.registry('block', event => {\n");
        sb.append("  event.create('").append(id).append("')");
        
        if (displayName != null) {
            sb.append("\n    .displayName('").append(displayName).append("')");
        }
        if (!"stone".equals(material)) {
            sb.append("\n    .material('").append(material).append("')");
        }
        if (hardness != 1.0f) {
            sb.append("\n    .hardness(").append(hardness).append(")");
        }
        if (resistance != 1.0f) {
            sb.append("\n    .resistance(").append(resistance).append(")");
        }
        if (harvestTool != null) {
            sb.append("\n    .harvestTool('").append(harvestTool).append("', ").append(harvestLevel).append(")");
        }
        if (hasTileEntity) {
            sb.append("\n    .blockEntity(true)");
        }
        
        sb.append("\n})");
        return sb.toString();
    }
}