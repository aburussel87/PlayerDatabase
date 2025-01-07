package com.example.Controller;

import com.example.application;
import com.example.database.ManagePlayer;
import com.example.database.Player;
import com.example.utils.NetworkMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.example.user.Standardization;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

public class AddPlayer {
    public Button BackPressed;
    public TextField name;
    public TextField jersey;
    public TextField salary;
    public TextField age;
    public TextField height;
    public TextField country;
    public TextField club;
    public Button save;
    public ImageView image;
    public Button imagechooser;
    public MenuButton position;
    public Text remainder;
    public Button clear;
    private String pimage;
    private String pposition;
    protected Player player = null;
    public void Back(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("/com/example/demo4/Admin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 600);
        Stage currentStage = (Stage) BackPressed.getScene().getWindow();
        currentStage.setTitle("Admin");
        currentStage.setScene(scene);
        currentStage.show();
    }

    public void saveclicked(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        String pname = name.getText();
        String pjersey = jersey.getText();
        String psalary = salary.getText();
        String page = age.getText();
        String pheight = height.getText();
        String pcountry = country.getText();
        String pclub = club.getText();

        resetStyles();

        boolean valid = true;

        if (!Standardization.isAllAlphabet(pname)) {
            name.setStyle("-fx-border-color: red;");
            name.setPromptText("Enter a valid name (alphabets only)");
            valid = false;
        }
        if (!Standardization.isAllAlphabet(pcountry)) {
            country.setStyle("-fx-border-color: red;");
            country.setPromptText("Enter a valid country (alphabets only)");
            valid = false;
        }
        if (!Standardization.isAllAlphabet(pclub)) {
            club.setStyle("-fx-border-color: red;");
            club.setPromptText("Enter a valid club (alphabets only)");
            valid = false;
        }
        if (!Standardization.isDouble(psalary) || Double.parseDouble(psalary) < 0) {
            salary.setStyle("-fx-border-color: red;");
            salary.setPromptText("Enter a valid salary (positive number)");
            valid = false;
        }
        if (!Standardization.isAllDigits(page) || Integer.parseInt(page) < 10 || Integer.parseInt(page) > 50) {
            age.setStyle("-fx-border-color: red;");
            age.setPromptText("Enter a valid age (10-50)");
            valid = false;
        }
        if (!Standardization.isDouble(pheight) || Double.parseDouble(pheight) < 0.5 || Double.parseDouble(pheight) > 2.8) {
            height.setStyle("-fx-border-color: red;");
            height.setPromptText("Enter a valid height (0.5-2.8 meters)");
            valid = false;
        }
        if (pposition == null || pposition.isEmpty()) {
            position.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            position.setText("Select Position");
            remainder.setText("Please select a valid position.");
            remainder.setStyle("-fx-text-fill: red;");
            valid = false;
        }
        if (!pjersey.isEmpty() && !Standardization.isAllDigits(pjersey)) {
            jersey.setStyle("-fx-border-color: red;");
            jersey.setPromptText("Enter a valid jersey number or leave blank");
            valid = false;
        }

        if (!valid) {
            return;
        }

        int j = pjersey.isEmpty() ? -1 : Integer.parseInt(pjersey);
        if (!Standardization.isMatchingClub(pclub).isEmpty()) {
            pclub = Standardization.isMatchingClub(pclub);
        }

        Player player = new Player(
                Standardization.toFullyStandardized(pname),
                Standardization.toFullyStandardized(pcountry),
                Integer.parseInt(page),
                Double.parseDouble(pheight),
                Standardization.toFullyStandardized(pclub),
                Standardization.toFullyStandardized(pposition),
                j,
                Double.parseDouble(psalary)
        );
            int status  = 0;
            try {
                Socket socket = new Socket("localhost", 12345);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in  = new ObjectInputStream(socket.getInputStream());
                out.writeObject(new NetworkMessage("newPlayer",player));
                out.flush();
                status  = (Integer) in.readObject();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(status == 1) {
                resetForm();
                this.player = player;
                FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("/com/example/demo4/SuccessfulAddition.fxml"));
                Scene scene = new Scene(fxmlLoader.load(),310,470);
                Stage stage = new Stage();
                stage.setTitle("Successfull Addition");
                stage.setScene(scene);
                stage.show();
            } else {
            name.setStyle("-fx-border-color: red;");
            name.setText("");
            name.setPromptText("Duplicate player name! Try a different name.");
        }
    }

    public void imagechosen(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
        if (!name.getText().isEmpty() && Standardization.isAllAlphabet(name.getText())) {
            remainder.setText("");
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                String imagePath = file.toURI().toString();
                Image playerImage = new Image(imagePath);
                this.image.setImage(playerImage);

                try {
                    File destinationFolder = new File("src/main/java/com/example/PlayersImage");
                    if (!destinationFolder.exists()) {
                        destinationFolder.mkdirs();
                    }
                    File destinationFile = new File(destinationFolder, Standardization.toFullyStandardized(name.getText()) + ".png");
                    Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    remainder.setText("Failed to save the image. Try again.");
                    remainder.setStyle("-fx-text-fill: red;");
                }
            }
        } else {
            remainder.setText("Enter a valid name to add image");
            remainder.setStyle("-fx-text-fill: red;");
        }
    }

    public void batsmanselected(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
        pposition = "Batsman";
        position.setText("Batsman");
        position.setStyle(null); // Reset error style
    }

    public void bowlerselected(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
        pposition = "Bowler";
        position.setText("Bowler");
        position.setStyle(null); // Reset error style
    }

    public void allrounderselected(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
        pposition = "Allrounder";
        position.setText("All Rounder");
        position.setStyle(null); // Reset error style
    }

    public void wicketkeeperselected(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
        pposition = "Wicket Keeper";
        position.setText("Wicket Keeper");
        position.setStyle(null); // Reset error style
    }

    public void cleared(ActionEvent actionEvent) {
        displayPlayers.buttonPressed();
        resetForm();
    }

    private void resetForm() {
        name.clear();
        jersey.clear();
        salary.clear();
        age.clear();
        height.clear();
        country.clear();
        club.clear();
        position.setText("Select Position");
        position.setStyle(null);
        remainder.setText("Enter valid details to add a player.");
        remainder.setStyle("-fx-text-fill: black;");
        pposition = null;
        File defaultFile = new File("src/main/resources/com/example/demo4/player1.png");
        image.setImage(new Image(defaultFile.toURI().toString()));
    }

    private void resetStyles() {
        name.setStyle(null);
        jersey.setStyle(null);
        salary.setStyle(null);
        age.setStyle(null);
        height.setStyle(null);
        country.setStyle(null);
        club.setStyle(null);
        position.setStyle(null);
        remainder.setText("");
    }
}
