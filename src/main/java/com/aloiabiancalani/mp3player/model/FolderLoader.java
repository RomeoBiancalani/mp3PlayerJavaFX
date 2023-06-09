package com.aloiabiancalani.mp3player.model;

import com.aloiabiancalani.mp3player.Main;
import com.mpatric.mp3agic.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;


import java.io.*;

import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class FolderLoader extends Task<Void> {
    private String loadingPath;

    public FolderLoader(String loadingPath) {
        this.loadingPath = loadingPath;
    }

    @Override
    protected Void call() throws Exception {
        File mp3Folder = new File(loadingPath);
        Playlist.clearPlaylist();
        File dataFolder = new File(Paths.get(mp3Folder.getAbsolutePath() + "/.data").toUri());
        if (dataFolder.exists()) {
            System.out.println("Cartella .data gia' esistente");
            loadFromFile(mp3Folder.getAbsolutePath() + "/.data/brani.bin");
            updateProgress(1,1); // set the progress as completed
            Thread.sleep(500);
            return null;
        }

        // creazione cartella
        if (!dataFolder.mkdir()) {
            throw new RuntimeException("Errore durante la creazione della cartella data.");
        }
        try {
            Files.setAttribute(dataFolder.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File[] files = mp3Folder.listFiles();

        if (files != null) {
            updateProgress(0,files.length);
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                System.out.println("Carico " + file.getName());
                if (file.getName().endsWith(".mp3")) {
                    try {
                        Mp3File mp3file = new Mp3File(file.getAbsolutePath());
                        long durataBrano = mp3file.getLengthInSeconds();

                        String nome = file.getName();
                        String artista = "Artista Sconosciuto";
                        String album = "Album Sconosciuto";
                        String copertinaName = Main.class.getResource("defaultCover.jpg").toString();
                        String songpath = file.getAbsolutePath();
                        byte[] copertina;

                        // get dati dal file mp3
                        if (mp3file.hasId3v1Tag()) {
                            ID3v1 tags = mp3file.getId3v1Tag();
                            nome = tags.getTitle();
                            artista = tags.getArtist();
                            album = tags.getAlbum();
                        }

                        if (mp3file.hasId3v2Tag()) {
                            ID3v2 tags = mp3file.getId3v2Tag();
                            nome = tags.getTitle();
                            artista = tags.getArtist();
                            album = tags.getAlbum();
                            copertina = tags.getAlbumImage();
                            if (copertina != null) {
                                String mimetype = tags.getAlbumImageMimeType();
                                if (mimetype.equals("image/jpeg")) {
                                    copertinaName = dataFolder.getAbsolutePath() + "/" + file.getName().replaceAll(".mp3", ".jpg");
                                    FileOutputStream copertinaFile = new FileOutputStream(copertinaName);
                                    copertinaFile.write(copertina, 0, copertina.length);
                                    copertinaFile.flush();
                                    copertinaFile.close();
                                }
                            }
                        }
                        Brano brano = new Brano(nome, artista, album, copertinaName, durataBrano, songpath);

                        Playlist.addBrano(brano);

                    } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                updateProgress(i, files.length - 1); //aggiorna la progress bar
            }

        }

        // salva la playlist sul file binario nella cartella .data
        saveToFile(mp3Folder.getAbsolutePath() + "/.data/brani.bin");
        updateProgress(1,1); // setta il progresso del caricamento come completato
        Thread.sleep(500);


        return null;
    }

    // caricamento dei dati dal file binario se la cartella .data esiste gia'
    private void loadFromFile(String filename) throws Exception {
        ObjectInputStream reader=null;

        try {
            reader = new ObjectInputStream(new FileInputStream(filename));
            ArrayList<Brano> app =(ArrayList<Brano>) reader.readObject();

            Playlist.addAll(app);
        } catch (Exception e) {
            throw e;
        }
        finally {
            if (reader!=null)
                reader.close();
        }


    }


    // salvataggio dati su file binario
    private void saveToFile(String filename) throws Exception {
        ObjectOutputStream writer = null;

        try {
            ArrayList<Brano> brani = new ArrayList<>(Playlist.getPlaylist());
            writer = new ObjectOutputStream(new FileOutputStream(filename));
            writer.writeObject(brani);
        } catch(Exception e) {
            throw e;
        }
        finally {
            if (writer != null)
                writer.close();
        }
    }

}
