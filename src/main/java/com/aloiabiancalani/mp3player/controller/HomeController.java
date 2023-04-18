package com.aloiabiancalani.mp3player.controller;

import com.aloiabiancalani.mp3player.Main;
import com.aloiabiancalani.mp3player.model.Brano;
import com.aloiabiancalani.mp3player.model.Playlist;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class HomeController {

    @FXML
    private BorderPane homeId;
    @FXML
    private Button backwardBtn;

    @FXML
    private Button forwardBtn;


    @FXML
    private Button playBtn;

    @FXML
    private Circle songCover;

    @FXML
    private Label songInfo;

    @FXML
    private Label songTitle;

    @FXML
    private TableView<Brano> songsTable;

    @FXML
    private TableColumn<Brano, String> durataFieldTable;

    @FXML
    private TableColumn<Brano, String> nomeFieldTable;

    @FXML
    void handleBackwards(MouseEvent event) {

    }

    @FXML
    void handleForward(MouseEvent event) {

    }

    @FXML
    void handlePlay(MouseEvent event) {

    }


    public void initialize() {
//        Per mettere immagine circolare
//        circle.setFill(new ImagePattern(new Image("path")));
        setupTableView();
        setupSongClick();
        setupControlsButtons();
//        setupDragAndDrop(); // Rotto, da fixare
//        setupCurrentlyPlaying();
    }

    private void setupControlsButtons() {
        playBtn.setOnMouseClicked(mouseEvent -> {
            boolean playing = Playlist.getPlayer().getStatus() == MediaPlayer.Status.PLAYING;
            if (playing) //pausing
            {
                Playlist.getPlayer().pause();
                handlePauseButton();


            }
            else { //playing
                Playlist.getPlayer().play();
                handlePlayButton();
            }
        });

        forwardBtn.setOnMouseClicked(mouseEvent -> {
            playNext();
        });

        backwardBtn.setOnMouseClicked(mouseEvent -> {
            replay();
        });
    }

    private void setupFirstPlay() {
        Brano playing = Playlist.getPlayingBrano();
        songsTable.getSelectionModel().select(Playlist.getPlayingIndex());
        if (playing.getTitolo() != null) {
            songTitle.setText(playing.getTitolo());
        }
        else {
            String titolo = getPathBrano(playing.getPathCopertina());
            songTitle.setText(titolo.substring(titolo.lastIndexOf("\\") + 1));
        }
        if (playing.getArtista() == null && playing.getAlbum() == null) {
            songInfo.setText("Artista Sconosciuto - Album Sconosciuto");
        }
        else if (playing.getArtista() == null) {
            songInfo.setText("Artista Sconosciuto - " + playing.getAlbum());
        }
        else if (playing.getAlbum() == null) {
            songInfo.setText(playing.getArtista() + " - Album Sconosciuto");
        }
        else {
            songInfo.setText(playing.getArtista() + " - " + playing.getAlbum());
        }
        songCover.setFill(new ImagePattern(new Image(playing.getPathCopertina())));
        File songFile = new File(getPathBrano(playing.getPathCopertina()));
        Media song = new Media(getPathBranoPlayer(playing));
        MediaPlayer player = new MediaPlayer(song);
        player.setOnReady(() -> {
            player.setOnEndOfMedia(() -> {
                // Passo alla canzone dopo
                playNext();
                player.dispose();
            });
        });
        Playlist.setPlayer(player);
    }

    private void playNext() {
        if (Playlist.getPlayingIndex() == Playlist.getPlaylist().size() - 1) {
            // Se e' arrivato all'ultimo elemento ha finito la coda e quindi si interrompe
            if(Playlist.getPlayer() != null) {
                Playlist.getPlayer().pause();
                handlePauseButton();
            }
            return;
        }
        if(Playlist.getPlayer() != null) {
            Playlist.getPlayer().pause();
        }

        Playlist.forwardPlayingBrano();
        handlePlayButton();
        Brano playing = Playlist.getPlayingBrano();
        songsTable.getSelectionModel().select(Playlist.getPlayingIndex());
        playingBrano(playing);
    }

    private void replay() {
        if(Playlist.getPlayer() != null) {
            Playlist.getPlayer().pause();
        }

        Playlist.backwardPlayingBrano();
        handlePlayButton();

        Brano playing = Playlist.getPlayingBrano();
        songsTable.getSelectionModel().select(Playlist.getPlayingIndex());
        playingBrano(playing);
    }

    private void playBrano(Brano playing) {
        playingBrano(playing);
    }

    private void playingBrano(Brano playing) {
        songTitle.setText(playing.getTitolo());
        if (playing.getArtista() == null && playing.getAlbum() == null) {
            songInfo.setText("Artista Sconosciuto - Album Sconosciuto");
        }
        else if (playing.getArtista() == null) {
            songInfo.setText("Artista Sconosciuto - " + playing.getAlbum());
        }
        else if (playing.getAlbum() == null) {
            songInfo.setText(playing.getArtista() + " - Album Sconosciuto");
        }
        else {
            songInfo.setText(playing.getArtista() + " - " + playing.getAlbum());
        }
        songCover.setFill(new ImagePattern(new Image(playing.getPathCopertina())));
        Media song = new Media(getPathBranoPlayer(playing));
        MediaPlayer player = new MediaPlayer(song);
        player.setOnReady(() -> {
            player.play();
            player.setOnEndOfMedia(() -> {
                player.dispose();
                playNext();
            });
        });
        Playlist.setPlayer(player);
    }

    private String getPathBranoPlayer(Brano playing) {
//        return "file:" + new File(getPathBrano(playing.getPathCopertina())).toURI().toString();
        return new File(getPathBrano(playing.getPathCopertina())).toURI().toString();
    }
    private String getPathBrano(String copertina) {
        return copertina.replace("\\.data","").replace("/", "\\").replace(".jpg", ".mp3");
    }

    private void setupSongClick() {
        // Setup del factory della tabella (quando viene aggiunta una nuova riga viene aggiunto il listener per il doppio click)
        songsTable.setRowFactory(tv -> {
            TableRow<Brano> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
//                System.out.println("Evento: " + event);
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Brano rowData = row.getItem();
                    System.out.println("Doppio click: " + rowData);

                    // metto in pausa il brano corrente
                    if(Playlist.getPlayer() != null) {
                        Playlist.getPlayer().pause();
                    }


                    handlePlayButton();


                    playBrano(rowData);
                }
            });
            return row;
        });
    }


    private void setupTableView() {
        nomeFieldTable.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        durataFieldTable.setCellValueFactory(new PropertyValueFactory<>("lunghezza"));
        songsTable.setItems(Playlist.getPlaylist());
    }


    public void setupDragAndDrop() {
        songsTable.setRowFactory(tv -> {
            TableRow<Brano> row = new TableRow<>();

            // Set up drag and drop for the row
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.put(DataFormat.PLAIN_TEXT, "Brano");
                    db.setContent(content);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString() && db.getString().equals("Brano")) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString() && db.getString().equals("Brano")) {
                    int draggedIndex = songsTable.getSelectionModel().getSelectedIndex();
                    int targetIndex = row.getIndex();
                    if (draggedIndex != targetIndex) {
                        ObservableList<Brano> items = songsTable.getItems();
                        Brano draggedItem = items.get(draggedIndex);
                        items.remove(draggedIndex);
                        items.add(targetIndex, draggedItem);
                        shiftItems(items, Math.min(draggedIndex, targetIndex), targetIndex);
                        songsTable.getSelectionModel().select(targetIndex);
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                }
            });

            return row;
        });
    }

    private void shiftItems(ObservableList<Brano> items, int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex >= items.size()) {
            return;
        }
        if (startIndex > endIndex) {
            int temp = startIndex;
            startIndex = endIndex;
            endIndex = temp;
        }
        Brano temp = items.get(endIndex);
        for (int i = endIndex; i > startIndex; i--) {
            items.set(i, items.get(i - 1));
        }
        items.set(startIndex, temp);
    }

    //gestisce la scelta della directory e apre la finestra di caricamento
    @FXML
    private void handleDirectoryChoice(MouseEvent event) {
        final DirectoryChooser directorychooser = new DirectoryChooser();
        Stage stage = (Stage) homeId.getScene().getWindow();
        File file = directorychooser.showDialog(stage);

        if(file != null) {
            // setta la nuova finestra di caricamento
            Stage loadingStage = new Stage();

            loadingStage.setOnCloseRequest(loadingEvent -> {
                // prevent window from closing manually
                loadingEvent.consume();
            });

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Loading.fxml")); // load fxml
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            loadingStage.setTitle("Caricamento della cartella");
            loadingStage.setWidth(300);
            loadingStage.setHeight(200);
            loadingStage.setResizable(false);

            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 300, 200);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            loadingStage.setScene(scene);
            LoadingController controller = fxmlLoader.getController();
            controller.startTask(file.getAbsolutePath(), loadingStage);

            loadingStage.setOnHidden(windowEvent -> {
                // code to run when the loading stage is closed
                setupFirstPlay();
            });
            loadingStage.show(); // mostra finestra di caricamento

        }



    }

    // playlist shuffle handler
    @FXML
    private void handleShuffle(MouseEvent mouseEvent) {
        Playlist.shuffle();
        System.out.println(Playlist.getPlaylist());
    }


    // switch from play to pause button
    @FXML
    private void handlePlayButton() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.PAUSE);
        icon.setFill(Color.rgb(35,0,250));
        icon.setSize("40");
        icon.setTabSize(8);
        playBtn.setGraphic(icon);
    }

    // switch from pause to play button
    @FXML
    private void handlePauseButton() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
        icon.setFill(Color.rgb(35,0,250));
        icon.setSize("40");
        icon.setTabSize(8);
        playBtn.setGraphic(icon);
    }
}