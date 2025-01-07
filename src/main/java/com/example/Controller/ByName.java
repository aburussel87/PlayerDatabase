package com.example.Controller;

import com.example.application;
import com.example.database.ManagePlayer;
import com.example.database.Player;
import com.example.utils.NetworkMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.user.userOperation;

public class ByName extends userOperation {
    public ListView<HBox> list;
    public TextField name;
    public Label alert;
    public Button back;
    public Button search;
    public MenuButton filter;
    public RadioButton reset;
    private String selectedPosition = "";
    private HashMap<String, Player> players;
    private HashMap<String, List<Player>> positionmap = new HashMap<>();

    public void initialize() {
        getPlayers();
        displayAllPlayers();
        loadPositions();
        name.setOnKeyReleased(this::filterList);
    }
    private void getPlayers() {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage("playerMap"));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            players = (HashMap<String,Player>) in.readObject();
            for(Player p: players.values()) {
                positionmap.putIfAbsent(p.getPosition(), new ArrayList<>());
                positionmap.get(p.getPosition()).add(p);
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void loadPositions() {
        filter.getItems().clear();
        for (String position : positionmap.keySet()) {
            MenuItem positionItem = new MenuItem(position);
            positionItem.setOnAction(event -> {
                displayPlayers.buttonPressed();
                selectedPosition = position;
                filter.setText(position);
                displayPlayersByPosition();
            });
            filter.getItems().add(positionItem);
        }
    }
    void displayPlayersByPosition() {
        int c = 0;
        List<Player> players = positionmap.get(selectedPosition);
        list.getItems().clear();
        if (players != null) {
            for (Player player : players) {
                c++;
                HBox hbox = new HBox();
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label(player.getName());
                nameLabel.setPrefWidth(200);
                Label countryLabel = new Label(player.getCountry());
                countryLabel.setPrefWidth(200);
                Label ageLabel = new Label(String.valueOf(player.getAge()+" years old"));
                ageLabel.setPrefWidth(150);
                Button playerButton = new Button("Details");
                playerButton.setPrefWidth(120);

                playerButton.setOnAction(e -> {
                    try {
                        showDetails(player.getName());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                hbox.getChildren().addAll(nameLabel, countryLabel, ageLabel, playerButton);
                list.getItems().add(hbox);
            }
        }
        if (c == 0)
            alert.setText("No Player Found");
        else
            alert.setText(c + " Player Found");
    }
    private void displayAllPlayers() {
        int c = 0;
        list.getItems().clear();

        for (String playerName : players.keySet()) {
            c++;
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(playerName);
            nameLabel.setPrefWidth(200);
            Label countryLabel = new Label(players.get(playerName).getCountry());
            countryLabel.setPrefWidth(200);
            Label ageLabel = new Label(String.valueOf(players.get(playerName).getAge()+" years old"));
            ageLabel.setPrefWidth(150);
            Button playerButton = new Button("Details");
            playerButton.setPrefWidth(120);

            playerButton.setOnAction(e -> {
                displayPlayers.buttonPressed();
                try {
                    showDetails(playerName);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            if (c == 0)
                alert.setText("No Player Found");
            else
                alert.setText(c + " Player Found");
            hbox.getChildren().addAll(nameLabel, countryLabel, ageLabel, playerButton);
            list.getItems().add(hbox);
        }
    }

    public void filterList(KeyEvent event) {
        int c = 0;
        String searchText = name.getText().toLowerCase();
        list.getItems().clear();
        for (String playerName : players.keySet()) {
          if(selectedPosition.isEmpty()) {
              if (playerName.toLowerCase().contains(searchText)) {
                  c++;
                  HBox hbox = new HBox();
                  hbox.setSpacing(10);
                  hbox.setAlignment(Pos.CENTER_LEFT);
                  Label nameLabel = new Label(playerName);
                  nameLabel.setPrefWidth(200);
                  Label countryLabel = new Label(players.get(playerName).getCountry());
                  countryLabel.setPrefWidth(200);
                  Label ageLabel = new Label(String.valueOf(players.get(playerName).getAge()) + " Years old");
                  ageLabel.setPrefWidth(150);
                  Button playerButton = new Button("Details");
                  playerButton.setPrefWidth(120);
                  playerButton.setOnAction(e -> {
                      try {
                          showDetails(playerName);
                      } catch (IOException ex) {
                          throw new RuntimeException(ex);
                      }
                  });
                  hbox.getChildren().addAll(nameLabel, countryLabel, ageLabel, playerButton);
                  list.getItems().add(hbox);
              }
          }else{
              if (playerName.toLowerCase().contains(searchText) && searchByName(playerName).getPosition().equalsIgnoreCase(selectedPosition)) {
                  c++;
                  HBox hbox = new HBox();
                  hbox.setSpacing(10);
                  hbox.setAlignment(Pos.CENTER_LEFT);
                  Label nameLabel = new Label(playerName);
                  nameLabel.setPrefWidth(200);
                  Label countryLabel = new Label(players.get(playerName).getCountry());
                  countryLabel.setPrefWidth(200);
                  Label ageLabel = new Label(String.valueOf(players.get(playerName).getAge()) + " Years old");
                  ageLabel.setPrefWidth(150);
                  Button playerButton = new Button("Details");
                  playerButton.setPrefWidth(120);
                  playerButton.setOnAction(e -> {
                      try {
                          showDetails(playerName);
                      } catch (IOException ex) {
                          throw new RuntimeException(ex);
                      }
                  });

                  hbox.getChildren().addAll(nameLabel, countryLabel, ageLabel, playerButton);
                  list.getItems().add(hbox);
              }
          }
        }

        if (list.getItems().isEmpty()) {
            alert.setText("No players found");
        } else {
            alert.setText(c +" players found");
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


    public void backPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("/com/example/demo4/Admin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Admin");
        stage.setScene(scene);
        stage.show();
    }

    public void searchPressed(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
        // Your existing search logic can remain here if needed
    }

    public void handlekey(KeyEvent keyEvent) {
        if (keyEvent.getCode() == javafx.scene.input.KeyCode.ENTER) {
            displayPlayers.buttonPressed();
            searchPressed(null);
        }
    }

    public void resetPressed(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
        name.setStyle(null);
        name.setText("");
        name.setPromptText("Enter player Name");
        selectedPosition = "";
        filter.setText("Filter");
        displayAllPlayers();
    }
}
