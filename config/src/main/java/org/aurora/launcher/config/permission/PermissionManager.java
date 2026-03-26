package org.aurora.launcher.config.permission;

import org.aurora.launcher.config.parser.ConfigParseException;
import org.aurora.launcher.config.parser.JsonParser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PermissionManager {
    
    private Map<String, PermissionGroup> groups;
    private Map<String, PermissionUser> users;
    private Path directory;
    
    public PermissionManager() {
        this.groups = new LinkedHashMap<>();
        this.users = new LinkedHashMap<>();
    }
    
    public void loadFromDirectory(Path dir) throws IOException {
        this.directory = dir;
        groups.clear();
        users.clear();
        
        if (!Files.exists(dir)) {
            return;
        }
        
        Path groupsFile = dir.resolve("groups.json");
        if (Files.exists(groupsFile)) {
            loadGroups(groupsFile);
        }
        
        Path usersFile = dir.resolve("users.json");
        if (Files.exists(usersFile)) {
            loadUsers(usersFile);
        }
    }
    
    public void saveToDirectory(Path dir) throws IOException {
        this.directory = dir;
        Files.createDirectories(dir);
        
        saveGroups(dir.resolve("groups.json"));
        saveUsers(dir.resolve("users.json"));
    }
    
    public PermissionGroup createGroup(String name) {
        PermissionGroup group = new PermissionGroup(name);
        groups.put(name, group);
        return group;
    }
    
    public void deleteGroup(String name) {
        groups.remove(name);
    }
    
    public PermissionGroup getGroup(String name) {
        return groups.get(name);
    }
    
    public List<PermissionGroup> getGroups() {
        return new ArrayList<>(groups.values());
    }
    
    public PermissionUser createUser(String name, String uuid) {
        PermissionUser user = new PermissionUser(name, uuid);
        users.put(name, user);
        return user;
    }
    
    public void deleteUser(String name) {
        users.remove(name);
    }
    
    public PermissionUser getUser(String name) {
        return users.get(name);
    }
    
    public PermissionUser getUserByUuid(String uuid) {
        for (PermissionUser user : users.values()) {
            if (uuid.equals(user.getUuid())) {
                return user;
            }
        }
        return null;
    }
    
    public List<PermissionUser> getUsers() {
        return new ArrayList<>(users.values());
    }
    
    public void addUserToGroup(String userName, String groupName) {
        PermissionUser user = users.get(userName);
        if (user != null && groups.containsKey(groupName)) {
            user.addGroup(groupName);
        }
    }
    
    public void removeUserFromGroup(String userName, String groupName) {
        PermissionUser user = users.get(userName);
        if (user != null) {
            user.removeGroup(groupName);
        }
    }
    
    public boolean hasPermission(String userName, String permission) {
        PermissionUser user = users.get(userName);
        if (user == null) {
            return false;
        }
        
        if (user.hasPermission(permission)) {
            return true;
        }
        
        for (String groupName : user.getGroups()) {
            PermissionGroup group = groups.get(groupName);
            if (group != null && group.hasPermission(permission)) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean hasPermission(String userName, String permission, String world) {
        PermissionUser user = users.get(userName);
        if (user == null) {
            return false;
        }
        
        if (user.hasPermission(permission, world)) {
            return true;
        }
        
        for (String groupName : user.getGroups()) {
            PermissionGroup group = groups.get(groupName);
            if (group != null && group.hasPermission(permission)) {
                return true;
            }
        }
        
        return false;
    }
    
    private void loadGroups(Path file) throws IOException {
        try {
            JsonParser parser = new JsonParser();
            try (InputStream input = Files.newInputStream(file)) {
                Map<String, Object> data = parser.parse(input);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> groupsData = (Map<String, Object>) data.get("groups");
                if (groupsData != null) {
                    for (Map.Entry<String, Object> entry : groupsData.entrySet()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> groupData = (Map<String, Object>) entry.getValue();
                        
                        PermissionGroup group = new PermissionGroup(entry.getKey());
                        group.setPrefix((String) groupData.get("prefix"));
                        group.setSuffix((String) groupData.get("suffix"));
                        group.setPriority(((Number) groupData.getOrDefault("priority", 0)).intValue());
                        
                        @SuppressWarnings("unchecked")
                        List<String> perms = (List<String>) groupData.get("permissions");
                        if (perms != null) {
                            group.setPermissions(new ArrayList<>(perms));
                        }
                        
                        @SuppressWarnings("unchecked")
                        List<String> inheritance = (List<String>) groupData.get("inheritance");
                        if (inheritance != null) {
                            group.setInheritance(new ArrayList<>(inheritance));
                        }
                        
                        groups.put(group.getName(), group);
                    }
                }
            }
        } catch (ConfigParseException e) {
            throw new IOException("Failed to load groups", e);
        }
    }
    
    private void loadUsers(Path file) throws IOException {
        try {
            JsonParser parser = new JsonParser();
            try (InputStream input = Files.newInputStream(file)) {
                Map<String, Object> data = parser.parse(input);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> usersData = (Map<String, Object>) data.get("users");
                if (usersData != null) {
                    for (Map.Entry<String, Object> entry : usersData.entrySet()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> userData = (Map<String, Object>) entry.getValue();
                        
                        String uuid = (String) userData.get("uuid");
                        PermissionUser user = new PermissionUser(entry.getKey(), uuid);
                        
                        @SuppressWarnings("unchecked")
                        List<String> groups = (List<String>) userData.get("groups");
                        if (groups != null) {
                            user.setGroups(new ArrayList<>(groups));
                        }
                        
                        @SuppressWarnings("unchecked")
                        List<String> perms = (List<String>) userData.get("permissions");
                        if (perms != null) {
                            user.setPermissions(new ArrayList<>(perms));
                        }
                        
                        users.put(user.getName(), user);
                    }
                }
            }
        } catch (ConfigParseException e) {
            throw new IOException("Failed to load users", e);
        }
    }
    
    private void saveGroups(Path file) throws IOException {
        Map<String, Object> groupsData = new LinkedHashMap<>();
        for (PermissionGroup group : groups.values()) {
            Map<String, Object> groupMap = new LinkedHashMap<>();
            groupMap.put("prefix", group.getPrefix());
            groupMap.put("suffix", group.getSuffix());
            groupMap.put("priority", group.getPriority());
            groupMap.put("permissions", group.getPermissions());
            groupMap.put("inheritance", group.getInheritance());
            groupsData.put(group.getName(), groupMap);
        }
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("groups", groupsData);
        
        JsonParser parser = new JsonParser();
        try (OutputStream output = Files.newOutputStream(file)) {
            parser.write(output, data);
        } catch (ConfigParseException e) {
            throw new IOException("Failed to save groups", e);
        }
    }
    
    private void saveUsers(Path file) throws IOException {
        Map<String, Object> usersData = new LinkedHashMap<>();
        for (PermissionUser user : users.values()) {
            Map<String, Object> userMap = new LinkedHashMap<>();
            userMap.put("uuid", user.getUuid());
            userMap.put("groups", user.getGroups());
            userMap.put("permissions", user.getPermissions());
            userMap.put("worldPermissions", user.getWorldPermissions());
            usersData.put(user.getName(), userMap);
        }
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("users", usersData);
        
        JsonParser parser = new JsonParser();
        try (OutputStream output = Files.newOutputStream(file)) {
            parser.write(output, data);
        } catch (ConfigParseException e) {
            throw new IOException("Failed to save users", e);
        }
    }
}