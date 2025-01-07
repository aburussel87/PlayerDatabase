package com.example.Controller;
import com.example.utils.NetworkMessage;
import com.example.application;
import com.example.database.Player;
import com.example.user.Standardization;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import com.example.user.userOperation;
import javafx.util.Duration;

public class ClubManager extends userOperation implements Initializable {
    public ListView<HBox> list;
    public TextField name;
    public Label alert;
    public Button search;
    public MenuButton filter;
    public RadioButton reset;
    public Label maxHeight;
    public Label maxSalary;
    public Label masAge;
    public Label YearlySalary;
    public Label countryCount;
    public Label playerCount;
    public Button logout;
    public Button cng;
    public Button back;
    public Button market;
    private String selectedPosition = "";
    String club;
    private HashMap<String, List<Player>> players = new HashMap<>();
    private HashMap<String, List<Player>> positionmap= new HashMap<>();
    private HashMap<String,Integer> country = new HashMap<>();

    private Socket socket;
    private ObjectInputStream in;
    void setFields() {
        List<Player> players;
        YearlySalary.setText(Standardization.toMillion(yearlySalary(club)));
        StringBuilder s = new StringBuilder();
        players = maximumAge(club);
        s.append("("+players.get(0).getAge()+" years)");
        for(Player player: players){
            s.append(player.getName());
            s.append(";");
        }
        masAge.setText(s.toString());

        players = maximumHeight(club);
        s  = new StringBuilder();
        s.append("("+players.get(0).getHeight()+" m)");
        for(Player player: players){
            s.append(player.getName());
            s.append(";");
        }
        maxHeight.setText(s.toString());

        players = maximumSalary(club);
        s  = new StringBuilder();
        s.append("("+Standardization.toMillion(players.get(0).getSalary())+")");
        for(Player player: players){
            s.append(player.getName());
            s.append(";");
        }
        maxSalary.setText(s.toString());
    }
    void setclub(String club){
        this.club = club;
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
                if(player.getClub().equals(club)) {
                    c++;
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    Label nameLabel = new Label(player.getName());
                    nameLabel.setPrefWidth(200);
                    Label countryLabel = new Label(player.getCountry());
                    countryLabel.setPrefWidth(200);
                    Button playerButton = new Button("Details");
                    playerButton.setPrefWidth(120);

                    playerButton.setOnAction(e -> {
                        try {
                            showDetails(player.getName());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    Button playerButton2 = new Button("Sell Request");
                    playerButton2.setPrefWidth(120);
                    playerButton2.setOnAction(e -> {
                        sellPressed(player);
                    });

                    hbox.getChildren().addAll(nameLabel, countryLabel,playerButton,playerButton2);
                    list.getItems().add(hbox);
                }
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
        List<Player> clubPlayers = players.get(club);
        if (clubPlayers == null) {
            alert.setText("No players found for the club: " + club);
            return;
        }
        for (Player player : clubPlayers) {
            c++;
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label(player.getName());
            nameLabel.setPrefWidth(200);
            Label countryLabel = new Label(player.getCountry());
            countryLabel.setPrefWidth(250);
            country.put(player.getCountry(), 1);
            Button playerButton = new Button("Details");
            playerButton.setPrefWidth(120);

            playerButton.setOnAction(e -> {
                try {
                    showDetails(player.getName());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            Button playerButton2 = new Button("Sell Request");
            playerButton2.setPrefWidth(120);
            playerButton2.setOnAction(e -> {
               sellPressed(player);
            });
            hbox.getChildren().addAll(nameLabel, countryLabel, playerButton,playerButton2);
            list.getItems().add(hbox);
        }

        if (c == 0) {
            alert.setText("No Player Found");
        } else {
            animateCountingRow(playerCount,c);
            animateCountingRow(countryCount,country.size());
            alert.setText(c + " Player Found");
        }
    }
    void animateCountingRow(Label label,int targetCount) {
        label.setText("0");
        Timeline timeline = new Timeline();
        for (int i = 1; i <= targetCount; i++) {
            int finalI = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100 * i), event -> label.setText(String.valueOf(finalI))));
        }
        timeline.play();
    }

    public void filterList(KeyEvent event) {
        int c = 0;
        String searchText = name.getText().toLowerCase();
        list.getItems().clear();
        for (Player player: players.get(club)) {
            if(selectedPosition.isEmpty()) {
                if (player.getName().toLowerCase().contains(searchText)) {
                    c++;
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    Label nameLabel = new Label(player.getName());
                    nameLabel.setPrefWidth(200);
                    Label countryLabel = new Label(player.getCountry());
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
                    Button playerButton2 = new Button("Sell Request");
                    playerButton2.setPrefWidth(120);
                    playerButton2.setOnAction(e -> {
                        try {
                            showDetails(player.getName());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    hbox.getChildren().addAll(nameLabel, countryLabel,playerButton,playerButton2);
                    list.getItems().add(hbox);
                }
            }else{
                if (player.getName().toLowerCase().contains(searchText) && searchByName(player.getName()).getPosition().equalsIgnoreCase(selectedPosition)) {
                    c++;
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    Label nameLabel = new Label(player.getName());
                    nameLabel.setPrefWidth(200);
                    Label countryLabel = new Label(player.getName());
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
                    Button playerButton2 = new Button("Sell Request");
                    playerButton2.setPrefWidth(120);
                    playerButton2.setOnAction(e -> {
                        sellPressed(player);
                    });
                    hbox.getChildren().addAll(nameLabel, countryLabel, playerButton,playerButton2);
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

    private void sellPressed( Player player){
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage("sell", player));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            NetworkMessage response = (NetworkMessage) in.readObject();
            if ("success".equals(response.getMessageType())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Player on sale");
                alert.setHeaderText("Player sent to marketplace!");
                alert.setContentText("Sell request sent to all the clubs");
                alert.showAndWait();
            } else if("exist".equals(response.getMessageType())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Already exists in marketplace");
                alert.setHeaderText("Player has not been sent to marketplace!");
                alert.setContentText("Player allready exists in marketplace!");
                alert.showAndWait();
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
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
    public void searchPressed(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
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
        getPlayers(club);
        displayAllPlayers();
    }
    public void logoutPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        try{
            Socket s  = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(new NetworkMessage("logout",club,1));
            out.flush();
        }catch (Exception e){

        }
        try {
            if (socket != null && !socket.isClosed()) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(new NetworkMessage("logout", club, 1));
                out.flush();
                Thread.sleep(100);
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/cmLogin.fxml"));
        Scene adminScene = new Scene(fxmlLoader.load(), 500, 300);
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setScene(adminScene);
        stage.setTitle("Club Manager Login");
        stage.show();
    }



    public void cngPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/resetPass.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),600, 420);
        Stage currentStage = (Stage)alert.getScene().getWindow();
        currentStage.setScene(scene);
        currentStage.setTitle(club);
        currentStage.show();
    }

    public void backPressed(ActionEvent actionEvent) {

    }
    void getPlayers(String club){
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage("myPlayers", null,club));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            players = (HashMap<String,List<Player>>) in.readObject();
            for(Player p: players.get(club)){
                positionmap.putIfAbsent(p.getPosition(),new ArrayList<>());
                positionmap.get(p.getPosition()).add(p);
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            if (logout != null && logout.getScene() != null) {
                Stage stage = (Stage) logout.getScene().getWindow();
                if (stage != null) {
                    String clubName = stage.getTitle();
                    club = clubName;
                    setclub(club);
                    getPlayers(club);
                    displayAllPlayers();
                    setFields();
                    loadPositions();
                    name.setOnKeyReleased(this::filterList);
                } else {
                    System.err.println("Stage is null.");
                }
            } else {
                System.err.println("Count or its scene is not initialized.");
            }
        });
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 12345);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                out.writeObject(new NetworkMessage("myPlayers",null,club));
                out.flush();
                HashMap<String,List<Player>> p = (HashMap<String, List<Player>>) in.readObject();
                displayAllPlayers();
                while (true) {
                    try {
                        NetworkMessage updateMessage = (NetworkMessage) in.readObject();
                        if("reset".equals(updateMessage.getMessageType())){
                            Platform.runLater(this::updateClub);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Connection lost or server error.");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateClub(){
        getPlayers(club);
        displayAllPlayers();
    }
    public void MarketPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/marketplace.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 601);
        Marketplace controller = fxmlLoader.getController();
        controller.setclub(club);
        Stage stage = (Stage) search.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle(club);
        stage.show();
    }
}
