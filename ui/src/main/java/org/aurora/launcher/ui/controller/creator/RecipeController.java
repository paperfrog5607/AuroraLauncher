package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @FXML
    private ComboBox<String> recipeTypeBox;

    @FXML
    private TableView<String> recipeTable;

    @FXML
    private TextField resultField;

    @FXML
    private TextField countField;

    @Override
    protected void onInitialize() {
        recipeTypeBox.getItems().addAll(
            "工作台 (crafting)",
            "熔炉 (furnace)",
            "高炉 (blast_furnace)",
            "烟熏炉 (smoker)",
            "酿造 (brewing)",
            "切石 (stonecutting)",
            "锻造 (smithing)"
        );
    }

    @FXML
    private void onAddRecipe() {
        logger.info("Adding new recipe");
    }

    @FXML
    private void onSaveRecipe() {
        logger.info("Saving recipe");
    }

    @FXML
    private void onClearRecipe() {
        logger.info("Clearing recipe");
    }
}
