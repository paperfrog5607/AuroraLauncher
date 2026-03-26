package org.aurora.launcher.ui.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class I18nManager {
    
    private static final Logger logger = LoggerFactory.getLogger(I18nManager.class);
    
    private static I18nManager instance;
    
    private Properties properties;
    private Locale currentLocale;
    private final Map<Locale, Properties> propertiesCache = new HashMap<>();
    private final List<LocaleChangeListener> listeners = new CopyOnWriteArrayList<>();
    
    public static final Locale CHINESE = Locale.SIMPLIFIED_CHINESE;
    public static final Locale ENGLISH = Locale.US;
    
    private I18nManager() {
        loadProperties(CHINESE);
    }
    
    public static synchronized I18nManager getInstance() {
        if (instance == null) {
            instance = new I18nManager();
        }
        return instance;
    }
    
    public void setLocale(Locale locale) {
        if (locale == null) {
            locale = CHINESE;
        }
        
        if (!locale.equals(currentLocale)) {
            loadProperties(locale);
            notifyListeners();
            logger.info("Locale changed to: {}", locale);
        }
    }
    
    private void loadProperties(Locale locale) {
        try {
            if (propertiesCache.containsKey(locale)) {
                properties = propertiesCache.get(locale);
            } else {
                properties = new Properties();
                String fileName = getPropertiesFileName(locale);
                InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
                
                if (is == null) {
                    fileName = getPropertiesFileName(CHINESE);
                    is = getClass().getClassLoader().getResourceAsStream(fileName);
                    locale = CHINESE;
                }
                
                if (is != null) {
                    try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        properties.load(reader);
                    }
                    propertiesCache.put(locale, properties);
                } else {
                    logger.error("No properties file found for locale: {}", locale);
                    properties = new Properties();
                }
            }
            currentLocale = locale;
        } catch (IOException e) {
            logger.error("Failed to load properties for locale: {}", locale, e);
            properties = new Properties();
            currentLocale = locale;
        }
    }
    
    private String getPropertiesFileName(Locale locale) {
        return String.format("i18n/messages_%s.properties", locale.toString());
    }
    
    public String get(String key) {
        try {
            String value = properties.getProperty(key);
            if (value != null) {
                return value;
            }
            logger.warn("Missing translation key: {}", key);
            return key;
        } catch (Exception e) {
            return key;
        }
    }
    
    public String get(String key, Object... args) {
        String template = get(key);
        if (args == null || args.length == 0) {
            return template;
        }
        try {
            return MessageFormat.format(template, args);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to format message for key: {}", key);
            return template;
        }
    }
    
    public boolean hasKey(String key) {
        return properties.containsKey(key);
    }
    
    public Locale getCurrentLocale() {
        return currentLocale;
    }
    
    public List<Locale> getSupportedLocales() {
        return Arrays.asList(CHINESE, ENGLISH);
    }
    
    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("i18n.messages", currentLocale);
    }
    
    public String getLocaleDisplayName(Locale locale) {
        if (locale.equals(CHINESE)) {
            return "简体中文";
        } else if (locale.equals(ENGLISH)) {
            return "English";
        }
        return locale.getDisplayName(locale);
    }
    
    public void addListener(LocaleChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }
    
    public void clearListeners() {
        listeners.clear();
    }
    
    private void notifyListeners() {
        for (LocaleChangeListener listener : listeners) {
            try {
                listener.onLocaleChanged(currentLocale);
            } catch (Exception e) {
                logger.warn("Error notifying locale change listener: {}", e.getMessage());
            }
        }
    }
    
    public static void reset() {
        instance = null;
    }
    
    @FunctionalInterface
    public interface LocaleChangeListener {
        void onLocaleChanged(Locale newLocale);
    }
}