package com.aloiabiancalani.mp3player.model;

import java.io.Serializable;

public class Brano implements Serializable {
    private String titolo, artista, album, pathCopertina;
    private long lunghezzaInSecondi;

    public Brano(String titolo, String artista, String album, String pathCopertina, long lunghezzaInSecondi) {
        this.titolo = titolo;
        this.artista = artista;
        this.album = album;
        this.pathCopertina = pathCopertina;
        this.lunghezzaInSecondi = lunghezzaInSecondi;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPathCopertina() {
        return pathCopertina;
    }

    public void setPathCopertina(String pathCopertina) {
        this.pathCopertina = pathCopertina;
    }

    public long getLunghezzaInSecondi() {
        return lunghezzaInSecondi;
    }

    public void setLunghezzaInSecondi(long lunghezzaInSecondi) {
        this.lunghezzaInSecondi = lunghezzaInSecondi;
    }

    @Override
    public String toString() {
        return "Brano{" +
                "titolo='" + titolo + '\'' +
                ", artista='" + artista + '\'' +
                ", album='" + album + '\'' +
                ", pathCopertina='" + pathCopertina + '\'' +
                ", lunghezzaInSecondi=" + lunghezzaInSecondi +
                '}';
    }
}