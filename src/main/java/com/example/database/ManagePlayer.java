package com.example.database;

import com.example.user.Standardization;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ManagePlayer {
    public static boolean newClub = false;
    private static HashMap<String, Player> playermap = new HashMap<>();
    private static HashMap<String, List<Player>> countrymap = new HashMap<>();
    private static HashMap<String, List<Player>> clubmap = new HashMap<>();
    private static HashMap<String, List<Player>> positionmap = new HashMap<>();
    private static List<Player> newPlayers = new ArrayList<>();

    private static boolean removed = false;
    public static boolean getRemoved() {
        return removed;
    }
    public static Player lastadded(){
        return newPlayers.get(newPlayers.size()-1);
    }
    public static void setNewPlayers(Player ob) {
        newPlayers.add(ob);
    }
    public static  void setNewClub(){
        newClub = false;
    }
    public static HashMap<String, Player> getPlayermap() {
        return playermap;
    }

    public static HashMap<String, List<Player>> getCountrymap() {
        return countrymap;
    }

    public static HashMap<String, List<Player>> getClubmap() {
        return clubmap;
    }

    public static HashMap<String, List<Player>> getPositionmap() {
        return positionmap;
    }

    public static boolean saveToFile() {
        try (FileWriter writer = new FileWriter("src/main/java/com/example/database/players.txt", true)) {
            for (Player player : newPlayers) {
                writer.write(player.getName() + "," +
                        player.getCountry() + "," +
                        player.getAge() + "," +
                        player.getHeight() + "," +
                        player.getClub() + "," +
                        player.getPosition() + "," +
                        (player.getJersey() != -1 ? player.getJersey() : "") + "," +
                        player.getSalary() + "\n");
            }
        } catch (IOException ex) {
            return false;
        }
        if (removed) {
            try (FileWriter writer = new FileWriter("src/main/java/com/example/database/players.txt", false)) {
                for (Player player : playermap.values()) {
                    writer.write(player.getName() + "," +
                            player.getCountry() + "," +
                            player.getAge() + "," +
                            player.getHeight() + "," +
                            player.getClub() + "," +
                            player.getPosition() + "," +
                            (player.getJersey() != -1 ? player.getJersey() : "") + "," +
                            player.getSalary() + "\n");
                }
            } catch (IOException ex) {
                return false;
            }
        }
        return true;
    }

    public static boolean addPlayer(Player ob) {
        if (playermap.containsKey(ob.getName())) {
            return false;
        } else {
            playermap.put(ob.getName(), ob);
            countrymap.putIfAbsent(ob.getCountry(), new ArrayList<>());
            countrymap.get(ob.getCountry()).add(ob);
            if(!clubmap.containsKey(ob.getClub())){
                newClub = true;
            }
            clubmap.putIfAbsent(ob.getClub(), new ArrayList<>());
            clubmap.get(ob.getClub()).add(ob);
            positionmap.putIfAbsent(ob.getPosition(), new ArrayList<>());
            positionmap.get(ob.getPosition()).add(ob);
            return true;
        }
    }

    public static boolean loadplayers() {
        try {
            playermap.clear();
            countrymap.clear();
            clubmap.clear();
            positionmap.clear();
            File file = new File("src/main/java/com/example/database/players.txt");
            Scanner in = new Scanner(file);

            while (in.hasNextLine()) {
                String m = in.nextLine().trim();
                String[] parts = m.split(",");

                if (parts.length != 8) {
                    continue;
                }

                if (parts[6].isEmpty()) {
                    parts[6] = "-1";
                }

                try {
                    String name = parts[0].trim();
                    name = Standardization.toFullyStandardized(name);
                    String country = parts[1].trim();
                    int age = Integer.parseInt(parts[2].trim());
                    double height = Double.parseDouble(parts[3].trim());
                    String club = parts[4].trim();
                    String position = parts[5].trim();
                    int jersey = Integer.parseInt(parts[6].trim());
                    double salary = Double.parseDouble(parts[7].trim());
                    Player ob = new Player(name, country, age, height, club, position, jersey, salary);
                    addPlayer(ob);
                } catch (NumberFormatException e) {

                }
            }

            in.close();
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean removePlayer(String name) {
        Player ob = playermap.get(name);
        playermap.remove(name);
        countrymap.get(ob.getCountry()).remove(ob);
        if (countrymap.get(ob.getCountry()).isEmpty()) {
            countrymap.remove(ob.getCountry());
        }
        clubmap.get(ob.getClub()).remove(ob);
        if (clubmap.get(ob.getClub()).isEmpty()) {
            clubmap.remove(ob.getClub());
        }
        positionmap.get(ob.getPosition()).remove(ob);
        if (positionmap.get(ob.getPosition()).isEmpty()) {
            positionmap.remove(ob.getPosition());
        }
        if (newPlayers.contains(ob)) {
            newPlayers.remove(ob);
        } else {
            removed = true;
        }
        return true;
    }
    public static void transfer(Player player,String club){
        Player pl = player;
        removePlayer(player.getName());
        Player p2 = new Player(pl.getName(),pl.getCountry(),pl.getAge(),pl.getHeight(),club,pl.getPosition(),pl.getJersey(),pl.getSalary());
        addPlayer(p2);
        saveToFile();
        playermap.clear();
        countrymap.clear();
        clubmap.clear();
        positionmap.clear();
        loadplayers();
    }

    public static HashMap<String,String> loadCmPass(){
        HashMap<String,String> cmPass = new HashMap<>();
        try{
            File file = new File("src/main/java/com/example/database/CMlogin.txt");
            Scanner in = new Scanner(file);
            while(in.hasNextLine()){
                String[] parts  = in.nextLine().split(",");
                cmPass.put(parts[0],parts[1]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return cmPass;
    }

    public static void setCmPass(HashMap<String,String> cmPass){
        try (FileWriter writer = new FileWriter("src/main/java/com/example/database/CMlogin.txt", false)) {
            for(Map.Entry<String,String> entry : cmPass.entrySet()){
                writer.write(entry.getKey()+","+entry.getValue()+"\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean CMauthorization(String cname, String pass){
        try {
            File file = new File("src/main/java/com/example/database/CMlogin.txt");
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                String m = in.nextLine().trim();
                String[] parts = m.split(",");
                if(parts[0].equalsIgnoreCase(cname) && parts[1].equals(pass)){
                    return true;
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            return false;
        }
        return false;
    }

}
