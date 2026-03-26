package org.aurora.launcher.download.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProxyConfigTest {

    @Test
    void defaultValues() {
        ProxyConfig config = new ProxyConfig();
        assertFalse(config.isEnabled());
        assertNull(config.getHost());
        assertEquals(0, config.getPort());
        assertNull(config.getUsername());
        assertNull(config.getPassword());
        assertEquals(ProxyConfig.ProxyType.HTTP, config.getType());
    }

    @Test
    void settersAndGetters() {
        ProxyConfig config = new ProxyConfig();
        config.setEnabled(true);
        config.setHost("127.0.0.1");
        config.setPort(7890);
        config.setUsername("user");
        config.setPassword("pass");
        config.setType(ProxyConfig.ProxyType.SOCKS5);

        assertTrue(config.isEnabled());
        assertEquals("127.0.0.1", config.getHost());
        assertEquals(7890, config.getPort());
        assertEquals("user", config.getUsername());
        assertEquals("pass", config.getPassword());
        assertEquals(ProxyConfig.ProxyType.SOCKS5, config.getType());
    }

    @Test
    void proxyTypeEnum() {
        assertEquals(3, ProxyConfig.ProxyType.values().length);
        assertEquals(ProxyConfig.ProxyType.HTTP, ProxyConfig.ProxyType.valueOf("HTTP"));
        assertEquals(ProxyConfig.ProxyType.SOCKS4, ProxyConfig.ProxyType.valueOf("SOCKS4"));
        assertEquals(ProxyConfig.ProxyType.SOCKS5, ProxyConfig.ProxyType.valueOf("SOCKS5"));
    }
}