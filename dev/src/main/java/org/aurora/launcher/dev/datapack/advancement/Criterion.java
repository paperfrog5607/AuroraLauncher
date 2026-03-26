package org.aurora.launcher.dev.datapack.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Criterion {
    private String name;
    private String trigger;
    private JsonObject conditions;

    public Criterion() {
    }

    public Criterion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public JsonObject getConditions() {
        return conditions;
    }

    public void setConditions(JsonObject conditions) {
        this.conditions = conditions;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("trigger", trigger);
        if (conditions != null) {
            json.add("conditions", conditions);
        }
        return json;
    }
}