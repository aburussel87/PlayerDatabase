package com.example.user;
import com.example.database.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class userOperation implements Operation {
    @Override
    public Player searchByName(String name) {
        if (ManagePlayer.getPlayermap().containsKey(name)) {
            return ManagePlayer.getPlayermap().get(name);
        }
        return null;
    }

    @Override
    public List<Player> searchByClubAndCountry(String country, String Club) {
        List<Player> players = new ArrayList<>();
        if (Club.equals("Any")) {
            if (ManagePlayer.getCountrymap().containsKey(country)) {
                players = ManagePlayer.getCountrymap().get(country);
            }
        } else {
            if (ManagePlayer.getCountrymap().containsKey(country)) {
                for (Player p : ManagePlayer.getCountrymap().get(country)) {
                    if (p.getClub().equals(Club)) {
                        players.add(p);
                    }
                }
            }
        }
        return players;
    }

    @Override
    public List<Player> searchByPosition(String position) {
        List<Player> players = new ArrayList<>();
        if (ManagePlayer.getPositionmap().containsKey(position)) {
            players = ManagePlayer.getPositionmap().get(position);
        }
        return players;
    }

    @Override
    public List<Player> searchBySalaryRange(Double min, Double max) {
        List<Player> players = new ArrayList<>();
        for (Player player : ManagePlayer.getPlayermap().values()) {
            if (player.getSalary() >= min && player.getSalary() <= max) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    public HashMap<String, List<Player>> countrywisePlayercount() {
        return ManagePlayer.getCountrymap();
    }

    @Override
    public List<Player> maximumSalary(String club) {
        List<Player> players = new ArrayList<>();
        if (ManagePlayer.getClubmap().containsKey(club)) {
            double max = Double.MIN_VALUE;
            for (Player p : ManagePlayer.getClubmap().get(club)) {
                if (p.getSalary() >= max) {
                    max = p.getSalary();
                }
            }
            for (Player p : ManagePlayer.getClubmap().get(club)) {
                if (p.getSalary() == max) {
                    players.add(p);
                }
            }
        }
        return players;
    }

    @Override
    public List<Player> maximumAge(String club) {
        List<Player> players = new ArrayList<>();
        if (ManagePlayer.getClubmap().containsKey(club)) {
            int max = Integer.MIN_VALUE;
            for (Player p : ManagePlayer.getClubmap().get(club)) {
                if (p.getAge() >= max) {
                    max = p.getAge();
                }
            }
            for (Player p : ManagePlayer.getClubmap().get(club)) {
                if (p.getAge() == max) {
                    players.add(p);
                }
            }
        }
        return players;
    }

    @Override
    public List<Player> maximumHeight(String club) {
        List<Player> players = new ArrayList<>();
        if (ManagePlayer.getClubmap().containsKey(club)) {
            Double max = Double.MIN_VALUE;
            for (Player p : ManagePlayer.getClubmap().get(club)) {
                if (p.getHeight() >= max) {
                    max = p.getHeight();
                }
            }
            for (Player p : ManagePlayer.getClubmap().get(club)) {
                if (p.getHeight() == max) {
                    players.add(p);
                }
            }
        }
        return players;
    }

    @Override
    public double yearlySalary(String club) {
        double total = 0;
        if (ManagePlayer.getClubmap().containsKey(club)) {
            for (Player p : ManagePlayer.getClubmap().get(club)) {
                total += p.getSalary();
            }
        }
        total *= 52;
        return total;
    }
    
    @Override
    public HashMap<String, List<Player>> lessthan() {
        return ManagePlayer.getClubmap();
    }
    @Override
    public List<Player> clubPosition(String club) {
        List<Player> players = new ArrayList<>();
        if (ManagePlayer.getClubmap().containsKey(club)) {
            players = ManagePlayer.getClubmap().get(club);
        }
        return players;
    }
}
