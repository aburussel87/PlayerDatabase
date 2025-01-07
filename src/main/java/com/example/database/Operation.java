package com.example.database;

import java.util.List;
import java.util.HashMap;

public interface Operation {
    public Player searchByName(String name);

    List<Player> searchByClubAndCountry(String country, String Club);

    List<Player> searchByPosition(String position);

    List<Player> searchBySalaryRange(Double min, Double max);

    HashMap<String, List<Player>> countrywisePlayercount();

    List<Player> maximumSalary(String club);

    List<Player> maximumAge(String club);

    List<Player> maximumHeight(String club);

    List<Player> clubPosition(String club);

    HashMap<String, List<Player>> lessthan();

    double yearlySalary(String club);
}
