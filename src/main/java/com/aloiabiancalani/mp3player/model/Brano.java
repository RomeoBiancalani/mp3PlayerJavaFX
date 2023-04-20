package com.aloiabiancalani.mp3player.model;

import java.io.Serializable;
import java.util.Objects;

public class Brano implements Serializable {
    private String titolo, artista, album, pathCopertina, songPath;
    private long lunghezzaInSecondi;

    public Brano(String titolo, String artista, String album, String pathCopertina, long lunghezzaInSecondi, String songPath) {
        this.titolo = titolo;
        this.artista = artista;
        this.album = album;
        this.pathCopertina = pathCopertina; // path della copertina del brano
        this.lunghezzaInSecondi = lunghezzaInSecondi;
        this.songPath = songPath; // path del file mp3
    }

    public String getTitolo() {
        return titolo;
    }

    public String getArtista() {
        return artista;
    }

    public String getAlbum() {
        return album;
    }

    public String getPathCopertina() {
        return pathCopertina;
    }

    public String getSongPath() {
        return songPath;
    }


    @Override
    public String toString() {
        return "Brano{" +
                "titolo='" + titolo + '\'' +
                ", artista='" + artista + '\'' +
                ", album='" + album + '\'' +
                ", pathCopertina='" + pathCopertina + '\'' +
                ", lunghezzaInSecondi=" + lunghezzaInSecondi +
                ", pathBrano=" + songPath + '}';
    }
}
