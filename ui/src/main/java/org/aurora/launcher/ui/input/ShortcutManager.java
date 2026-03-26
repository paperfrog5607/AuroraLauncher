package org.aurora.launcher.ui.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ShortcutManager {
    private static final Logger logger = LoggerFactory.getLogger(ShortcutManager.class);
    
    private static ShortcutManager instance;
    
    private boolean enabled = true;
    private boolean gamepadEnabled = true;
    
    private Map<String, Keybind> keybinds = new HashMap<>();
    
    public static ShortcutManager getInstance() {
        if (instance == null) {
            instance = new ShortcutManager();
            instance.initializeDefaults();
        }
        return instance;
    }
    
    private ShortcutManager() {}
    
    private void initializeDefaults() {
        keybinds.put("fullscreen", new Keybind("F11", KeyType.SINGLE));
        keybinds.put("refresh", new Keybind("R", KeyType.SINGLE));
        keybinds.put("search", new Keybind("/", KeyType.SINGLE));
        keybinds.put("help", new Keybind("F1", KeyType.SINGLE));
        keybinds.put("close", new Keybind("ESC", KeyType.DOUBLE));
        keybinds.put("navigate", new Keybind("ARROWS", KeyType.FIXED));
        keybinds.put("navigateHJKL", new Keybind("HJKL", KeyType.FIXED));
        keybinds.put("select", new Keybind("Tab", KeyType.SINGLE));
        keybinds.put("confirm", new Keybind("Enter", KeyType.FIXED));
        keybinds.put("next", new Keybind("L", KeyType.SINGLE));
        keybinds.put("previous", new Keybind("J", KeyType.SINGLE));
        keybinds.put("jumpFirst", new Keybind("G", KeyType.SINGLE));
        keybinds.put("jumpLast", new Keybind("Shift+G", KeyType.COMBO));
        keybinds.put("navGroup1", new Keybind("1", KeyType.SINGLE));
        keybinds.put("navGroup2", new Keybind("2", KeyType.SINGLE));
        keybinds.put("navGroup3", new Keybind("3", KeyType.SINGLE));
        keybinds.put("navGroup4", new Keybind("4", KeyType.SINGLE));
        
        logger.info("ShortcutManager initialized with defaults");
    }
    
    public Map<String, Keybind> getAllKeybinds() {
        return new HashMap<>(keybinds);
    }
    
    public Keybind getKeybind(String action) {
        return keybinds.get(action);
    }
    
    public void setKeybind(String action, Keybind keybind) {
        keybinds.put(action, keybind);
        logger.info("Keybind updated: {} = {}", action, keybind);
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isGamepadEnabled() {
        return gamepadEnabled;
    }
    
    public void setGamepadEnabled(boolean gamepadEnabled) {
        this.gamepadEnabled = gamepadEnabled;
    }
    
    public static class Keybind {
        private String primary;
        private KeyType type;
        
        public Keybind(String primary, KeyType type) {
            this.primary = primary;
            this.type = type;
        }
        
        public String getPrimary() {
            return primary;
        }
        
        public void setPrimary(String primary) {
            this.primary = primary;
        }
        
        public KeyType getType() {
            return type;
        }
        
        public String getDisplayString() {
            return primary;
        }
    }
    
    public enum KeyType {
        SINGLE,
        DOUBLE,
        COMBO,
        FIXED
    }
}