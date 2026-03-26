package org.aurora.launcher.download.config;

public class ProxyConfig {
    public enum ProxyType {
        HTTP, SOCKS4, SOCKS5
    }

    private boolean enabled = false;
    private String host;
    private int port;
    private String username;
    private String password;
    private ProxyType type = ProxyType.HTTP;

    public ProxyConfig() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ProxyType getType() {
        return type;
    }

    public void setType(ProxyType type) {
        this.type = type;
    }
}