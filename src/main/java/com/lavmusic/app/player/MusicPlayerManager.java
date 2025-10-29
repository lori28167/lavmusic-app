package com.lavmusic.app.player;

import com.lavmusic.app.config.ConfigManager;
import com.lavmusic.app.model.Track;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Music player manager that handles playback
 * This is a simplified implementation for demonstration
 */
public class MusicPlayerManager {
    private static final Logger logger = LoggerFactory.getLogger(MusicPlayerManager.class);
    
    private final ConfigManager config;
    private final List<Track> queue;
    private final ObjectProperty<Track> currentTrack;
    private final BooleanProperty playing;
    private final IntegerProperty volume;
    private final DoubleProperty position;
    private final AtomicBoolean initialized;
    private final LavalinkClient lavalinkClient;
    
    public MusicPlayerManager(ConfigManager config) {
        this.config = config;
        this.queue = new ArrayList<>();
        this.currentTrack = new SimpleObjectProperty<>();
        this.playing = new SimpleBooleanProperty(false);
        this.volume = new SimpleIntegerProperty(config.getDefaultVolume());
        this.position = new SimpleDoubleProperty(0.0);
        this.initialized = new AtomicBoolean(false);
        this.lavalinkClient = new LavalinkClient(
            config.getLavalinkHost(), 
            config.getLavalinkPort(), 
            config.getLavalinkPassword()
        );
    }
    
    /**
     * Initialize connection to Lavalink server
     */
    public void initialize() {
        if (initialized.get()) {
            return;
        }
        
        try {
            logger.info("Connecting to Lavalink server at {}:{}", 
                config.getLavalinkHost(), config.getLavalinkPort());
            
            // Test connection to Lavalink server
            boolean connected = lavalinkClient.testConnection();
            if (connected) {
                initialized.set(true);
                logger.info("Successfully connected to Lavalink server");
            } else {
                logger.warn("Could not connect to Lavalink server, running in offline mode");
                // Still mark as initialized to allow the app to start
                initialized.set(true);
            }
        } catch (Exception e) {
            logger.error("Failed to initialize Lavalink connection", e);
            // Still mark as initialized to allow the app to start in offline mode
            initialized.set(true);
            logger.warn("Running in offline mode");
        }
    }
    
    /**
     * Add a track to the queue
     */
    public void addToQueue(Track track) {
        queue.add(track);
        logger.info("Added to queue: {}", track);
        
        // If nothing is playing, start playing this track
        if (currentTrack.get() == null) {
            playNext();
        }
    }
    
    /**
     * Play the specified track
     */
    public void play(Track track) {
        logger.info("Playing: {}", track);
        currentTrack.set(track);
        playing.set(true);
        position.set(0.0);
        
        // In a real implementation, you would send play command to Lavalink
    }
    
    /**
     * Play or resume playback
     */
    public void play() {
        if (currentTrack.get() != null) {
            logger.info("Resuming playback");
            playing.set(true);
        } else if (!queue.isEmpty()) {
            playNext();
        }
    }
    
    /**
     * Pause playback
     */
    public void pause() {
        logger.info("Pausing playback");
        playing.set(false);
    }
    
    /**
     * Stop playback
     */
    public void stop() {
        logger.info("Stopping playback");
        playing.set(false);
        currentTrack.set(null);
        position.set(0.0);
    }
    
    /**
     * Skip to next track in queue
     */
    public void skipNext() {
        logger.info("Skipping to next track");
        playNext();
    }
    
    /**
     * Play the next track in the queue
     */
    private void playNext() {
        if (!queue.isEmpty()) {
            Track track = queue.remove(0);
            play(track);
        } else {
            stop();
        }
    }
    
    /**
     * Set volume (0-100)
     */
    public void setVolume(int vol) {
        int newVolume = Math.max(0, Math.min(100, vol));
        volume.set(newVolume);
        logger.info("Volume set to: {}", newVolume);
    }
    
    /**
     * Search for tracks
     */
    public List<Track> search(String query) {
        logger.info("Searching for: {}", query);
        
        List<Track> results = new ArrayList<>();
        
        try {
            // Try to search using Lavalink
            results = lavalinkClient.searchTracks(query);
            
            if (results.isEmpty()) {
                logger.warn("No results found from Lavalink for query: {}", query);
            } else {
                logger.info("Found {} results from Lavalink", results.size());
                return results;
            }
        } catch (Exception e) {
            logger.error("Error during search", e);
        }
        
        // Fallback to demo results if Lavalink search failed or returned no results
        if (results.isEmpty()) {
            logger.warn("Returning demo results as fallback");
            results.add(new Track("Sample Song 1", "Artist A", "https://example.com/1", 180000));
            results.add(new Track("Sample Song 2", "Artist B", "https://example.com/2", 210000));
            results.add(new Track("Sample Song 3", "Artist C", "https://example.com/3", 195000));
        }
        
        return results;
    }
    
    /**
     * Clear the queue
     */
    public void clearQueue() {
        queue.clear();
        logger.info("Queue cleared");
    }
    
    /**
     * Shutdown the player
     */
    public void shutdown() {
        logger.info("Shutting down music player");
        stop();
        queue.clear();
        lavalinkClient.shutdown();
        initialized.set(false);
    }
    
    // Property getters
    public ObjectProperty<Track> currentTrackProperty() {
        return currentTrack;
    }
    
    public BooleanProperty playingProperty() {
        return playing;
    }
    
    public IntegerProperty volumeProperty() {
        return volume;
    }
    
    public DoubleProperty positionProperty() {
        return position;
    }
    
    public List<Track> getQueue() {
        return new ArrayList<>(queue);
    }
}
