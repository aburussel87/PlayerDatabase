package com.example.Controller;

import com.example.database.ManagePlayer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class StartApplication {

    public Label player;
    public Label database;
    public Button admin;
    public ImageView image;
    public Label players;
    public Label countries;
    public Label clubs;
    public Button cm;
    public Button music;
    private FXMLLoader fxmlLoader;
     int playerNumber = Integer.parseInt(String.valueOf(ManagePlayer.getPlayermap().size()));
     int countryNumber = Integer.parseInt(String.valueOf(ManagePlayer.getCountrymap().size()));
     int clubNumber = Integer.parseInt(String.valueOf(ManagePlayer.getClubmap().size()));
    private MediaPlayer mediaPlayer;
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
    public void startCounter(Label label, int targetNumber) {
        Timeline counterTimeline = new Timeline();
        final int[] currentNumber = {0};

        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(30),
                event -> {
                    if (currentNumber[0] <= targetNumber) {
                        label.setText(String.valueOf(currentNumber[0]));
                        currentNumber[0]++;
                    }
                }
        );

        counterTimeline.getKeyFrames().add(keyFrame);
        counterTimeline.setCycleCount(Timeline.INDEFINITE);
        counterTimeline.play();
    }

    public void playTypewriterEffect() {
        database.setText("");
        String playerText = "Player";
        String databaseText = "Database";
        Timeline playerTimeline = new Timeline();
        StringBuilder playerDisplayedText = new StringBuilder();
        for (int i = 0; i < playerText.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(100 * (i + 1)),
                    event -> {
                        playerDisplayedText.append(playerText.charAt(index));
                        player.setText(playerDisplayedText.toString());
                    }
            );
            playerTimeline.getKeyFrames().add(keyFrame);
        }

        Timeline databaseTimeline = new Timeline();
        StringBuilder databaseDisplayedText = new StringBuilder();
        for (int i = 0; i < databaseText.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(100 * (i + 1)),
                    event -> {
                        databaseDisplayedText.append(databaseText.charAt(index));
                        database.setText(databaseDisplayedText.toString());
                    }
            );
            databaseTimeline.getKeyFrames().add(keyFrame);
        }

        playerTimeline.setOnFinished(event -> databaseTimeline.play());
        playerTimeline.play();
        databaseTimeline.setOnFinished(event -> {
            startCounter(players, playerNumber);
            startCounter(countries, countryNumber);
            startCounter(clubs, clubNumber);
        });
    }
    public void adminpressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/Admin.fxml"));
        Scene adminScene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage)admin.getScene().getWindow();
        stage.setScene(adminScene);
        stage.setTitle("Admin");
        stage.show();
    }

    public void cmpressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/cmLogin.fxml"));
        Scene adminScene = new Scene(fxmlLoader.load(), 500, 300);
        Stage stage = new Stage();
        stage.setScene(adminScene);
        stage.setTitle("Club Manager Login");
        stage.show();
    }

    public void musicPressed(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/musicSettings.fxml"));
            Scene musicScene = new Scene(fxmlLoader.load(), 400, 200);
            Stage stage = new Stage();
            stage.setScene(musicScene);
            stage.setTitle("Music Settings");
            MusicSettingsController musicSettingsController = fxmlLoader.getController();
            musicSettingsController.setMediaPlayer(mediaPlayer);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error opening music settings window.");
        }
    }
}
