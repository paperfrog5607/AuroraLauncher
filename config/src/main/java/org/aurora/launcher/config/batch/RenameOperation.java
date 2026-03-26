package org.aurora.launcher.config.batch;

import org.aurora.launcher.config.editor.ConfigEditor;

public class RenameOperation extends BatchOperation {
    
    private String newKey;
    
    public RenameOperation(String oldKey, String newKey) {
        super(oldKey);
        this.newKey = newKey;
    }
    
    @Override
    public void apply(ConfigEditor editor) {
        Object value = editor.get(key);
        if (value != null) {
            editor.remove(key);
            editor.set(newKey, value);
        }
    }
    
    @Override
    public String getDescription() {
        return "Rename " + key + " to " + newKey;
    }
    
    public String getNewKey() {
        return newKey;
    }
    
    public void setNewKey(String newKey) {
        this.newKey = newKey;
    }
}