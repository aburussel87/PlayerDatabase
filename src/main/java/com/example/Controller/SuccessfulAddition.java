package com.example.Controller;

import com.example.database.ManagePlayer;
import com.example.database.Player;
import com.example.user.Standardization;
import com.example.utils.NetworkMessage;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.Socket;

public class SuccessfulAddition {
    private Player player;
    public ImageView image;
    public Label display;
    private String tempPass = null;
    public void initialize() {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new NetworkMessage("Successful Addition"));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            player = (Player) in.readObject();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(player!=null) {
            tryNewClub(player);
            show(player);
        }
    }
    void show(Player ob){
        StringBuilder s = new StringBuilder();
        s.append(String.format("%-15s: %s\n", "Name", ob.getName()));
        s.append(String.format("%-15s: %d\n", "Age", ob.getAge()));
        s.append(String.format("%-15s: %.2f m\n", "Height", ob.getHeight()));
        s.append(String.format("%-15s: %s\n", "Country", ob.getCountry()));
        s.append(String.format("%-15s: %s\n", "Club", ob.getClub()));
        s.append(String.format("%-15s: %s\n", "Position", ob.getPosition()));

        if (ob.getJersey() != -1) {
            s.append(String.format("%-15s: %d\n", "Jersey No", ob.getJersey()));
        } else {
            s.append(String.format("%-15s: %s\n", "Jersey No", "N/A"));
        }

        s.append(String.format("%-15s: %s\n", "Weekly Salary", Standardization.toMillion(ob.getSalary())));
        if(tempPass!=null){
            showNewClubAlert(ob.getClub(),tempPass);
        }
        image.setImage(getImage(ob.getName()));
        display.setText(s.toString());
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
    void tryNewClub(Player ob){
        if(ManagePlayer.newClub){
            try (FileWriter writer = new FileWriter("src/main/java/com/example/database/CMLogin.txt", true)) {
                tempPass = Standardization.generatePassword(8);
                writer.write(ob.getClub() + ","+tempPass+"\n");
                ManagePlayer.setNewClub();
            } catch (IOException ex) {

            }
        }
    }

    void showNewClubAlert(String club, String password) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("New Club Manager Added");
        alert.setHeaderText("A new Club Manager has been added!");
        alert.setContentText("Club: " + club + "\nPassword: " + password);
        alert.showAndWait();
    }
}
