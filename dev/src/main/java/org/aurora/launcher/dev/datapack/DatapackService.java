package org.aurora.launcher.dev.datapack;

import org.aurora.launcher.dev.template.TemplateManager;

import java.nio.file.Path;
import java.util.List;

public class DatapackService {
    private final TemplateManager templateManager;

    public DatapackService(TemplateManager templateManager) {
        this.templateManager = templateManager != null ? templateManager : new TemplateManager();
    }

    public DatapackBuilder createBuilder() {
        return new DatapackBuilder();
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
    }
}