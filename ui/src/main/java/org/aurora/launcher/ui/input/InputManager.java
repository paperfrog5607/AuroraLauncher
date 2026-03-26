package org.aurora.launcher.ui.input;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.aurora.launcher.ui.AuroraApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InputManager {
    private static final Logger logger = LoggerFactory.getLogger(InputManager.class);
    
    private static InputManager instance;
    
    private Scene currentScene;
    private GamepadManager gamepadManager;
    private Node focusedNode;
    private List<Node> focusableNodes = new ArrayList<>();
    private int focusIndex = 0;
    
    private Node selectedNode;
    private boolean hasSelection = false;
    private long lastEscPress = 0;
    private int escPressCount = 0;
    private static final long DOUBLE_PRESS_THRESHOLD = 500;
    
    private InputHintsOverlay hintsOverlay;
    private KeyboardHelpOverlay helpOverlay;
    private GamepadHelpOverlay gamepadHelpOverlay;
    private ShortcutManager shortcutManager;
    private InputMode currentMode = InputMode.KEYBOARD;
    private GamepadManager.GamepadListener gamepadListener;
    
    public enum InputMode {
        KEYBOARD,
        GAMEPAD,
        MOUSE
    }
    
    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }
    
    private InputManager() {}
    
    public void initialize(Scene scene) {
        this.currentScene = scene;
        this.shortcutManager = ShortcutManager.getInstance();
        setupKeyboardInput();
        setupGamepad();
        setupMouseInput();
        
        scene.setOnKeyPressed(this::handleKeyPress);
        
        logger.info("InputManager initialized");
    }
    
    private void setupGamepad() {
        gamepadManager = GamepadManager.getInstance();
        gamepadListener = new GamepadManager.GamepadListener() {
            @Override
            public void onButtonPressed(GamepadManager.GamepadButton button) {
                handleGamepadButton(button);
            }
            
            @Override
            public void onButtonReleased(GamepadManager.GamepadButton button) {
            }
            
            @Override
            public void onAxisMoved(GamepadManager.GamepadAxis axis, float value) {
            }
        };
        gamepadManager.addListener(gamepadListener);
        gamepadManager.startPolling();
    }
    
    private void handleGamepadButton(GamepadManager.GamepadButton button) {
        switch (button) {
            case START:
                toggleGamepadHelpPanel();
                break;
            case GUIDE:
                switchInputMode();
                break;
            default:
                break;
        }
    }
    
    private void setupMouseInput() {
        if (currentScene == null) return;
        
        currentScene.setOnMouseClicked(event -> {
            if (currentMode != InputMode.MOUSE && hintsOverlay != null) {
                switchToMode(InputMode.MOUSE);
            }
        });
    }
    
    public void switchToMode(InputMode mode) {
        if (this.currentMode == mode) return;
        
        this.currentMode = mode;
        if (hintsOverlay != null) {
            switch (mode) {
                case KEYBOARD:
                    hintsOverlay.showKeyboardHints(true);
                    break;
                case GAMEPAD:
                    hintsOverlay.showGamepadHints(true);
                    break;
                case MOUSE:
                    hintsOverlay.showMouseHints(true);
                    break;
            }
        }
        logger.info("Input mode switched to: {}", mode.name().toLowerCase());
    }
    
    public InputMode getCurrentMode() {
        return currentMode;
    }
    
    private void setupKeyboardInput() {
        if (currentScene == null) return;
    }
    
    public void handleKeyPress(KeyEvent event) {
        if (!shortcutManager.isEnabled()) return;
        
        KeyCode code = event.getCode();
        
        if (code == KeyCode.ESCAPE) {
            handleEscape();
            event.consume();
            return;
        }
        
        if (hasSelection && code == KeyCode.TAB) {
            handleTabNavigation(!event.isShiftDown());
            event.consume();
            return;
        }
        
        switch (code) {
            case UP:
            case W:
                navigateUp();
                event.consume();
                break;
            case DOWN:
            case S:
                navigateDown();
                event.consume();
                break;
            case LEFT:
            case A:
                navigateLeft();
                event.consume();
                break;
            case RIGHT:
            case D:
                navigateRight();
                event.consume();
                break;
            case L:
                if (hasSelection) {
                    navigateNextInSelection();
                }
                event.consume();
                break;
            case J:
                if (hasSelection) {
                    navigatePreviousInSelection();
                }
                event.consume();
                break;
            case ENTER:
            case SPACE:
                handleConfirm();
                event.consume();
                break;
            case F11:
                toggleFullscreen();
                event.consume();
                break;
            case SLASH:
            case F1:
                toggleHelpPanel();
                event.consume();
                break;
            default:
                if (event.isControlDown() && code == KeyCode.K) {
                    switchInputMode();
                    event.consume();
                }
                break;
        }
    }
    
    private void handleEscape() {
        if (hasSelection) {
            clearSelection();
            return;
        }
        
        long now = System.currentTimeMillis();
        if (now - lastEscPress < DOUBLE_PRESS_THRESHOLD) {
            closeApplication();
        } else {
            escPressCount = 1;
            lastEscPress = now;
        }
    }
    
    private void handleTabNavigation(boolean forward) {
        if (focusableNodes.isEmpty()) return;
        
        int step = forward ? 1 : -1;
        int newIndex = focusIndex;
        
        for (int i = 0; i < focusableNodes.size(); i++) {
            newIndex = (newIndex + step + focusableNodes.size()) % focusableNodes.size();
            Node node = focusableNodes.get(newIndex);
            if (isValidSelectionTarget(node)) {
                focusIndex = newIndex;
                setSelection(node);
                break;
            }
        }
    }
    
    private void navigateUp() {
        if (focusedNode == null) {
            selectFirstFocusable();
            return;
        }
        
        Node next = findNearestNode(focusedNode, Direction.UP);
        if (next != null) {
            setFocus(next);
        }
    }
    
    private void navigateDown() {
        if (focusedNode == null) {
            selectFirstFocusable();
            return;
        }
        
        Node next = findNearestNode(focusedNode, Direction.DOWN);
        if (next != null) {
            setFocus(next);
        }
    }
    
    private void navigateLeft() {
        if (focusedNode == null) {
            selectFirstFocusable();
            return;
        }
        
        Node next = findNearestNode(focusedNode, Direction.LEFT);
        if (next != null) {
            setFocus(next);
        }
    }
    
    private void navigateRight() {
        if (focusedNode == null) {
            selectFirstFocusable();
            return;
        }
        
        Node next = findNearestNode(focusedNode, Direction.RIGHT);
        if (next != null) {
            setFocus(next);
        }
    }
    
    private void navigateNextInSelection() {
        if (focusableNodes.isEmpty()) return;
        
        int startIndex = focusIndex;
        int newIndex = (focusIndex + 1) % focusableNodes.size();
        
        for (int i = 0; i < focusableNodes.size(); i++) {
            Node node = focusableNodes.get(newIndex);
            if (isValidSelectionTarget(node)) {
                focusIndex = newIndex;
                setSelection(node);
                break;
            }
            newIndex = (newIndex + 1) % focusableNodes.size();
            if (newIndex == startIndex) break;
        }
    }
    
    private void navigatePreviousInSelection() {
        if (focusableNodes.isEmpty()) return;
        
        int startIndex = focusIndex;
        int newIndex = (focusIndex - 1 + focusableNodes.size()) % focusableNodes.size();
        
        for (int i = 0; i < focusableNodes.size(); i++) {
            Node node = focusableNodes.get(newIndex);
            if (isValidSelectionTarget(node)) {
                focusIndex = newIndex;
                setSelection(node);
                break;
            }
            newIndex = (newIndex - 1 + focusableNodes.size()) % focusableNodes.size();
            if (newIndex == startIndex) break;
        }
    }
    
    private boolean isValidSelectionTarget(Node node) {
        if (node == null || !node.isVisible() || !node.isManaged()) return false;
        if (node instanceof ButtonBase || node instanceof ComboBox) return true;
        return false;
    }
    
    private void handleConfirm() {
        if (!hasSelection || selectedNode == null) {
            return;
        }
        
        logger.info("Confirming selection on: {}", selectedNode.getClass().getSimpleName());
        
        if (selectedNode instanceof ButtonBase) {
            ((ButtonBase) selectedNode).fire();
        } else if (selectedNode instanceof ComboBox) {
            ((ComboBox<?>) selectedNode).show();
        }
    }
    
    private void setSelection(Node node) {
        clearSelectionVisuals();
        
        selectedNode = node;
        hasSelection = true;
        
        if (node != null) {
            node.getStyleClass().add("input-selected");
            node.requestFocus();
        }
        
        logger.debug("Selection set on: {}", node != null ? node.getClass().getSimpleName() : "null");
    }
    
    private void clearSelection() {
        clearSelectionVisuals();
        selectedNode = null;
        hasSelection = false;
        logger.debug("Selection cleared");
    }
    
    private void clearSelectionVisuals() {
        for (Node node : focusableNodes) {
            node.getStyleClass().remove("input-selected");
        }
    }
    
    private void closeApplication() {
        AuroraApplication app = AuroraApplication.getInstance();
        if (app != null && app.getPrimaryStage() != null) {
            app.getPrimaryStage().close();
        }
    }
    
    private void toggleFullscreen() {
        AuroraApplication app = AuroraApplication.getInstance();
        if (app != null && app.getPrimaryStage() != null) {
            Stage stage = app.getPrimaryStage();
            stage.setFullScreen(!stage.isFullScreen());
            logger.info("Fullscreen toggled: {}", stage.isFullScreen());
        }
    }
    
    private void toggleHelpPanel() {
        if (helpOverlay != null) {
            if (helpOverlay.isShowing()) {
                helpOverlay.hide();
            } else {
                helpOverlay.show();
            }
        }
    }
    
    private void toggleGamepadHelpPanel() {
        if (gamepadHelpOverlay != null) {
            if (gamepadHelpOverlay.isShowing()) {
                gamepadHelpOverlay.hide();
            } else {
                gamepadHelpOverlay.show();
            }
        }
    }
    
    private void selectFirstFocusable() {
        updateFocusableNodes();
        if (!focusableNodes.isEmpty()) {
            for (int i = 0; i < focusableNodes.size(); i++) {
                Node node = focusableNodes.get(i);
                if (isValidSelectionTarget(node)) {
                    focusIndex = i;
                    setFocus(node);
                    break;
                }
            }
        }
    }
    
    public void setFocus(Node node) {
        if (focusedNode != null) {
            focusedNode.getStyleClass().remove("input-focused");
        }
        
        focusedNode = node;
        if (node != null && focusableNodes.contains(node)) {
            focusIndex = focusableNodes.indexOf(node);
        }
        
        if (focusedNode != null) {
            focusedNode.getStyleClass().add("input-focused");
            focusedNode.requestFocus();
        }
    }
    
    private void updateFocusableNodes() {
        focusableNodes.clear();
        
        if (currentScene == null) return;
        
        Node root = currentScene.getRoot();
        if (root != null) {
            findFocusableNodes(root, focusableNodes);
        }
    }
    
    private void findFocusableNodes(Node node, List<Node> list) {
        if (node == null) return;
        
        if (node.isFocusTraversable() && node.isVisible() && node.isManaged()) {
            list.add(node);
        }
        
        if (node instanceof javafx.scene.Parent) {
            javafx.scene.Parent parent = (javafx.scene.Parent) node;
            parent.getChildrenUnmodifiable().forEach(child -> findFocusableNodes(child, list));
        }
    }
    
    private Node findNearestNode(Node current, Direction dir) {
        if (currentScene == null) return null;
        
        updateFocusableNodes();
        
        double currentX = current.getBoundsInParent().getMinX() + current.getBoundsInParent().getWidth() / 2;
        double currentY = current.getBoundsInParent().getMinY() + current.getBoundsInParent().getHeight() / 2;
        
        Node nearest = null;
        double nearestScore = Double.MAX_VALUE;
        
        for (Node node : focusableNodes) {
            if (node == current) continue;
            
            double nodeX = node.getBoundsInParent().getMinX() + node.getBoundsInParent().getWidth() / 2;
            double nodeY = node.getBoundsInParent().getMinY() + node.getBoundsInParent().getHeight() / 2;
            
            boolean isInDirection = false;
            double distance = 0;
            double lateralDistance = 0;
            
            switch (dir) {
                case UP:
                    if (nodeY < currentY - 10) {
                        isInDirection = true;
                        distance = currentY - nodeY;
                        lateralDistance = Math.abs(nodeX - currentX);
                    }
                    break;
                case DOWN:
                    if (nodeY > currentY + 10) {
                        isInDirection = true;
                        distance = nodeY - currentY;
                        lateralDistance = Math.abs(nodeX - currentX);
                    }
                    break;
                case LEFT:
                    if (nodeX < currentX - 10) {
                        isInDirection = true;
                        distance = currentX - nodeX;
                        lateralDistance = Math.abs(nodeY - currentY);
                    }
                    break;
                case RIGHT:
                    if (nodeX > currentX + 10) {
                        isInDirection = true;
                        distance = nodeX - currentX;
                        lateralDistance = Math.abs(nodeY - currentY);
                    }
                    break;
            }
            
            if (isInDirection) {
                double score = distance + lateralDistance * 0.5;
                if (score < nearestScore) {
                    nearestScore = score;
                    nearest = node;
                }
            }
        }
        
        return nearest;
    }
    
    public void refreshFocusableNodes() {
        updateFocusableNodes();
    }
    
    public Node getFocusedNode() {
        return focusedNode;
    }
    
    public Node getSelectedNode() {
        return selectedNode;
    }
    
    public boolean hasSelection() {
        return hasSelection;
    }
    
    public List<Node> getFocusableNodes() {
        return new ArrayList<>(focusableNodes);
    }
    
    public void shutdown() {
        if (gamepadManager != null) {
            gamepadManager.stopPolling();
            gamepadManager.removeListener(gamepadListener);
        }
    }
    
    public void showKeyboardHints() {
        if (hintsOverlay != null) {
            hintsOverlay.showKeyboardHints(true);
        }
    }
    
    public void showGamepadHints() {
        if (hintsOverlay != null) {
            hintsOverlay.showGamepadHints(true);
        }
    }
    
    public void setHintsOverlay(InputHintsOverlay overlay) {
        this.hintsOverlay = overlay;
    }
    
    public void setHelpOverlay(KeyboardHelpOverlay overlay) {
        this.helpOverlay = overlay;
    }
    
    public void setGamepadHelpOverlay(GamepadHelpOverlay overlay) {
        this.gamepadHelpOverlay = overlay;
    }
    
    private void switchInputMode() {
        switch (currentMode) {
            case KEYBOARD:
                switchToMode(InputMode.GAMEPAD);
                break;
            case GAMEPAD:
                switchToMode(InputMode.MOUSE);
                break;
            case MOUSE:
                switchToMode(InputMode.KEYBOARD);
                break;
        }
    }
    
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}