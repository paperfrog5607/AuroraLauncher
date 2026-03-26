package org.aurora.launcher.config.parser;

import com.google.gson.*;

import java.io.*;
import java.util.*;

public class JsonParser implements ConfigParser {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    @Override
    public Map<String, Object> parse(InputStream input) throws ConfigParseException {
        JsonElement element;
        try {
            element = com.google.gson.JsonParser.parseReader(new InputStreamReader(input));
        } catch (JsonSyntaxException e) {
            throw new ConfigParseException("Invalid JSON", e);
        }
        
        if (!element.isJsonObject()) {
            throw new ConfigParseException("Expected JSON object");
        }
        
        return jsonObjectToMap(element.getAsJsonObject());
    }
    
    @Override
    public void write(OutputStream output, Map<String, Object> config) throws ConfigParseException {
        JsonObject json = mapToJsonObject(config);
        try {
            output.write(GSON.toJson(json).getBytes());
        } catch (IOException e) {
            throw new ConfigParseException("Failed to write JSON", e);
        }
    }
    
    public JsonObject parseObject(InputStream input) throws ConfigParseException {
        try {
            JsonElement element = com.google.gson.JsonParser.parseReader(new InputStreamReader(input));
            return element.getAsJsonObject();
        } catch (Exception e) {
            throw new ConfigParseException("Failed to parse JSON object", e);
        }
    }
    
    public JsonArray parseArray(InputStream input) throws ConfigParseException {
        try {
            JsonElement element = com.google.gson.JsonParser.parseReader(new InputStreamReader(input));
            return element.getAsJsonArray();
        } catch (Exception e) {
            throw new ConfigParseException("Failed to parse JSON array", e);
        }
    }
    
    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"json"};
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> jsonObjectToMap(JsonObject json) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), jsonElementToObject(entry.getValue()));
        }
        return map;
    }
    
    private Object jsonElementToObject(JsonElement element) {
        if (element.isJsonNull()) {
            return null;
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else {
                return primitive.getAsString();
            }
        } else if (element.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonElement item : element.getAsJsonArray()) {
                list.add(jsonElementToObject(item));
            }
            return list;
        } else if (element.isJsonObject()) {
            return jsonObjectToMap(element.getAsJsonObject());
        }
        return null;
    }
    
    private JsonObject mapToJsonObject(Map<String, Object> map) {
        JsonObject json = new JsonObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.add(entry.getKey(), objectToJsonElement(entry.getValue()));
        }
        return json;
    }
    
    private JsonElement objectToJsonElement(Object value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        } else if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean) value);
        } else if (value instanceof Number) {
            return new JsonPrimitive((Number) value);
        } else if (value instanceof String) {
            return new JsonPrimitive((String) value);
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            return mapToJsonObject(map);
        } else if (value instanceof List) {
            JsonArray array = new JsonArray();
            for (Object item : (List<?>) value) {
                array.add(objectToJsonElement(item));
            }
            return array;
        }
        return new JsonPrimitive(String.valueOf(value));
    }
}