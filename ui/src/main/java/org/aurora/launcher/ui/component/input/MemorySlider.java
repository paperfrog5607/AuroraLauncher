package org.aurora.launcher.ui.component.input;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MemorySlider extends VBox {
    
    @FXML
    private Slider minSlider;
    @FXML
    private Slider maxSlider;
    @FXML
    private Label minLabel;
    @FXML
    private Label maxLabel;
    @FXML
    private ComboBox<String> presetBox;
    
    private final long systemMemoryMB;
    private final LongProperty minValue = new SimpleLongProperty(2048);
    private final LongProperty maxValue = new SimpleLongProperty(4096);
    
    public MemorySlider() {
        this.systemMemoryMB = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        loadFxml();
        initialize();
    }
    
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/MemorySlider.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MemorySlider.fxml", e);
        }
    }
    
    private void initialize() {
        long maxAllowed = (long) (systemMemoryMB * 0.8);
        
        minSlider.setMin(512);
        minSlider.setMax(maxAllowed);
        minSlider.setValue(2048);
        
        maxSlider.setMin(512);
        maxSlider.setMax(maxAllowed);
        maxSlider.setValue(4096);
        
        minLabel.setText(formatMemory(2048));
        maxLabel.setText(formatMemory(4096));
        
        minSlider.valueProperty().addListener((obs, old, newVal) -> {
            long value = newVal.longValue();
            minLabel.setText(formatMemory(value));
            minValue.set(value);
            if (value > maxSlider.getValue()) {
                maxSlider.setValue(value);
            }
        });
        
        maxSlider.valueProperty().addListener((obs, old, newVal) -> {
            long value = newVal.longValue();
            maxLabel.setText(formatMemory(value));
            maxValue.set(value);
            if (value < minSlider.getValue()) {
                minSlider.setValue(value);
            }
        });
        
        presetBox.getItems().addAll(
            "自动",
            "低 (1-2 GB)",
            "标准 (2-4 GB)",
            "高 (4-8 GB)"
        );
        presetBox.getSelectionModel().selectFirst();
        
        presetBox.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                applyPreset(newVal);
            }
        });
    }
    
    private void applyPreset(String preset) {
        long[] values;
        if (preset.startsWith("低")) {
            values = new long[]{1024, 2048};
        } else if (preset.startsWith("标准")) {
            values = new long[]{2048, 4096};
        } else if (preset.startsWith("高")) {
            values = new long[]{4096, 8192};
        } else {
            values = calculateAutoMemory();
        }
        minSlider.setValue(values[0]);
        maxSlider.setValue(values[1]);
    }
    
    private long[] calculateAutoMemory() {
        long half = systemMemoryMB / 2;
        return new long[]{Math.max(1024, half / 2), Math.min(half, 8192)};
    }
    
    public long getMinValue() {
        return (long) minSlider.getValue() * 1024 * 1024;
    }
    
    public long getValue() {
        return (long) maxSlider.getValue() * 1024 * 1024;
    }
    
    public void setValue(long bytes) {
        maxSlider.setValue(bytes / 1024.0 / 1024.0);
    }
    
    public void setMinValue(long bytes) {
        minSlider.setValue(bytes / 1024.0 / 1024.0);
    }
    
    public LongProperty minValueProperty() {
        return minValue;
    }
    
    public LongProperty maxValueProperty() {
        return maxValue;
    }
    
    private String formatMemory(long mb) {
        if (mb >= 1024) {
            return String.format("%.1f GB", mb / 1024.0);
        }
        return mb + " MB";
    }
}