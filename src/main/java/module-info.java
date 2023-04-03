module com.aloiabiancalani.mp3player.mp3playerjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.aloiabiancalani.mp3player to javafx.fxml;
    exports com.aloiabiancalani.mp3player;
}