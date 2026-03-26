package org.aurora.launcher.resource.language;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LanguageEditor {
    
    private LanguageFile file;
    private List<LanguageChangeListener> listeners;
    private Stack<Map<String, String>> undoStack;
    private Stack<Map<String, String>> redoStack;
    
    public interface LanguageChangeListener {
        void onLanguageChanged(String key, String oldValue, String newValue);
    }
    
    public LanguageEditor(LanguageFile file) {
        this.file = file;
        this.listeners = new ArrayList<>();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }
    
    public void setEntry(String key, String value) {
        saveUndoState();
        
        String oldValue = file.get(key);
        file.set(key, value);
        
        notifyListeners(key, oldValue, value);
    }
    
    public void removeEntry(String key) {
        saveUndoState();
        
        String oldValue = file.get(key);
        file.remove(key);
        
        notifyListeners(key, oldValue, null);
    }
    
    public void addEntries(Map<String, String> entries) {
        saveUndoState();
        
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String oldValue = file.get(entry.getKey());
            file.set(entry.getKey(), entry.getValue());
            notifyListeners(entry.getKey(), oldValue, entry.getValue());
        }
    }
    
    public CompletableFuture<Void> save() {
        LanguageFileManager manager = new LanguageFileManager();
        return manager.save(file);
    }
    
    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(copyEntries(file.getEntries()));
            file.setEntries(undoStack.pop());
        }
    }
    
    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(copyEntries(file.getEntries()));
            file.setEntries(redoStack.pop());
        }
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    public List<String> search(String query) {
        String lowerQuery = query.toLowerCase();
        
        return file.getKeys().stream()
                .filter(key -> key.toLowerCase().contains(lowerQuery) ||
                              (file.get(key) != null && file.get(key).toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
    }
    
    public List<String> getMissingKeys(LanguageFile reference) {
        return reference.getKeys().stream()
                .filter(key -> !file.has(key))
                .collect(Collectors.toList());
    }
    
    public void addListener(LanguageChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(LanguageChangeListener listener) {
        listeners.remove(listener);
    }
    
    public LanguageFile getFile() {
        return file;
    }
    
    private void saveUndoState() {
        undoStack.push(copyEntries(file.getEntries()));
        redoStack.clear();
    }
    
    private Map<String, String> copyEntries(Map<String, String> entries) {
        return new LinkedHashMap<>(entries);
    }
    
    private void notifyListeners(String key, String oldValue, String newValue) {
        for (LanguageChangeListener listener : listeners) {
            listener.onLanguageChanged(key, oldValue, newValue);
        }
    }
}