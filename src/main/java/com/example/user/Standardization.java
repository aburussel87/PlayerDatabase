package com.example.user;

import java.security.SecureRandom;

public class Standardization {
    public static boolean isAllDigits(String str) {
        return str != null && !str.isEmpty() && str.chars().allMatch(Character::isDigit);
    }

    public static String toFullyStandardized(String input) {
        input = input.trim().replaceAll("\\s+", " ");
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (String word : input.split(" ")) {
            if (word.length() == 2 && word.equalsIgnoreCase(word.toUpperCase()) && i == 0) {
                result.append(word.toUpperCase()).append(" ");
            } else if (word.length() > 1) {
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase()).append(" ");
            } else {
                result.append(word.toUpperCase()).append(" ");
            }
            i++;
        }
        return result.toString().trim();
    }

    public static String toMillion(double salary) {
        if (salary < 1000000) {
            return String.format("%.2f $", salary);
        } else if (salary < 200000000) {
            return String.format("%.2f M $", salary / 1000000);
        } else
            return String.format("%.2f Cr. $", salary / 10000000);
    }

    public static boolean isAllAlphabet(String str) {
        return str != null && !str.isEmpty()
                && str.chars().allMatch(c -> Character.isLetter(c) || Character.isWhitespace(c));
    }

    public static boolean isDouble(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?");
    }

    public static String isMatchingClub(String club) {
        if (club.equalsIgnoreCase("kkr")) {
            return "Kolkata Knight Riders";
        } else if (club.equalsIgnoreCase("rcb")) {
            return "Royal Challengers Bangalore";
        } else if (club.equalsIgnoreCase("mi")) {
            return "Mumbai Indians";
        } else if (club.equalsIgnoreCase("srh")) {
            return "Sunrisers Hyderabad";
        } else if (club.equalsIgnoreCase("csk")) {
            return "Chennai Super Kings";
        } else if (club.equalsIgnoreCase("gt")) {
            return "Gujarat Titans";
        } else if (club.equalsIgnoreCase("lsg")) {
            return "Lucknow Super Giants";
        } else if (club.equalsIgnoreCase("dc")) {
            return "Delhi Capitals";
        } else if (club.equalsIgnoreCase("rr")) {
            return "Rajasthan Royals";
        }
        return "";
    }

    public static String generatePassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4 characters.");
        }
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String allCharacters = upperCaseLetters /* +lowerCaseLetters */ + digits;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        //password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        for (int i = 4; i < length; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }
        return shuffleString(password.toString(), random);
    }

    private static String shuffleString(String input, SecureRandom random) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}
