package com.example.Controller;

import com.example.application;
import com.example.database.Player;
import com.example.utils.NetworkMessage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class CountryWisePlayerCount {

    public Button back;
    public ListView<HBox> display;
    public Pane flow;
    HashMap<String, List<Player>> countryMap = new HashMap<>();

    public void initialize() {
        loadMap();
        show();
    }
    private void loadMap(){
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage("countryMap"));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            countryMap = (HashMap<String, List<Player>>) in.readObject();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void show() {
        display.getItems().clear();
        display.getItems().addAll(createRow("Countries", "Total Players"));
        display.getItems().add(new HBox());
        Text text = new Text("Country Wise Player Count");
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-text-fill: blue;");
        flow.getChildren().add(text);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(5), text);
        transition.setFromX(0);
        transition.setToX(200);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();
        int total  = 0;
        for (String country : countryMap.keySet()) {
            int totalPlayers = countryMap.get(country).size();
            animateCountingRow(country, totalPlayers,false);
            total += totalPlayers;
        }
        display.getItems().add(new HBox());
        display.getItems().add(new HBox());
        animateCountingRow("Total Players", total,true);
    }

    void animateCountingRow(String country, int targetCount, boolean isLarge) {
        Label countryLabel = new Label(country);
        countryLabel.setMinWidth(600);
        Label countLabel = new Label("0");
        countLabel.setMinWidth(450);
        if (isLarge) {
            countryLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
            countLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
        }
        HBox row = new HBox(10);
        row.getChildren().addAll(countryLabel, countLabel);
        display.getItems().add(row);
        Timeline timeline = new Timeline();
        for (int i = 1; i <= targetCount; i++) {
            int finalI = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(50 * i), event -> countLabel.setText(String.valueOf(finalI))));
        }
        timeline.play();
    }


    public void backPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("/com/example/demo4/Admin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Admin");
        stage.setScene(scene);
        stage.show();
    }

    HBox createRow(String label1, String label2) {
        Label l1 = new Label(label1);
        l1.setMinWidth(520);
        Label l2 = new Label(label2);
        l2.setMinWidth(450);
        HBox row = new HBox(10);
        l1.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        l2.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");
        row.getChildren().addAll(l1, l2);
        return row;
    }
}
