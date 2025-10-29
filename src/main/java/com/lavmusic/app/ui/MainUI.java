package com.lavmusic.app.ui;

import com.lavmusic.app.model.Track;
import com.lavmusic.app.player.MusicPlayerManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main UI for the Ticly Lavamusic application with Material Expressive design
 */
public class MainUI {
    private final MusicPlayerManager playerManager;
    private final Stage stage;
    
    // UI Components
    private Label currentTrackLabel;
    private Label artistLabel;
    private Label currentTimeLabel;
    private Label durationLabel;
    private Button playPauseButton;
    private Button previousButton;
    private Button nextButton;
    private Button shuffleButton;
    private Button repeatButton;
    private Slider volumeSlider;
    private Slider progressSlider;
    private ListView<Track> queueListView;
    private TextField searchField;
    private ListView<Track> searchResultsView;
    
    // Material Design Colors
    private static final String PRIMARY_COLOR = "#6200EE";
    private static final String PRIMARY_VARIANT = "#3700B3";
    private static final String SECONDARY_COLOR = "#03DAC6";
    private static final String BACKGROUND_COLOR = "#FAFAFA";
    private static final String SURFACE_COLOR = "#FFFFFF";
    private static final String ERROR_COLOR = "#B00020";
    private static final String ON_PRIMARY = "#FFFFFF";
    private static final String ON_BACKGROUND = "#000000";
    
    public MainUI(MusicPlayerManager playerManager, Stage stage) {
        this.playerManager = playerManager;
        this.stage = stage;
    }
    
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Top: App Bar
        root.setTop(createAppBar());
        
        // Center: Main content
        root.setCenter(createMainContent());
        
        // Bottom: Player controls
        root.setBottom(createPlayerControls());
        
        // Setup bindings
        setupBindings();
        
        Scene scene = new Scene(root, 1000, 700);
        return scene;
    }
    
    private VBox createAppBar() {
        VBox appBar = new VBox();
        appBar.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-padding: 16;");
        appBar.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Ticly Lavamusic");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(ON_PRIMARY));
        
        appBar.getChildren().add(titleLabel);
        return appBar;
    }
    
    private HBox createMainContent() {
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20));
        
        // Left: Search and results
        VBox leftPanel = createSearchPanel();
        leftPanel.setMinWidth(400);
        
        // Right: Queue
        VBox rightPanel = createQueuePanel();
        rightPanel.setMinWidth(400);
        
        mainContent.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        
        return mainContent;
    }
    
    private VBox createSearchPanel() {
        VBox panel = new VBox(15);
        panel.setStyle("-fx-background-color: " + SURFACE_COLOR + "; " +
                      "-fx-background-radius: 16; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        panel.setPadding(new Insets(20));
        
        // Search header
        Label searchHeader = new Label("Search Music");
        searchHeader.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        // Search box
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Enter song name or artist...");
        searchField.setStyle("-fx-background-radius: 8; -fx-padding: 10;");
        searchField.setOnAction(e -> performSearch()); // Support Enter key
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        Button searchButton = createMaterialButton("Search");
        searchButton.setOnAction(e -> performSearch());
        
        searchBox.getChildren().addAll(searchField, searchButton);
        
        // Search results
        Label resultsHeader = new Label("Results");
        resultsHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        searchResultsView = new ListView<>();
        searchResultsView.setStyle("-fx-background-radius: 8;");
        searchResultsView.setCellFactory(lv -> new TrackCell(true));
        VBox.setVgrow(searchResultsView, Priority.ALWAYS);
        
        panel.getChildren().addAll(searchHeader, searchBox, resultsHeader, searchResultsView);
        return panel;
    }
    
    private VBox createQueuePanel() {
        VBox panel = new VBox(15);
        panel.setStyle("-fx-background-color: " + SURFACE_COLOR + "; " +
                      "-fx-background-radius: 16; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        panel.setPadding(new Insets(20));
        
        // Queue header
        HBox queueHeader = new HBox(10);
        queueHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label queueLabel = new Label("Queue");
        queueLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        HBox.setHgrow(queueLabel, Priority.ALWAYS);
        
        Button clearButton = createTextButton("🗑", ERROR_COLOR);
        clearButton.setTooltip(new Tooltip("Clear queue"));
        clearButton.setOnAction(e -> clearQueue());
        
        queueHeader.getChildren().addAll(queueLabel, clearButton);
        
        // Queue list
        queueListView = new ListView<>();
        queueListView.setStyle("-fx-background-radius: 8;");
        queueListView.setCellFactory(lv -> new TrackCell(false));
        VBox.setVgrow(queueListView, Priority.ALWAYS);
        
        panel.getChildren().addAll(queueHeader, queueListView);
        return panel;
    }
    
    private VBox createPlayerControls() {
        VBox controls = new VBox(15);
        controls.setStyle("-fx-background-color: " + SURFACE_COLOR + "; " +
                         "-fx-border-color: #E0E0E0; " +
                         "-fx-border-width: 1 0 0 0;");
        controls.setPadding(new Insets(20));
        
        // Current track info
        VBox trackInfo = new VBox(5);
        currentTrackLabel = new Label("No track playing");
        currentTrackLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        artistLabel = new Label("");
        artistLabel.setFont(Font.font("System", 12));
        artistLabel.setTextFill(Color.GRAY);
        
        trackInfo.getChildren().addAll(currentTrackLabel, artistLabel);
        
        // Progress bar
        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.CENTER);
        
        currentTimeLabel = new Label("0:00");
        currentTimeLabel.setFont(Font.font("System", 10));
        currentTimeLabel.setMinWidth(40);
        
        progressSlider = new Slider(0, 1, 0);
        progressSlider.setStyle("-fx-background-radius: 8;");
        progressSlider.setOnMousePressed(e -> {
            // Allow seeking when clicking on slider
            if (playerManager.currentTrackProperty().get() != null) {
                playerManager.seek(progressSlider.getValue());
            }
        });
        progressSlider.setOnMouseDragged(e -> {
            // Allow seeking when dragging slider
            if (playerManager.currentTrackProperty().get() != null) {
                playerManager.seek(progressSlider.getValue());
            }
        });
        HBox.setHgrow(progressSlider, Priority.ALWAYS);
        
        durationLabel = new Label("0:00");
        durationLabel.setFont(Font.font("System", 10));
        durationLabel.setMinWidth(40);
        
        progressBox.getChildren().addAll(currentTimeLabel, progressSlider, durationLabel);
        
        // Control buttons
        HBox controlButtons = new HBox(15);
        controlButtons.setAlignment(Pos.CENTER);
        
        // Shuffle button
        shuffleButton = createTextButton("🔀", PRIMARY_COLOR);
        shuffleButton.setTooltip(new Tooltip("Shuffle"));
        shuffleButton.setOnAction(e -> toggleShuffle());
        
        // Previous button
        previousButton = createTextButton("⏮", PRIMARY_COLOR);
        previousButton.setTooltip(new Tooltip("Previous"));
        previousButton.setOnAction(e -> skipPrevious());
        
        // Play/Pause button
        playPauseButton = createTextButton("▶", PRIMARY_COLOR);
        playPauseButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 30; " +
                                "-fx-min-width: 60; -fx-min-height: 60; " +
                                "-fx-font-size: 24;");
        playPauseButton.setOnAction(e -> togglePlayPause());
        
        // Next button
        nextButton = createTextButton("⏭", PRIMARY_COLOR);
        nextButton.setTooltip(new Tooltip("Next"));
        nextButton.setOnAction(e -> skipNext());
        
        // Repeat button
        repeatButton = createTextButton("🔁", PRIMARY_COLOR);
        repeatButton.setTooltip(new Tooltip("Repeat: Off"));
        repeatButton.setOnAction(e -> cycleRepeat());
        
        // Volume control
        HBox volumeBox = new HBox(10);
        volumeBox.setAlignment(Pos.CENTER);
        volumeBox.setMaxWidth(200);
        
        Label volumeIcon = new Label("🔊");
        volumeIcon.setFont(Font.font(20));
        
        volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setStyle("-fx-background-radius: 8;");
        volumeSlider.valueProperty().addListener((obs, old, val) -> 
            playerManager.setVolume(val.intValue()));
        HBox.setHgrow(volumeSlider, Priority.ALWAYS);
        
        volumeBox.getChildren().addAll(volumeIcon, volumeSlider);
        
        controlButtons.getChildren().addAll(
            shuffleButton, previousButton, playPauseButton, nextButton, repeatButton, volumeBox
        );
        
        controls.getChildren().addAll(trackInfo, progressBox, controlButtons);
        return controls;
    }
    
    private Button createMaterialButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; " +
                       "-fx-text-fill: " + ON_BACKGROUND + "; " +
                       "-fx-background-radius: 8; " +
                       "-fx-padding: 10 20; " +
                       "-fx-font-weight: bold;");
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: derive(" + SECONDARY_COLOR + ", -10%); " +
                          "-fx-text-fill: " + ON_BACKGROUND + "; " +
                          "-fx-background-radius: 8; " +
                          "-fx-padding: 10 20; " +
                          "-fx-font-weight: bold;"));
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; " +
                          "-fx-text-fill: " + ON_BACKGROUND + "; " +
                          "-fx-background-radius: 8; " +
                          "-fx-padding: 10 20; " +
                          "-fx-font-weight: bold;"));
        
        return button;
    }
    
    private Button createTextButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font(18));
        button.setTextFill(Color.web(color));
        button.setStyle("-fx-background-color: transparent; " +
                       "-fx-cursor: hand; " +
                       "-fx-padding: 10;");
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: rgba(0,0,0,0.05); " +
            "-fx-background-radius: 50%; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 10;"));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 10;"));
        
        return button;
    }
    
    private void setupBindings() {
        // Update UI when current track changes - using runLater for thread safety
        playerManager.currentTrackProperty().addListener((obs, old, track) -> {
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(() -> updateCurrentTrackUI(track));
            } else {
                updateCurrentTrackUI(track);
            }
        });
        
        // Update play/pause button - using runLater for thread safety
        playerManager.playingProperty().addListener((obs, old, playing) -> {
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(() -> updatePlayPauseButton(playing));
            } else {
                updatePlayPauseButton(playing);
            }
        });
        
        // Update progress slider - using runLater for thread safety
        playerManager.positionProperty().addListener((obs, old, position) -> {
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(() -> updateProgressUI(position.doubleValue()));
            } else {
                updateProgressUI(position.doubleValue());
            }
        });
        
        // Update volume slider
        volumeSlider.setValue(playerManager.volumeProperty().get());
    }
    
    private void updateCurrentTrackUI(Track track) {
        if (track != null) {
            currentTrackLabel.setText(track.getTitle());
            artistLabel.setText(track.getAuthor());
            durationLabel.setText(track.getFormattedDuration());
        } else {
            currentTrackLabel.setText("No track playing");
            artistLabel.setText("");
            durationLabel.setText("0:00");
        }
    }
    
    private void updatePlayPauseButton(boolean playing) {
        String symbol = playing ? "⏸" : "▶";
        playPauseButton.setText(symbol);
    }
    
    private void updateProgressUI(double position) {
        progressSlider.setValue(position);
        
        if (playerManager.currentTrackProperty().get() != null) {
            long currentMs = (long) (position * playerManager.currentTrackProperty().get().getDuration());
            long currentSeconds = currentMs / 1000;
            long minutes = currentSeconds / 60;
            long seconds = currentSeconds % 60;
            currentTimeLabel.setText(String.format("%d:%02d", minutes, seconds));
        }
    }
    
    private void toggleShuffle() {
        playerManager.toggleShuffle();
        updateShuffleButton();
    }
    
    private void updateShuffleButton() {
        if (playerManager.isShuffleEnabled()) {
            shuffleButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; " +
                                  "-fx-background-radius: 50%; " +
                                  "-fx-cursor: hand; " +
                                  "-fx-padding: 10;");
        } else {
            shuffleButton.setStyle("-fx-background-color: transparent; " +
                                  "-fx-cursor: hand; " +
                                  "-fx-padding: 10;");
        }
    }
    
    private void cycleRepeat() {
        playerManager.cycleRepeatMode();
        updateRepeatButton();
    }
    
    private void updateRepeatButton() {
        String tooltip = switch (playerManager.getRepeatMode()) {
            case OFF -> "Repeat: Off";
            case ONE -> "Repeat: One";
            case ALL -> "Repeat: All";
        };
        repeatButton.setTooltip(new Tooltip(tooltip));
        
        if (playerManager.getRepeatMode() != MusicPlayerManager.RepeatMode.OFF) {
            repeatButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; " +
                                 "-fx-background-radius: 50%; " +
                                 "-fx-cursor: hand; " +
                                 "-fx-padding: 10;");
            
            if (playerManager.getRepeatMode() == MusicPlayerManager.RepeatMode.ONE) {
                repeatButton.setText("🔂");
            } else {
                repeatButton.setText("🔁");
            }
        } else {
            repeatButton.setText("🔁");
            repeatButton.setStyle("-fx-background-color: transparent; " +
                                 "-fx-cursor: hand; " +
                                 "-fx-padding: 10;");
        }
    }
    
    private void skipPrevious() {
        playerManager.skipPrevious();
    }
    
    private void performSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            searchResultsView.getItems().clear();
            searchResultsView.getItems().addAll(playerManager.search(query));
        }
    }
    
    private void togglePlayPause() {
        if (playerManager.playingProperty().get()) {
            playerManager.pause();
        } else {
            playerManager.play();
        }
    }
    
    private void skipNext() {
        playerManager.skipNext();
        updateQueueView();
    }
    
    private void clearQueue() {
        playerManager.clearQueue();
        updateQueueView();
    }
    
    private void updateQueueView() {
        queueListView.getItems().clear();
        queueListView.getItems().addAll(playerManager.getQueue());
    }
    
    /**
     * Custom cell for displaying tracks
     */
    private class TrackCell extends ListCell<Track> {
        private final boolean showAddButton;
        
        public TrackCell(boolean showAddButton) {
            this.showAddButton = showAddButton;
        }
        
        @Override
        protected void updateItem(Track track, boolean empty) {
            super.updateItem(track, empty);
            
            if (empty || track == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cell = new HBox(10);
                cell.setAlignment(Pos.CENTER_LEFT);
                cell.setPadding(new Insets(8));
                
                VBox trackInfo = new VBox(2);
                Label titleLabel = new Label(track.getTitle());
                titleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
                
                Label artistLabel = new Label(track.getAuthor() + " • " + track.getFormattedDuration());
                artistLabel.setFont(Font.font("System", 11));
                artistLabel.setTextFill(Color.GRAY);
                
                trackInfo.getChildren().addAll(titleLabel, artistLabel);
                HBox.setHgrow(trackInfo, Priority.ALWAYS);
                
                cell.getChildren().add(trackInfo);
                
                if (showAddButton) {
                    Button addButton = createTextButton("+", SECONDARY_COLOR);
                    addButton.setTooltip(new Tooltip("Add to queue"));
                    addButton.setOnAction(e -> {
                        playerManager.addToQueue(track);
                        updateQueueView();
                    });
                    cell.getChildren().add(addButton);
                }
                
                setGraphic(cell);
            }
        }
    }
}
