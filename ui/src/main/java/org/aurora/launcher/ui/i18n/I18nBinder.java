package org.aurora.launcher.ui.i18n;

import java.util.Locale;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class I18nBinder {
    
    private static final WeakHashMap<Object, I18nManager.LocaleChangeListener> bindings = new WeakHashMap<>();
    
    public static void bindText(TextHolder holder, String key) {
        holder.setText(I18nManager.getInstance().get(key));
        
        I18nManager.LocaleChangeListener listener = newLocale -> {
            holder.setText(I18nManager.getInstance().get(key));
        };
        
        I18nManager.getInstance().addListener(listener);
        bindings.put(holder, listener);
    }
    
    public static void bindText(TextHolder holder, String key, Object... args) {
        holder.setText(I18nManager.getInstance().get(key, args));
        
        I18nManager.LocaleChangeListener listener = newLocale -> {
            holder.setText(I18nManager.getInstance().get(key, args));
        };
        
        I18nManager.getInstance().addListener(listener);
        bindings.put(holder, listener);
    }
    
    public static void unbind(Object holder) {
        I18nManager.LocaleChangeListener listener = bindings.remove(holder);
        if (listener != null) {
            I18nManager.getInstance().removeListener(listener);
        }
    }
    
    public static void bind(Consumer<String> textSetter, String key) {
        textSetter.accept(I18nManager.getInstance().get(key));
        
        I18nManager.LocaleChangeListener listener = newLocale -> {
            textSetter.accept(I18nManager.getInstance().get(key));
        };
        
        I18nManager.getInstance().addListener(listener);
    }
    
    public static void bind(Consumer<String> textSetter, String key, Object... args) {
        textSetter.accept(I18nManager.getInstance().get(key, args));
        
        I18nManager.LocaleChangeListener listener = newLocale -> {
            textSetter.accept(I18nManager.getInstance().get(key, args));
        };
        
        I18nManager.getInstance().addListener(listener);
    }
    
    public static String format(String key, Object... args) {
        return I18nManager.getInstance().get(key, args);
    }
    
    public static String get(String key) {
        return I18nManager.getInstance().get(key);
    }
    
    public interface TextHolder {
        void setText(String text);
        String getText();
    }
    
    public static class SimpleTextHolder implements TextHolder {
        private String text;
        
        public SimpleTextHolder() {
            this.text = "";
        }
        
        public SimpleTextHolder(String initialText) {
            this.text = initialText != null ? initialText : "";
        }
        
        @Override
        public void setText(String text) {
            this.text = text != null ? text : "";
        }
        
        @Override
        public String getText() {
            return text;
        }
    }
    
    public static class DynamicTextHolder implements TextHolder {
        private String text;
        private final String key;
        private Object[] args;
        
        public DynamicTextHolder(String key) {
            this.key = key;
            this.text = I18nManager.getInstance().get(key);
        }
        
        public DynamicTextHolder(String key, Object... args) {
            this.key = key;
            this.args = args;
            this.text = I18nManager.getInstance().get(key, args);
        }
        
        public void setArgs(Object... args) {
            this.args = args;
            this.text = I18nManager.getInstance().get(key, args);
        }
        
        @Override
        public void setText(String text) {
            this.text = text != null ? text : "";
        }
        
        @Override
        public String getText() {
            return text;
        }
        
        public void refresh() {
            this.text = I18nManager.getInstance().get(key, args);
        }
    }
}