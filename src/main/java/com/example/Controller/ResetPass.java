package com.example.Controller;

import com.example.database.ManagePlayer;
import com.example.utils.NetworkMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ResetPass {

    public TextField old;
    public TextField rpnew;
    public TextField pnew;
    public Button save;
    public Button back;
    public Label alert;
    private String club;

    public void initialize() {
        Platform.runLater(() -> {
            if (save != null && save.getScene() != null) {
                Stage stage = (Stage) save.getScene().getWindow();
                if (stage != null) {
                    String clubName = stage.getTitle();
                    club = clubName;
                    setclub(clubName);
                }
            }
        });
    }
    private void setclub(String club) {
        this.club = club;
    }

public void savePressed(ActionEvent actionEvent) {
    displayPlayers.buttonPressed();
    String opass = old.getText();
    String newpass = pnew.getText();
    String newrepeatpass = rpnew.getText();
    reset();

    int status = -1;
    try (Socket socket = new Socket("localhost", 12345)) {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(new NetworkMessage("reset", club, opass, newpass, newrepeatpass));
        out.flush();

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        status = (int) in.readObject();
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Handle the response status
    final int finalStatus = status;
    Platform.runLater(() -> {
        if (finalStatus == 1) {
            popup();
        } else if (finalStatus == 0) {
            alert.setText("Wrong Password");
        } else if (finalStatus == -1) {
            alert.setText("Password must contain 8 characters");
        } else if (finalStatus == 2) {
            alert.setText("Try a different password");
        } else if (finalStatus == 3) {
            alert.setText("New passwords didn't match");
        }
    });
}
    void reset(){
        old.setStyle(null);
        pnew.setStyle(null);
        rpnew.setStyle(null);
        alert.setStyle(null);
    }
    void popup(){
        alert.setStyle(null);
        old.clear();
        pnew.clear();
        rpnew.clear();
        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.setTitle("Reset Password");
        alert1.setHeaderText(null);
        alert1.setContentText("Passwords was changed successfully");
        alert1.showAndWait();
    }
    public void backPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/ClubManager.fxml"));
        Scene adminScene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setScene(adminScene);
        stage.setTitle(club);
        stage.show();
    }
}
