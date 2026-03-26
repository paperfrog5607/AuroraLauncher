package org.aurora.launcher.ai.translation;

public class LanguagePair {
    
    public static final LanguagePair EN_TO_ZH = new LanguagePair("en_us", "zh_cn");
    public static final LanguagePair ZH_TO_EN = new LanguagePair("zh_cn", "en_us");
    
    private final String source;
    private final String target;
    
    public LanguagePair(String source, String target) {
        this.source = source;
        this.target = target;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getTarget() {
        return target;
    }
    
    @Override
    public String toString() {
        return source + " -> " + target;
    }
}