package org.aurora.launcher.core.i18n;

import java.text.MessageFormat;
import java.util.*;

public final class I18n {
    private static Locale currentLocale = Locale.SIMPLIFIED_CHINESE;
    private static ResourceBundle bundle;
    private static final String BUNDLE_NAME = "i18n.messages";
    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
            Locale.SIMPLIFIED_CHINESE,
            Locale.US
    );

    private I18n() {
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        } catch (MissingResourceException e) {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.SIMPLIFIED_CHINESE);
        }
    }

    public static String get(String key) {
        try {
            if (bundle == null) {
                setLocale(currentLocale);
            }
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static String get(String key, Object... args) {
        String pattern = get(key);
        if (args == null || args.length == 0) {
            return pattern;
        }
        return MessageFormat.format(pattern, args);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(SUPPORTED_LOCALES);
    }
}