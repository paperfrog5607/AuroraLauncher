package org.aurora.launcher.account.storage;

import org.aurora.launcher.account.model.OfflineAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AccountStorageTest {

    @TempDir
    Path tempDir;
    
    private AccountStorage storage;
    private Path storagePath;

    @BeforeEach
    void setUp() {
        storagePath = tempDir.resolve("accounts.json");
        storage = new AccountStorage(storagePath);
    }

    @Test
    void load_emptyFile_createsEmptyStorage() throws StorageException {
        storage.load();
        
        assertTrue(storage.getAllAccounts().isEmpty());
    }

    @Test
    void addAccount_accountAdded() {
        OfflineAccount account = new OfflineAccount();
        account.setUsername("TestPlayer");
        
        storage.addAccount(account);
        
        assertEquals(1, storage.getAllAccounts().size());
        assertEquals(account, storage.getAccount(account.getId()));
    }

    @Test
    void removeAccount_accountRemoved() {
        OfflineAccount account = new OfflineAccount();
        account.setUsername("TestPlayer");
        storage.addAccount(account);
        
        storage.removeAccount(account.getId());
        
        assertTrue(storage.getAllAccounts().isEmpty());
    }

    @Test
    void selectAccount_accountSelected() {
        OfflineAccount account = new OfflineAccount();
        account.setUsername("TestPlayer");
        storage.addAccount(account);
        
        storage.selectAccount(account.getId());
        
        assertEquals(account, storage.getSelectedAccount());
        assertTrue(account.isSelected());
    }

    @Test
    void save_andLoad_preservesAccounts() throws StorageException {
        OfflineAccount account = new OfflineAccount();
        account.setUsername("TestPlayer");
        account.setUuid("test-uuid-1234");
        storage.addAccount(account);
        storage.selectAccount(account.getId());
        
        storage.save();
        
        AccountStorage newStorage = new AccountStorage(storagePath);
        newStorage.load();
        
        assertEquals(1, newStorage.getAllAccounts().size());
        assertEquals("TestPlayer", newStorage.getSelectedAccount().getUsername());
    }
}