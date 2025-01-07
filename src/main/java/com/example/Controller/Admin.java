package com.example.Controller;

import com.example.database.ManagePlayer;
import com.example.database.Player;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

public class Admin {
    public Button back;
    public Button byName;
    public Label flag;
    public Label info;
    public HBox playersBox;
    private final HashMap<String, Image> imageCache = new HashMap<>();
    private HashMap<String, List<Player>> countrymap = ManagePlayer.getCountrymap();
    private HashMap<String, Player> players = ManagePlayer.getPlayermap();
    private Iterator<Map.Entry<String, List<Player>>> countryIterator;
    private Timeline slideshowTimeline;

    public void initialize() {
        countryIterator = countrymap.entrySet().iterator();
        Platform.runLater(this::startSlideshow);
        Platform.runLater(this::showPlayers);
    }

    private void startSlideshow() {
        slideshowTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            if (!countryIterator.hasNext()) {
                countryIterator = countrymap.entrySet().iterator();
            }
            if (countryIterator.hasNext()) {
                Map.Entry<String, List<Player>> entry = countryIterator.next();
                String country = entry.getKey();
                List<Player> players = entry.getValue();

                URL resource = getClass().getResource("/com/example/demo4/Flag/" + country + ".png");
                if (resource == null) {
                    resource = getClass().getResource("/com/example/demo4/Flag/default.png");
                }

                ImageView flagImageView = new ImageView(new Image(resource.toExternalForm()));
                flagImageView.setFitWidth(200);
                flagImageView.setFitHeight(100);
                FadeTransition fadeOutFlag = new FadeTransition(Duration.seconds(0.5), flag);
                fadeOutFlag.setFromValue(1.0);
                fadeOutFlag.setToValue(0.0);

                fadeOutFlag.setOnFinished(e -> {
                    flag.setGraphic(flagImageView);
                    FadeTransition fadeInFlag = new FadeTransition(Duration.seconds(0.5), flag);
                    fadeInFlag.setFromValue(0.0);
                    fadeInFlag.setToValue(1.0);
                    fadeInFlag.play();
                });
                fadeOutFlag.play();
                FadeTransition fadeOutInfo = new FadeTransition(Duration.seconds(0.5), info);
                fadeOutInfo.setFromValue(1.0);
                fadeOutInfo.setToValue(0.0);

                fadeOutInfo.setOnFinished(e -> {
                    info.setText(
                            country + "\nPlayers: " + players.size() +
                                    "\nBatsman: " + getsize("Batsman", country) +
                                    "\nBowler: " + getsize("Bowler", country) +
                                    "\nAllrounder: " + getsize("Allrounder", country) +
                                    "\nWicketkeeper: " + getsize("Wicketkeeper", country)
                    );
                    FadeTransition fadeInInfo = new FadeTransition(Duration.seconds(0.5), info);
                    fadeInInfo.setFromValue(0.0);
                    fadeInInfo.setToValue(1.0);
                    fadeInInfo.play();
                });
                fadeOutInfo.play();
            }
        }));

        slideshowTimeline.setCycleCount(Timeline.INDEFINITE);
        slideshowTimeline.play();
    }

    private void showPlayers() {
        playersBox.getChildren().clear();

        Task<Void> loadPlayersTask = new Task<>() {
            @Override
            protected Void call() {
                for (Player player : players.values()) {
                    VBox playerBox = createPlayerBox(player);
                    Platform.runLater(() -> playersBox.getChildren().add(playerBox));
                }
                return null;
            }
        };

        new Thread(loadPlayersTask).start();
        setupScrollingAnimation();
    }

    private void setupScrollingAnimation() {
        double totalWidth = players.values().size() * 120;
        if (totalWidth > 0) {
            playersBox.setTranslateX(800);
            TranslateTransition transition = new TranslateTransition(Duration.seconds(57), playersBox);
            transition.setFromX(800);
            transition.setToX(-totalWidth);
            transition.setCycleCount(Timeline.INDEFINITE);
            transition.setInterpolator(javafx.animation.Interpolator.LINEAR);
            transition.play();
            transition.setOnFinished(e -> playersBox.setTranslateX(800));
        }
    }

    private VBox createPlayerBox(Player player) {
        VBox playerBox = new VBox();
        playerBox.setSpacing(5);

        ImageView playerImageView = new ImageView();
        playerImageView.setFitWidth(100);
        playerImageView.setFitHeight(100);

        Image image = loadImage(player.getName());
        playerImageView.setImage(image);

        Label nameLabel = new Label(player.getName());
        Label ageLabel = new Label("Age: " + player.getAge());

        playerBox.getChildren().addAll(playerImageView, nameLabel, ageLabel);
        return playerBox;
    }

    private Image loadImage(String playerName) {
        if (imageCache.containsKey(playerName)) {
            return imageCache.get(playerName);
        }
        String imagePath = "src/main/java/com/example/PlayersImage/" + playerName + ".png";
        File imageFile = new File(imagePath);

        Image image;
        if (imageFile.exists()) {
            image = new Image(imageFile.toURI().toString());
        } else {
            File defaultFile = new File("src/main/java/com/example/PlayersImage/player1.png");
            image = new Image(defaultFile.toURI().toString());
        }
        imageCache.put(playerName, image);
        return image;
    }

    String getsize(String position, String country) {
        return Long.toString(countrymap.get(country).stream().filter(p -> p.getPosition().equals(position)).count());
    }

    public void backPressed(ActionEvent actionEvent) throws IOException {
        slideshowTimeline.stop();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/StartApplication.fxml"));
        Scene adminScene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setScene(adminScene);
        stage.setTitle("Player Database");
        com.example.Controller.StartApplication controller = fxmlLoader.getController();
        controller.countries.setText(Integer.toString(controller.countryNumber));
        controller.players.setText(Integer.toString(controller.playerNumber));
        controller.clubs.setText(Integer.toString(controller.clubNumber));
        stage.show();
    }

    public void SearchByName(ActionEvent actionEvent) throws IOException {
        slideshowTimeline.stop();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/byName.fxml"));
        Scene adminScene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setScene(adminScene);
        stage.setTitle("Search By Name");
        stage.show();
    }

    public void SearchBySalaryRange(ActionEvent actionEvent) throws IOException {
        slideshowTimeline.stop();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/salaryRange.fxml"));
        Scene adminScene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setScene(adminScene);
        stage.setTitle("Search By Salary Range");
        stage.show();
    }

    public void SearchByClubAndCountry(ActionEvent actionEvent) throws IOException {
        slideshowTimeline.stop();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/clubCountry.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Search By Club and Country");
        stage.setScene(scene);
        stage.show();
    }

    public void CountryWisePlayerCount(ActionEvent actionEvent) throws IOException {
        slideshowTimeline.stop();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/countryWisePlayerCount.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Country wise Player count");
        stage.setScene(scene);
        stage.show();
    }

    public void addPlayerPressed(ActionEvent actionEvent) throws IOException {
        slideshowTimeline.stop();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/addPlayer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Add Player");
        stage.setScene(scene);
        stage.show();
    }

    public void byclubPressed(ActionEvent actionEvent) throws IOException {
        slideshowTimeline.stop();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/byClub.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Search by Club");
        stage.setScene(scene);
        stage.show();
    }
}
