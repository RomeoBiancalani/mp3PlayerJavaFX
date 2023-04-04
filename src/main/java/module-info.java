module com.aloiabiancalani.mp3player.mp3playerjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires mp3agic;


    opens com.aloiabiancalani.mp3player to javafx.fxml;
    exports com.aloiabiancalani.mp3player;
    exports com.aloiabiancalani.mp3player.controller;
    opens com.aloiabiancalani.mp3player.controller to javafx.fxml;
}