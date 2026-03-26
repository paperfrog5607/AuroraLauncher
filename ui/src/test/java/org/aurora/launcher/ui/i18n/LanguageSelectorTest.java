package org.aurora.launcher.ui.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LanguageSelectorTest {
    
    private LanguageSelector selector;
    
    @BeforeEach
    void setUp() {
        I18nManager.reset();
        selector = new LanguageSelector();
    }
    
    @AfterEach
    void tearDown() {
        I18nManager.reset();
    }
    
    @Test
    void testGetAvailableLocales() {
        List<LanguageSelector.LocaleOption> options = selector.getAvailableLocales();
        
        assertEquals(2, options.size());
        assertEquals("简体中文", options.get(0).getDisplayName());
        assertEquals("English", options.get(1).getDisplayName());
    }
    
    @Test
    void testDefaultSelectedLocale() {
        assertEquals(I18nManager.CHINESE, selector.getSelectedLocale());
    }
    
    @Test
    void testSelectLocale() {
        selector.selectLocale(I18nManager.ENGLISH);
        
        assertEquals(I18nManager.ENGLISH, selector.getSelectedLocale());
        assertEquals(I18nManager.ENGLISH, I18nManager.getInstance().getCurrentLocale());
    }
    
    @Test
    void testSelectSameLocale() {
        Locale initialLocale = selector.getSelectedLocale();
        
        selector.selectLocale(initialLocale);
        
        assertEquals(initialLocale, selector.getSelectedLocale());
    }
    
    @Test
    void testSelectLocaleByCode() {
        selector.selectLocaleByCode("en-US");
        
        assertEquals(I18nManager.ENGLISH, selector.getSelectedLocale());
    }
    
    @Test
    void testSelectionListener() {
        final boolean[] called = {false};
        final Locale[] oldLocale = new Locale[1];
        final Locale[] newLocale = new Locale[1];
        
        selector.addSelectionListener((old, newVal) -> {
            called[0] = true;
            oldLocale[0] = old;
            newLocale[0] = newVal;
        });
        
        selector.selectLocale(I18nManager.ENGLISH);
        
        assertTrue(called[0]);
        assertEquals(I18nManager.CHINESE, oldLocale[0]);
        assertEquals(I18nManager.ENGLISH, newLocale[0]);
    }
    
    @Test
    void testRemoveSelectionListener() {
        final int[] callCount = {0};
        
        LanguageSelector.LocaleSelectionListener listener = (old, newVal) -> callCount[0]++;
        
        selector.addSelectionListener(listener);
        selector.selectLocale(I18nManager.ENGLISH);
        assertEquals(1, callCount[0]);
        
        selector.removeSelectionListener(listener);
        selector.selectLocale(I18nManager.CHINESE);
        assertEquals(1, callCount[0]);
    }
    
    @Test
    void testGetSelectedIndex() {
        assertEquals(0, selector.getSelectedIndex());
        
        selector.selectLocale(I18nManager.ENGLISH);
        assertEquals(1, selector.getSelectedIndex());
    }
    
    @Test
    void testSelectByIndex() {
        selector.selectByIndex(1);
        
        assertEquals(I18nManager.ENGLISH, selector.getSelectedLocale());
        
        selector.selectByIndex(0);
        
        assertEquals(I18nManager.CHINESE, selector.getSelectedLocale());
    }
    
    @Test
    void testSelectByInvalidIndex() {
        Locale initialLocale = selector.getSelectedLocale();
        
        selector.selectByIndex(-1);
        assertEquals(initialLocale, selector.getSelectedLocale());
        
        selector.selectByIndex(100);
        assertEquals(initialLocale, selector.getSelectedLocale());
    }
    
    @Test
    void testLocaleOption() {
        LanguageSelector.LocaleOption option = new LanguageSelector.LocaleOption(
                I18nManager.CHINESE, "简体中文");
        
        assertEquals(I18nManager.CHINESE, option.getLocale());
        assertEquals("简体中文", option.getDisplayName());
        assertEquals("zh-CN", option.getLanguageTag());
        assertEquals("简体中文", option.toString());
    }
    
    @Test
    void testLocaleOptionEquals() {
        LanguageSelector.LocaleOption option1 = new LanguageSelector.LocaleOption(
                I18nManager.CHINESE, "简体中文");
        LanguageSelector.LocaleOption option2 = new LanguageSelector.LocaleOption(
                I18nManager.CHINESE, "Chinese");
        LanguageSelector.LocaleOption option3 = new LanguageSelector.LocaleOption(
                I18nManager.ENGLISH, "English");
        
        assertEquals(option1, option2);
        assertNotEquals(option1, option3);
        assertEquals(option1.hashCode(), option2.hashCode());
    }
}