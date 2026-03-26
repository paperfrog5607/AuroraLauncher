package org.aurora.launcher.account.skin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.aurora.launcher.account.model.SkinModel;
import org.aurora.launcher.account.model.SkinProfile;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SkinService {
    private OkHttpClient httpClient;
    private Gson gson;

    public SkinService() {
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    public SkinProfile fetchFromMojang(String uuid) throws SkinException {
        String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", "");
        
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new SkinException("Failed to fetch skin: " + response.code());
            }
            
            String body = response.body().string();
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            
            SkinProfile profile = new SkinProfile();
            
            if (json.has("properties")) {
                for (com.google.gson.JsonElement element : json.getAsJsonArray("properties")) {
                    JsonObject prop = element.getAsJsonObject();
                    if ("textures".equals(prop.get("name").getAsString())) {
                        String textureData = prop.get("value").getAsString();
                        String decoded = new String(java.util.Base64.getDecoder().decode(textureData));
                        JsonObject textures = JsonParser.parseString(decoded).getAsJsonObject();
                        
                        if (textures.has("textures")) {
                            JsonObject texturesObj = textures.getAsJsonObject("textures");
                            if (texturesObj.has("SKIN")) {
                                JsonObject skin = texturesObj.getAsJsonObject("SKIN");
                                profile.setSkinUrl(skin.getAsJsonObject("url").get("value").getAsString());
                                if (skin.has("metadata")) {
                                    JsonObject metadata = skin.getAsJsonObject("metadata");
                                    profile.setSlim("slim".equals(metadata.get("model").getAsString()));
                                    profile.setModel(SkinModel.ALEX);
                                } else {
                                    profile.setModel(SkinModel.STEVE);
                                }
                            }
                            if (texturesObj.has("CAPE")) {
                                JsonObject cape = texturesObj.getAsJsonObject("CAPE");
                                profile.setCapeUrl(cape.get("url").getAsString());
                            }
                        }
                    }
                }
            }
            
            return profile;
        } catch (IOException e) {
            throw new SkinException("Network error", e);
        }
    }

    public SkinProfile fetchFromCrafatar(String uuid) {
        SkinProfile profile = new SkinProfile();
        profile.setSkinUrl("https://crafatar.com/skins/" + uuid);
        return profile;
    }
}