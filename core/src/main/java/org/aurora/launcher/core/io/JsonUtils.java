package org.aurora.launcher.core.io;

import com.google.gson.*;
import java.lang.reflect.Type;

public final class JsonUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson gsonCompact = new Gson();

    private JsonUtils() {
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static String toJson(Object obj) {
        return gsonCompact.toJson(obj);
    }

    public static String toJsonPretty(Object obj) {
        return gson.toJson(obj);
    }

    public static JsonObject parseObject(String json) {
        JsonElement element = JsonParser.parseString(json);
        return element.getAsJsonObject();
    }

    public static JsonArray parseArray(String json) {
        JsonElement element = JsonParser.parseString(json);
        return element.getAsJsonArray();
    }
}