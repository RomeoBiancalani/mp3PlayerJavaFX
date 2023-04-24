package com.aloiabiancalani.mp3player.controller;

import com.aloiabiancalani.mp3player.Main;
import com.aloiabiancalani.mp3player.model.Brano;
import com.aloiabiancalani.mp3player.model.Playlist;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
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

public class HomeController {
    private String folderPath;

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

    public void initialize() {
        setupTableView();
        handleControlButtons();
        handleMouseEvents();
    }

    public String getFolderPath() {
        return folderPath;
    }

    // setup tabella
    private void setupTableView() {
        nomeFieldTable.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        durataFieldTable.setCellValueFactory(new PropertyValueFactory<>("lunghezza"));
        songsTable.setItems(Playlist.getPlaylist());
    }


    // gestione drag and drop
    public void handleMouseEvents() {
        // Setup del factory della tabella (quando viene aggiunta una nuova riga viene aggiunto il listener per il doppio click)
        songsTable.setRowFactory(tv -> {
            TableRow<Brano> row = new TableRow<>();

            // click su una canzone
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) { // doppio click: riproduci brano
                    Brano rowData = row.getItem();

                    // metto in pausa il brano corrente
                    if (Playlist.getPlayer() != null) {
                        Playlist.getPlayer().pause();
                    }

                    handlePlayButton(); // cambio icona del tasto play
                    playBrano(rowData); // set brano corrente
                }
            });


            // gestione drag and drop per la riga
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

                    if(targetIndex >= Playlist.getPlaylist().size()) { // se l'indice e' out of bounds (maggore della dimensione massima della playlist)
                        targetIndex = Playlist.getPlaylist().size() -1; // spostamento del brano nell'ultima posizione
                    }

                    if (draggedIndex != targetIndex) {
                        ObservableList<Brano> items = songsTable.getItems();
                        Brano draggedItem = items.get(draggedIndex);
                        items.remove(draggedIndex);
                        items.add(targetIndex, draggedItem);
                        shiftItems(items, targetIndex, draggedIndex); // shift brani della playlist
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

    // scambia posizione dei brani durante il drag and drop
    private void shiftItems(ObservableList<Brano> items, int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex >= items.size()) { // index out of bounds
            return;
        }
        if (startIndex > endIndex) { // swap indici se startIndex > endIndex
            int temp = startIndex;
            startIndex = endIndex;
            endIndex = temp;
        }

        Brano temp = items.get(endIndex); // get canzone da spostare

        int i = 0;
        for (i = endIndex; i > startIndex+1; i--) { // set nuovo ordine dei brani
            items.set(i, items.get(i-1));
        }

        items.set(endIndex, temp); // canzone spostata nella posizione corretta
    }


    // gestisce la scelta della directory
    @FXML
    private void handleDirectoryChoice(MouseEvent event) {
        final DirectoryChooser directorychooser = new DirectoryChooser();
        Stage stage = (Stage) homeId.getScene().getWindow();
        File file = directorychooser.showDialog(stage);
        handleDirectoryLoading(file, stage);
    }

    //apre la finestra di caricamento e carica la cartella
    public void handleDirectoryLoading(File file, Stage stage) {


        if(file != null) {
            // setup nuova finestra di caricamento
            Stage loadingStage = new Stage();

            loadingStage.setOnCloseRequest(loadingEvent -> {
                // impedisce alla finestra di chiudersi manualmente
                loadingEvent.consume();
            });

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/LoadingView.fxml")); // load fxml
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

            loadingStage.setOnHidden(windowEvent -> { // codice per quando viene chiusa la finestra di caricamento
                if(Playlist.getPlaylist().size() != 0)
                    setupFirstPlay();
            });
            loadingStage.show(); // mostra finestra di caricamento

            folderPath = file.getAbsolutePath();
        }
    }

    // handler per il controllo della riproduzione
    private void handleControlButtons() {
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

    // setup riproduzione
    private void setupFirstPlay() {
        Brano playing = Playlist.getPlayingBrano();
        songsTable.getSelectionModel().select(Playlist.getPlayingIndex());
        if (playing.getTitolo() != null) {
            songTitle.setText(playing.getTitolo());
        }
        else {
            String titolo = playing.getTitolo();
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

        Media song = new Media(new File(playing.getSongPath()).toURI().toString());

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


    // gestione riproduzione brano corrente
    private void playBrano(Brano playing) {
        playingBrano(playing);
    }

    // setup informazioni sul brano corrente e riproduzione
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
        Media song = new Media(new File(playing.getSongPath().replace("/","\\")).toURI().toString()); // set path del brano
        MediaPlayer player = new MediaPlayer(song);
        player.setOnReady(() -> { // riproduzione brano
            player.play();
            player.setOnEndOfMedia(() -> {
                player.dispose();
                playNext();
            });
        });
        Playlist.setPlayer(player);
    }


    // gestione riproduzione della prossima canzone
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

    // gestione replay della canzone corrente
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

    // switch da play a pause button
    @FXML
    private void handlePlayButton() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.PAUSE);
        icon.setFill(Color.rgb(35,0,250));
        icon.setSize("40");
        icon.setTabSize(8);
        playBtn.setGraphic(icon);
    }

    // switch da pause a play button
    @FXML
    private void handlePauseButton() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
        icon.setFill(Color.rgb(35,0,250));
        icon.setSize("40");
        icon.setTabSize(8);
        playBtn.setGraphic(icon);
    }


    // playlist shuffle handler
    @FXML
    private void handleShuffle(MouseEvent mouseEvent) {
        Brano playing = Playlist.getPlayingBrano();
        Playlist.shuffle();
        int newIndex = Playlist.getPlaylist().indexOf(playing);
        Playlist.setPlayingIndex(newIndex);
    }



}