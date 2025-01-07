package com.example.utils;
import com.example.database.Player;

import java.io.Serializable;
import java.net.Socket;

public class NetworkMessage implements Serializable {
    private String messageType; // Type of message: "sell", "buy", "getMarketplace", etc.
    private Player player;      // Player data (if applicable)
    private String errorMessage; // Error message (optional)
    private String club;
    private String pass;
    private String newpass;
    private String renewpass;
    //getMap
    public NetworkMessage(String messageType){
        this.messageType = messageType;
    }
    //logout
    public NetworkMessage(String messageType,String club, int x){
        this.messageType = messageType;
        this.club = club;
    }
    //sell
    public NetworkMessage(String messageType, Player player) {
        this.messageType = messageType;
        this.player = player;
    }
    //MarketPlace_ClubPlayers_Buy
    public NetworkMessage(String messageType, Player player, String currentClub) {
        this.messageType = messageType;
        this.player = player;
        this.club = currentClub;
    }
    //login
    public NetworkMessage(String messageType, String club, String pass,int x){
        this.messageType = messageType;
        this.club = club;
        this.pass = pass;
    }
    //reset
    public NetworkMessage(String messageType, String club, String old, String newpass, String renewpass){
        this.messageType = messageType;
        this.club = club;
        this.pass = old;
        this.newpass = newpass;
        this.renewpass = renewpass;
    }

    public String getNewpass(){
        return newpass;
    }
    public String getreNewpass(){
        return renewpass;
    }
    public String getClub(){
        return club;
    }
    public String getPass(){
        return pass;
    }
    public String getMessageType() {
        return messageType;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    @Override
    public String toString() {
        return "NetworkMessage{" +
                "messageType='" + messageType + '\'' +
                ", player=" + (player != null ? player.getName() : "null") +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}