package org.aurora.launcher.core.i18n;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

class I18nTest {

    @Test
    void setLocale_changesCurrentLocale() {
        I18n.setLocale(Locale.SIMPLIFIED_CHINESE);
        assertEquals(Locale.SIMPLIFIED_CHINESE, I18n.getCurrentLocale());
    }

    @Test
    void get_returnsValue() {
        I18n.setLocale(Locale.SIMPLIFIED_CHINESE);
        String result = I18n.get("app.title");
        assertNotNull(result);
    }

    @Test
    void get_withArgs_formatsMessage() {
        I18n.setLocale(Locale.SIMPLIFIED_CHINESE);
        String result = I18n.get("app.greeting", "World");
        assertNotNull(result);
    }

    @Test
    void getSupportedLocales_returnsNonEmptyList() {
        List<Locale> locales = I18n.getSupportedLocales();
        assertNotNull(locales);
        assertFalse(locales.isEmpty());
    }
}