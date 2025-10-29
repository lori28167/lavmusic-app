package com.lavmusic.app;

import com.lavmusic.app.config.ConfigManager;
import com.lavmusic.app.model.Playlist;
import com.lavmusic.app.model.Track;
import com.lavmusic.app.player.MusicPlayerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for new features: playlists, favorites, shuffle, repeat, progress tracking
 */
class AdvancedFeaturesTest {
    
    private ConfigManager config;
    private MusicPlayerManager player;
    
    @BeforeEach
    void setUp() {
        config = new ConfigManager();
        player = new MusicPlayerManager(config);
        player.initialize();
    }
    
    @Test
    void testPlaylistCreation() {
        Playlist playlist = player.createPlaylist("Test Playlist");
        
        assertNotNull(playlist);
        assertEquals("Test Playlist", playlist.getName());
        assertEquals(0, playlist.size());
        assertTrue(player.getPlaylists().contains(playlist));
    }
    
    @Test
    void testPlaylistWithTracks() {
        Playlist playlist = player.createPlaylist("My Music");
        Track track1 = new Track("Song 1", "Artist 1", "https://example.com/1", 180000);
        Track track2 = new Track("Song 2", "Artist 2", "https://example.com/2", 210000);
        
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        
        assertEquals(2, playlist.size());
        assertTrue(playlist.getTracks().contains(track1));
        assertTrue(playlist.getTracks().contains(track2));
    }
    
    @Test
    void testSaveQueueAsPlaylist() {
        // Stop any playing track first
        player.stop();
        
        Track track1 = new Track("Song 1", "Artist 1", "https://example.com/1", 180000);
        Track track2 = new Track("Song 2", "Artist 2", "https://example.com/2", 210000);
        
        player.addToQueue(track1);
        player.addToQueue(track2);
        
        Playlist savedPlaylist = player.saveQueueAsPlaylist("Saved Queue");
        
        assertNotNull(savedPlaylist);
        assertEquals("Saved Queue", savedPlaylist.getName());
        assertTrue(savedPlaylist.size() >= 2); // At least 2 tracks (plus possibly current track)
    }
    
    @Test
    void testLoadPlaylist() {
        Playlist playlist = player.createPlaylist("Load Test");
        Track track1 = new Track("Song 1", "Artist 1", "https://example.com/1", 180000);
        Track track2 = new Track("Song 2", "Artist 2", "https://example.com/2", 210000);
        
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        
        player.loadPlaylist(playlist);
        
        // Queue should have at least 1 track (one becomes current track if playing)
        assertTrue(player.getQueue().size() >= 1 || player.currentTrackProperty().get() != null);
    }
    
    @Test
    void testDeletePlaylist() {
        Playlist playlist = player.createPlaylist("To Delete");
        
        assertTrue(player.getPlaylists().contains(playlist));
        
        player.deletePlaylist(playlist);
        
        assertFalse(player.getPlaylists().contains(playlist));
    }
    
    @Test
    void testFavorites() {
        Track track = new Track("Favorite Song", "Artist", "https://example.com/fav", 180000);
        
        assertFalse(player.isFavorite(track));
        
        player.addToFavorites(track);
        assertTrue(player.isFavorite(track));
        assertEquals(1, player.getFavorites().size());
        
        player.removeFromFavorites(track);
        assertFalse(player.isFavorite(track));
        assertEquals(0, player.getFavorites().size());
    }
    
    @Test
    void testFavoritesNoDuplicates() {
        Track track = new Track("Song", "Artist", "https://example.com/song", 180000);
        
        player.addToFavorites(track);
        player.addToFavorites(track); // Try adding twice
        
        assertEquals(1, player.getFavorites().size());
    }
    
    @Test
    void testShuffleToggle() {
        assertFalse(player.isShuffleEnabled());
        
        player.toggleShuffle();
        assertTrue(player.isShuffleEnabled());
        
        player.toggleShuffle();
        assertFalse(player.isShuffleEnabled());
    }
    
    @Test
    void testRepeatModeCycle() {
        assertEquals(MusicPlayerManager.RepeatMode.OFF, player.getRepeatMode());
        
        player.cycleRepeatMode();
        assertEquals(MusicPlayerManager.RepeatMode.ONE, player.getRepeatMode());
        
        player.cycleRepeatMode();
        assertEquals(MusicPlayerManager.RepeatMode.ALL, player.getRepeatMode());
        
        player.cycleRepeatMode();
        assertEquals(MusicPlayerManager.RepeatMode.OFF, player.getRepeatMode());
    }
    
    @Test
    void testSeek() {
        Track track = new Track("Test Song", "Artist", "https://example.com/test", 180000);
        player.play(track);
        
        player.seek(0.5); // Seek to 50%
        assertEquals(0.5, player.positionProperty().get(), 0.01);
        
        player.seek(0.0); // Seek to start
        assertEquals(0.0, player.positionProperty().get(), 0.01);
        
        player.seek(1.0); // Seek to end
        assertEquals(1.0, player.positionProperty().get(), 0.01);
    }
    
    @Test
    void testSeekBoundaries() {
        Track track = new Track("Test Song", "Artist", "https://example.com/test", 180000);
        player.play(track);
        
        // Test seeking beyond boundaries
        player.seek(-0.5); // Should clamp to 0
        assertEquals(0.0, player.positionProperty().get(), 0.01);
        
        player.seek(1.5); // Should clamp to 1
        assertEquals(1.0, player.positionProperty().get(), 0.01);
    }
    
    @Test
    void testSkipPrevious() {
        Track track = new Track("Test Song", "Artist", "https://example.com/test", 180000);
        player.play(track);
        
        Track currentBefore = player.currentTrackProperty().get();
        
        player.skipPrevious();
        
        // Should restart the same track
        Track currentAfter = player.currentTrackProperty().get();
        assertEquals(currentBefore.getTitle(), currentAfter.getTitle());
        assertTrue(player.playingProperty().get());
    }
    
    @Test
    void testProgressTracking() throws InterruptedException {
        Track track = new Track("Test Song", "Artist", "https://example.com/test", 10000); // 10 second track
        player.play(track);
        
        // Wait a bit for progress to update
        Thread.sleep(200);
        
        // Progress should be greater than 0
        assertTrue(player.positionProperty().get() > 0.0);
        
        player.stop();
    }
}
