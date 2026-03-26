package org.aurora.launcher.core.util;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilsTest {

    @Test
    void emptyIfNull_nullList_returnsEmptyList() {
        List<String> result = CollectionUtils.emptyIfNull(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void emptyIfNull_nonNullList_returnsSameList() {
        List<String> list = Arrays.asList("a", "b");
        List<String> result = CollectionUtils.emptyIfNull(list);
        assertSame(list, result);
    }

    @Test
    void isNullOrEmpty_nullCollection_returnsTrue() {
        assertTrue(CollectionUtils.isNullOrEmpty(null));
    }

    @Test
    void isNullOrEmpty_emptyCollection_returnsTrue() {
        assertTrue(CollectionUtils.isNullOrEmpty(Collections.emptyList()));
    }

    @Test
    void isNullOrEmpty_nonEmptyCollection_returnsFalse() {
        assertFalse(CollectionUtils.isNullOrEmpty(Arrays.asList("a", "b")));
    }
}