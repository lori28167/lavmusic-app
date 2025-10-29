package com.lavmusic.app.player;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lavmusic.app.model.Track;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Client for interacting with the Lavalink REST API
 */
public class LavalinkClient {
    private static final Logger logger = LoggerFactory.getLogger(LavalinkClient.class);
    
    private final String host;
    private final int port;
    private final String password;
    private final OkHttpClient httpClient;
    private final Gson gson;
    
    public LavalinkClient(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.gson = new Gson();
        
        // Create HTTP client with reasonable timeouts
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    }
    
    /**
     * Search for tracks using the Lavalink REST API
     * @param query The search query
     * @return List of tracks matching the query
     */
    public List<Track> searchTracks(String query) {
        List<Track> tracks = new ArrayList<>();
        
        try {
            // Encode the query parameter
            String encodedQuery = URLEncoder.encode("ytsearch:" + query, StandardCharsets.UTF_8);
            
            // Build the request URL
            String url = String.format("http://%s:%d/v4/loadtracks?identifier=%s", 
                host, port, encodedQuery);
            
            logger.debug("Searching Lavalink: {}", url);
            
            // Create the request with authorization header
            Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", password)
                .get()
                .build();
            
            // Execute the request
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Lavalink search failed with status: {}", response.code());
                    return tracks;
                }
                
                String responseBody = response.body().string();
                logger.debug("Lavalink response: {}", responseBody);
                
                // Parse the response
                JsonObject json = gson.fromJson(responseBody, JsonObject.class);
                
                // Check load type
                String loadType = json.get("loadType").getAsString();
                
                if ("search".equals(loadType) || "track".equals(loadType) || "playlist".equals(loadType)) {
                    JsonArray data = json.getAsJsonArray("data");
                    
                    for (JsonElement element : data) {
                        JsonObject trackData = element.getAsJsonObject();
                        JsonObject info = trackData.getAsJsonObject("info");
                        
                        String title = info.get("title").getAsString();
                        String author = info.get("author").getAsString();
                        String uri = info.get("uri").getAsString();
                        long duration = info.get("length").getAsLong();
                        
                        tracks.add(new Track(title, author, uri, duration));
                        
                        // Limit to 20 results
                        if (tracks.size() >= 20) {
                            break;
                        }
                    }
                    
                    logger.info("Found {} tracks for query: {}", tracks.size(), query);
                } else {
                    logger.warn("No results found for query: {}", query);
                }
                
            }
        } catch (IOException e) {
            logger.error("Error searching tracks", e);
        } catch (Exception e) {
            logger.error("Unexpected error during search", e);
        }
        
        return tracks;
    }
    
    /**
     * Test connection to the Lavalink server
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try {
            String url = String.format("http://%s:%d/version", host, port);
            
            Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", password)
                .get()
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String version = response.body().string();
                    logger.info("Connected to Lavalink server version: {}", version);
                    return true;
                } else {
                    logger.error("Failed to connect to Lavalink server: {}", response.code());
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error("Error testing Lavalink connection", e);
            return false;
        }
    }
    
    /**
     * Shutdown the HTTP client
     */
    public void shutdown() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}
