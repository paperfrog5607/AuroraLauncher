package org.aurora.launcher.mod.version;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {
    
    @Override
    public int compare(String v1, String v2) {
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return -1;
        if (v2 == null) return 1;
        
        String[] parts1 = v1.split("[._-]");
        String[] parts2 = v2.split("[._-]");
        
        int length = Math.max(parts1.length, parts2.length);
        
        for (int i = 0; i < length; i++) {
            String p1 = i < parts1.length ? parts1[i] : "0";
            String p2 = i < parts2.length ? parts2[i] : "0";
            
            int cmp = compareParts(p1, p2);
            if (cmp != 0) {
                return cmp;
            }
        }
        
        return 0;
    }
    
    private int compareParts(String p1, String p2) {
        boolean n1 = isNumeric(p1);
        boolean n2 = isNumeric(p2);
        
        if (n1 && n2) {
            try {
                long num1 = Long.parseLong(p1);
                long num2 = Long.parseLong(p2);
                return Long.compare(num1, num2);
            } catch (NumberFormatException e) {
                return p1.compareTo(p2);
            }
        }
        
        if (n1) return -1;
        if (n2) return 1;
        
        return p1.compareTo(p2);
    }
    
    private boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}