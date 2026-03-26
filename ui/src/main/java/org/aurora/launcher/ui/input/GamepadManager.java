package org.aurora.launcher.ui.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GamepadManager {
    private static final Logger logger = LoggerFactory.getLogger(GamepadManager.class);
    
    private static GamepadManager instance;
    
    private List<GamepadListener> listeners = new ArrayList<>();
    private AtomicBoolean isPolling = new AtomicBoolean(false);
    private Thread pollThread;
    
    public interface GamepadListener {
        void onButtonPressed(GamepadButton button);
        void onButtonReleased(GamepadButton button);
        void onAxisMoved(GamepadAxis axis, float value);
    }
    
    public enum GamepadButton {
        A(0), B(1), X(2), Y(3),
        LB(4), RB(5), LS(6), RS(7),
        START(8), SELECT(9), GUIDE(10),
        DPAD_UP(11), DPAD_DOWN(12), DPAD_LEFT(13), DPAD_RIGHT(14);
        
        private final int code;
        
        GamepadButton(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return code;
        }
    }
    
    public enum GamepadAxis {
        LEFT_STICK_X(0), LEFT_STICK_Y(1), RIGHT_STICK_X(2), RIGHT_STICK_Y(3);
        
        private final int code;
        
        GamepadAxis(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return code;
        }
    }
    
    public static GamepadManager getInstance() {
        if (instance == null) {
            instance = new GamepadManager();
        }
        return instance;
    }
    
    private GamepadManager() {}
    
    public void addListener(GamepadListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(GamepadListener listener) {
        listeners.remove(listener);
    }
    
    public void startPolling() {
        if (isPolling.compareAndSet(false, true)) {
            pollThread = new Thread(this::pollLoop, "GamepadPolling");
            pollThread.setDaemon(true);
            pollThread.start();
            logger.info("Gamepad polling started");
        }
    }
    
    public void stopPolling() {
        if (isPolling.compareAndSet(true, false)) {
            if (pollThread != null) {
                pollThread.interrupt();
            }
            logger.info("Gamepad polling stopped");
        }
    }
    
    private void pollLoop() {
        try {
            logger.info("Gamepad polling not available - jinput requires native libraries");
            isPolling.set(false);
        } catch (Exception e) {
            logger.error("Failed to initialize gamepad", e);
            isPolling.set(false);
        }
    }
    
    private GamepadButton getButton(int index) {
        for (GamepadButton button : GamepadButton.values()) {
            if (button.getCode() == index) {
                return button;
            }
        }
        return null;
    }
    
    private void notifyButtonPressed(GamepadButton button) {
        for (GamepadListener listener : listeners) {
            try {
                listener.onButtonPressed(button);
            } catch (Exception e) {
                logger.error("Error in button pressed callback", e);
            }
        }
    }
    
    private void notifyButtonReleased(GamepadButton button) {
        for (GamepadListener listener : listeners) {
            try {
                listener.onButtonReleased(button);
            } catch (Exception e) {
                logger.error("Error in button released callback", e);
            }
        }
    }
    
    private void notifyAxisMoved(GamepadAxis axis, float value) {
        for (GamepadListener listener : listeners) {
            try {
                listener.onAxisMoved(axis, value);
            } catch (Exception e) {
                logger.error("Error in axis moved callback", e);
            }
        }
    }
}
