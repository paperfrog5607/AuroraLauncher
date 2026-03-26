package org.aurora.launcher.mod.dependency;

import org.aurora.launcher.mod.scanner.ModInfo;
import org.aurora.launcher.mod.scanner.Dependency;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DependencyAnalyzerTest {

    @Test
    void shouldCreateAnalyzer() {
        DependencyAnalyzer analyzer = new DependencyAnalyzer();
        
        assertNotNull(analyzer);
    }

    @Test
    void shouldAnalyzeEmptyModList() throws Exception {
        DependencyAnalyzer analyzer = new DependencyAnalyzer();
        
        DependencyTree tree = analyzer.analyze(new ArrayList<>()).get();
        
        assertNotNull(tree);
        assertFalse(tree.hasMissingDependencies());
        assertFalse(tree.hasConflicts());
    }

    @Test
    void shouldDetectMissingDependency() throws Exception {
        DependencyAnalyzer analyzer = new DependencyAnalyzer();
        
        ModInfo mod = new ModInfo();
        mod.setId("test-mod");
        Dependency dep = new Dependency("missing-mod", Dependency.DependencyType.DEPENDS);
        mod.addDependency(dep);
        
        DependencyTree tree = analyzer.analyze(Collections.singletonList(mod)).get();
        
        assertTrue(tree.hasMissingDependencies());
        assertEquals(1, tree.getMissingDependencies().size());
    }

    @Test
    void shouldDetectConflicts() {
        DependencyAnalyzer analyzer = new DependencyAnalyzer();
        
        ModInfo mod1 = new ModInfo();
        mod1.setId("mod1");
        Dependency breaks = new Dependency("mod2", Dependency.DependencyType.BREAKS);
        mod1.addDependency(breaks);
        
        ModInfo mod2 = new ModInfo();
        mod2.setId("mod2");
        
        List<ConflictInfo> conflicts = analyzer.getConflicts(Arrays.asList(mod1, mod2));
        
        assertFalse(conflicts.isEmpty());
    }
}