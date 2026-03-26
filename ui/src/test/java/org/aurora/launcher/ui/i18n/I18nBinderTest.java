package org.aurora.launcher.ui.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class I18nBinderTest {
    
    @BeforeEach
    void setUp() {
        I18nManager.reset();
    }
    
    @AfterEach
    void tearDown() {
        I18nManager.reset();
    }
    
    @Test
    void testBindText() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.CHINESE);
        
        I18nBinder.SimpleTextHolder holder = new I18nBinder.SimpleTextHolder();
        I18nBinder.bindText(holder, "tab.launch");
        
        assertEquals("启动", holder.getText());
    }
    
    @Test
    void testBindTextWithArgs() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.CHINESE);
        
        I18nBinder.SimpleTextHolder holder = new I18nBinder.SimpleTextHolder();
        I18nBinder.bindText(holder, "home.welcome", "测试玩家");
        
        assertEquals("欢迎回来，测试玩家！", holder.getText());
    }
    
    @Test
    void testBindTextLocaleChange() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.CHINESE);
        
        I18nBinder.SimpleTextHolder holder = new I18nBinder.SimpleTextHolder();
        I18nBinder.bindText(holder, "tab.launch");
        
        assertEquals("启动", holder.getText());
        
        manager.setLocale(I18nManager.ENGLISH);
        
        assertEquals("Launch", holder.getText());
    }
    
    @Test
    void testUnbind() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.CHINESE);
        
        I18nBinder.SimpleTextHolder holder = new I18nBinder.SimpleTextHolder();
        I18nBinder.bindText(holder, "tab.launch");
        
        assertEquals("启动", holder.getText());
        
        I18nBinder.unbind(holder);
        
        manager.setLocale(I18nManager.ENGLISH);
        
        assertEquals("启动", holder.getText());
    }
    
    @Test
    void testFormat() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.CHINESE);
        
        String result = I18nBinder.format("home.welcome", "玩家");
        assertEquals("欢迎回来，玩家！", result);
    }
    
    @Test
    void testGet() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.ENGLISH);
        
        String result = I18nBinder.get("tab.launch");
        assertEquals("Launch", result);
    }
    
    @Test
    void testSimpleTextHolder() {
        I18nBinder.SimpleTextHolder holder = new I18nBinder.SimpleTextHolder("Initial");
        
        assertEquals("Initial", holder.getText());
        
        holder.setText("Updated");
        assertEquals("Updated", holder.getText());
        
        holder.setText(null);
        assertEquals("", holder.getText());
    }
    
    @Test
    void testDynamicTextHolder() {
        I18nManager manager = I18nManager.getInstance();
        manager.setLocale(I18nManager.CHINESE);
        
        I18nBinder.DynamicTextHolder holder = new I18nBinder.DynamicTextHolder("home.welcome", "玩家1");
        
        assertEquals("欢迎回来，玩家1！", holder.getText());
        
        holder.setArgs("玩家2");
        assertEquals("欢迎回来，玩家2！", holder.getText());
        
        manager.setLocale(I18nManager.ENGLISH);
        holder.refresh();
        assertEquals("Welcome back, 玩家2!", holder.getText());
    }
}