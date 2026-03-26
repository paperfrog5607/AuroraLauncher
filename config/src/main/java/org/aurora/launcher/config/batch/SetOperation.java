package org.aurora.launcher.config.batch;

import org.aurora.launcher.config.editor.ConfigEditor;

public class SetOperation extends BatchOperation {
    
    private Object value;
    private String comment;
    
    public SetOperation(String key, Object value) {
        super(key);
        this.value = value;
    }
    
    public SetOperation(String key, Object value, String comment) {
        super(key);
        this.value = value;
        this.comment = comment;
    }
    
    @Override
    public void apply(ConfigEditor editor) {
        if (comment != null) {
            editor.set(key, value, comment);
        } else {
            editor.set(key, value);
        }
    }
    
    @Override
    public String getDescription() {
        return "Set " + key + " = " + value;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}