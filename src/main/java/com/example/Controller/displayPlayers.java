package com.example.Controller;

import com.example.database.Player;
import com.example.user.Standardization;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;

public class displayPlayers {
    private static MediaPlayer mediaPlayer;
    static HBox display_player(Player ob) {
        int labelWidtht = 110;
        Label nameLabel = new Label("Name");
        nameLabel.setMinWidth(labelWidtht);

        Label ageLabel = new Label("Age");
        ageLabel.setMinWidth(labelWidtht);

        Label heightLabel = new Label("Height");
        heightLabel.setMinWidth(labelWidtht);

        Label countryLabel = new Label("Country");
        countryLabel.setMinWidth(labelWidtht);

        Label clubLabel = new Label("Club");
        clubLabel.setMinWidth(labelWidtht);

        Label positionLabel = new Label("Position");
        positionLabel.setMinWidth(labelWidtht);

        Label jerseyLabel = new Label("Jersey No");
        jerseyLabel.setMinWidth(labelWidtht);

        Label salaryLabel = new Label("Weekly Salary");
        salaryLabel.setMinWidth(labelWidtht);

        Label nameValue = new Label(ob.getName());
        Label ageValue = new Label(String.valueOf(ob.getAge()));
        Label heightValue = new Label(String.format("%.2f m", ob.getHeight()));
        Label countryValue = new Label(ob.getCountry());
        Label clubValue = new Label(ob.getClub());
        Label positionValue = new Label(ob.getPosition());

        Label jerseyValue;
        if (ob.getJersey() != -1) {
            jerseyValue = new Label(String.valueOf(ob.getJersey()));
        } else {
            jerseyValue = new Label("N/A");
        }

        Label salaryValue = new Label(Standardization.toMillion(ob.getSalary()));

        VBox detailsBox = new VBox();
        detailsBox.getChildren().addAll(
                new HBox(nameLabel, nameValue),
                new HBox(ageLabel, ageValue),
                new HBox(heightLabel, heightValue),
                new HBox(countryLabel, countryValue),
                new HBox(clubLabel, clubValue),
                new HBox(positionLabel, positionValue),
                new HBox(jerseyLabel, jerseyValue),
                new HBox(salaryLabel, salaryValue)
        );

        Image playerImage = getImage(ob.getName());
        ImageView imageView = new ImageView(playerImage);
        imageView.setFitWidth(130);
        imageView.setFitHeight(130);

        HBox playerBox = new HBox(10);
        playerBox.setAlignment(Pos.CENTER_LEFT);
        playerBox.getChildren().addAll(imageView, detailsBox);
        return playerBox;
    }

    static Image getImage(String name) {
        String imagePath = "src/main/java/com/example/PlayersImage/" + name + ".png";
        File file = new File(imagePath);

        if (file.exists()) {
            return new Image(file.toURI().toString());
        }
        File defaultFile = new File("src/main/java/com/example/PlayersImage/player1.png");
        return defaultFile.exists() ? new Image(defaultFile.toURI().toString()) : null;
    }

    public static void buttonPressed(){
        try {
            URL resource = displayPlayers.class.getResource("/com/example/demo4/music/button.mp3");
            if (resource == null) {
                System.err.println("Music file not found! Check the path.");
                return;
            }
            Media media = new Media(resource.toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
