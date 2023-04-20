package com.aloiabiancalani.mp3player.controller;
import com.aloiabiancalani.mp3player.model.FolderLoader;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class LoadingController {

    @FXML
    private ProgressBar progressBar = new ProgressBar(0); // progress bar
    private Stage stage;
    public String loadingPath; // path della cartella da caricare


    //costruttore vuoto
    public LoadingController() {

    }

    public void initialize() {
    }



    //start thread to load mp3 files
    public void startTask(String loadingPath, Stage loadingStage) {
        this.loadingPath = loadingPath; // set path cartella
        this.stage = loadingStage; // set stage

        System.out.println("Caricamento cartella " + loadingPath);
        Task<Void> folderLoader = new FolderLoader(loadingPath);
        progressBar.progressProperty().bind(folderLoader.progressProperty()); //bind la progress bar al worker thread

        // event listener per tracciare il valore della progress bar
        progressBar.progressProperty().addListener(observable -> {
                System.out.println("Changed: " + progressBar.progressProperty().get());
        });

        try {
            new Thread(folderLoader).start();
        }
        catch(Exception e) {
            folderLoader.setOnFailed(event -> {
                // task failed, chiudi finestra
                System.out.println("Loading failed!");
                stage.close();
            });
        }

        folderLoader.setOnSucceeded(event -> {
            // task succeeded, chiudi finestra
            System.out.println("Success!");
            stage.close();
        });

    }

}
