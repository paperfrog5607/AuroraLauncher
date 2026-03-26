package org.aurora.launcher.dev.datapack.function;

public class FunctionBuilder {
    private String namespace;
    private String name;
    private StringBuilder commands;

    public FunctionBuilder() {
        this.commands = new StringBuilder();
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

    public FunctionBuilder command(String command) {
        if (commands.length() > 0) {
            commands.append("\n");
        }
        commands.append(command);
        return this;
    }

    public FunctionBuilder comment(String comment) {
        commands.append("# ").append(comment).append("\n");
        return this;
    }

    public String build() {
        return commands.toString();
    }

    public McFunction toMcFunction() {
        McFunction function = new McFunction();
        function.setNamespace(namespace);
        function.setName(name);
        function.setCommands(build());
        return function;
    }
}