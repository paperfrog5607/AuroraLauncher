package org.aurora.launcher.dev.kubejs;

import org.aurora.launcher.dev.template.KubeJsCategory;
import org.aurora.launcher.dev.template.KubeJsTemplate;
import org.aurora.launcher.dev.template.TemplateManager;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class KubeJsService {
    private final TemplateManager templateManager;

    public KubeJsService(TemplateManager templateManager) {
        this.templateManager = templateManager != null ? templateManager : new TemplateManager();
    }

    public List<KubeJsTemplate> getTemplates(KubeJsCategory category) {
        return templateManager.getByCategory(category);
    }

    public String generate(KubeJsTemplate template, Map<String, Object> params) {
        return template.render(params);
    }

    public Path export(String code, String fileName, Path targetDir) throws IOException {
        Files.createDirectories(targetDir);
        Path filePath = targetDir.resolve(fileName);
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8)) {
            writer.write(code);
        }
        return filePath;
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
    }
}