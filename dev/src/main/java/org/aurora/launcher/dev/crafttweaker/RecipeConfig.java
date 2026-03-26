package org.aurora.launcher.dev.crafttweaker;

import org.aurora.launcher.dev.kubejs.recipe.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class RecipeConfig {
    private RecipeType type;
    private String id;
    private String output;
    private List<String> inputs;
    private String[] pattern;
    private float xp;
    private int time;

    public RecipeConfig() {
        this.inputs = new ArrayList<>();
        this.time = 200;
    }

    public RecipeType getType() {
        return type;
    }

    public void setType(RecipeType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public void addInput(String input) {
        this.inputs.add(input);
    }

    public String[] getPattern() {
        return pattern;
    }

    public void setPattern(String[] pattern) {
        this.pattern = pattern;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}