package util;

import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StatsTracker {

    // --- Stats Data ---
    private int totalEnemiesDefeated, totalDeaths, potionsCollected;
    private int coins, savedCoins;
    private int highestLevel = 0;
    private ArrayList<String> deathLocations = new ArrayList<>();

    // --- Upgrades ---
    private int bonusMaxHealth = 0;
    private int bonusDamage = 0;
    private boolean doubleJumpUnlocked = false;
    private boolean speedBoostUnlocked = false;
    private boolean fireBladeUnlocked = false;

    // --- Record Methods ---
    public void recordEnemyDefeated() { totalEnemiesDefeated++; }
    public void recordPotionCollected() { potionsCollected++; }

    public void recordDeath(Rectangle2D.Float hitbox) {
        totalDeaths++;
        deathLocations.add(String.format("X: %.2f, Y: %.2f", hitbox.x, hitbox.y));
    }

    // --- Upgrade Getters/Setters ---
    public void setDoubleJumpUnlocked(boolean b) { this.doubleJumpUnlocked = b; }
    public boolean isDoubleJumpUnlocked() { return doubleJumpUnlocked; }

    public void setSpeedBoostUnlocked(boolean b) { this.speedBoostUnlocked = b; }
    public boolean isSpeedBoostUnlocked() { return speedBoostUnlocked; }

    public void addBonusMaxHealth(int amount) { this.bonusMaxHealth += amount; }
    public int getBonusMaxHealth() { return bonusMaxHealth; }

    public void addBonusDamage(int damage) { this.bonusDamage += damage; }
    public int getBonusDamage() { return bonusDamage; }

    public void unlockFireBlade(boolean b) { this.fireBladeUnlocked = b; }
    public boolean hasFireBlade() { return fireBladeUnlocked; }

    // --- Economy Methods ---
    public void addCoins(int amount) { coins += amount; }
    public void lockInCoins() { savedCoins = coins; }
    public void revertCoins() { coins = savedCoins; }

    public boolean spendCoins(int cost) {
        if (coins >= cost) {
            coins -= cost;
            savedCoins = coins;
            return true;
        }
        return false;
    }
    public int getCoins() { return coins; }

    // --- Level Progression ---
    public void unlockNextLevel(int nextLevelIndex) {
        highestLevel = Math.max(highestLevel, nextLevelIndex);
    }
    public int getHighestLevel() { return highestLevel; }

    // --- Data Persistence ---
    public void exportData() {
        try (FileWriter writer = new FileWriter("Game_Telemetry_Data.txt")) {
            writer.write("=== GAME COMPLETION TELEMETRY ===\n");
            writer.write("Total Enemies Defeated: " + totalEnemiesDefeated + "\n");
            writer.write("Potions Collected: " + potionsCollected + "\n");
            writer.write("Total Player Deaths: " + totalDeaths + "\n");
            writer.write("DoubleJumpUnlocked: " + doubleJumpUnlocked + "\n");
            writer.write("BonusHealth: " + bonusMaxHealth + "\n");
            writer.write("BonusDamage: " + bonusDamage + "\n");

            writer.write("\n=== DEATH LOCATIONS ===\n");
            if (deathLocations.isEmpty()) {
                writer.write("Flawless run!\n");
            } else {
                for (String loc : deathLocations) writer.write("Death at -> " + loc + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetTracker() {
        totalEnemiesDefeated = totalDeaths = potionsCollected = 0;
        deathLocations.clear();
    }
    public int getTotalEnemiesDefeated() {
        return totalEnemiesDefeated;
    }
    public int getPotionsCollected(){
        return potionsCollected;
    }
    public int getTotalDeaths(){
        return totalDeaths;
    }
    public int getSavedCoins(){
        return savedCoins;
    }
}