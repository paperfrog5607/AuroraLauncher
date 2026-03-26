package org.aurora.launcher.account.model;

import java.time.Instant;

public class MicrosoftAccount extends Account {
    private String accessToken;
    private String refreshToken;
    private String xboxToken;
    private String minecraftToken;
    private Instant tokenExpiry;
    private SkinProfile skin;

    public MicrosoftAccount() {
        super();
        this.type = AccountType.MICROSOFT;
    }

    public String getAccessToken() {
        return minecraftToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getXboxToken() {
        return xboxToken;
    }

    public void setXboxToken(String xboxToken) {
        this.xboxToken = xboxToken;
    }

    public String getMinecraftToken() {
        return minecraftToken;
    }

    public void setMinecraftToken(String minecraftToken) {
        this.minecraftToken = minecraftToken;
    }

    public Instant getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(Instant tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    public SkinProfile getSkin() {
        return skin;
    }

    public void setSkin(SkinProfile skin) {
        this.skin = skin;
    }

    @Override
    public boolean isValid() {
        if (minecraftToken == null || minecraftToken.isEmpty()) {
            return false;
        }
        if (tokenExpiry == null) {
            return false;
        }
        return tokenExpiry.isAfter(Instant.now());
    }

    @Override
    public void refresh() {
    }

    @Override
    public void logout() {
        this.accessToken = null;
        this.refreshToken = null;
        this.xboxToken = null;
        this.minecraftToken = null;
        this.tokenExpiry = null;
    }
}