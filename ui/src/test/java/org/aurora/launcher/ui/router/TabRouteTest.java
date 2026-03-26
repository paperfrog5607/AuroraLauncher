package org.aurora.launcher.ui.router;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TabRouteTest {

    @Test
    void testCreateTabRoute() {
        TabRoute route = new TabRoute("launch", "LaunchController", "/fxml/LaunchView.fxml");

        assertEquals("launch", route.getId());
        assertEquals("LaunchController", route.getControllerName());
        assertEquals("/fxml/LaunchView.fxml", route.getFxml());
        assertFalse(route.hasSubTabs());
    }

    @Test
    void testAddSubTabs() {
        TabRoute route = new TabRoute("download", "DownloadController", "/fxml/DownloadView.fxml")
            .subTab("version", "VersionController", "/fxml/download/VersionView.fxml")
            .subTab("mod", "ModController", "/fxml/download/ModView.fxml");

        assertTrue(route.hasSubTabs());
        assertEquals(2, route.getSubTabs().size());
        assertEquals("version", route.getDefaultSubTab());
    }

    @Test
    void testSubTabRoute() {
        SubTabRoute subRoute = new SubTabRoute("version", "VersionController", "/fxml/download/VersionView.fxml");

        assertEquals("version", subRoute.getId());
        assertEquals("VersionController", subRoute.getControllerName());
        assertEquals("/fxml/download/VersionView.fxml", subRoute.getFxml());
    }
}