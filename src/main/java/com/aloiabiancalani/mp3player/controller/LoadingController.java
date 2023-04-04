package com.aloiabiancalani.mp3player.controller;

import com.aloiabiancalani.mp3player.Main;
import com.aloiabiancalani.mp3player.model.FolderLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class LoadingController {

    @FXML
    private ProgressBar progressBar;
    private static Scene scene;

    public static String loadingPath;


    public static void display(String loadingPath) {
        LoadingController.loadingPath = loadingPath;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Loading.fxml"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Caricamento della cartella");
        stage.setWidth(300);
        stage.setHeight(200);
        stage.setResizable(false);

        try {
            Scene scene = new Scene(fxmlLoader.load(), 300, 200);
            LoadingController.scene = scene;
            stage.setScene(scene);
            System.out.println("Caricamento cartella " + LoadingController.loadingPath);
            FolderLoader folderLoader = new FolderLoader(scene);
            folderLoader.start();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public void initialize() {
//        System.out.println("Caricamento cartella " + LoadingController.loadingPath);
//        FolderLoader folderLoader = new FolderLoader(progressBar, LoadingController.scene.getWindow());
//        folderLoader.start();
//    }

}
