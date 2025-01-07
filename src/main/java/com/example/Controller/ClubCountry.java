package com.example.Controller;

import com.example.application;
import com.example.database.Player;
import com.example.utils.NetworkMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class ClubCountry {

    public Button back;
    public ListView<HBox> display;
    public MenuButton countrySelected;
    public MenuButton clubSelected;
    public Label count;
    private HashMap<String, List<Player>> countryMap;
    private HashMap<String, List<Player>> clubMap;
    private String selectedCountry;
    private String selectedClub;
    private int c = 0;

    public void initialize() {
        getPlayers("clubMap");
        getPlayers("countryMap");
        loadCountries();
    }
    private void getPlayers(String string) {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage(string));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            if(string.equals("countryMap")) {
                countryMap = (HashMap<String, List<Player>>) in.readObject();
            }else if(string.equals("clubMap")) {
                clubMap = (HashMap<String, List<Player>>) in.readObject();
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadCountries() {
        countrySelected.getItems().clear();
        for (String country : countryMap.keySet()) {
            MenuItem countryItem = new MenuItem(country);
            countryItem.setOnAction(event -> {
                displayPlayers.buttonPressed();
                clubSelected.setText("Select a Club");
                selectedCountry = country;
                countrySelected.setText(country);
                loadClubs();
                displayPlayersByCountry();
            });
            countrySelected.getItems().add(countryItem);
        }
    }

    private void loadClubs() {
        clubSelected.getItems().clear();
        List<Player> playersInCountry = countryMap.get(selectedCountry);
        if (playersInCountry != null) {
            for (Player player : playersInCountry) {
                String club = player.getClub();
                if (club != null && !club.trim().isEmpty() &&
                        clubSelected.getItems().stream().noneMatch(item -> item.getText().equals(club))) {
                    MenuItem clubItem = new MenuItem(club);
                    clubItem.setOnAction(event -> {
                        displayPlayers.buttonPressed();
                        clubSelected.setText(club);
                        selectedClub = club;
                        displayPlayersByClub();
                    });
                    clubSelected.getItems().add(clubItem);
                }
            }
        }
    }

    void displayPlayersByCountry() {
        c = 0;
        List<Player> players = countryMap.get(selectedCountry);
        display.getItems().clear();
        if (players != null) {
            for (Player player : players) {
                c++;
                HBox playerBox = displayPlayers.display_player(player);
                display.getItems().add(playerBox);
            }
        }
        if (c == 0)
            count.setText("No Player Found");
        else
            count.setText(c + " Player Found");
    }

    void displayPlayersByClub() {
        c = 0;
        List<Player> players = clubMap.get(selectedClub);
        display.getItems().clear();
        if (players != null) {
            for (Player player : players) {
                if (player.getCountry().equals(selectedCountry)) {
                    c++;
                    HBox playerBox = displayPlayers.display_player(player);
                    display.getItems().add(playerBox);
                }
            }
        }
        if (c == 0)
            count.setText("No Player Found");
        else
            count.setText(c + " Player Found");
    }

    public void backpressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("/com/example/demo4/Admin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Admin");
        stage.setScene(scene);
        stage.show();
    }
}
