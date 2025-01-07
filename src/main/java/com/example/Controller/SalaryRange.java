package com.example.Controller;

import com.example.database.Player;
import com.example.user.Standardization;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import com.example.user.userOperation;
import static com.example.Controller.displayPlayers.display_player;

public class SalaryRange extends userOperation{

    public Button back;
    public Button search;
    public TextField min;
    public TextField max;
    public ListView display;
    public Label count;

    public void searchPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        String mins  = min.getText();
        String maxs = max.getText();
        display.getItems().clear();
        reset();
        if(!Standardization.isDouble(mins) || Double.parseDouble(mins)<0) {
            min.setPromptText("Invalid Salary");
            min.setStyle("-fx-text-fill: red");
        }
        else if(!Standardization.isDouble(maxs) || Double.parseDouble(maxs)<0 || Double.parseDouble(mins)>Double.parseDouble(maxs)) {
            if(!Standardization.isDouble(maxs))
                max.setPromptText("Invalid Salary");
            else max.setPromptText("Invalid Range");
            max.setStyle("-fx-text-fill: red");
        }else{
            int c = 0;
            Double minss = Double.parseDouble(mins);
            Double maxss = Double.parseDouble(maxs);
            List<Player> players = searchBySalaryRange(minss,maxss);
            if (players != null) {
                for (Player player : players) {
                    c++;
                    HBox playerBox = display_player(player);
                    display.getItems().add(playerBox);
                }
            }
            if(c == 0){
                count.setText("No Player found");
            }else
                count.setText(c+" player found");
        }
    }

    public void backPressed(ActionEvent actionEvent) throws IOException {
        displayPlayers.buttonPressed();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/com/example/demo4/Admin.fxml"));
        Scene adminScene = new Scene(fxmlLoader.load(), 840, 601);
        Stage stage = (Stage)back.getScene().getWindow();
        stage.setScene(adminScene);
        stage.setTitle("Admin");
        stage.show();
    }
    void reset(){
        min.setStyle(null);
        max.setStyle(null);
    }

    public void handleKey(KeyEvent keyEvent) throws IOException {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            displayPlayers.buttonPressed();
            searchPressed(null);
        }
    }
}
