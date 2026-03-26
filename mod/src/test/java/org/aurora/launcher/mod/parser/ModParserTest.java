package org.aurora.launcher.mod.parser;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FabricModParserTest {

    @Test
    void shouldCreateParser() {
        FabricModParser parser = new FabricModParser();
        
        assertNotNull(parser);
    }

    @Test
    void shouldRejectNonJarFiles() {
        FabricModParser parser = new FabricModParser();
        
        assertFalse(parser.canParse(Paths.get("test.txt")));
        assertFalse(parser.canParse(Paths.get("test.zip")));
    }
}

class ForgeModParserTest {

    @Test
    void shouldCreateParser() {
        ForgeModParser parser = new ForgeModParser();
        
        assertNotNull(parser);
    }

    @Test
    void shouldRejectNonJarFiles() {
        ForgeModParser parser = new ForgeModParser();
        
        assertFalse(parser.canParse(Paths.get("test.txt")));
        assertFalse(parser.canParse(Paths.get("test.zip")));
    }
}