package org.aurora.launcher.dev.datapack.function;

public class McFunction {
    private String namespace;
    private String name;
    private String commands;

    public McFunction() {
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public String getFullName() {
        return namespace + ":" + name;
    }
}