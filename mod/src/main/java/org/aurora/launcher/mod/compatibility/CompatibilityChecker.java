package org.aurora.launcher.mod.compatibility;

import org.aurora.launcher.mod.scanner.ModInfo;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityChecker {
    
    public CompatibilityReport check(List<ModInfo> mods, String mcVersion, String loader) {
        CompatibilityReport report = new CompatibilityReport();
        report.setMcVersion(mcVersion);
        report.setLoader(loader);
        
        for (ModInfo mod : mods) {
            checkModCompatibility(mod, mcVersion, loader, report);
        }
        
        return report;
    }
    
    private void checkModCompatibility(ModInfo mod, String mcVersion, String loader, CompatibilityReport report) {
        if (mod.getLoader() != null) {
            String modLoader = mod.getLoader().name().toLowerCase();
            if (!modLoader.equals(loader.toLowerCase())) {
                report.addIssue(new CompatibilityIssue(
                        mod.getId(),
                        "Loader mismatch: mod requires " + modLoader + " but instance uses " + loader,
                        CompatibilityIssue.IssueType.LOADER_MISMATCH
                ));
            }
        }
        
        if (mod.getMcVersion() != null && mcVersion != null) {
            if (!mod.getMcVersion().equals(mcVersion)) {
                report.addIssue(new CompatibilityIssue(
                        mod.getId(),
                        "MC version mismatch: mod requires " + mod.getMcVersion() + " but instance uses " + mcVersion,
                        CompatibilityIssue.IssueType.VERSION_MISMATCH
                ));
            }
        }
    }
    
    public boolean isServerSideMod(ModInfo mod) {
        if (mod.getId() == null) return false;
        
        String id = mod.getId().toLowerCase();
        String name = mod.getName() != null ? mod.getName().toLowerCase() : "";
        
        String[] serverKeywords = {"serversided", "server-side", "serveronly", "server_only"};
        for (String keyword : serverKeywords) {
            if (id.contains(keyword) || name.contains(keyword)) {
                return true;
            }
        }
        
        String[] clientKeywords = {"client", "gui", "hud", "renderer", "shader", "visual"};
        for (String keyword : clientKeywords) {
            if (id.contains(keyword) || name.contains(keyword)) {
                return false;
            }
        }
        
        return false;
    }
}