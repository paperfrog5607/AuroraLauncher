package org.aurora.launcher.dev.datapack.loot_table;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Condition {
    private String condition;
    private String target;
    private String mode;

    public Condition() {
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("condition", condition);
        if (target != null) {
            json.addProperty("target", target);
        }
        if (mode != null) {
            json.addProperty("mode", mode);
        }
        return json;
    }
}