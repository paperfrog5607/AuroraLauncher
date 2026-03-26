package org.aurora.launcher.account.model;

public class SkinProfile {
    private String skinUrl;
    private String capeUrl;
    private SkinModel model;
    private boolean slim;

    public SkinProfile() {
    }

    public String getSkinUrl() {
        return skinUrl;
    }

    public void setSkinUrl(String skinUrl) {
        this.skinUrl = skinUrl;
    }

    public String getCapeUrl() {
        return capeUrl;
    }

    public void setCapeUrl(String capeUrl) {
        this.capeUrl = capeUrl;
    }

    public SkinModel getModel() {
        return model;
    }

    public void setModel(SkinModel model) {
        this.model = model;
    }

    public boolean isSlim() {
        return slim;
    }

    public void setSlim(boolean slim) {
        this.slim = slim;
    }
}