package com.example.Controller;

import com.example.database.ManagePlayer;
import com.example.database.Player;
import com.example.user.Standardization;
import com.example.utils.NetworkMessage;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class Details {
    public ImageView image;
    public ScrollPane details;
    Player ob = null;
    public void initialize() {
        Platform.runLater(() -> {
            HashMap<String,Player> players = new HashMap<>();
            if (details != null && details.getScene() != null) {
                Stage stage = (Stage) details.getScene().getWindow();
                if (stage != null) {
                    String pname = stage.getTitle();
                    try {
                        Socket socket = new Socket("localhost", 12345);
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(new NetworkMessage("playerMap"));
                        out.flush();
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        players = (HashMap<String,Player>) in.readObject();
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    show(players.get(pname));
                } else {
                    System.err.println("Stage is null.");
                }
            } else {
                System.err.println("Display or its scene is not initialized.");
            }
        });
    }
    void show(Player ob) {
        int labelWidtht = 110;
        int width = 150;
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
        nameValue.setMinWidth(width);
        Label ageValue = new Label(String.valueOf(ob.getAge()));
        ageValue.setMinWidth(width);
        Label heightValue = new Label(String.format("%.2f m", ob.getHeight()));
        heightValue.setMinWidth(width);
        Label countryValue = new Label(ob.getCountry());
        countryValue.setMinWidth(width);
        Label clubValue = new Label(ob.getClub());
        clubValue.setMinWidth(width);
        Label positionValue = new Label(ob.getPosition());
        positionValue.setMinWidth(width);

        Label jerseyValue;
        if (ob.getJersey() != -1) {
            jerseyValue = new Label(String.valueOf(ob.getJersey()));
        } else {
            jerseyValue = new Label("N/A");
        }
        jerseyValue.setMinWidth(width);

        Label salaryValue = new Label(Standardization.toMillion(ob.getSalary()));
        salaryValue.setMinWidth(width);
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
        details.setContent(detailsBox);
        image.setImage(getImage(ob.getName()));
    }




    Image getImage(String name) {
        String imagePath = "src/main/java/com/example/PlayersImage/" + name + ".png";
        File file = new File(imagePath);

        if (file.exists()) {
            return new Image(file.toURI().toString());
        }
        File defaultFile = new File("src/main/java/com/example/PlayersImage/player1.png");
        return defaultFile.exists() ? new Image(defaultFile.toURI().toString()) : null;
    }
}
