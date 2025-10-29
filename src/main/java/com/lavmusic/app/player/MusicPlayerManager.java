package com.lavmusic.app.player;

import com.lavmusic.app.config.ConfigManager;
import com.lavmusic.app.model.Playlist;
import com.lavmusic.app.model.Track;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Music player manager that handles playback
 * This is a simplified implementation for demonstration
 */
public class MusicPlayerManager {
    private static final Logger logger = LoggerFactory.getLogger(MusicPlayerManager.class);
    
    /**
     * Repeat modes for playback
     */
    public enum RepeatMode {
        OFF,      // No repeat
        ONE,      // Repeat current track
        ALL       // Repeat entire queue
    }
    
    private final ConfigManager config;
    private final List<Track> queue;
    private final ObjectProperty<Track> currentTrack;
    private final BooleanProperty playing;
    private final IntegerProperty volume;
    private final DoubleProperty position;
    private final AtomicBoolean initialized;
    private final LavalinkClient lavalinkClient;
    private Timer progressTimer;
    private long trackStartTime;
    private boolean shuffle;
    private RepeatMode repeatMode;
    private List<Playlist> playlists;
    private List<Track> favorites;
    
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
        this.shuffle = false;
        this.repeatMode = RepeatMode.OFF;
        this.playlists = new ArrayList<>();
        this.favorites = new ArrayList<>();
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
        trackStartTime = System.currentTimeMillis();
        
        startProgressTimer();
        
        // In a real implementation, you would send play command to Lavalink
    }
    
    /**
     * Play or resume playback
     */
    public void play() {
        if (currentTrack.get() != null) {
            logger.info("Resuming playback");
            playing.set(true);
            startProgressTimer();
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
        stopProgressTimer();
    }
    
    /**
     * Stop playback
     */
    public void stop() {
        logger.info("Stopping playback");
        playing.set(false);
        currentTrack.set(null);
        position.set(0.0);
        stopProgressTimer();
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
        } else if (repeatMode == RepeatMode.ALL && currentTrack.get() != null) {
            // If repeat all is enabled and queue is empty, we just finished the last track
            // In a real implementation, we'd reload the original queue
            logger.info("Repeat all enabled but queue is empty");
            stop();
        } else {
            stop();
        }
    }
    
    /**
     * Play the previous track
     */
    public void skipPrevious() {
        logger.info("Skipping to previous track");
        // In a simple implementation, we just restart the current track
        // A full implementation would maintain a history of played tracks
        if (currentTrack.get() != null) {
            play(currentTrack.get());
        }
    }
    
    /**
     * Start the progress timer
     */
    private void startProgressTimer() {
        stopProgressTimer();
        
        progressTimer = new Timer(true);
        progressTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (playing.get() && currentTrack.get() != null) {
                    long elapsed = System.currentTimeMillis() - trackStartTime;
                    double progress = (double) elapsed / currentTrack.get().getDuration();
                    position.set(Math.min(progress, 1.0));
                    
                    // Check if track has ended
                    if (progress >= 1.0) {
                        handleTrackEnd();
                    }
                }
            }
        }, 0, 100); // Update every 100ms
    }
    
    /**
     * Stop the progress timer
     */
    private void stopProgressTimer() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
    }
    
    /**
     * Handle track end
     */
    private void handleTrackEnd() {
        stopProgressTimer();
        
        if (repeatMode == RepeatMode.ONE) {
            // Repeat current track
            Track current = currentTrack.get();
            if (current != null) {
                play(current);
            }
        } else {
            // Play next track
            skipNext();
        }
    }
    
    /**
     * Toggle shuffle mode
     */
    public void toggleShuffle() {
        shuffle = !shuffle;
        logger.info("Shuffle: {}", shuffle ? "ON" : "OFF");
        
        if (shuffle && !queue.isEmpty()) {
            Collections.shuffle(queue, new Random());
            logger.info("Queue shuffled");
        }
    }
    
    /**
     * Cycle through repeat modes
     */
    public void cycleRepeatMode() {
        repeatMode = switch (repeatMode) {
            case OFF -> RepeatMode.ONE;
            case ONE -> RepeatMode.ALL;
            case ALL -> RepeatMode.OFF;
        };
        logger.info("Repeat mode: {}", repeatMode);
    }
    
    /**
     * Seek to a position in the current track (0.0 to 1.0)
     */
    public void seek(double position) {
        if (currentTrack.get() != null) {
            this.position.set(Math.max(0.0, Math.min(1.0, position)));
            long newTime = (long) (position * currentTrack.get().getDuration());
            trackStartTime = System.currentTimeMillis() - newTime;
            logger.info("Seeked to position: {}", position);
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
        stopProgressTimer();
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
    
    public boolean isShuffleEnabled() {
        return shuffle;
    }
    
    public RepeatMode getRepeatMode() {
        return repeatMode;
    }
    
    /**
     * Create a new playlist
     */
    public Playlist createPlaylist(String name) {
        Playlist playlist = new Playlist(name);
        playlists.add(playlist);
        logger.info("Created playlist: {}", name);
        return playlist;
    }
    
    /**
     * Get all playlists
     */
    public List<Playlist> getPlaylists() {
        return new ArrayList<>(playlists);
    }
    
    /**
     * Delete a playlist
     */
    public void deletePlaylist(Playlist playlist) {
        playlists.remove(playlist);
        logger.info("Deleted playlist: {}", playlist.getName());
    }
    
    /**
     * Load a playlist into the queue
     */
    public void loadPlaylist(Playlist playlist) {
        clearQueue();
        for (Track track : playlist.getTracks()) {
            addToQueue(track);
        }
        logger.info("Loaded playlist: {}", playlist.getName());
    }
    
    /**
     * Save current queue as a playlist
     */
    public Playlist saveQueueAsPlaylist(String name) {
        List<Track> currentQueue = new ArrayList<>(queue);
        if (currentTrack.get() != null) {
            currentQueue.add(0, currentTrack.get());
        }
        
        Playlist playlist = new Playlist(name, currentQueue);
        playlists.add(playlist);
        logger.info("Saved queue as playlist: {}", name);
        return playlist;
    }
    
    /**
     * Add a track to favorites
     */
    public void addToFavorites(Track track) {
        if (!favorites.contains(track)) {
            favorites.add(track);
            logger.info("Added to favorites: {}", track);
        }
    }
    
    /**
     * Remove a track from favorites
     */
    public void removeFromFavorites(Track track) {
        favorites.remove(track);
        logger.info("Removed from favorites: {}", track);
    }
    
    /**
     * Check if a track is in favorites
     */
    public boolean isFavorite(Track track) {
        return favorites.contains(track);
    }
    
    /**
     * Get all favorite tracks
     */
    public List<Track> getFavorites() {
        return new ArrayList<>(favorites);
    }
}
