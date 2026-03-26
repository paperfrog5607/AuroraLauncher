package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CTScriptController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CTScriptController.class);

    @FXML
    private TreeView<String> fileTree;

    @FXML
    private TabPane editorTabs;

    @FXML
    private TextArea codeEditor;

    @Override
    protected void onInitialize() {
    }

    @FXML
    private void onNewFile() {
        logger.info("Creating new CraftTweaker file");
    }

    @FXML
    private void onDeleteFile() {
        logger.info("Deleting CraftTweaker file");
    }

    @FXML
    private void insertShapedRecipe() {
        String template = """
            recipes.addShaped("name", <item:minecraft:diamond>, [
                [<item:minecraft:coal>, <item:minecraft:coal>, <item:minecraft:coal>],
                [<item:minecraft:iron_ingot>, <item:minecraft:iron_ingot>, <item:minecraft:iron_ingot>],
                [<item:minecraft:gold_ingot>, <item:minecraft:gold_ingot>, <item:minecraft:gold_ingot>]
            ]);
            """;
        insertTemplate(template);
    }

    @FXML
    private void insertShapelessRecipe() {
        String template = """
            recipes.addShapeless("name", <item:minecraft:coal> * 9, [<item:minecraft:charcoal>]);
            """;
        insertTemplate(template);
    }

    @FXML
    private void insertFurnaceRecipe() {
        String template = """
            furnace.addRecipe("name", <item:minecraft:stone>, <item:minecraft:cobblestone>, 0.1);
            """;
        insertTemplate(template);
    }

    @FXML
    private void insertRemoveRecipe() {
        String template = """
            recipes.remove(<item:minecraft:diamond>);
            """;
        insertTemplate(template);
    }

    @FXML
    private void insertRemoveAllRecipes() {
        insertTemplate("recipes.removeAll();");
    }

    private void insertTemplate(String template) {
        int caret = codeEditor.getCaretPosition();
        codeEditor.insertText(caret, template);
    }
}
