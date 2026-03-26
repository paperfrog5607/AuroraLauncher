package org.aurora.launcher.dev.datapack.tag;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    private String namespace;
    private String name;
    private String type;
    private boolean replace;
    private List<String> values;

    public Tag() {
        this.values = new ArrayList<>();
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void addValue(String value) {
        values.add(value);
    }

    public String getFullName() {
        return namespace + ":" + name;
    }
}