package org.aurora.launcher.launcher.launch;

public class LaunchOptions {
    private boolean fullscreen = false;
    private int width = 854;
    private int height = 480;
    private String serverAddress;
    private int serverPort = 25565;
    private boolean demo = false;
    private boolean disableMultiplayer = false;
    private boolean disableChat = false;

    public LaunchOptions() {
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isDemo() {
        return demo;
    }

    public void setDemo(boolean demo) {
        this.demo = demo;
    }

    public boolean isDisableMultiplayer() {
        return disableMultiplayer;
    }

    public void setDisableMultiplayer(boolean disableMultiplayer) {
        this.disableMultiplayer = disableMultiplayer;
    }

    public boolean isDisableChat() {
        return disableChat;
    }

    public void setDisableChat(boolean disableChat) {
        this.disableChat = disableChat;
    }
}