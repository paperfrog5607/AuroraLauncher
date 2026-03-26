package org.aurora.launcher.api.mojang;

import java.util.ArrayList;
import java.util.List;

public class MojangProfile {
    
    private String id;
    private String name;
    private List<Property> properties;
    
    public MojangProfile() {
        this.properties = new ArrayList<>();
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
    
    public List<Property> getProperties() {
        return properties;
    }
    
    public void setProperties(List<Property> properties) {
        this.properties = properties != null ? properties : new ArrayList<>();
    }
    
    public static class Property {
        private String name;
        private String value;
        private String signature;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
        
        public String getSignature() {
            return signature;
        }
        
        public void setSignature(String signature) {
            this.signature = signature;
        }
    }
}