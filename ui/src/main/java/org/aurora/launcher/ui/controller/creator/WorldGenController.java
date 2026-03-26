package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldGenController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(WorldGenController.class);

    @FXML
    private ComboBox<String> worldGenTypeBox;

    @FXML
    private TreeView<String> worldGenTree;

    @FXML
    private Label currentFileLabel;

    @FXML
    private TextArea jsonEditor;

    @Override
    protected void onInitialize() {
        worldGenTypeBox.getItems().addAll(
            "维度 (dimensions)",
            "生物群系 (biomes)",
            "特征 (features)",
            "结构 (structures)",
            "噪声设置 (noise_settings)"
        );
    }

    @FXML
    private void onNewWorldGen() {
        logger.info("Creating new worldgen file");
    }

    @FXML
    private void onSave() {
        logger.info("Saving worldgen file");
    }

    @FXML
    private void onFormat() {
        logger.info("Formatting JSON");
    }
}
