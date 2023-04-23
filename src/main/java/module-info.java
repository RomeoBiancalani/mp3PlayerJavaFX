module com.aloiabiancalani.mp3player.mp3playerjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires mp3agic;
    requires de.jensd.fx.glyphs.fontawesome;


    opens com.aloiabiancalani.mp3player to javafx.fxml;
    exports com.aloiabiancalani.mp3player;
    exports com.aloiabiancalani.mp3player.controller;
    exports com.aloiabiancalani.mp3player.model;
    opens com.aloiabiancalani.mp3player.controller to javafx.fxml;
    opens com.aloiabiancalani.mp3player.model to javafx.fxml;
}