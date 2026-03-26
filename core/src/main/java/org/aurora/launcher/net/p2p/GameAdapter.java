package org.aurora.launcher.net.p2p;

public interface GameAdapter {
    
    void start();
    
    void stop();
    
    boolean isRunning();
    
    int getLocalPort();
    
    void setOnGameDataCallback(GameDataCallback callback);
    
    void sendGameData(byte[] data);
    
    interface GameDataCallback {
        void onDataReceived(byte[] data, String senderId);
    }
}
