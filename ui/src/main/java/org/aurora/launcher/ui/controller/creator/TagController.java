package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @FXML
    private ComboBox<String> tagTypeBox;

    @FXML
    private ListView<String> tagList;

    @FXML
    private ListView<String> tagValuesList;

    @FXML
    private TextField addValueField;

    @Override
    protected void onInitialize() {
        tagTypeBox.getItems().addAll(
            "方块标签 (blocks)",
            "物品标签 (items)",
            "实体标签 (entities)",
            "流体标签 (fluids)",
            "实体类型标签 (entity_types)"
        );
    }

    @FXML
    private void onNewTag() {
        logger.info("Creating new tag");
    }

    @FXML
    private void onAddValue() {
        logger.info("Adding tag value");
    }

    @FXML
    private void onRemoveValue() {
        logger.info("Removing tag value");
    }

    @FXML
    private void onSaveTag() {
        logger.info("Saving tag");
    }
}
