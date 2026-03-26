package org.aurora.launcher.config.permission;

import java.util.*;

public class PermissionUser {
    
    private String name;
    private String uuid;
    private List<String> groups;
    private List<String> permissions;
    private Map<String, List<String>> worldPermissions;
    
    public PermissionUser() {
        this.groups = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.worldPermissions = new HashMap<>();
    }
    
    public PermissionUser(String name, String uuid) {
        this();
        this.name = name;
        this.uuid = uuid;
    }
    
    public boolean hasPermission(String permission) {
        return permissions.contains(permission) || permissions.contains("*");
    }
    
    public boolean hasPermission(String permission, String world) {
        if (hasPermission(permission)) {
            return true;
        }
        
        List<String> worldPerms = worldPermissions.get(world);
        if (worldPerms != null) {
            return worldPerms.contains(permission) || worldPerms.contains("*");
        }
        
        return false;
    }
    
    public void addPermission(String permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }
    
    public void removePermission(String permission) {
        permissions.remove(permission);
    }
    
    public void addWorldPermission(String world, String permission) {
        worldPermissions.computeIfAbsent(world, k -> new ArrayList<>()).add(permission);
    }
    
    public void removeWorldPermission(String world, String permission) {
        List<String> perms = worldPermissions.get(world);
        if (perms != null) {
            perms.remove(permission);
        }
    }
    
    public void addGroup(String group) {
        if (!groups.contains(group)) {
            groups.add(group);
        }
    }
    
    public void removeGroup(String group) {
        groups.remove(group);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUuid() {
        return uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public List<String> getGroups() {
        return groups;
    }
    
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    
    public Map<String, List<String>> getWorldPermissions() {
        return worldPermissions;
    }
    
    public void setWorldPermissions(Map<String, List<String>> worldPermissions) {
        this.worldPermissions = worldPermissions;
    }
}