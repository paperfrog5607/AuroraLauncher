package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LootTableController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(LootTableController.class);

    @FXML
    private ComboBox<String> lootTypeBox;

    @FXML
    private TableView<String> lootTable;

    @FXML
    private TextField poolNameField;

    @FXML
    private TextField minCountField;

    @FXML
    private TextField maxCountField;

    @Override
    protected void onInitialize() {
        lootTypeBox.getItems().addAll(
            "方块掉落 (blocks)",
            "实体掉落 (entities)",
            "钓鱼 (fishing)",
            "宝箱 (chest)"
        );
    }

    @FXML
    private void onAddLootTable() {
        logger.info("Adding new loot table");
    }

    @FXML
    private void onSaveLootTable() {
        logger.info("Saving loot table");
    }

    @FXML
    private void onClearLootTable() {
        logger.info("Clearing loot table");
    }
}
