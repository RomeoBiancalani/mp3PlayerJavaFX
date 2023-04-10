package com.aloiabiancalani.mp3player.controller;

import com.aloiabiancalani.mp3player.Main;
import com.aloiabiancalani.mp3player.model.Playlist;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HomeController {

    @FXML
    private Button button;

    @FXML
    private BorderPane homeId;

    public void initialize() {
//        Per mettere immagine circolare
//        circle.setFill(new ImagePattern(new Image("path")));
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
            loadingStage.show(); // mostra finestra di caricamento
        }

    }

    // playlist shuffle handler
    @FXML
    private void handleShuffle(MouseEvent mouseEvent) {
        Playlist.shuffle();
        System.out.println(Playlist.getPlaylist());
    }
}