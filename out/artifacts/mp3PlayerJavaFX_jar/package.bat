jpackage --type exe --input . --dest . --main-jar .\mp3PlayerJavaFX.jar --main-class com.aloiabiancalani.mp3player.Main --module-path .\javafx-jmods-17.0.7\ --add-modules javafx.controls,javafx.fxml,javafx.media --win-shortcut --win-menu --vendor "Aloia Biancalani" --name "Mp3 Player" --app-version "1.0" --icon .\icon.ico