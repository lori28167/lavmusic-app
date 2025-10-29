package com.lavmusic.app.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Configuration manager for the Ticly Lavamusic application
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "/config.json";
    private final JsonObject config;
    
    public ConfigManager() {
        Gson gson = new Gson();
        try (InputStream is = getClass().getResourceAsStream(CONFIG_FILE);
             InputStreamReader reader = new InputStreamReader(is)) {
            this.config = gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    public String getLavalinkHost() {
        return config.getAsJsonObject("lavalink").get("host").getAsString();
    }
    
    public int getLavalinkPort() {
        return config.getAsJsonObject("lavalink").get("port").getAsInt();
    }
    
    public String getLavalinkPassword() {
        return config.getAsJsonObject("lavalink").get("password").getAsString();
    }
    
    public int getDefaultVolume() {
        return config.getAsJsonObject("player").get("defaultVolume").getAsInt();
    }
    
    public int getBufferDuration() {
        return config.getAsJsonObject("player").get("bufferDuration").getAsInt();
    }
}
