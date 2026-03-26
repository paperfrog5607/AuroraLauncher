package org.aurora.launcher.mod.version;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionComparatorTest {

    @Test
    void shouldCompareVersions() {
        VersionComparator comparator = new VersionComparator();
        
        assertTrue(comparator.compare("1.0.0", "2.0.0") < 0);
        assertTrue(comparator.compare("2.0.0", "1.0.0") > 0);
        assertEquals(0, comparator.compare("1.0.0", "1.0.0"));
    }

    @Test
    void shouldCompareDifferentLengths() {
        VersionComparator comparator = new VersionComparator();
        
        assertTrue(comparator.compare("1.0", "1.0.1") < 0);
        assertTrue(comparator.compare("1.0.1", "1.0") > 0);
        assertEquals(0, comparator.compare("1.0.0", "1.0"));
    }

    @Test
    void shouldCompareWithSuffixes() {
        VersionComparator comparator = new VersionComparator();
        
        assertTrue(comparator.compare("1.0.0", "1.0.1") < 0);
        assertEquals(0, comparator.compare("1.0.0", "1.0.0"));
    }
}

class VersionRangeTest {

    @Test
    void shouldMatchExactVersion() {
        VersionRange range = new VersionRange();
        range.setMinVersion("1.0.0");
        range.setMaxVersion("1.0.0");
        
        assertTrue(range.matches("1.0.0"));
        assertFalse(range.matches("1.0.1"));
    }

    @Test
    void shouldMatchRange() {
        VersionRange range = new VersionRange();
        range.setMinVersion("1.0.0");
        range.setMaxVersion("2.0.0");
        
        assertTrue(range.matches("1.5.0"));
        assertTrue(range.matches("1.0.0"));
        assertTrue(range.matches("2.0.0"));
        assertFalse(range.matches("0.9.0"));
        assertFalse(range.matches("2.1.0"));
    }

    @Test
    void shouldParseRange() {
        VersionRange range = VersionRange.parse(">=1.0.0");
        
        assertEquals("1.0.0", range.getMinVersion());
        assertTrue(range.isIncludeMin());
    }
}