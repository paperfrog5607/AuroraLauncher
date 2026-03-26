package org.aurora.launcher.config.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

class ConfigParserFactoryTest {
    
    @BeforeAll
    static void setUp() {
        ConfigParserFactory.registerParser(new PropertiesParser());
        ConfigParserFactory.registerParser(new JsonParser());
        ConfigParserFactory.registerParser(new TomlParser());
        ConfigParserFactory.registerParser(new YamlParser());
    }
    
    @Test
    void getParser_properties_returnsPropertiesParser() {
        ConfigParser parser = ConfigParserFactory.getParser("properties");
        
        assertTrue(parser instanceof PropertiesParser);
    }
    
    @Test
    void getParser_json_returnsJsonParser() {
        ConfigParser parser = ConfigParserFactory.getParser("json");
        
        assertTrue(parser instanceof JsonParser);
    }
    
    @Test
    void getParser_toml_returnsTomlParser() {
        ConfigParser parser = ConfigParserFactory.getParser("toml");
        
        assertTrue(parser instanceof TomlParser);
    }
    
    @Test
    void getParser_yaml_returnsYamlParser() {
        ConfigParser parser = ConfigParserFactory.getParser("yaml");
        
        assertTrue(parser instanceof YamlParser);
    }
    
    @Test
    void getParser_yml_returnsYamlParser() {
        ConfigParser parser = ConfigParserFactory.getParser("yml");
        
        assertTrue(parser instanceof YamlParser);
    }
    
    @Test
    void getParserByFile_validFile_returnsCorrectParser() {
        ConfigParser parser = ConfigParserFactory.getParserByFile(Paths.get("config.json"));
        
        assertTrue(parser instanceof JsonParser);
    }
    
    @Test
    void getParserByFile_propertiesFile_returnsPropertiesParser() {
        ConfigParser parser = ConfigParserFactory.getParserByFile(Paths.get("server.properties"));
        
        assertTrue(parser instanceof PropertiesParser);
    }
    
    @Test
    void isSupported_supportedExtension_returnsTrue() {
        assertTrue(ConfigParserFactory.isSupported("json"));
        assertTrue(ConfigParserFactory.isSupported("properties"));
        assertTrue(ConfigParserFactory.isSupported("toml"));
        assertTrue(ConfigParserFactory.isSupported("yaml"));
        assertTrue(ConfigParserFactory.isSupported("yml"));
    }
    
    @Test
    void isSupported_unsupportedExtension_returnsFalse() {
        assertFalse(ConfigParserFactory.isSupported("txt"));
        assertFalse(ConfigParserFactory.isSupported("xml"));
    }
}