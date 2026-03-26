package org.aurora.launcher.config.template;

public class TemplateRule {
    
    private String key;
    private RuleCondition condition;
    private Object value;
    private String comment;
    
    public enum RuleCondition {
        ALWAYS, IF_MISSING, IF_EMPTY, CUSTOM
    }
    
    public TemplateRule() {
        this.condition = RuleCondition.ALWAYS;
    }
    
    public TemplateRule(String key, Object value) {
        this.key = key;
        this.value = value;
        this.condition = RuleCondition.ALWAYS;
    }
    
    public TemplateRule(String key, Object value, RuleCondition condition) {
        this.key = key;
        this.value = value;
        this.condition = condition;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public RuleCondition getCondition() {
        return condition;
    }
    
    public void setCondition(RuleCondition condition) {
        this.condition = condition;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}