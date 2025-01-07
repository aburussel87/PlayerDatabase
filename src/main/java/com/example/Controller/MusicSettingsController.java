package com.example.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;

public class MusicSettingsController {
    @FXML
    public Slider slider;
    private MediaPlayer mediaPlayer;
    @FXML
    private Button toggle;
    private boolean isMusicPlaying = true;

    public void initialize() {
        if(isMusicPlaying) {
            toggle.setText("Turn off");
        }
        else toggle.setText("Turn on");
    }
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            changeVolume();
        });
    }


    public void toggleMusic() {
        if(mediaPlayer != null) {
            if (isMusicPlaying) {
                mediaPlayer.pause();
                toggle.setText("Turn On");
            } else {
                mediaPlayer.play();
                toggle.setText("Turn Off");
            }
            isMusicPlaying = !isMusicPlaying;
        }
    }

    public void changeVolume() {
        double volume = slider.getValue();
        mediaPlayer.setVolume(volume);
    }
}
