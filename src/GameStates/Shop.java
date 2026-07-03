package GameStates;

import Main.Game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Shop extends State implements Statemethods {

    public Shop(Game game) {
        super(game);
    }

    @Override
    public void update() {
        // We can add hovering button logic here later!
    }

    @Override
    public void draw(Graphics g) {
        // 1. Draw a dark transparent background
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // 2. Draw the Shop Menu Board (Increased height to fit new items)
        int menuWidth = (int) (400 * Game.SCALE);
        int menuHeight = (int) (400 * Game.SCALE); // Increased from 300 to 400
        int menuX = Game.GAME_WIDTH / 2 - menuWidth / 2;
        int menuY = Game.GAME_HEIGHT / 2 - menuHeight / 2;

        g.setColor(new Color(139, 69, 19)); // Brown wooden color
        g.fillRect(menuX, menuY, menuWidth, menuHeight);
        g.setColor(Color.BLACK);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);

        // 3. Draw Title and Wallet
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int) (24 * Game.SCALE)));
        g.drawString("MERCHANT SHOP", menuX + (int)(100 * Game.SCALE), menuY + (int)(40 * Game.SCALE));

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, (int) (16 * Game.SCALE)));
        int coins = game.getStatsTracker().getCoins();
        g.drawString("Wallet: $" + coins, menuX + (int)(150 * Game.SCALE), menuY + (int)(70 * Game.SCALE));

        // 4. Draw Items for Sale
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, (int) (14 * Game.SCALE)));

        g.drawString("Press [1] to Buy: +20 Max Health ($10)", menuX + (int)(50 * Game.SCALE), menuY + (int)(120 * Game.SCALE));
// Replace the old refill health string
        g.drawString("Press [2] to Buy: Speed Boots ($50)", menuX + (int)(50 * Game.SCALE), menuY + (int)(170 * Game.SCALE));
        // NEW COMBAT UPGRADES
        g.drawString("Press [3] to Buy: +1 Base Damage ($50)", menuX + (int)(50 * Game.SCALE), menuY + (int)(220 * Game.SCALE));
        g.drawString("Press [4] to Buy: Fire Blade DOT ($100)", menuX + (int)(50 * Game.SCALE), menuY + (int)(270 * Game.SCALE));

        // 5. Draw Exit Instructions
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Press ESC to return to Menu", menuX + (int)(100 * Game.SCALE), menuY + (int)(360 * Game.SCALE));
        g.drawString("Press [5] to Buy: Double Jump ($25)", menuX + (int)(50 * Game.SCALE), menuY + (int)(320 * Game.SCALE));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Gamestate.state = Gamestate.MENU;
        }
        else if (e.getKeyCode() == KeyEvent.VK_1) {
            // BUY UPGRADE 1: Max Health
            if (game.getStatsTracker().spendCoins(10)) {
                game.getStatsTracker().addBonusMaxHealth(20);
                if (game.getPlaying().getPlayer() != null) {
                    game.getPlaying().getPlayer().increaseMaxHealth(20);
                }
                System.out.println("DEBUG [Shop]: Bought Health! Coins: " + game.getStatsTracker().getCoins());
            } else {
                System.out.println("DEBUG [Shop]: Not enough coins.");
            }
        }
// In Shop.java -> keyPressed() method
        else if (e.getKeyCode() == KeyEvent.VK_2) {
            // BUY UPGRADE 2: Speed Boots
            if (game.getStatsTracker().spendCoins(50)) {
                game.getStatsTracker().setSpeedBoostUnlocked(true);

                // Update the player immediately if they are in the game
                if (game.getPlaying().getPlayer() != null) {
                    game.getPlaying().getPlayer().updateSpeed();
                }
                System.out.println("DEBUG [Shop]: Speed Boost Unlocked!");
            } else {
                System.out.println("DEBUG [Shop]: Not enough coins.");
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_3) {
            // BUY UPGRADE 3: Base Damage
            if (game.getStatsTracker().spendCoins(50)) {
                // You will need to add this method to your StatsTracker class!
                game.getStatsTracker().addBonusDamage(1);
                System.out.println("DEBUG [Shop]: Upgraded Base Damage!");
            } else {
                System.out.println("DEBUG [Shop]: Not enough coins!");
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_4) {
            // BUY UPGRADE 4: Fire Blade DOT
            if (game.getStatsTracker().spendCoins(100)) {
                // You will need to add a boolean and setter to your StatsTracker class!
                game.getStatsTracker().unlockFireBlade(true);
                System.out.println("DEBUG [Shop]: Unlocked Fire Blade!");
            } else {
                System.out.println("DEBUG [Shop]: Not enough coins!");
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_5) {
            if (game.getStatsTracker().spendCoins(25)) {
                game.getStatsTracker().setDoubleJumpUnlocked(true);

                // Apply to current player if they are in the game

                System.out.println("DEBUG [Shop]: Double Jump Unlocked!");
            }
        }


    }

    @Override
    public void mouseclicked(MouseEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
}