package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SaveManager {

    // The file will be created in your main project folder
    private static final String FILE_PATH = "leaderboard.txt";

    // 1. Save a new run to the file
    public static void saveScore(String playerName, int enemies, int potions, int deaths) {
        // The 'true' in FileWriter means we APPEND to the file, not overwrite it!
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(playerName + "," + enemies + "," + potions + "," + deaths);
            bw.newLine();
            System.out.println("Stats saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving stats!");
            e.printStackTrace();
        }
    }

    // 2. Load all saved runs to display on the Stats Screen
    public static ArrayList<String> loadScores() {
        ArrayList<String> scores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                scores.add(line); // Adds the format: "Name,Enemies,Potions,Deaths"
            }
        } catch (IOException e) {
            System.out.println("No previous save file found. A new one will be created when you save.");
        }
        return scores;
    }
}