package org.aurora.launcher.ui.controller.settings;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.aurora.launcher.ui.controller.BaseController;

public class AccountSettingsController extends BaseController {
    
    @FXML
    private ImageView avatarView;
    
    @FXML
    private Label usernameLabel;
    
    @FXML
    private Label accountTypeLabel;
    
    @FXML
    private ListView<Object> accountList;
    
    @FXML
    private Button loginMicrosoftButton;
    
    @FXML
    private Button loginOfflineButton;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private Button refreshSkinButton;
    
    @Override
    protected void onInitialize() {
        loadCurrentAccount();
        loadAccountList();
    }
    
    private void loadCurrentAccount() {
        if (usernameLabel != null) {
            usernameLabel.setText(t("account.notLoggedIn"));
        }
    }
    
    private void loadAccountList() {
    }
    
    @FXML
    private void onLoginMicrosoft() {
        if (loginMicrosoftButton != null) {
            loginMicrosoftButton.setDisable(true);
        }
    }
    
    @FXML
    private void onLoginOffline() {
    }
    
    @FXML
    private void onLogout() {
        loadCurrentAccount();
    }
    
    @FXML
    private void onRefreshSkin() {
    }
    
    @FXML
    private void onSwitchAccount() {
    }
    
    @FXML
    private void onDeleteAccount() {
    }
}