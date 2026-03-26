package org.aurora.launcher.account.model;

import java.time.Instant;

public class CustomAccount extends Account {
    private String authServerUrl;
    private String accessToken;
    private String refreshToken;
    private Instant tokenExpiry;

    public CustomAccount() {
        super();
        this.type = AccountType.CUSTOM;
    }

    public String getAuthServerUrl() {
        return authServerUrl;
    }

    public void setAuthServerUrl(String authServerUrl) {
        this.authServerUrl = authServerUrl;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
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

    public Instant getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(Instant tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    @Override
    public boolean isValid() {
        if (accessToken == null || accessToken.isEmpty()) {
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
        this.tokenExpiry = null;
    }
}