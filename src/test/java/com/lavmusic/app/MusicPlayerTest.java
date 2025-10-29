package com.lavmusic.app;

import com.lavmusic.app.config.ConfigManager;
import com.lavmusic.app.model.Track;
import com.lavmusic.app.player.MusicPlayerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the music player functionality
 */
class MusicPlayerTest {
    
    private ConfigManager config;
    private MusicPlayerManager player;
    
    @BeforeEach
    void setUp() {
        config = new ConfigManager();
        player = new MusicPlayerManager(config);
        player.initialize();
    }
    
    @Test
    void testConfiguration() {
        assertEquals("lavalink.jirayu.net", config.getLavalinkHost());
        assertEquals(13592, config.getLavalinkPort());
        assertEquals("youshallnotpass", config.getLavalinkPassword());
        assertEquals(50, config.getDefaultVolume());
    }
    
    @Test
    void testTrackCreation() {
        Track track = new Track("Test Song", "Test Artist", "https://example.com/test", 180000);
        
        assertEquals("Test Song", track.getTitle());
        assertEquals("Test Artist", track.getAuthor());
        assertEquals("https://example.com/test", track.getUri());
        assertEquals(180000, track.getDuration());
        assertEquals("3:00", track.getFormattedDuration());
    }
    
    @Test
    void testAddToQueue() {
        Track track1 = new Track("Test Song 1", "Test Artist", "https://example.com/test1", 180000);
        Track track2 = new Track("Test Song 2", "Test Artist", "https://example.com/test2", 180000);
        
        // First track gets played automatically
        player.addToQueue(track1);
        assertEquals(track1, player.currentTrackProperty().get());
        assertTrue(player.playingProperty().get());
        
        // Second track stays in queue
        player.addToQueue(track2);
        assertEquals(1, player.getQueue().size());
    }
    
    @Test
    void testVolumeControl() {
        player.setVolume(75);
        assertEquals(75, player.volumeProperty().get());
        
        player.setVolume(0);
        assertEquals(0, player.volumeProperty().get());
        
        player.setVolume(100);
        assertEquals(100, player.volumeProperty().get());
        
        // Test boundary conditions
        player.setVolume(-10);
        assertEquals(0, player.volumeProperty().get());
        
        player.setVolume(150);
        assertEquals(100, player.volumeProperty().get());
    }
    
    @Test
    void testPlayPauseStop() {
        Track track = new Track("Test Song", "Test Artist", "https://example.com/test", 180000);
        
        // Initially not playing
        assertFalse(player.playingProperty().get());
        
        // Play track
        player.play(track);
        assertTrue(player.playingProperty().get());
        assertEquals(track, player.currentTrackProperty().get());
        
        // Pause
        player.pause();
        assertFalse(player.playingProperty().get());
        
        // Resume
        player.play();
        assertTrue(player.playingProperty().get());
        
        // Stop
        player.stop();
        assertFalse(player.playingProperty().get());
        assertNull(player.currentTrackProperty().get());
    }
    
    @Test
    void testSearch() {
        var results = player.search("test query");
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should return demo results
        assertTrue(results.size() >= 3);
        
        Track firstResult = results.get(0);
        assertNotNull(firstResult.getTitle());
        assertNotNull(firstResult.getAuthor());
    }
    
    @Test
    void testClearQueue() {
        // Stop any current playback first
        player.stop();
        
        Track track1 = new Track("Song 1", "Artist 1", "https://example.com/1", 180000);
        Track track2 = new Track("Song 2", "Artist 2", "https://example.com/2", 210000);
        Track track3 = new Track("Song 3", "Artist 3", "https://example.com/3", 195000);
        
        player.addToQueue(track1);  // This will start playing
        player.addToQueue(track2);  // This stays in queue
        player.addToQueue(track3);  // This stays in queue
        
        assertEquals(2, player.getQueue().size());
        
        player.clearQueue();
        assertTrue(player.getQueue().isEmpty());
    }
}
