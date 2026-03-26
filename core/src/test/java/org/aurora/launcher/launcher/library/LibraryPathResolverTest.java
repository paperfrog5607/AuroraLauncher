package org.aurora.launcher.launcher.library;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibraryPathResolverTest {

    @Test
    void resolve_withValidName_returnsCorrectPath() {
        LibraryPathResolver resolver = new LibraryPathResolver();
        
        String path = resolver.resolve("com.mojang:authlib:3.11.50");
        
        assertEquals("com/mojang/authlib/3.11.50/authlib-3.11.50.jar", path);
    }

    @Test
    void resolve_withGroupIdContainingDots_convertsToPath() {
        LibraryPathResolver resolver = new LibraryPathResolver();
        
        String path = resolver.resolve("org.apache.commons:commons-lang3:3.12.0");
        
        assertEquals("org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar", path);
    }

    @Test
    void resolve_withInvalidName_returnsNull() {
        LibraryPathResolver resolver = new LibraryPathResolver();
        
        String path = resolver.resolve("invalid-name");
        
        assertNull(path);
    }
}