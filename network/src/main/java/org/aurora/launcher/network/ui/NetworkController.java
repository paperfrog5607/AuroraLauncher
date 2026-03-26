package org.aurora.launcher.network.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.aurora.launcher.network.p2p.*;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import java.util.UUID;

public class NetworkController {
    private final P2PManager p2pManager;
    private final ObservableList<String> memberList;
    private String playerId;
    private String playerName;

    @FXML
    private VBox mainContainer;

    @FXML
    private VBox homePanel;

    @FXML
    private VBox createRoomPanel;

    @FXML
    private VBox joinRoomPanel;

    @FXML
    private VBox roomInfoPanel;

    @FXML
    private Label roomCodeLabel;

    @FXML
    private TextField inviteLinkField;

    @FXML
    private Label roomStatusLabel;

    @FXML
    private Label memberCountLabel;

    @FXML
    private ListView<String> memberListView;

    @FXML
    private TextField roomCodeInput;

    @FXML
    private TextField inviteLinkInput;

    @FXML
    private Label natTypeLabel;

    @FXML
    private Label latencyLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button createRoomBtn;

    @FXML
    private Button joinRoomBtn;

    @FXML
    private Button copyCodeBtn;

    @FXML
    private Button copyLinkBtn;

    @FXML
    private Button startGameBtn;

    @FXML
    private Button closeRoomBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button disconnectBtn;

    public NetworkController() {
        this.p2pManager = new P2PManager();
        this.memberList = FXCollections.observableArrayList();
        this.playerId = UUID.randomUUID().toString();
        this.playerName = "Player" + (int)(Math.random() * 10000);
    }

    @FXML
    private void initialize() {
        p2pManager.addEventListener(this::onP2PEvent);
        
        memberListView.setItems(memberList);
        
        updateNatType();
    }

    public void init(String playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        p2pManager.start(playerId, playerName);
    }

    @FXML
    private void onCreateRoom(ActionEvent event) {
        showCreateRoomPanel();
    }

    @FXML
    private void onJoinRoom(ActionEvent event) {
        showJoinRoomPanel();
    }

    @FXML
    private void onBack(ActionEvent event) {
        showHomePanel();
    }

    @FXML
    private void onCopyRoomCode(ActionEvent event) {
        String code = roomCodeLabel.getText();
        if (code != null && !code.isEmpty()) {
            copyToClipboard(code);
            showSuccess("房间号已复制");
        }
    }

    @FXML
    private void onCopyInviteLink(ActionEvent event) {
        String link = inviteLinkField.getText();
        if (link != null && !link.isEmpty()) {
            copyToClipboard(link);
            showSuccess("邀请链接已复制");
        }
    }

    @FXML
    private void onJoinByCode(ActionEvent event) {
        String code = roomCodeInput.getText();
        if (code == null || code.trim().isEmpty()) {
            showError("请输入房间号");
            return;
        }
        
        joinRoom(code.trim());
    }

    @FXML
    private void onJoinByLink(ActionEvent event) {
        String link = inviteLinkInput.getText();
        if (link == null || link.trim().isEmpty()) {
            showError("请粘贴邀请链接");
            return;
        }
        
        joinRoom(link.trim());
    }

    @FXML
    private void onStartGame(ActionEvent event) {
        Room room = p2pManager.getCurrentRoom();
        if (room != null) {
            room.setState(Room.RoomState.IN_GAME);
            roomStatusLabel.setText("游戏中");
            showSuccess("游戏已开始");
        }
    }

    @FXML
    private void onCloseRoom(ActionEvent event) {
        p2pManager.leaveRoom();
        showHomePanel();
        showSuccess("房间已关闭");
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        updateRoomInfo();
    }

    @FXML
    private void onDisconnect(ActionEvent event) {
        p2pManager.leaveRoom();
        showHomePanel();
        showSuccess("已断开连接");
    }

    private void joinRoom(String roomCodeOrLink) {
        errorLabel.setVisible(false);
        
        boolean success = p2pManager.joinRoom(roomCodeOrLink, playerName);
        if (success) {
            showRoomInfoPanel();
            updateRoomInfo();
        } else {
            showError("加入房间失败，请检查房间号或链接");
        }
    }

    private void showHomePanel() {
        homePanel.setVisible(true);
        createRoomPanel.setVisible(false);
        joinRoomPanel.setVisible(false);
        roomInfoPanel.setVisible(false);
    }

    private void showCreateRoomPanel() {
        Room room = p2pManager.createRoom();
        
        roomCodeLabel.setText(room.getRoomCode());
        inviteLinkField.setText(room.getInviteLink());
        
        homePanel.setVisible(false);
        createRoomPanel.setVisible(true);
        joinRoomPanel.setVisible(false);
        roomInfoPanel.setVisible(false);
        
        updateRoomInfo();
    }

    private void showJoinRoomPanel() {
        roomCodeInput.clear();
        inviteLinkInput.clear();
        errorLabel.setVisible(false);
        
        homePanel.setVisible(false);
        createRoomPanel.setVisible(false);
        joinRoomPanel.setVisible(true);
        roomInfoPanel.setVisible(false);
    }

    private void showRoomInfoPanel() {
        homePanel.setVisible(false);
        createRoomPanel.setVisible(false);
        joinRoomPanel.setVisible(false);
        roomInfoPanel.setVisible(true);
    }

    private void updateRoomInfo() {
        Room room = p2pManager.getCurrentRoom();
        if (room == null) {
            return;
        }
        
        roomCodeLabel.setText(room.getRoomCode());
        inviteLinkField.setText(room.getInviteLink());
        
        String stateText = switch (room.getState()) {
            case WAITING -> "等待中";
            case IN_GAME -> "游戏中";
            case CLOSED -> "已关闭";
        };
        roomStatusLabel.setText(stateText);
        memberCountLabel.setText(room.getMembers().size() + "/" + room.getMaxMembers());
        
        memberList.clear();
        for (RoomMember member : room.getMembers()) {
            String role = member.isHost() ? "(房主)" : "";
            String latency = member.getLatency() >= 0 ? " - " + member.getLatency() + "ms" : "";
            memberList.add(member.getDisplayName() + role + latency);
        }
    }

    private void updateNatType() {
        NatType natType = p2pManager.getNatType();
        String typeText = switch (natType) {
            case OPEN -> "开放";
            case MODERATE -> "中等";
            case STRICT -> "严格";
            case SYMMETRIC -> "对称";
            case UNKNOWN -> "检测中...";
        };
        natTypeLabel.setText(typeText);
    }

    private void onP2PEvent(P2PManager.P2PEvent event) {
        Platform.runLater(() -> {
            switch (event.getType()) {
                case ROOM_CREATED, ROOM_JOINED -> {
                    showRoomInfoPanel();
                    updateRoomInfo();
                }
                case ROOM_LEFT -> showHomePanel();
                case PEER_CONNECTED, PEER_DISCONNECTED -> updateRoomInfo();
                case NAT_TYPE_CHANGED -> updateNatType();
                case LATENCY_UPDATED -> updateRoomInfo();
                case ERROR -> showError(event.getData());
                default -> {}
            }
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #00ff88;");
        errorLabel.setVisible(true);
        
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    errorLabel.setStyle("-fx-text-fill: #ff4444;");
                    if (p2pManager.getCurrentRoom() == null) {
                        errorLabel.setVisible(false);
                    }
                });
            } catch (InterruptedException ignored) {}
        }).start();
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    public void shutdown() {
        p2pManager.stop();
    }
}
