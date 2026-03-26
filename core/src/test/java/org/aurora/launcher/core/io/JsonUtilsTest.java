package org.aurora.launcher.core.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Test
    void fromJson_parsesObject() {
        String json = "{\"name\":\"test\",\"value\":123}";
        TestObject result = JsonUtils.fromJson(json, TestObject.class);
        
        assertNotNull(result);
        assertEquals("test", result.name);
        assertEquals(123, result.value);
    }

    @Test
    void toJson_serializesObject() {
        TestObject obj = new TestObject("test", 123);
        String json = JsonUtils.toJson(obj);
        
        assertTrue(json.contains("\"name\":\"test\""));
        assertTrue(json.contains("\"value\":123"));
    }

    @Test
    void toJsonPretty_formatsNicely() {
        TestObject obj = new TestObject("test", 123);
        String json = JsonUtils.toJsonPretty(obj);
        
        assertTrue(json.contains("\n"));
    }

    @Test
    void parseObject_returnsJsonObject() {
        String json = "{\"name\":\"test\"}";
        JsonObject obj = JsonUtils.parseObject(json);
        
        assertEquals("test", obj.get("name").getAsString());
    }

    @Test
    void parseArray_returnsJsonArray() {
        String json = "[1,2,3]";
        JsonArray arr = JsonUtils.parseArray(json);
        
        assertEquals(3, arr.size());
    }

    static class TestObject {
        String name;
        int value;
        
        TestObject() {}
        TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}