package com.aloiabiancalani.mp3player.controller;

import com.aloiabiancalani.mp3player.Main;
import com.aloiabiancalani.mp3player.model.FolderLoader;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoadingController {

    @FXML
    private ProgressBar progressBar;
    private Stage stage;

    public String loadingPath;

    public LoadingController(String loadingPath) {

        this.loadingPath = loadingPath;
    }

    //empty constructor
    public LoadingController() {

    }

    //initialize the controller
    public void initialize() {


        this.stage = new Stage();

//        LoadingController.loadingPath = loadingPath;
        stage.setOnCloseRequest(event -> {
            // prevent window from closing manually
            event.consume();
        });
    }



    //start the Thread to load mp3 files
    public void startTask() {
        this.initialize(); //initialize controller
        progressBar = new ProgressBar();
        Task<Void> task = new FolderLoader(loadingPath, progressBar);
        progressBar.progressProperty().bind(task.progressProperty()); //bind the progress bar to the thread


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Loading.fxml"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Caricamento della cartella");
        stage.setWidth(300);
        stage.setHeight(200);
        stage.setResizable(false);

        try {
            Scene scene = new Scene(fxmlLoader.load(), 300, 200);
            stage.setScene(scene);
            System.out.println("Caricamento cartella " + loadingPath);
            FolderLoader folderLoader = new FolderLoader(loadingPath, progressBar);
            stage.show(); //show stage
            new Thread(folderLoader).start();

            folderLoader.setOnSucceeded(event -> {
                // task succeeded, close window
                stage.close();
            });

//            task.setOnFailed(event -> {
//            // task failed, close window
//                stage.close();
//            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void cancelTask() {
        // cancel task if running
    }


}
