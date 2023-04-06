package com.aloiabiancalani.mp3player.model;

import com.aloiabiancalani.mp3player.Main;
import com.mpatric.mp3agic.*;
import javafx.concurrent.Task;


import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

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
            load(dataFolder);
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

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
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
                updateProgress(i, files.length); //update the progress bar
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // TODO: write playlist to the binary file


        }

        System.out.println("Thread end!");
        updateProgress(1,1); //set the progress as completed
        return null;
    }

    //TODO: implement loading playlist from the binary file
    private void load(File dataFolder) {

    }

}
