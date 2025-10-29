package com.lavmusic.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a music playlist
 */
public class Playlist {
    private String name;
    private List<Track> tracks;
    
    public Playlist(String name) {
        this.name = name;
        this.tracks = new ArrayList<>();
    }
    
    public Playlist(String name, List<Track> tracks) {
        this.name = name;
        this.tracks = new ArrayList<>(tracks);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Track> getTracks() {
        return new ArrayList<>(tracks);
    }
    
    public void addTrack(Track track) {
        tracks.add(track);
    }
    
    public void removeTrack(int index) {
        if (index >= 0 && index < tracks.size()) {
            tracks.remove(index);
        }
    }
    
    public void clear() {
        tracks.clear();
    }
    
    public int size() {
        return tracks.size();
    }
    
    @Override
    public String toString() {
        return name + " (" + tracks.size() + " tracks)";
    }
}
