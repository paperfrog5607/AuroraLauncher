package org.aurora.launcher.dev.crafttweaker;

public class ZenScriptTemplate {
    private String id;
    private String name;
    private String description;
    private String template;

    public ZenScriptTemplate() {
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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String render(java.util.Map<String, Object> params) {
        if (template == null) return "";
        String result = template;
        if (params != null) {
            for (java.util.Map.Entry<String, Object> entry : params.entrySet()) {
                result = result.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
        }
        return result;
    }
}