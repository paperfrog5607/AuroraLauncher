package org.aurora.launcher.dev.datapack.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AdvancementBuilder {
    private String parent;
    private DisplayConfig display;
    private List<Criterion> criteria;
    private List<String> requirements;

    public AdvancementBuilder() {
        this.criteria = new ArrayList<>();
        this.requirements = new ArrayList<>();
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public DisplayConfig getDisplay() {
        return display;
    }

    public void setDisplay(DisplayConfig display) {
        this.display = display;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public AdvancementBuilder addCriterion(Criterion criterion) {
        criteria.add(criterion);
        return this;
    }

    public JsonElement build() {
        JsonObject json = new JsonObject();
        
        if (parent != null) {
            json.addProperty("parent", parent);
        }
        
        if (display != null) {
            json.add("display", display.toJson());
        }
        
        JsonObject criteriaObj = new JsonObject();
        for (Criterion criterion : criteria) {
            criteriaObj.add(criterion.getName(), criterion.toJson());
        }
        json.add("criteria", criteriaObj);
        
        return json;
    }
}