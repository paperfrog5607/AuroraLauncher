package org.aurora.launcher.dev.datapack.tag;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class TagBuilder {
    private boolean replace = false;
    private List<String> values;

    public TagBuilder() {
        this.values = new ArrayList<>();
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    public List<String> getValues() {
        return values;
    }

    public TagBuilder add(String value) {
        values.add(value);
        return this;
    }

    public TagBuilder addAll(List<String> values) {
        this.values.addAll(values);
        return this;
    }

    public JsonElement build() {
        JsonObject json = new JsonObject();
        json.addProperty("replace", replace);
        
        JsonArray valuesArray = new JsonArray();
        for (String value : values) {
            valuesArray.add(value);
        }
        json.add("values", valuesArray);
        
        return json;
    }
}