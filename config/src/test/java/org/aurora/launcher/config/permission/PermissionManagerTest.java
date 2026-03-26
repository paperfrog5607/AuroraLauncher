package org.aurora.launcher.config.permission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class PermissionManagerTest {
    
    @TempDir
    Path tempDir;
    
    private PermissionManager manager;
    
    @BeforeEach
    void setUp() {
        manager = new PermissionManager();
    }
    
    @Test
    void createGroup_and_getGroup() {
        PermissionGroup group = manager.createGroup("admin");
        
        assertNotNull(group);
        assertEquals("admin", manager.getGroup("admin").getName());
    }
    
    @Test
    void deleteGroup_removesGroup() {
        manager.createGroup("admin");
        manager.deleteGroup("admin");
        
        assertNull(manager.getGroup("admin"));
    }
    
    @Test
    void getGroups_returnsAllGroups() {
        manager.createGroup("admin");
        manager.createGroup("default");
        
        assertEquals(2, manager.getGroups().size());
    }
    
    @Test
    void createUser_and_getUser() {
        PermissionUser user = manager.createUser("Player1", "uuid-123");
        
        assertNotNull(user);
        assertEquals("Player1", manager.getUser("Player1").getName());
    }
    
    @Test
    void getUserByUuid_returnsCorrectUser() {
        manager.createUser("Player1", "uuid-123");
        
        assertEquals("Player1", manager.getUserByUuid("uuid-123").getName());
    }
    
    @Test
    void deleteUser_removesUser() {
        manager.createUser("Player1", "uuid-123");
        manager.deleteUser("Player1");
        
        assertNull(manager.getUser("Player1"));
    }
    
    @Test
    void addUserToGroup_addsGroupToUser() {
        manager.createGroup("admin");
        PermissionUser user = manager.createUser("Player1", "uuid-123");
        
        manager.addUserToGroup("Player1", "admin");
        
        assertTrue(user.getGroups().contains("admin"));
    }
    
    @Test
    void removeUserFromGroup_removesGroupFromUser() {
        manager.createGroup("admin");
        manager.createUser("Player1", "uuid-123");
        manager.addUserToGroup("Player1", "admin");
        
        manager.removeUserFromGroup("Player1", "admin");
        
        assertFalse(manager.getUser("Player1").getGroups().contains("admin"));
    }
    
    @Test
    void hasPermission_userPermission_returnsTrue() {
        PermissionUser user = manager.createUser("Player1", "uuid-123");
        user.addPermission("minecraft.command.gamemode");
        
        assertTrue(manager.hasPermission("Player1", "minecraft.command.gamemode"));
    }
    
    @Test
    void hasPermission_groupPermission_returnsTrue() {
        PermissionGroup group = manager.createGroup("admin");
        group.addPermission("minecraft.command.gamemode");
        manager.createUser("Player1", "uuid-123");
        manager.addUserToGroup("Player1", "admin");
        
        assertTrue(manager.hasPermission("Player1", "minecraft.command.gamemode"));
    }
    
    @Test
    void hasPermission_withWorld_returnsTrue() {
        PermissionUser user = manager.createUser("Player1", "uuid-123");
        user.addWorldPermission("world", "minecraft.command.gamemode");
        
        assertTrue(manager.hasPermission("Player1", "minecraft.command.gamemode", "world"));
    }
    
    @Test
    void saveAndLoad_persistsData() throws Exception {
        PermissionGroup group = manager.createGroup("admin");
        group.addPermission("*");
        group.setPrefix("[Admin]");
        
        PermissionUser user = manager.createUser("Player1", "uuid-123");
        manager.addUserToGroup("Player1", "admin");
        
        manager.saveToDirectory(tempDir);
        
        PermissionManager newManager = new PermissionManager();
        newManager.loadFromDirectory(tempDir);
        
        assertNotNull(newManager.getGroup("admin"));
        assertNotNull(newManager.getUser("Player1"));
        assertEquals("[Admin]", newManager.getGroup("admin").getPrefix());
        assertTrue(newManager.hasPermission("Player1", "any.permission"));
    }
}