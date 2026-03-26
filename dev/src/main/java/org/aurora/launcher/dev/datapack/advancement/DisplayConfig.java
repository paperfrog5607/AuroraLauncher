package org.aurora.launcher.dev.datapack.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DisplayConfig {
    private String icon;
    private String title;
    private String description;
    private String frame = "task";
    private boolean showToast = true;
    private boolean announceToChat = true;
    private boolean hidden = false;
    private String background;

    public DisplayConfig() {
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public boolean isShowToast() {
        return showToast;
    }

    public void setShowToast(boolean showToast) {
        this.showToast = showToast;
    }

    public boolean isAnnounceToChat() {
        return announceToChat;
    }

    public void setAnnounceToChat(boolean announceToChat) {
        this.announceToChat = announceToChat;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        
        JsonObject iconObj = new JsonObject();
        iconObj.addProperty("item", icon);
        json.add("icon", iconObj);
        
        JsonObject titleObj = new JsonObject();
        titleObj.addProperty("text", title);
        json.add("title", titleObj);
        
        JsonObject descObj = new JsonObject();
        descObj.addProperty("text", description);
        json.add("description", descObj);
        
        json.addProperty("frame", frame);
        json.addProperty("show_toast", showToast);
        json.addProperty("announce_to_chat", announceToChat);
        json.addProperty("hidden", hidden);
        
        if (background != null) {
            json.addProperty("background", background);
        }
        
        return json;
    }
}