package org.aurora.launcher.ui.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageSelector {
    
    private final List<LocaleSelectionListener> selectionListeners = new ArrayList<>();
    private Locale selectedLocale;
    
    public LanguageSelector() {
        this.selectedLocale = I18nManager.getInstance().getCurrentLocale();
    }
    
    public List<LocaleOption> getAvailableLocales() {
        List<LocaleOption> options = new ArrayList<>();
        
        for (Locale locale : I18nManager.getInstance().getSupportedLocales()) {
            options.add(new LocaleOption(locale, I18nManager.getInstance().getLocaleDisplayName(locale)));
        }
        
        return options;
    }
    
    public Locale getSelectedLocale() {
        return selectedLocale;
    }
    
    public void selectLocale(Locale locale) {
        if (locale != null && !locale.equals(selectedLocale)) {
            Locale oldLocale = selectedLocale;
            selectedLocale = locale;
            I18nManager.getInstance().setLocale(locale);
            notifySelectionListeners(oldLocale, locale);
        }
    }
    
    public void selectLocaleByCode(String languageTag) {
        Locale locale = Locale.forLanguageTag(languageTag);
        selectLocale(locale);
    }
    
    public void addSelectionListener(LocaleSelectionListener listener) {
        if (listener != null && !selectionListeners.contains(listener)) {
            selectionListeners.add(listener);
        }
    }
    
    public void removeSelectionListener(LocaleSelectionListener listener) {
        selectionListeners.remove(listener);
    }
    
    private void notifySelectionListeners(Locale oldLocale, Locale newLocale) {
        for (LocaleSelectionListener listener : selectionListeners) {
            try {
                listener.onLocaleSelected(oldLocale, newLocale);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    public int getSelectedIndex() {
        List<Locale> supported = I18nManager.getInstance().getSupportedLocales();
        for (int i = 0; i < supported.size(); i++) {
            if (supported.get(i).equals(selectedLocale)) {
                return i;
            }
        }
        return 0;
    }
    
    public void selectByIndex(int index) {
        List<Locale> supported = I18nManager.getInstance().getSupportedLocales();
        if (index >= 0 && index < supported.size()) {
            selectLocale(supported.get(index));
        }
    }
    
    @FunctionalInterface
    public interface LocaleSelectionListener {
        void onLocaleSelected(Locale oldLocale, Locale newLocale);
    }
    
    public static class LocaleOption {
        private final Locale locale;
        private final String displayName;
        
        public LocaleOption(Locale locale, String displayName) {
            this.locale = locale;
            this.displayName = displayName;
        }
        
        public Locale getLocale() {
            return locale;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getLanguageTag() {
            return locale.toLanguageTag();
        }
        
        @Override
        public String toString() {
            return displayName;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            LocaleOption that = (LocaleOption) obj;
            return locale.equals(that.locale);
        }
        
        @Override
        public int hashCode() {
            return locale.hashCode();
        }
    }
}