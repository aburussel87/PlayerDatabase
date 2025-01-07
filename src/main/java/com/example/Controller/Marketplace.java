package com.example.Controller;

import com.example.application;
import com.example.database.ManagePlayer;
import com.example.database.Player;
import com.example.utils.NetworkMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Marketplace{
    public Button reload;
    int c = 0;
    public Button Back;
    public ListView list;
    public Label alert;
    private String club;
    public void initialize(){
        Platform.runLater(() -> {
            if (Back != null && Back.getScene() != null) {
                Stage stage = (Stage) Back.getScene().getWindow();
                if (stage != null) {
                    club = stage.getTitle();
                    loadMarketplacePlayers(club);
                }
            }
        });
    }
    public void setclub(String club){
        this.club = club;
    }
    static HashMap<String,Player> marketplacePlayers;
    private void loadMarketplacePlayers(String cl) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 12345);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(new NetworkMessage("getMarketplace", null, cl));
                out.flush();
                Object response = in.readObject();
                if(response instanceof HashMap<?,?>) {
                    marketplacePlayers = (HashMap<String, Player>) response;
                }
                Platform.runLater(this::display);
                while (true) {
                    NetworkMessage updateMessage = (NetworkMessage) in.readObject();
                    if ("reset".equals(updateMessage.getMessageType())) {
                        out.writeObject(new NetworkMessage("getMarketplace", null, cl));
                        out.flush();
                        response = in.readObject();
                        if(response instanceof HashMap<?,?>) {
                            marketplacePlayers = (HashMap<String, Player>) response;
                        }
                        Platform.runLater(this::display);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void display() {
        c = 0;
        list.getItems().clear();
        for(String name:marketplacePlayers.keySet()){
            Player player = marketplacePlayers.get(name);
            c++;
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label(player.getName());
            nameLabel.setPrefWidth(200);
            Label countryLabel = new Label(player.getClub());
            countryLabel.setPrefWidth(250);
            Button playerButton = new Button("Details");
            playerButton.setPrefWidth(120);
            playerButton.setOnAction(e -> {
                try {
                    showDetails(player.getName());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            Button playerButton2 = new Button("Buy");
            playerButton2.setPrefWidth(120);
            playerButton2.setOnAction(e -> {
                buy(player);
            });

            hbox.getChildren().addAll(nameLabel, countryLabel,playerButton,playerButton2);
            list.getItems().add(hbox);
        }

        if (c == 0) {
            alert.setText("No Player Found");
        } else {
            alert.setText(c + " Player Found");
        }
    }
    void showDetails(String playerName) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("/com/example/demo4/details.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 336, 477);
        Stage stage = new Stage();
        stage.setTitle(playerName);
        stage.setScene(scene);
        stage.setTitle(playerName);
        stage.show();
    }
    public void buy(Player player) {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage("buy",player,club));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            NetworkMessage response = (NetworkMessage) in.readObject();
            if ("success".equals(response.getMessageType())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Player Transfer");
                alert.setHeaderText("Congratulations!!!");
                alert.setContentText(player.getName()+" now belongs to your club");
                alert.showAndWait();
            } else if("failure".equals(response.getMessageType())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid Transfer");
                alert.setHeaderText("Something went wrong");
                alert.setContentText("Couldn't transfer "+player.getName()+"!!!");
                alert.showAndWait();
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void backPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/ClubManager.fxml"));
        Scene adminScene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) Back.getScene().getWindow();
        stage.setScene(adminScene);
        stage.setTitle(club);
        stage.show();
    }

    public void reloadPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/marketplace.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) reload.getScene().getWindow();
        stage.setTitle(club);
        stage.setScene(scene);
        stage.show();
    }
}
