package org.aurora.launcher.config.batch;

import org.aurora.launcher.config.editor.ConfigEditor;

public abstract class BatchOperation {
    
    protected String key;
    
    public BatchOperation(String key) {
        this.key = key;
    }
    
    public abstract void apply(ConfigEditor editor);
    
    public abstract String getDescription();
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
}