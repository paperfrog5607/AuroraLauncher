package org.aurora.launcher.account.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class OfflineAccountTest {

    @Test
    void constructor_setsDefaultValues() {
        OfflineAccount account = new OfflineAccount();
        
        assertNotNull(account.getId());
        assertNull(account.getUsername());
        assertNull(account.getDisplayName());
        assertNull(account.getUuid());
        assertEquals(AccountType.OFFLINE, account.getType());
        assertNull(account.getCreatedAt());
        assertNull(account.getLastUsed());
        assertFalse(account.isSelected());
    }

    @Test
    void setUsername_setsUsername() {
        OfflineAccount account = new OfflineAccount();
        account.setUsername("TestPlayer");
        
        assertEquals("TestPlayer", account.getUsername());
    }

    @Test
    void setDisplayName_setsDisplayName() {
        OfflineAccount account = new OfflineAccount();
        account.setDisplayName("Display Name");
        
        assertEquals("Display Name", account.getDisplayName());
    }

    @Test
    void setUuid_setsUuid() {
        OfflineAccount account = new OfflineAccount();
        account.setUuid("test-uuid-1234");
        
        assertEquals("test-uuid-1234", account.getUuid());
    }

    @Test
    void setCreatedAt_setsCreatedAt() {
        OfflineAccount account = new OfflineAccount();
        Instant now = Instant.now();
        account.setCreatedAt(now);
        
        assertEquals(now, account.getCreatedAt());
    }

    @Test
    void setLastUsed_setsLastUsed() {
        OfflineAccount account = new OfflineAccount();
        Instant now = Instant.now();
        account.setLastUsed(now);
        
        assertEquals(now, account.getLastUsed());
    }

    @Test
    void setSelected_setsSelected() {
        OfflineAccount account = new OfflineAccount();
        account.setSelected(true);
        
        assertTrue(account.isSelected());
    }

    @Test
    void isValid_returnsTrue() {
        OfflineAccount account = new OfflineAccount();
        assertTrue(account.isValid());
    }

    @Test
    void refresh_doesNothing() {
        OfflineAccount account = new OfflineAccount();
        assertDoesNotThrow(() -> account.refresh());
    }

    @Test
    void getAccessToken_returnsEmptyString() {
        OfflineAccount account = new OfflineAccount();
        assertEquals("", account.getAccessToken());
    }

    @Test
    void logout_clearsNothing() {
        OfflineAccount account = new OfflineAccount();
        account.setUsername("TestPlayer");
        account.logout();
        
        assertEquals("TestPlayer", account.getUsername());
    }
}