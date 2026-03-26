package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StructureController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(StructureController.class);

    @FXML
    private TreeView<String> structureTree;

    @FXML
    private Label currentStructureLabel;

    @FXML
    private TextArea nbtEditor;

    @Override
    protected void onInitialize() {
    }

    @FXML
    private void onNewStructure() {
        logger.info("Creating new structure");
    }

    @FXML
    private void onImportStructure() {
        logger.info("Importing structure");
    }

    @FXML
    private void onSave() {
        logger.info("Saving structure");
    }

    @FXML
    private void onExport() {
        logger.info("Exporting structure");
    }
}
