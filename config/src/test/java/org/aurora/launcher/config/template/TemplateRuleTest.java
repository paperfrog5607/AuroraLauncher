package org.aurora.launcher.config.template;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TemplateRuleTest {
    
    @Test
    void constructor_keyValue_setsFields() {
        TemplateRule rule = new TemplateRule("key", "value");
        
        assertEquals("key", rule.getKey());
        assertEquals("value", rule.getValue());
        assertEquals(TemplateRule.RuleCondition.ALWAYS, rule.getCondition());
    }
    
    @Test
    void constructor_withCondition_setsCondition() {
        TemplateRule rule = new TemplateRule("key", "value", TemplateRule.RuleCondition.IF_MISSING);
        
        assertEquals(TemplateRule.RuleCondition.IF_MISSING, rule.getCondition());
    }
    
    @Test
    void setters_updateFields() {
        TemplateRule rule = new TemplateRule();
        rule.setKey("newKey");
        rule.setValue(123);
        rule.setCondition(TemplateRule.RuleCondition.IF_EMPTY);
        rule.setComment("test comment");
        
        assertEquals("newKey", rule.getKey());
        assertEquals(123, rule.getValue());
        assertEquals(TemplateRule.RuleCondition.IF_EMPTY, rule.getCondition());
        assertEquals("test comment", rule.getComment());
    }
}