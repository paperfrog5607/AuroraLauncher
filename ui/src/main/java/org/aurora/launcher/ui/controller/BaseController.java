package org.aurora.launcher.ui.controller;

import javafx.fxml.Initializable;
import org.aurora.launcher.ui.i18n.I18nManager;
import org.aurora.launcher.ui.router.TabRouter;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {
    protected TabRouter router;
    protected Map<String, Object> params;
    protected ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        onInitialize();
    }

    protected void onInitialize() {
    }

    public void setRouter(TabRouter router) {
        this.router = router;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
        onParamsSet();
    }

    protected void onParamsSet() {
    }

    protected String t(String key) {
        I18nManager i18n = I18nManager.getInstance();
        if (i18n != null) {
            return i18n.get(key);
        }
        if (resources != null) {
            return resources.getString(key);
        }
        return key;
    }

    protected String t(String key, Object... args) {
        I18nManager i18n = I18nManager.getInstance();
        if (i18n != null) {
            return i18n.get(key, args);
        }
        if (resources != null) {
            return String.format(resources.getString(key), args);
        }
        return key;
    }

    protected void switchTab(String tabId) {
        if (router != null) {
            router.switchTab(tabId);
        }
    }

    protected void switchTab(String tabId, String subTabId) {
        if (router != null) {
            router.switchTab(tabId, subTabId);
        }
    }

    protected void switchSubTab(String subTabId) {
        if (router != null) {
            router.switchSubTab(subTabId);
        }
    }

    public TabRouter getRouter() {
        return router;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}