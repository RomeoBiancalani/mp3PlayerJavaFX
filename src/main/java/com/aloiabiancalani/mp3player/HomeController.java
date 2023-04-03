package com.aloiabiancalani.mp3player;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class HomeController {

    @FXML
    private Button button;

    @FXML
    private BorderPane homeId;

    public void initialize() {
//        Per mettere immagine circolare
//        circle.setFill(new ImagePattern(new Image("path")));
    }
    @FXML
    private void handleDirectoryChoice(MouseEvent event) {
        final DirectoryChooser directorychooser = new DirectoryChooser();
        Stage stage = (Stage) homeId.getScene().getWindow();
        File file = directorychooser.showDialog(stage);
        if(file != null) {
            System.out.println("Path: " + file.getAbsolutePath());
        }

    }
}