package com.aloiabiancalani.mp3player;

import com.aloiabiancalani.mp3player.controller.HomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/HomeView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 800, 640);
        HomeController homeController =  fxmlLoader.getController();
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setTitle("Mp3 Player");
        stage.setResizable(false);
        stage.setWidth(800);
        stage.setHeight(640);
        stage.setScene(scene);

        String folderPath = null;

        // load path della cartella precedentemente caricata
        try {
            FileReader fileReader = new FileReader("src/main/resources/com/aloiabiancalani/mp3player/folderPath.txt");
            BufferedReader reader = new BufferedReader(fileReader);
            folderPath = reader.readLine();

        } catch(Exception e) {
            System.out.println("errore in lettura");
        }

        if(folderPath != null) {
            File initialFolder = new File(folderPath);
            if(initialFolder.exists() && initialFolder.isDirectory()) { // se la cartella esiste
                homeController.handleDirectoryLoading(initialFolder, stage);
            }
        }

        stage.show(); // mostra finestra

        // quando viene chiusa la finestra
        stage.setOnCloseRequest(ClosingEvent -> {

            // salvataggio su file .txt della path dell'ultima cartella caricata
            try {
                FileWriter writer = new FileWriter("src/main/resources/com/aloiabiancalani/mp3player/folderPath.txt");
                writer.write(homeController.getFolderPath());
                writer.close();
                System.out.println("Path della cartella salvata correttamente!");
            }
            catch (Exception e) {
                System.out.println("Nessuna path salvata.");
            }
        });

    }

    public static void main(String[] args) {
        launch();
    }


}