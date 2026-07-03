package util;

import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StatsTracker {

    private int totalEnemiesDefeated = 0;
    private int totalDeaths = 0;
    private int potionsCollected = 0;

    // Storing exact coordinates of where the player died
    private ArrayList<String> deathLocations = new ArrayList<>();

    public void recordEnemyDefeated() {
        totalEnemiesDefeated++;
    }

    public void recordPotionCollected() {
        potionsCollected++;
    }

    public void recordDeath(Rectangle2D.Float hitbox) {
        totalDeaths++;
        // Format the coordinates to 2 decimal places for cleaner reading
        String location = String.format("X: %.2f, Y: %.2f", hitbox.x, hitbox.y);
        deathLocations.add(location);
    }

    public void exportData() {
        try {
            // This creates a file right in your project folder
            FileWriter writer = new FileWriter("Game_Telemetry_Data.txt");

            writer.write("=== GAME COMPLETION TELEMETRY ===\n");
            writer.write("Total Enemies Defeated: " + totalEnemiesDefeated + "\n");
            writer.write("Potions Collected: " + potionsCollected + "\n");
            writer.write("Total Player Deaths: " + totalDeaths + "\n");

            writer.write("\n=== DEATH LOCATIONS ===\n");
            if (deathLocations.isEmpty()) {
                writer.write("Flawless run! No deaths.\n");
            } else {
                for (int i = 0; i < deathLocations.size(); i++) {
                    writer.write("Death " + (i + 1) + " at -> " + deathLocations.get(i) + "\n");
                }
            }

            writer.close();
            System.out.println("Telemetry successfully exported to Game_Telemetry_Data.txt");

        } catch (IOException e) {
            System.out.println("Failed to export telemetry data.");
            e.printStackTrace();
        }
    }
    public int getTotalEnemiesDefeated() {
        return totalEnemiesDefeated;
    }

    public int getPotionsCollected() {
        return potionsCollected;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    // Optional: Reset stats if they hit replay
    public void resetTracker() {
        totalEnemiesDefeated = 0;
        totalDeaths = 0;
        potionsCollected = 0;
        deathLocations.clear();
    }

}