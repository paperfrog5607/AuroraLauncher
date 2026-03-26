package org.aurora.launcher.config.compare;

import org.aurora.launcher.config.editor.ConfigDiffNode;
import java.util.*;

public class ConfigDiffReporter {
    
    private boolean includeComments = false;
    private boolean includeSections = true;
    private Set<ConfigDiff.DiffType> filters = new HashSet<>();
    
    public String generateTextReport(ConfigComparison comparison) {
        StringBuilder sb = new StringBuilder();
        sb.append("Config Comparison Report\n");
        sb.append("========================\n\n");
        sb.append("Summary:\n");
        sb.append("  Added: ").append(comparison.getAddedCount()).append("\n");
        sb.append("  Removed: ").append(comparison.getRemovedCount()).append("\n");
        sb.append("  Modified: ").append(comparison.getModifiedCount()).append("\n");
        sb.append("  Total: ").append(comparison.getTotalDifferences()).append("\n\n");
        
        if (comparison.hasDifferences()) {
            sb.append("Differences:\n");
            sb.append("------------\n");
            for (ConfigDiff diff : comparison.getDiffs()) {
                if (shouldInclude(diff)) {
                    sb.append(formatDiff(diff));
                }
            }
        } else {
            sb.append("No differences found.\n");
        }
        
        return sb.toString();
    }
    
    public String generateHtmlReport(ConfigComparison comparison) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html><head><title>Config Comparison Report</title>\n");
        sb.append("<style>\n");
        sb.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        sb.append(".summary { background: #f5f5f5; padding: 15px; border-radius: 5px; }\n");
        sb.append(".added { color: green; }\n");
        sb.append(".removed { color: red; }\n");
        sb.append(".modified { color: orange; }\n");
        sb.append(".diff-item { margin: 10px 0; padding: 10px; border-left: 3px solid #ccc; }\n");
        sb.append("</style></head><body>\n");
        
        sb.append("<h1>Config Comparison Report</h1>\n");
        sb.append("<div class=\"summary\">\n");
        sb.append("<p>Added: <span class=\"added\">").append(comparison.getAddedCount()).append("</span></p>\n");
        sb.append("<p>Removed: <span class=\"removed\">").append(comparison.getRemovedCount()).append("</span></p>\n");
        sb.append("<p>Modified: <span class=\"modified\">").append(comparison.getModifiedCount()).append("</span></p>\n");
        sb.append("</div>\n");
        
        sb.append("<h2>Differences</h2>\n");
        for (ConfigDiff diff : comparison.getDiffs()) {
            if (shouldInclude(diff)) {
                sb.append("<div class=\"diff-item ").append(diff.getType().name().toLowerCase()).append("\">\n");
                sb.append("<strong>").append(diff.getType()).append("</strong>: ").append(diff.getKey()).append("<br>\n");
                if (diff.getOldValue() != null) {
                    sb.append("Old: ").append(diff.getOldValue()).append("<br>\n");
                }
                if (diff.getNewValue() != null) {
                    sb.append("New: ").append(diff.getNewValue()).append("\n");
                }
                sb.append("</div>\n");
            }
        }
        
        sb.append("</body></html>");
        return sb.toString();
    }
    
    public String generateMarkdownReport(ConfigComparison comparison) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Config Comparison Report\n\n");
        sb.append("## Summary\n\n");
        sb.append("| Type | Count |\n");
        sb.append("|------|-------|\n");
        sb.append("| Added | ").append(comparison.getAddedCount()).append(" |\n");
        sb.append("| Removed | ").append(comparison.getRemovedCount()).append(" |\n");
        sb.append("| Modified | ").append(comparison.getModifiedCount()).append(" |\n");
        sb.append("| **Total** | **").append(comparison.getTotalDifferences()).append("** |\n\n");
        
        if (comparison.hasDifferences()) {
            sb.append("## Differences\n\n");
            for (ConfigDiff diff : comparison.getDiffs()) {
                if (shouldInclude(diff)) {
                    sb.append("### ").append(diff.getKey()).append("\n\n");
                    sb.append("- **Type**: ").append(diff.getType()).append("\n");
                    if (diff.getOldValue() != null) {
                        sb.append("- **Old**: ").append(diff.getOldValue()).append("\n");
                    }
                    if (diff.getNewValue() != null) {
                        sb.append("- **New**: ").append(diff.getNewValue()).append("\n");
                    }
                    sb.append("\n");
                }
            }
        }
        
        return sb.toString();
    }
    
    public void setIncludeComments(boolean include) {
        this.includeComments = include;
    }
    
    public void setIncludeSections(boolean include) {
        this.includeSections = include;
    }
    
    public void setFilter(ConfigDiff.DiffType... types) {
        filters.clear();
        for (ConfigDiff.DiffType type : types) {
            filters.add(type);
        }
    }
    
    private boolean shouldInclude(ConfigDiff diff) {
        if (filters.isEmpty()) {
            return true;
        }
        return filters.contains(diff.getType());
    }
    
    private String formatDiff(ConfigDiff diff) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(diff.getType()).append("] ").append(diff.getKey()).append("\n");
        if (diff.getOldValue() != null) {
            sb.append("  Old: ").append(diff.getOldValue()).append("\n");
        }
        if (diff.getNewValue() != null) {
            sb.append("  New: ").append(diff.getNewValue()).append("\n");
        }
        if (includeComments && diff.getOldComment() != null) {
            sb.append("  Old Comment: ").append(diff.getOldComment()).append("\n");
        }
        if (includeComments && diff.getNewComment() != null) {
            sb.append("  New Comment: ").append(diff.getNewComment()).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}