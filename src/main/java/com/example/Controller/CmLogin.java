package com.example.Controller;

import com.example.database.Player;
import com.example.user.Standardization;
import com.example.utils.NetworkMessage;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class CmLogin {

    public PasswordField password;
    public Button login;
    public Label alert;
    public ChoiceBox<String> clubs;
    private HashMap<String, List<Player>> clubmap = new HashMap<>();

    public void initialize() {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage("clubMap"));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            clubmap = (HashMap<String, List<Player>>) in.readObject();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadclubs();
        setupClubSelectionListener();
    }

    private void loadclubs() {
        clubs.setItems(FXCollections.observableArrayList(clubmap.keySet()));
    }

    private void setupClubSelectionListener() {
        clubs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //username.setText(newValue);
            }
        });
    }

    public void resetStyles() {
        password.setStyle(null);
        alert.setText("");
        clubs.setStyle(null);
    }
    public void loginpressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        String cname = clubs.getValue();
        String pwd = password.getText();
        if(cname == null || pwd == null) {
            clubs.setStyle("-fx-border-color: red");
            return;
        }
        if (!Standardization.isMatchingClub(cname).isEmpty()) {
            cname = Standardization.isMatchingClub(cname);
        }
        cname = Standardization.toFullyStandardized(cname);
        resetStyles();
        int status = 0;
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage("login", cname,pwd,1));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            status = (int)in.readObject();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (status == 1) {
            FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/clubManager.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 840, 608);
            Stage currentStage = (Stage) login.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle(cname);
            currentStage.show();
        } else {
            alert.setStyle("-fx-text-fill: red ; -fx-underline: true");
            alert.setText("Invalid Login");
        }
    }

    public void handlekey(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == javafx.scene.input.KeyCode.ENTER) {
            displayPlayers.buttonPressed();
            loginpressed(null);
        }
    }
}
