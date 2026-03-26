package org.aurora.launcher.config.permission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PermissionUserTest {
    
    private PermissionUser user;
    
    @BeforeEach
    void setUp() {
        user = new PermissionUser("Player1", "uuid-123");
    }
    
    @Test
    void constructor_nameUuid_setsFields() {
        assertEquals("Player1", user.getName());
        assertEquals("uuid-123", user.getUuid());
    }
    
    @Test
    void hasPermission_exactMatch_returnsTrue() {
        user.addPermission("minecraft.command.gamemode");
        
        assertTrue(user.hasPermission("minecraft.command.gamemode"));
    }
    
    @Test
    void hasPermission_wildcard_returnsTrue() {
        user.addPermission("*");
        
        assertTrue(user.hasPermission("any.permission"));
    }
    
    @Test
    void hasPermission_missing_returnsFalse() {
        user.addPermission("minecraft.command.gamemode");
        
        assertFalse(user.hasPermission("minecraft.command.tp"));
    }
    
    @Test
    void hasPermission_withWorld_returnsTrue() {
        user.addWorldPermission("world", "minecraft.command.gamemode");
        
        assertTrue(user.hasPermission("minecraft.command.gamemode", "world"));
    }
    
    @Test
    void hasPermission_wrongWorld_returnsFalse() {
        user.addWorldPermission("world", "minecraft.command.gamemode");
        
        assertFalse(user.hasPermission("minecraft.command.gamemode", "nether"));
    }
    
    @Test
    void addGroup_noDuplicate() {
        user.addGroup("admin");
        user.addGroup("admin");
        
        assertEquals(1, user.getGroups().size());
    }
    
    @Test
    void removeGroup_removesGroup() {
        user.addGroup("admin");
        user.removeGroup("admin");
        
        assertFalse(user.getGroups().contains("admin"));
    }
}