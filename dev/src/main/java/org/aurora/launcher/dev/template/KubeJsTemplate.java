package org.aurora.launcher.dev.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KubeJsTemplate {
    private String id;
    private String name;
    private String description;
    private KubeJsCategory category;
    private String template;
    private List<TemplateParameter> parameters;
    private List<String> requiredMods;

    public KubeJsTemplate() {
        this.parameters = new ArrayList<>();
        this.requiredMods = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public KubeJsCategory getCategory() {
        return category;
    }

    public void setCategory(KubeJsCategory category) {
        this.category = category;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<TemplateParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<TemplateParameter> parameters) {
        this.parameters = parameters != null ? parameters : new ArrayList<>();
    }

    public List<String> getRequiredMods() {
        return requiredMods;
    }

    public void setRequiredMods(List<String> requiredMods) {
        this.requiredMods = requiredMods != null ? requiredMods : new ArrayList<>();
    }

    public String render(Map<String, Object> params) {
        if (template == null) {
            return "";
        }
        String result = template;
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                result = result.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
        }
        for (TemplateParameter param : parameters) {
            if (param.getDefaultValue() != null && !result.contains("${" + param.getName() + "}")) {
                continue;
            }
            Object value = params != null ? params.getOrDefault(param.getName(), param.getDefaultValue()) : param.getDefaultValue();
            if (value != null) {
                result = result.replace("${" + param.getName() + "}", String.valueOf(value));
            }
        }
        return result;
    }
}