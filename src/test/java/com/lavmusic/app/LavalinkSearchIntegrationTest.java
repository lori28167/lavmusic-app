package com.lavmusic.app;

import com.lavmusic.app.config.ConfigManager;
import com.lavmusic.app.model.Track;
import com.lavmusic.app.player.LavalinkClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Lavalink search functionality
 */
class LavalinkSearchIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(LavalinkSearchIntegrationTest.class);
    
    @Test
    void testRealLavalinkSearch() {
        ConfigManager config = new ConfigManager();
        
        LavalinkClient client = new LavalinkClient(
            config.getLavalinkHost(),
            config.getLavalinkPort(),
            config.getLavalinkPassword()
        );
        
        try {
            logger.info("Testing connection to Lavalink server...");
            boolean connected = client.testConnection();
            logger.info("Connection status: {}", connected ? "SUCCESS" : "FAILED");
            
            if (connected) {
                logger.info("Searching for 'never gonna give you up'...");
                List<Track> results = client.searchTracks("never gonna give you up");
                
                logger.info("Found {} results", results.size());
                assertNotNull(results);
                assertFalse(results.isEmpty(), "Should find results for a popular song");
                
                Track firstResult = results.get(0);
                logger.info("First result: {} - {} ({})", 
                    firstResult.getTitle(), 
                    firstResult.getAuthor(), 
                    firstResult.getFormattedDuration());
                
                assertNotNull(firstResult.getTitle());
                assertNotNull(firstResult.getAuthor());
                assertTrue(firstResult.getDuration() > 0);
            } else {
                logger.warn("Lavalink server not reachable, skipping integration test");
                // Test passes even if server is not available (for CI environments)
            }
        } finally {
            client.shutdown();
        }
    }
}
