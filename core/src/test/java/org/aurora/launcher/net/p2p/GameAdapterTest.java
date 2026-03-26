package org.aurora.launcher.net.p2p;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameAdapterTest {
    
    @Test
    public void testGameAdapterInterfaceExists() {
        GameAdapter adapter = new TcpProxyAdapter(8192);
        assertNotNull(adapter);
        assertFalse(adapter.isRunning());
    }
    
    @Test
    public void testTcpAdapterDefaultPort() {
        TcpProxyAdapter adapter = new TcpProxyAdapter();
        assertEquals(8192, adapter.getLocalPort());
        assertFalse(adapter.isRunning());
    }
    
    @Test
    public void testUdpAdapterDefaultPort() {
        UdpProxyAdapter adapter = new UdpProxyAdapter();
        assertEquals(8193, adapter.getLocalPort());
        assertFalse(adapter.isRunning());
    }
    
    @Test
    public void testStartAndStop() {
        TcpProxyAdapter adapter = new TcpProxyAdapter(19201);
        adapter.start();
        assertTrue(adapter.isRunning());
        adapter.stop();
        assertFalse(adapter.isRunning());
    }
    
    @Test
    public void testAdapterCannotStartTwice() {
        TcpProxyAdapter adapter = new TcpProxyAdapter(19202);
        adapter.start();
        assertTrue(adapter.isRunning());
        adapter.start();
        assertTrue(adapter.isRunning());
        adapter.stop();
        assertFalse(adapter.isRunning());
    }
    
    @Test
    public void testUdpStartAndStop() {
        UdpProxyAdapter adapter = new UdpProxyAdapter(19203);
        adapter.start();
        assertTrue(adapter.isRunning());
        adapter.stop();
        assertFalse(adapter.isRunning());
    }
    
    @Test
    public void testCallbackCanBeSet() {
        TcpProxyAdapter adapter = new TcpProxyAdapter(19204);
        GameAdapter.GameDataCallback callback = new GameAdapter.GameDataCallback() {
            @Override
            public void onDataReceived(byte[] data, String senderId) {
            }
        };
        adapter.setOnGameDataCallback(callback);
    }
}
