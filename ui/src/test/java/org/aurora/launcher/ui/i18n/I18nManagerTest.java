package org.aurora.launcher.ui.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class I18nManagerTest {
    
    @BeforeEach
    void setUp() {
        I18nManager.reset();
    }
    
    @AfterEach
    void tearDown() {
        I18nManager.reset();
    }
    
    @Test
    void testGetInstance() {
        I18nManager instance1 = I18nManager.getInstance();
        I18nManager instance2 = I18nManager.getInstance();
        
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }
    
    @Test
    void testDefaultLocale() {
        I18nManager manager = I18nManager.getInstance();
        
        assertEquals(I18nManager.CHINESE, manager.getCurrentLocale());
    }
    
    @Test
    void testSetLocale() {
        I18nManager manager = I18nManager.getInstance();
        
        manager.setLocale(I18nManager.ENGLISH);
        assertEquals(I18nManager.ENGLISH, manager.getCurrentLocale());
        
        manager.setLocale(I18nManager.CHINESE);
        assertEquals(I18nManager.CHINESE, manager.getCurrentLocale());
    }
    
    @Test
    void testGetChineseText() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.CHINESE);
        
        assertEquals("启动", manager.get("tab.launch"));
        assertEquals("下载", manager.get("tab.download"));
        assertEquals("更多", manager.get("tab.settings"));
    }
    
    @Test
    void testGetEnglishText() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.ENGLISH);
        
        assertEquals("Launch", manager.get("tab.launch"));
        assertEquals("Download", manager.get("tab.download"));
        assertEquals("More", manager.get("tab.settings"));
    }
    
    @Test
    void testGetMissingKey() {
        I18nManager manager = I18nManager.getInstance();
        
        String result = manager.get("nonexistent.key");
        assertEquals("nonexistent.key", result);
    }
    
    @Test
    void testGetWithArgs() {
        I18nManager manager = I18nManager.getInstance();
        
        manager.setLocale(I18nManager.CHINESE);
        String result = manager.get("home.welcome", "玩家");
        assertEquals("欢迎回来，玩家！", result);
        
        manager.setLocale(I18nManager.ENGLISH);
        result = manager.get("home.welcome", "Player");
        assertEquals("Welcome back, Player!", result);
    }
    
    @Test
    void testHasKey() {
        I18nManager manager = I18nManager.getInstance();
        
        assertTrue(manager.hasKey("tab.launch"));
        assertTrue(manager.hasKey("action.ok"));
        assertFalse(manager.hasKey("nonexistent.key"));
    }
    
    @Test
    void testGetSupportedLocales() {
        I18nManager manager = I18nManager.getInstance();
        
        assertEquals(2, manager.getSupportedLocales().size());
        assertTrue(manager.getSupportedLocales().contains(I18nManager.CHINESE));
        assertTrue(manager.getSupportedLocales().contains(I18nManager.ENGLISH));
    }
    
    @Test
    void testGetLocaleDisplayName() {
        I18nManager manager = I18nManager.getInstance();
        
        assertEquals("简体中文", manager.getLocaleDisplayName(I18nManager.CHINESE));
        assertEquals("English", manager.getLocaleDisplayName(I18nManager.ENGLISH));
    }
    
    @Test
    void testLocaleChangeListener() {
        I18nManager manager = I18nManager.getInstance();
        
        final boolean[] called = {false};
        I18nManager.LocaleChangeListener listener = newLocale -> called[0] = true;
        
        manager.addListener(listener);
        manager.setLocale(I18nManager.ENGLISH);
        
        assertTrue(called[0]);
    }
    
    @Test
    void testRemoveListener() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.CHINESE);
        
        final int[] callCount = {0};
        I18nManager.LocaleChangeListener listener = newLocale -> callCount[0]++;
        
        manager.addListener(listener);
        manager.setLocale(I18nManager.ENGLISH);
        assertEquals(1, callCount[0]);
        
        manager.removeListener(listener);
        manager.setLocale(I18nManager.CHINESE);
        assertEquals(1, callCount[0]);
    }
    
    @Test
    void testSetNullLocale() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.ENGLISH);
        
        manager.setLocale(null);
        
        assertEquals(I18nManager.CHINESE, manager.getCurrentLocale());
    }
}