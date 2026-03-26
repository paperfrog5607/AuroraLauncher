package org.aurora.launcher.launcher.version;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VersionTypeTest {

    @Test
    void values_containsAllTypes() {
        VersionType[] types = VersionType.values();
        assertEquals(4, types.length);
    }

    @Test
    void valueOf_release() {
        assertEquals(VersionType.RELEASE, VersionType.valueOf("RELEASE"));
    }

    @Test
    void valueOf_snapshot() {
        assertEquals(VersionType.SNAPSHOT, VersionType.valueOf("SNAPSHOT"));
    }

    @Test
    void valueOf_old_alpha() {
        assertEquals(VersionType.OLD_ALPHA, VersionType.valueOf("OLD_ALPHA"));
    }

    @Test
    void valueOf_old_beta() {
        assertEquals(VersionType.OLD_BETA, VersionType.valueOf("OLD_BETA"));
    }

    @Test
    void fromString_handlesVariousCases() {
        assertEquals(VersionType.RELEASE, VersionType.fromString("release"));
        assertEquals(VersionType.SNAPSHOT, VersionType.fromString("snapshot"));
        assertEquals(VersionType.OLD_ALPHA, VersionType.fromString("old_alpha"));
        assertEquals(VersionType.OLD_BETA, VersionType.fromString("old_beta"));
    }
}