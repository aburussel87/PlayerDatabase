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
import com.example.user.Standardization;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import com.example.user.userOperation;
public class ByClub extends userOperation{

    public Button back;
    public MenuButton filter;
    public MenuButton club;
    public ListView display;
    public Label count;
    private HashMap<String, List<Player>> clubMap = new HashMap<>();
    private  int c = 0;
    private String selectedClub;
    private String selectedFilter;

    public void initialize() {
        getplayers();
        loadClub();
    }
    private void getplayers() {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage("clubMap"));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                clubMap = (HashMap<String, List<Player>>) in.readObject();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void loadClub() {
        club.getItems().clear();
        for (String clubs : clubMap.keySet()) {
            MenuItem clubItem = new MenuItem(clubs);
            clubItem.setOnAction(event -> {
                displayPlayers.buttonPressed();
                filter.setText("Filter");
                selectedClub = clubs;
                club.setText(clubs);
                loadfilters();
                displayPlayersByClub();
            });
            club.getItems().add(clubItem);
        }
    }
    void loadfilters() {
        filter.getItems().clear();
        MenuItem filterItem = new MenuItem("Maximum Age");
        MenuItem filterItem2 = new MenuItem("Maximum Height");
        MenuItem filterItem3 = new MenuItem("Maximum Salary");
        MenuItem filterItem4 = new MenuItem("Yearly Salary");
        filterItem.setOnAction(event -> {
            displayPlayers.buttonPressed();
            selectedFilter = "Maximum Age";
            displayPlayersByMaxAge();
            filter.setText("Maximum Age");
        });
        filterItem2.setOnAction(event -> {
            displayPlayers.buttonPressed();
            selectedFilter = "Maximum Height";
            displayPlayersByMaxHeight();
            filter.setText("Maximum Height");
        });
        filterItem3.setOnAction(event -> {
            displayPlayers.buttonPressed();
            selectedFilter = "Maximum Salary";
            displayByMaxSalary();
            filter.setText("Maximum Salary");
        });
        filterItem4.setOnAction(event -> {
            displayPlayers.buttonPressed();
            selectedFilter = "Yearly Salary";
            displayYearlySalary();
            filter.setText("Yearly Salary");
        });
        filter.getItems().addAll(filterItem, filterItem2, filterItem3, filterItem4);
    }
    void displayByMaxSalary() {
        c = 0;
        List<Player> players = maximumSalary(selectedClub);
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
    void displayYearlySalary() {
        c = 0;
        List<Player> players = clubMap.get(selectedClub);
        display.getItems().clear();
        double totalYearlySalary = 0;

        if (players != null) {
            HBox header = createRow("Name", "Country", "Weekly Salary",true);
            display.getItems().add(header);
            display.getItems().add(new HBox());
            for (Player player : players) {
                c++;
                HBox playerRow = createRow(
                        player.getName(),
                        player.getCountry(),
                        Standardization.toMillion(player.getSalary()),
                        false
                );
                display.getItems().add(playerRow);
                totalYearlySalary += player.getSalary() * 52;
            }

            display.getItems().add(new HBox());
            display.getItems().add(new HBox());

            HBox totalRow = createRow("Total Yearly Salary", "", Standardization.toMillion(totalYearlySalary),true);
            display.getItems().add(totalRow);
        }

        if (c == 0)
            count.setText("No Player Found");
        else
            count.setText(c + " Player Found");
    }

    HBox createRow(String label1, String label2, String label3,boolean isLarge) {
        Label l1 = new Label(label1);
        l1.setMinWidth(300);

        Label l2 = new Label(label2);
        l2.setMinWidth(300);

        Label l3 = new Label(label3);
        l3.setMinWidth(300);
        if(isLarge){
            l2.setMinWidth(270);
            l1.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            l2.setStyle("-fx-font-size: 16;-fx-font-weight: bold;");
            l3.setStyle("-fx-font-size: 16;-fx-font-weight: bold;");
        }
        HBox row = new HBox(10);
        row.getChildren().addAll(l1, l2, l3);
        return row;
    }

    void displayPlayersByMaxHeight(){
        c = 0;
        List<Player> players = maximumHeight(selectedClub);
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
    void displayPlayersByMaxAge() {
        c = 0;
        List<Player> players = maximumAge(selectedClub);
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
    public void backPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("/com/example/demo4/Admin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Admin");
        stage.setScene(scene);
        stage.show();
    }
}
