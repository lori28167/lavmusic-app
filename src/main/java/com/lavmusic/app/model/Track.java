package com.lavmusic.app.model;

/**
 * Represents a music track
 */
public class Track {
    private final String title;
    private final String author;
    private final String uri;
    private final long duration;
    
    public Track(String title, String author, String uri, long duration) {
        this.title = title;
        this.author = author;
        this.uri = uri;
        this.duration = duration;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getUri() {
        return uri;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public String getFormattedDuration() {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    @Override
    public String toString() {
        return title + " - " + author + " (" + getFormattedDuration() + ")";
    }
}
