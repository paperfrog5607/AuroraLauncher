package org.aurora.launcher.config.batch;

import org.aurora.launcher.config.editor.ConfigEditor;

public class RemoveOperation extends BatchOperation {
    
    public RemoveOperation(String key) {
        super(key);
    }
    
    @Override
    public void apply(ConfigEditor editor) {
        editor.remove(key);
    }
    
    @Override
    public String getDescription() {
        return "Remove " + key;
    }
}