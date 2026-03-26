package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import org.aurora.launcher.ui.controller.BaseController;
import org.aurora.launcher.ui.service.ModDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubeJSController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(KubeJSController.class);

    @FXML
    private TreeView<String> fileTree;

    @FXML
    private TabPane editorTabs;

    @FXML
    private TextArea codeEditor;

    private ModDetectionService modDetection;

    @Override
    protected void onInitialize() {
        modDetection = ModDetectionService.getInstance();
        checkKubeJSInstalled();
    }

    private void checkKubeJSInstalled() {
        if (!modDetection.isModInstalled("kubejs")) {
            logger.info("KubeJS not installed, visualization features disabled");
        }
    }

    @FXML
    private void onNewFile() {
        logger.info("Creating new KubeJS file");
    }

    @FXML
    private void onDeleteFile() {
        logger.info("Deleting KubeJS file");
    }

    @FXML
    private void insertShapedRecipe() {
        String template = """
            ServerEvents.recipes(event => {
                event.shaped('minecraft:diamond', [
                    'AAA',
                    'BBB',
                    'CCC'
                ], {
                    A: 'minecraft:coal',
                    B: 'minecraft:iron_ingot',
                    C: 'minecraft:gold_ingot'
                })
            })
            """;
        insertTemplate(template);
    }

    @FXML
    private void insertShapelessRecipe() {
        String template = """
            ServerEvents.recipes(event => {
                event.shapeless('minecraft:coal', ['minecraft:charcoal'], 'minecraft:oak_planks')
            })
            """;
        insertTemplate(template);
    }

    @FXML
    private void insertStonecutting() {
        String template = """
            ServerEvents.recipes(event => {
                event.stonecutting('minecraft:stone', 'minecraft:cobblestone')
            })
            """;
        insertTemplate(template);
    }

    @FXML
    private void insertSmithing() {
        String template = """
            ServerEvents.recipes(event => {
                event.smithing('minecraft:netherite_helmet', 'minecraft:diamond_helmet', 'minecraft:netherite_ingot')
            })
            """;
        insertTemplate(template);
    }

    @FXML
    private void insertServerEventsRecipes() {
        insertTemplate("ServerEvents.recipes(event => {\n    \n})");
    }

    @FXML
    private void insertServerEventsTick() {
        insertTemplate("ServerEvents.tick(event => {\n    \n})");
    }

    @FXML
    private void insertPlayerEventsLoggedIn() {
        insertTemplate("PlayerEvents.loggedIn(event => {\n    \n})");
    }

    private void insertTemplate(String template) {
        int caret = codeEditor.getCaretPosition();
        codeEditor.insertText(caret, template);
    }
}
