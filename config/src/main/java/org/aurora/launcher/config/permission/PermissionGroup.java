package org.aurora.launcher.config.permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionGroup {
    
    private String name;
    private String prefix;
    private String suffix;
    private List<String> permissions;
    private List<String> inheritance;
    private int priority;
    
    public PermissionGroup() {
        this.permissions = new ArrayList<>();
        this.inheritance = new ArrayList<>();
    }
    
    public PermissionGroup(String name) {
        this();
        this.name = name;
    }
    
    public boolean hasPermission(String permission) {
        if (permissions.contains(permission)) {
            return true;
        }
        
        if (permissions.contains("*")) {
            return true;
        }
        
        for (String perm : permissions) {
            if (permission.startsWith(perm.replace("*", ""))) {
                return true;
            }
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    
    public List<String> getInheritance() {
        return inheritance;
    }
    
    public void setInheritance(List<String> inheritance) {
        this.inheritance = inheritance;
    }
    
    public void addInheritance(String group) {
        if (!inheritance.contains(group)) {
            inheritance.add(group);
        }
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
}