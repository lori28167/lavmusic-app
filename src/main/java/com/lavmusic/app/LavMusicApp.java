package com.lavmusic.app;

import com.lavmusic.app.config.ConfigManager;
import com.lavmusic.app.player.MusicPlayerManager;
import com.lavmusic.app.ui.MainUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for Ticly Lavamusic
 */
public class LavMusicApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(LavMusicApp.class);
    
    private MusicPlayerManager playerManager;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting Ticly Lavamusic application...");
            
            // Load configuration
            ConfigManager config = new ConfigManager();
            
            // Initialize player manager
            playerManager = new MusicPlayerManager(config);
            playerManager.initialize();
            
            // Create UI
            MainUI mainUI = new MainUI(playerManager, primaryStage);
            Scene scene = mainUI.createScene();
            
            // Setup stage
            primaryStage.setTitle("Ticly Lavamusic - Modern Music Player");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(650);
            primaryStage.setOnCloseRequest(e -> shutdown());
            
            primaryStage.show();
            
            logger.info("Ticly Lavamusic application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            throw new RuntimeException("Failed to start application", e);
        }
    }
    
    @Override
    public void stop() {
        shutdown();
    }
    
    private void shutdown() {
        logger.info("Shutting down application...");
        if (playerManager != null) {
            playerManager.shutdown();
        }
        logger.info("Application shutdown complete");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
