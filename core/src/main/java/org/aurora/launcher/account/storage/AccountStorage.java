package org.aurora.launcher.account.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.aurora.launcher.account.model.*;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AccountStorage {
    private Path storagePath;
    private Map<String, Account> accounts;
    private String selectedAccountId;
    private byte[] encryptionKey;
    private Gson gson;

    public AccountStorage(Path storagePath) {
        this.storagePath = storagePath;
        this.accounts = new LinkedHashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void load() throws StorageException {
        if (!Files.exists(storagePath)) {
            return;
        }
        
        try {
            String content = new String(Files.readAllBytes(storagePath), "UTF-8");
            JsonObject root = JsonParser.parseString(content).getAsJsonObject();
            
            selectedAccountId = root.has("selectedAccountId") ? root.get("selectedAccountId").getAsString() : null;
            
            if (root.has("accounts")) {
                Type listType = new TypeToken<List<JsonObject>>(){}.getType();
                List<JsonObject> accountList = gson.fromJson(root.get("accounts"), listType);
                
                for (JsonObject json : accountList) {
                    Account account = deserializeAccount(json);
                    if (account != null) {
                        accounts.put(account.getId(), account);
                    }
                }
            }
        } catch (Exception e) {
            throw new StorageException(StorageErrorCode.READ_ERROR, e);
        }
    }

    public void save() throws StorageException {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("version", 1);
            root.addProperty("selectedAccountId", selectedAccountId);
            
            List<JsonObject> accountList = new ArrayList<>();
            for (Account account : accounts.values()) {
                accountList.add(serializeAccount(account));
            }
            root.add("accounts", gson.toJsonTree(accountList));
            
            Path parent = storagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(storagePath, gson.toJson(root).getBytes("UTF-8"));
        } catch (Exception e) {
            throw new StorageException(StorageErrorCode.WRITE_ERROR, e);
        }
    }

    public void addAccount(Account account) {
        accounts.put(account.getId(), account);
    }

    public void removeAccount(String accountId) {
        accounts.remove(accountId);
        if (accountId.equals(selectedAccountId)) {
            selectedAccountId = null;
        }
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public void selectAccount(String accountId) {
        for (Account account : accounts.values()) {
            account.setSelected(account.getId().equals(accountId));
        }
        selectedAccountId = accountId;
    }

    public Account getSelectedAccount() {
        return selectedAccountId != null ? accounts.get(selectedAccountId) : null;
    }

    public void setEncryptionKey(byte[] key) {
        this.encryptionKey = key;
    }

    public void changeEncryptionKey(byte[] newKey) {
        this.encryptionKey = newKey;
    }

    private Account deserializeAccount(JsonObject json) {
        String type = json.get("type").getAsString();
        Account account;
        
        switch (type) {
            case "microsoft":
                account = deserializeMicrosoftAccount(json);
                break;
            case "offline":
                account = deserializeOfflineAccount(json);
                break;
            case "custom":
                account = deserializeCustomAccount(json);
                break;
            default:
                return null;
        }
        
        account.setId(json.get("id").getAsString());
        account.setUsername(json.get("username").getAsString());
        account.setDisplayName(json.has("displayName") ? json.get("displayName").getAsString() : json.get("username").getAsString());
        account.setUuid(json.get("uuid").getAsString());
        if (json.has("createdAt")) {
            account.setCreatedAt(Instant.parse(json.get("createdAt").getAsString()));
        }
        if (json.has("lastUsed")) {
            account.setLastUsed(Instant.parse(json.get("lastUsed").getAsString()));
        }
        account.setSelected(json.has("selected") && json.get("selected").getAsBoolean());
        
        return account;
    }

    private OfflineAccount deserializeOfflineAccount(JsonObject json) {
        return new OfflineAccount();
    }

    private MicrosoftAccount deserializeMicrosoftAccount(JsonObject json) {
        MicrosoftAccount account = new MicrosoftAccount();
        if (json.has("accessToken")) {
            account.setAccessToken(json.get("accessToken").getAsString());
        }
        if (json.has("refreshToken")) {
            account.setRefreshToken(json.get("refreshToken").getAsString());
        }
        if (json.has("xboxToken")) {
            account.setXboxToken(json.get("xboxToken").getAsString());
        }
        if (json.has("minecraftToken")) {
            account.setMinecraftToken(json.get("minecraftToken").getAsString());
        }
        if (json.has("tokenExpiry")) {
            account.setTokenExpiry(Instant.parse(json.get("tokenExpiry").getAsString()));
        }
        return account;
    }

    private CustomAccount deserializeCustomAccount(JsonObject json) {
        CustomAccount account = new CustomAccount();
        if (json.has("authServerUrl")) {
            account.setAuthServerUrl(json.get("authServerUrl").getAsString());
        }
        if (json.has("accessToken")) {
            account.setAccessToken(json.get("accessToken").getAsString());
        }
        if (json.has("refreshToken")) {
            account.setRefreshToken(json.get("refreshToken").getAsString());
        }
        if (json.has("tokenExpiry")) {
            account.setTokenExpiry(Instant.parse(json.get("tokenExpiry").getAsString()));
        }
        return account;
    }

    private JsonObject serializeAccount(Account account) {
        JsonObject json = new JsonObject();
        
        json.addProperty("id", account.getId());
        json.addProperty("type", account.getType().name().toLowerCase());
        json.addProperty("username", account.getUsername());
        json.addProperty("displayName", account.getDisplayName());
        json.addProperty("uuid", account.getUuid());
        if (account.getCreatedAt() != null) {
            json.addProperty("createdAt", account.getCreatedAt().toString());
        }
        if (account.getLastUsed() != null) {
            json.addProperty("lastUsed", account.getLastUsed().toString());
        }
        json.addProperty("selected", account.isSelected());
        
        if (account instanceof MicrosoftAccount) {
            MicrosoftAccount ms = (MicrosoftAccount) account;
            if (ms.getRefreshToken() != null) {
                json.addProperty("refreshToken", ms.getRefreshToken());
            }
            if (ms.getMinecraftToken() != null) {
                json.addProperty("minecraftToken", ms.getMinecraftToken());
            }
            if (ms.getTokenExpiry() != null) {
                json.addProperty("tokenExpiry", ms.getTokenExpiry().toString());
            }
        } else if (account instanceof CustomAccount) {
            CustomAccount custom = (CustomAccount) account;
            if (custom.getAuthServerUrl() != null) {
                json.addProperty("authServerUrl", custom.getAuthServerUrl());
            }
            if (custom.getRefreshToken() != null) {
                json.addProperty("refreshToken", custom.getRefreshToken());
            }
            if (custom.getTokenExpiry() != null) {
                json.addProperty("tokenExpiry", custom.getTokenExpiry().toString());
            }
        }
        
        return json;
    }
}