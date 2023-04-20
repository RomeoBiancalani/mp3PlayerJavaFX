package com.aloiabiancalani.mp3player.model;

import javafx.scene.media.MediaPlayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Playlist implements Serializable {
    private static int playingIndex = 0;
    private static ObservableList<Brano> playlist = FXCollections.observableArrayList();
    private static MediaPlayer player = null;


    public static int getPlayingIndex() {
        return playingIndex;
    }

    public static Brano getPlayingBrano() {
        return playlist.get(playingIndex);
    }

    public static void forwardPlayingBrano() {
        if (playingIndex <= playlist.size() - 1) {
            playingIndex++;
        }
        else {
            playingIndex = 0;
        }
    }

    public static void backwardPlayingBrano() {
        if (playingIndex > 0) {
            playingIndex--;
        }
    }

    public static ObservableList<Brano> getPlaylist() {
        return playlist;
    }

    public static void addBrano(Brano brano) {
        Playlist.playlist.add(brano);
    }

    public static void shuffle() {
        Collections.shuffle(playlist);
    }

    public static void addAll(ArrayList<Brano> app) {
        playlist.addAll(app);
    }

    public static void setPlayer(MediaPlayer player) {
        Playlist.player = player;
    }

    public static MediaPlayer getPlayer() {
        return Playlist.player;
    }
}
