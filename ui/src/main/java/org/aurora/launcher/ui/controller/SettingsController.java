package org.aurora.launcher.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * 设置控制器
 */
public class SettingsController extends BaseController {

    @Override
    protected void onInitialize() {
        // 初始化设置
    }

    @FXML
    private void onBackClick() {
        switchTab("home");
    }

    @FXML
    private void onHomeClick() {
        switchTab("home");
    }

    @FXML
    private void onLibraryClick() {
        switchTab("library");
    }

    @FXML
    private void onToolsClick() {
        switchTab("tools");
    }

    @FXML
    private void onCommunityClick() {
        switchTab("community");
    }

    @FXML
    private void onSettingsClick() {
        // 已经在设置页
    }
}
