package org.aurora.launcher.dev.template;

import java.util.*;
import java.util.stream.Collectors;

public class TemplateManager {
    private final Map<String, List<KubeJsTemplate>> templates;

    public TemplateManager() {
        this.templates = new HashMap<>();
    }

    public void addTemplate(KubeJsTemplate template) {
        String categoryKey = template.getCategory() != null ? template.getCategory().name() : "OTHER";
        templates.computeIfAbsent(categoryKey, k -> new ArrayList<>()).add(template);
    }

    public Optional<KubeJsTemplate> getById(String id) {
        return templates.values().stream()
                .flatMap(List::stream)
                .filter(t -> t.getId() != null && t.getId().equals(id))
                .findFirst();
    }

    public List<KubeJsTemplate> getByCategory(KubeJsCategory category) {
        return templates.getOrDefault(category.name(), Collections.emptyList());
    }

    public void removeTemplate(String id) {
        templates.values().forEach(list -> 
            list.removeIf(t -> t.getId() != null && t.getId().equals(id))
        );
    }

    public List<KubeJsTemplate> getAllTemplates() {
        return templates.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void clear() {
        templates.clear();
    }
}