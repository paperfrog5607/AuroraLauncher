package org.aurora.launcher.config.editor;

import java.util.ArrayList;
import java.util.List;

public class ConfigNode {
    
    private String key;
    private Object value;
    private String type;
    private String comment;
    private List<ConfigNode> children;
    private boolean expanded;
    
    public ConfigNode() {
        this.children = new ArrayList<>();
    }
    
    public ConfigNode(String key, Object value) {
        this.key = key;
        this.value = value;
        this.children = new ArrayList<>();
        this.type = value != null ? value.getClass().getSimpleName() : "null";
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
        this.type = value != null ? value.getClass().getSimpleName() : "null";
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public List<ConfigNode> getChildren() {
        return children;
    }
    
    public void setChildren(List<ConfigNode> children) {
        this.children = children;
    }
    
    public void addChild(ConfigNode child) {
        this.children.add(child);
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    public boolean isSection() {
        return children != null && !children.isEmpty();
    }
    
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }
}