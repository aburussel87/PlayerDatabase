package com.example;

import com.example.Controller.StartApplication;
import com.example.database.ManagePlayer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;
import java.io.IOException;

public class application extends Application {

    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("/com/example/demo4/StartApplication.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        stage.setTitle("Player Database");
        stage.setScene(scene);
        stage.show();
        com.example.Controller.StartApplication controller = fxmlLoader.getController();
        controller.playTypewriterEffect();
        playBackgroundMusic(fxmlLoader);
    }

    private void playBackgroundMusic(FXMLLoader fxmlLoader) {
        try {
            URL resource = getClass().getResource("/com/example/demo4/music/main.mp3");
            if (resource == null) {
                System.err.println("Music file not found! Check the path.");
                return;
            }
            Media media = new Media(resource.toString());
            mediaPlayer = new MediaPlayer(media);
            StartApplication startApplication = fxmlLoader.getController();
            startApplication.setMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void reset(){
        ManagePlayer.loadplayers();
    }
    public static void main(String[] args) {
        ManagePlayer.loadplayers();
        launch();
        ManagePlayer.saveToFile();
    }
}
