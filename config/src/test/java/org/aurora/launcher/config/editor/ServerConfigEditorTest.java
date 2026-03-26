package org.aurora.launcher.config.editor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ServerConfigEditorTest {
    
    @TempDir
    Path tempDir;
    
    private Path instancePath;
    
    @BeforeEach
    void setUp() throws Exception {
        instancePath = tempDir.resolve("instance");
        Files.createDirectories(instancePath);
    }
    
    @Test
    void constructor_createsEditor() throws Exception {
        ServerConfigEditor editor = new ServerConfigEditor(instancePath);
        
        assertNotNull(editor);
    }
    
    @Test
    void setServerPort_getServerPort_works() throws Exception {
        ServerConfigEditor editor = new ServerConfigEditor(instancePath);
        
        editor.setServerPort(25566);
        
        assertEquals(25566, editor.getServerPort());
    }
    
    @Test
    void setMaxPlayers_getMaxPlayers_works() throws Exception {
        ServerConfigEditor editor = new ServerConfigEditor(instancePath);
        
        editor.setMaxPlayers(50);
        
        assertEquals(50, editor.getMaxPlayers());
    }
    
    @Test
    void setMotd_getMotd_works() throws Exception {
        ServerConfigEditor editor = new ServerConfigEditor(instancePath);
        
        editor.setMotd("Welcome to my server!");
        
        assertEquals("Welcome to my server!", editor.getMotd());
    }
    
    @Test
    void setOnlineMode_isOnlineMode_works() throws Exception {
        ServerConfigEditor editor = new ServerConfigEditor(instancePath);
        
        editor.setOnlineMode(false);
        
        assertFalse(editor.isOnlineMode());
    }
    
    @Test
    void getServerPort_defaultValue_returns25565() throws Exception {
        ServerConfigEditor editor = new ServerConfigEditor(instancePath);
        
        assertEquals(25565, editor.getServerPort());
    }
    
    @Test
    void getMaxPlayers_defaultValue_returns20() throws Exception {
        ServerConfigEditor editor = new ServerConfigEditor(instancePath);
        
        assertEquals(20, editor.getMaxPlayers());
    }
    
    @Test
    void save_writesServerProperties() throws Exception {
        ServerConfigEditor editor = new ServerConfigEditor(instancePath);
        editor.setServerPort(25566);
        editor.setMaxPlayers(100);
        editor.setMotd("Test Server");
        
        editor.save();
        
        Path propsPath = instancePath.resolve("server.properties");
        assertTrue(Files.exists(propsPath));
        String content = new String(Files.readAllBytes(propsPath));
        assertTrue(content.contains("server-port"));
        assertTrue(content.contains("25566"));
    }
}