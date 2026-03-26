package org.aurora.launcher.launcher.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.aurora.launcher.launcher.GameDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountManager {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountManager.class);
    private static AccountManager instance;
    
    private static final String ACCOUNTS_FILE = "accounts.json";
    
    private final Path accountsFile;
    private final Gson gson;
    private List<Account> accounts;
    private Account selectedAccount;
    
    public static class Account {
        private String id;
        private String username;
        private String uuid;
        private String type;
        private long createdAt;
        
        public Account() {
            this.type = "offline";
            this.createdAt = System.currentTimeMillis();
        }
        
        public Account(String username) {
            this();
            this.id = UUID.randomUUID().toString();
            this.username = username;
            this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes()).toString();
        }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { 
            this.username = username;
            this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes()).toString();
        }
        
        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
        
        public String getDisplayName() {
            return username != null ? username : "Unknown";
        }
        
        public String getUuidWithDashes() {
            if (uuid == null || uuid.length() != 32) return uuid;
            return uuid.substring(0, 8) + "-" + 
                   uuid.substring(8, 12) + "-" +
                   uuid.substring(12, 16) + "-" +
                   uuid.substring(16, 20) + "-" +
                   uuid.substring(20, 32);
        }
    }
    
    private AccountManager() {
        this.accountsFile = GameDirectory.getInstance().getAccountsDir().resolve(ACCOUNTS_FILE);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.accounts = new ArrayList<>();
        loadAccounts();
    }
    
    public static synchronized AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }
    
    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }
    
    public Account getSelectedAccount() {
        return selectedAccount;
    }
    
    public void setSelectedAccount(Account account) {
        this.selectedAccount = account;
        saveAccounts();
    }
    
    public void addAccount(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
            saveAccounts();
            logger.info("Added account: {}", account.getUsername());
        }
    }
    
    public void removeAccount(String accountId) {
        accounts.removeIf(a -> a.getId().equals(accountId));
        if (selectedAccount != null && selectedAccount.getId().equals(accountId)) {
            selectedAccount = accounts.isEmpty() ? null : accounts.get(0);
        }
        saveAccounts();
    }
    
    public void updateAccount(Account account) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId().equals(account.getId())) {
                accounts.set(i, account);
                if (selectedAccount != null && selectedAccount.getId().equals(account.getId())) {
                    selectedAccount = account;
                }
                break;
            }
        }
        saveAccounts();
    }
    
    private void loadAccounts() {
        if (!Files.exists(accountsFile)) {
            logger.info("No accounts file found, creating default");
            Account defaultAccount = new Account("Player");
            accounts.add(defaultAccount);
            selectedAccount = defaultAccount;
            saveAccounts();
            return;
        }
        
        try (Reader reader = Files.newBufferedReader(accountsFile)) {
            Account[] loadedAccounts = gson.fromJson(reader, Account[].class);
            if (loadedAccounts != null) {
                for (Account account : loadedAccounts) {
                    accounts.add(account);
                }
            }
            
            if (!accounts.isEmpty()) {
                selectedAccount = accounts.get(0);
            }
            
            logger.info("Loaded {} accounts", accounts.size());
            
        } catch (Exception e) {
            logger.error("Failed to load accounts", e);
            Account defaultAccount = new Account("Player");
            accounts.add(defaultAccount);
            selectedAccount = defaultAccount;
        }
    }
    
    private void saveAccounts() {
        try {
            Files.createDirectories(accountsFile.getParent());
            try (Writer writer = Files.newBufferedWriter(accountsFile)) {
                gson.toJson(accounts.toArray(new Account[0]), writer);
                logger.debug("Saved {} accounts", accounts.size());
            }
        } catch (Exception e) {
            logger.error("Failed to save accounts", e);
        }
    }
    
    public Account createOfflineAccount(String username) {
        Account account = new Account(username);
        addAccount(account);
        selectedAccount = account;
        return account;
    }
}
