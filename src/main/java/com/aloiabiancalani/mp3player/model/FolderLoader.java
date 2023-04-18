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
        System.out.println("Thread start!");
        File mp3Folder = new File(loadingPath);
        File dataFolder = new File(Paths.get(mp3Folder.getAbsolutePath() + "/.data").toUri());
        if (dataFolder.exists()) {
            System.out.println("Cartella .data gia' esistente");
            loadFromFile(mp3Folder.getAbsolutePath() + "/.data/brani.bin");
            updateProgress(1,1); // set the progress as completed
            Thread.sleep(500);
            return null;
        }

        // Creazione cartella
        if (!dataFolder.mkdir()) {
            throw new RuntimeException("Errore durante la creazione della cartella data.");
        }
        try {
            Files.setAttribute(dataFolder.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File[] files = mp3Folder.listFiles();
        System.out.println("Files: ");
//        for (File file : files) {
//            System.out.println(file);
//        }

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
                        byte[] copertina;


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
                        System.out.printf("Nome %s, Artista %s, Album %s, Copertina: %s, durata %d\n", nome, artista, album, copertinaName, durataBrano);
                        Brano brano = new Brano(nome, artista, album, copertinaName, durataBrano);

                        Playlist.addBrano(brano);
                        System.out.println(brano);

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
        System.out.println("Salvo il bin: " + mp3Folder.getAbsolutePath() + "/.data/brani.bin");
        saveToFile(mp3Folder.getAbsolutePath() + "/.data/brani.bin");
        System.out.println("Salvato!");
        updateProgress(1,1); // set the progress as completed
        Thread.sleep(250);
        System.out.println("Thread end!");

        return null;
    }

    // loading dei dati su file binario se la cartella .data esiste gia'
    private void loadFromFile(String filename) throws Exception {
        ObjectInputStream reader=null;

        try {
            reader = new ObjectInputStream(new FileInputStream(filename));
            ArrayList<Brano> app =(ArrayList<Brano>) reader.readObject();

//            Playlist.setPlaylist(FXCollections.observableArrayList(app)); // set the playlist
            Playlist.addAll(app);
            System.out.println(Playlist.getPlaylist());
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
//        ObjectOutputStream writer = null;
//
//        try {
//            writer = new ObjectOutputStream(new FileOutputStream(filename));
//            writer.writeObject(Playlist.getPlaylist());
//        } catch(Exception e) {
//            throw e; //rilancio l'eccezione
//        }
//        finally {
//            if (writer != null)
//                writer.close();
//        }
        ArrayList<Brano> brani = new ArrayList<>(Playlist.getPlaylist());
        ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(filename));
        writer.writeObject(brani);
    }

}
