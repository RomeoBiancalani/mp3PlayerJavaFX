package com.aloiabiancalani.mp3player.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Playlist implements Serializable {
    private static ArrayList<Brano> playlist = new ArrayList<>();

    public Playlist() {
    }

    public static ArrayList<Brano> getPlaylist() {
        return playlist;
    }

    public static void setPlaylist(ArrayList<Brano> playlist) {
        Playlist.playlist = playlist;
    }
    public static void addBrano(Brano brano) {
        Playlist.playlist.add(brano);
    }

    public static void shuffle() {
        Collections.shuffle(playlist);
    }

}
