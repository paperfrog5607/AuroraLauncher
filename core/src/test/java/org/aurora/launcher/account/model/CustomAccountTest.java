package org.aurora.launcher.account.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class CustomAccountTest {

    @Test
    void constructor_setsDefaultValues() {
        CustomAccount account = new CustomAccount();
        
        assertNotNull(account.getId());
        assertNull(account.getUsername());
        assertNull(account.getDisplayName());
        assertNull(account.getUuid());
        assertEquals(AccountType.CUSTOM, account.getType());
        assertNull(account.getCreatedAt());
        assertNull(account.getLastUsed());
        assertFalse(account.isSelected());
        assertNull(account.getAccessToken());
        assertNull(account.getRefreshToken());
        assertNull(account.getAuthServerUrl());
        assertNull(account.getTokenExpiry());
    }

    @Test
    void setAuthServerUrl_setsAuthServerUrl() {
        CustomAccount account = new CustomAccount();
        account.setAuthServerUrl("https://auth.example.com");
        
        assertEquals("https://auth.example.com", account.getAuthServerUrl());
    }

    @Test
    void setAccessToken_setsAccessToken() {
        CustomAccount account = new CustomAccount();
        account.setAccessToken("test-access-token");
        
        assertEquals("test-access-token", account.getAccessToken());
    }

    @Test
    void setRefreshToken_setsRefreshToken() {
        CustomAccount account = new CustomAccount();
        account.setRefreshToken("test-refresh-token");
        
        assertEquals("test-refresh-token", account.getRefreshToken());
    }

    @Test
    void setTokenExpiry_setsTokenExpiry() {
        CustomAccount account = new CustomAccount();
        Instant expiry = Instant.now().plusSeconds(3600);
        account.setTokenExpiry(expiry);
        
        assertEquals(expiry, account.getTokenExpiry());
    }

    @Test
    void isValid_withValidToken_returnsTrue() {
        CustomAccount account = new CustomAccount();
        account.setAccessToken("valid-token");
        account.setTokenExpiry(Instant.now().plusSeconds(3600));
        
        assertTrue(account.isValid());
    }

    @Test
    void isValid_withExpiredToken_returnsFalse() {
        CustomAccount account = new CustomAccount();
        account.setAccessToken("expired-token");
        account.setTokenExpiry(Instant.now().minusSeconds(3600));
        
        assertFalse(account.isValid());
    }

    @Test
    void isValid_withNoToken_returnsFalse() {
        CustomAccount account = new CustomAccount();
        
        assertFalse(account.isValid());
    }

    @Test
    void logout_clearsTokens() {
        CustomAccount account = new CustomAccount();
        account.setAccessToken("access");
        account.setRefreshToken("refresh");
        account.setTokenExpiry(Instant.now());
        
        account.logout();
        
        assertNull(account.getAccessToken());
        assertNull(account.getRefreshToken());
        assertNull(account.getTokenExpiry());
    }
}