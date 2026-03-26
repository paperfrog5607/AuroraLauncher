package org.aurora.launcher.config.permission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PermissionGroupTest {
    
    private PermissionGroup group;
    
    @BeforeEach
    void setUp() {
        group = new PermissionGroup("admin");
    }
    
    @Test
    void constructor_name_setsName() {
        assertEquals("admin", group.getName());
    }
    
    @Test
    void hasPermission_exactMatch_returnsTrue() {
        group.addPermission("minecraft.command.gamemode");
        
        assertTrue(group.hasPermission("minecraft.command.gamemode"));
    }
    
    @Test
    void hasPermission_wildcard_returnsTrue() {
        group.addPermission("*");
        
        assertTrue(group.hasPermission("any.permission"));
    }
    
    @Test
    void hasPermission_prefixWildcard_returnsTrue() {
        group.addPermission("minecraft.*");
        
        assertTrue(group.hasPermission("minecraft.command.gamemode"));
        assertTrue(group.hasPermission("minecraft.command.tp"));
    }
    
    @Test
    void hasPermission_missing_returnsFalse() {
        group.addPermission("minecraft.command.gamemode");
        
        assertFalse(group.hasPermission("minecraft.command.tp"));
    }
    
    @Test
    void addPermission_noDuplicate() {
        group.addPermission("test.perm");
        group.addPermission("test.perm");
        
        assertEquals(1, group.getPermissions().size());
    }
    
    @Test
    void removePermission_removesPermission() {
        group.addPermission("test.perm");
        group.removePermission("test.perm");
        
        assertFalse(group.hasPermission("test.perm"));
    }
    
    @Test
    void setters_updateFields() {
        group.setPrefix("[Admin]");
        group.setSuffix("");
        group.setPriority(100);
        group.addInheritance("default");
        
        assertEquals("[Admin]", group.getPrefix());
        assertEquals("", group.getSuffix());
        assertEquals(100, group.getPriority());
        assertTrue(group.getInheritance().contains("default"));
    }
}