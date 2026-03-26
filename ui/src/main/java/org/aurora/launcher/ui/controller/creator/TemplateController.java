package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    @FXML
    private TextField templateNameField;

    @FXML
    private ListView<String> templateList;

    @Override
    protected void onInitialize() {
    }

    @FXML
    private void onNewTemplate() {
        String name = templateNameField.getText();
        if (name != null && !name.trim().isEmpty()) {
            logger.info("Creating new template: {}", name);
        }
    }

    @FXML
    private void onEditTemplate() {
        logger.info("Editing template");
    }

    @FXML
    private void onCopyTemplate() {
        logger.info("Copying template");
    }

    @FXML
    private void onDeleteTemplate() {
        logger.info("Deleting template");
    }
}
