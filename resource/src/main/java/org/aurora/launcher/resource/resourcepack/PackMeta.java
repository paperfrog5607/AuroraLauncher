package org.aurora.launcher.resource.resourcepack;

import java.util.Map;

public class PackMeta {
    
    private int packFormat;
    private String description;
    private Map<String, Object> raw;
    
    public PackMeta() {
    }
    
    public PackMeta(int packFormat, String description) {
        this.packFormat = packFormat;
        this.description = description;
    }
    
    public static PackMeta parse(byte[] mcmetaData) {
        PackMeta meta = new PackMeta();
        if (mcmetaData == null || mcmetaData.length == 0) {
            return meta;
        }
        
        try {
            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(new String(mcmetaData)).getAsJsonObject();
            if (json.has("pack")) {
                com.google.gson.JsonObject pack = json.getAsJsonObject("pack");
                if (pack.has("pack_format")) {
                    meta.packFormat = pack.get("pack_format").getAsInt();
                }
                if (pack.has("description")) {
                    meta.description = pack.get("description").getAsString();
                }
            }
        } catch (Exception e) {
            // Ignore parse errors
        }
        
        return meta;
    }
    
    public int getPackFormat() {
        return packFormat;
    }
    
    public void setPackFormat(int packFormat) {
        this.packFormat = packFormat;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, Object> getRaw() {
        return raw;
    }
    
    public void setRaw(Map<String, Object> raw) {
        this.raw = raw;
    }
}