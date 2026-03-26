package org.aurora.launcher.account.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class MicrosoftAccountTest {

    @Test
    void constructor_setsDefaultValues() {
        MicrosoftAccount account = new MicrosoftAccount();
        
        assertNotNull(account.getId());
        assertNull(account.getUsername());
        assertNull(account.getDisplayName());
        assertNull(account.getUuid());
        assertEquals(AccountType.MICROSOFT, account.getType());
        assertNull(account.getCreatedAt());
        assertNull(account.getLastUsed());
        assertFalse(account.isSelected());
        assertNull(account.getAccessToken());
        assertNull(account.getRefreshToken());
        assertNull(account.getXboxToken());
        assertNull(account.getMinecraftToken());
        assertNull(account.getTokenExpiry());
        assertNull(account.getSkin());
    }

    @Test
    void setMinecraftToken_setsAccessToken() {
        MicrosoftAccount account = new MicrosoftAccount();
        account.setMinecraftToken("test-minecraft-token");
        
        assertEquals("test-minecraft-token", account.getAccessToken());
    }

    @Test
    void setRefreshToken_setsRefreshToken() {
        MicrosoftAccount account = new MicrosoftAccount();
        account.setRefreshToken("test-refresh-token");
        
        assertEquals("test-refresh-token", account.getRefreshToken());
    }

    @Test
    void setXboxToken_setsXboxToken() {
        MicrosoftAccount account = new MicrosoftAccount();
        account.setXboxToken("test-xbox-token");
        
        assertEquals("test-xbox-token", account.getXboxToken());
    }

    @Test
    void setMinecraftToken_setsMinecraftToken() {
        MicrosoftAccount account = new MicrosoftAccount();
        account.setMinecraftToken("test-minecraft-token");
        
        assertEquals("test-minecraft-token", account.getMinecraftToken());
    }

    @Test
    void setTokenExpiry_setsTokenExpiry() {
        MicrosoftAccount account = new MicrosoftAccount();
        Instant expiry = Instant.now().plusSeconds(3600);
        account.setTokenExpiry(expiry);
        
        assertEquals(expiry, account.getTokenExpiry());
    }

    @Test
    void setSkin_setsSkin() {
        MicrosoftAccount account = new MicrosoftAccount();
        SkinProfile skin = new SkinProfile();
        account.setSkin(skin);
        
        assertEquals(skin, account.getSkin());
    }

    @Test
    void isValid_withValidToken_returnsTrue() {
        MicrosoftAccount account = new MicrosoftAccount();
        account.setMinecraftToken("valid-token");
        account.setTokenExpiry(Instant.now().plusSeconds(3600));
        
        assertTrue(account.isValid());
    }

    @Test
    void isValid_withExpiredToken_returnsFalse() {
        MicrosoftAccount account = new MicrosoftAccount();
        account.setMinecraftToken("expired-token");
        account.setTokenExpiry(Instant.now().minusSeconds(3600));
        
        assertFalse(account.isValid());
    }

    @Test
    void isValid_withNoToken_returnsFalse() {
        MicrosoftAccount account = new MicrosoftAccount();
        
        assertFalse(account.isValid());
    }

    @Test
    void getAccessToken_returnsMinecraftToken() {
        MicrosoftAccount account = new MicrosoftAccount();
        account.setMinecraftToken("minecraft-token");
        
        assertEquals("minecraft-token", account.getAccessToken());
    }

    @Test
    void logout_clearsTokens() {
        MicrosoftAccount account = new MicrosoftAccount();
        account.setAccessToken("access");
        account.setRefreshToken("refresh");
        account.setXboxToken("xbox");
        account.setMinecraftToken("minecraft");
        account.setTokenExpiry(Instant.now());
        
        account.logout();
        
        assertNull(account.getAccessToken());
        assertNull(account.getRefreshToken());
        assertNull(account.getXboxToken());
        assertNull(account.getMinecraftToken());
        assertNull(account.getTokenExpiry());
    }
}