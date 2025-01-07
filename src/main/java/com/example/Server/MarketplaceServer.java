package com.example.Server;

import com.example.database.ManagePlayer;
import com.example.database.Player;
import com.example.utils.NetworkMessage;
import com.example.application;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MarketplaceServer {
    private static final int PORT = 12345;
    private static final HashMap<String,Player> mp = new HashMap<>();
    private static final List<ObjectOutputStream> clientOutputs = new ArrayList<>();
    private static final HashMap<String,ObjectOutputStream> clubOutputs = new HashMap<>();
    private static final List<String> online = new ArrayList<>();
    private static HashMap<String,String> cmPass = new HashMap<>();
    private static Player newplayer = null;
    public static void main(String[] args) {
        ManagePlayer.loadplayers();
        cmPass =ManagePlayer.loadCmPass();
        System.out.println("Server is online...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (ManagePlayer.getRemoved()) {
                ManagePlayer.saveToFile();
                System.out.println("Database updated successfully");
            }
            System.out.println("Server is offline...");
        }));
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            String clubname = "A";
            try (
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())
            )
            {
                synchronized (clientOutputs) {
                    clientOutputs.add(out);
                }
                while (true) {

                    NetworkMessage message = (NetworkMessage) in.readObject();
                    switch (message.getMessageType()) {
                        case "sell":
                            handleSell(out, message);
                            break;
                        case "getMarketplace":
                            handleGetMarketplace(out, message);
                            break;
                        case "buy":
                            handleBuy(out, message);
                            break;
                        case "myPlayers":
                            handlemyPlayers(out,message);
                            break;
                        case "login":
                            handleLogin(out,message);
                            break;
                        case "logout":
                            handleLogout(out,message);
                            clubname = message.getClub();
                            break;
                        case "reset":
                            handlereset(out,message);
                            break;
                        case "newClub":
                            handlenewClub(out,message);
                            break;
                        case "playerMap":
                            handlePlayerMap(out,message);
                            break;
                        case "clubMap":
                            handlePlayerMap(out,message);
                            break;
                        case "countryMap":
                            handlePlayerMap(out,message);
                            break;
                        case "positionMap":
                            handlePlayerMap(out,message);
                            break;
                        case "newPlayer":
                            handlenew(out,message);
                            break;
                        case"Successful Addition":
                            handlePlayerMap(out,message);
                            break;
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                //System.out.println("Client disconnected: " + clientSocket);
            } finally {
                if (!Objects.equals(clubname, "A") && clubname!=null) {
                    synchronized (online) {
                        online.remove(clubname);
                    }
                    synchronized (clubOutputs) {
                        clubOutputs.remove(clubname);
                    }
                    //System.out.println("Cleaned up resources for: " + clubname);
                }
            }
        }
        private void handlenew(ObjectOutputStream out, NetworkMessage message) throws IOException {
            Player player = message.getPlayer();
            if(player!=null){
                System.out.println("New Player addition");
                newplayer = player;
                ManagePlayer.addPlayer(player);
                ManagePlayer.setNewPlayers(player);
                ManagePlayer.saveToFile();
                cmPass.clear();
                cmPass = ManagePlayer.loadCmPass();
                out.writeObject(1);
            }else{
                out.writeObject(0);
            }
            out.flush();
        }
        private void handlePlayerMap(ObjectOutputStream out, NetworkMessage message) throws IOException {
            if(newplayer == null) {
                ManagePlayer.getPlayermap().clear();
                ManagePlayer.getPositionmap().clear();
                ManagePlayer.getClubmap().clear();
                ManagePlayer.getCountrymap().clear();
                ManagePlayer.loadplayers();
            }
            if(message.getMessageType().equals("playerMap")) {
                HashMap<String, Player> pm = ManagePlayer.getPlayermap();
                synchronized (pm) {
                    out.writeObject(pm);
                }
                out.flush();
            }
            else if(message.getMessageType().equals("clubMap")) {
                HashMap<String, List<Player>> pm = ManagePlayer.getClubmap();
                synchronized (pm) {
                    out.writeObject(pm);
                }
                out.flush();
            }else if(message.getMessageType().equals("countryMap")){
                HashMap<String, List<Player>> pm = ManagePlayer.getCountrymap();
                synchronized (pm) {
                    out.writeObject(pm);
                }
                out.flush();
            }else if(message.getMessageType().equals("positionMap")){
                HashMap<String, List<Player>> pm = ManagePlayer.getPositionmap();
                synchronized (pm) {
                    out.writeObject(pm);
                }
                out.flush();
            } else if(message.getMessageType().equals("Successful Addition")){
                    out.writeObject(newplayer);
                    out.flush();
                    newplayer = null;
            }
        }
        private void handlenewClub(ObjectOutputStream out, NetworkMessage message) throws IOException {
            synchronized (clientOutputs) {
                clientOutputs.add(out);
                clubOutputs.put(message.getClub(), out);
            }
            out.flush();
        }
        private void handlereset(ObjectOutputStream out,NetworkMessage message) throws IOException {
            int x = 0;
            String opass = message.getPass();
            String newpass = message.getNewpass();
            String newrepeatpass = message.getreNewpass();
            String club = message.getClub();
            if(ManagePlayer.CMauthorization(club,opass)){
                if(newpass.equals(newrepeatpass)&& newpass.length() == 8){
                    x = 1;
                    for(String clubName : cmPass.keySet()){
                    if(clubName.equals(club)){
                        cmPass.remove(clubName);
                        cmPass.put(clubName,newpass);
                        ManagePlayer.setCmPass(cmPass);
                        cmPass =ManagePlayer.loadCmPass();
                        System.out.println("::"+club+" changed their password(Updated Successfully)");
                        break;
                    }
                }
                }
                else if(!newpass.equals(newrepeatpass)){
                    x = 3;
                }else{
                    x = -1;
                }
            }else{
                x  = 0;
            }
            if(opass.equals(newpass)) x = 2;
            out.writeObject(x);
            out.flush();
        }
        private void handleLogout(ObjectOutputStream out, NetworkMessage message) throws IOException {
            String club = message.getClub();

            synchronized (online) {
                online.remove(club);
            }
            synchronized (clientOutputs) {
                clientOutputs.remove(out);
            }
            synchronized (clubOutputs) {
                clubOutputs.remove(club);
            }

            System.out.println("Client: " + club + " disconnected");

            out.flush();
        }

        private void handleLogin(ObjectOutputStream out, NetworkMessage message) throws IOException {
            int x;
            if(online.contains(message.getClub())){
                x = 0;
                System.out.println("Invalid Login: "+message.getClub()+" is already logged in");
            }else if(ManagePlayer.CMauthorization(message.getClub(),message.getPass())){
               x = 1;
               online.add(message.getClub());
               clientOutputs.add(out);
               clubOutputs.put(message.getClub(),out);
               System.out.println("Client Login: " +message.getClub()+" just logged in!");
           }else{
               x = 0;
               System.out.println("Invalid login: "+message.getClub()+" tried to login");
           }
            out.writeObject(x);
           out.flush();
        }
        private void handlemyPlayers(ObjectOutputStream out, NetworkMessage message) throws IOException {
            synchronized (mp) {
                HashMap<String,List<Player>> clubs = ManagePlayer.getClubmap();
                out.writeObject(clubs);
            }
            out.flush();
        }

        private void handleSell(ObjectOutputStream out, NetworkMessage message) throws IOException {
            Player playerToSell = message.getPlayer();
            if(mp.containsKey(playerToSell.getName())) {
                System.out.println("Already on sale: " + playerToSell.getName());
                out.writeObject(new NetworkMessage("exist", playerToSell));
                out.flush();
                return;
            }
            if (playerToSell != null) {
                synchronized (mp){
                    mp.put(playerToSell.getName(),playerToSell);
                    System.out.println("Player on sell: " + playerToSell.getName()+ " by "+playerToSell.getClub());
                }
                out.writeObject(new NetworkMessage("success", playerToSell));
                out.flush();
                notifyClientsAboutNewPlayer(playerToSell);
                notifyClientsReset();
            } else {
                out.writeObject(new NetworkMessage("failure", null));
                out.flush();
            }
        }

        private void handleGetMarketplace(ObjectOutputStream out, NetworkMessage message) throws IOException {
            synchronized (mp){
                HashMap<String,Player> filteredList = new HashMap<>();
                for (String name : mp.keySet()) {
                    Player player = mp.get(name);
                    if(player.getClub().equals(message.getClub())){
                        continue;
                    }
                    filteredList.put(name,player);
                }
                out.writeObject(filteredList);
            }
            out.flush();
        }

        private void handleBuy(ObjectOutputStream out, NetworkMessage message) throws IOException {
            Player playerToBuy = message.getPlayer();
            String Buying_club = message.getClub();
            if (playerToBuy != null) {
                synchronized (mp) {
                    if (mp.containsKey(playerToBuy.getName())) {
                        System.out.println("Player Transfer: From " + playerToBuy.getClub() + " (" + playerToBuy.getName() + ") to " + Buying_club);
                        ManagePlayer.transfer(playerToBuy, Buying_club);
                        mp.remove(playerToBuy.getName());
                        application.reset();
                        out.writeObject(new NetworkMessage("success", null));
                        out.flush();
                        notifyClientsAboutPlayerPurchase(playerToBuy);
                        notifyClientsReset();
                    } else {
                        out.writeObject(new NetworkMessage("failure", null));
                        out.flush();
                    }
                }
            } else {
                out.writeObject(new NetworkMessage("failure", null));
                out.flush();
            }
        }

        private void notifyClientsReset() {
            synchronized (clientOutputs) {
                for (ObjectOutputStream clientOut : clientOutputs) {
                    try {
                        clientOut.writeObject(new NetworkMessage("reset", null));
                        clientOut.flush();
                    } catch (IOException e) {
                        System.err.println("Failed to notify a client about reset. Removing from the list.");
                        clientOutputs.remove(clientOut);
                    }
                }
            }
        }
        private void notifyClientsAboutNewPlayer(Player playerToSell) throws IOException {
            for (Iterator<ObjectOutputStream> it = clientOutputs.iterator(); it.hasNext();) {
                ObjectOutputStream clientOut = it.next();
                try {
                    clientOut.writeObject(new NetworkMessage("playerSold", playerToSell));
                    clientOut.flush();
                } catch (IOException e) {
                    it.remove();
                }
            }
        }

        private void notifyClientsAboutPlayerPurchase(Player playerToBuy) {
            for (Iterator<ObjectOutputStream> it = clientOutputs.iterator(); it.hasNext();) {
                ObjectOutputStream clientOut = it.next();
                try {
                    clientOut.writeObject(new NetworkMessage("playerBought", playerToBuy));
                    clientOut.flush();
                } catch (IOException e) {
                    //System.err.println(i+" Failed to notify a client. Removing from the list.");
                    it.remove();
                }
            }
        }
    }
}
