package GameStates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Main.Game;
import util.SaveManager;

public class Stats extends State implements Statemethods {

    // This will hold our loaded scores so we don't read the file 120 times a second!
    private ArrayList<String> savedScores = new ArrayList<>();

    public Stats(Game game) {
        super(game);
        refreshLeaderboard(); // Load the scores when the game first starts
    }

    // Call this method right after saving a new score to update the list!
    public void refreshLeaderboard() {
        savedScores = SaveManager.loadScores();
    }

    @Override
    public void update() {
        // Nothing needs to constantly update here
    }

    @Override
    public void draw(Graphics g) {
        // 1. Dark background overlay
        g.setColor(new Color(10, 10, 10, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // --- MENU BOARD ---
        int boardWidth = (int) (400 * Game.SCALE); // Made slightly wider to fit names
        int boardHeight = (int) (300 * Game.SCALE);
        int boardX = (Game.GAME_WIDTH / 2) - (boardWidth / 2);
        int boardY = (int) (40 * Game.SCALE);

        // Draw the main panel
        g.setColor(new Color(40, 40, 40, 240));
        g.fillRoundRect(boardX, boardY, boardWidth, boardHeight, 30, 30);

        // Draw border
        g.setColor(new Color(180, 180, 180));
        g.drawRoundRect(boardX, boardY, boardWidth, boardHeight, 30, 30);
        g.drawRoundRect(boardX + 2, boardY + 2, boardWidth - 4, boardHeight - 4, 28, 28);

        // --- HEADER ---
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int) (20 * Game.SCALE)));
        String title = "TOP SCORES";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.GAME_WIDTH / 2) - (titleWidth / 2), boardY + (int)(40 * Game.SCALE));

        g.drawLine(boardX + 30, boardY + (int)(55 * Game.SCALE), boardX + boardWidth - 30, boardY + (int)(55 * Game.SCALE));

        // --- DRAW LEADERBOARD DATA ---
        g.setFont(new Font("Arial", Font.PLAIN, (int) (12 * Game.SCALE)));
        int startY = boardY + (int) (85 * Game.SCALE);
        int spacing = (int) (35 * Game.SCALE);

        if (savedScores.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("No stats saved yet. Go play the game!", boardX + (int)(60 * Game.SCALE), startY);
        } else {
            // Only draw the Top 5 so it doesn't run off the bottom of the board!
            int limit = Math.min(5, savedScores.size());

            // Loop backwards so the most recent run is at the top!
            int drawIndex = 0;
            for (int i = savedScores.size() - 1; i >= savedScores.size() - limit; i--) {
                String rawData = savedScores.get(i);
                String[] parts = rawData.split(","); // Splits: Name, Enemies, Potions, Deaths

                if(parts.length == 4) {
                    String name = parts[0];
                    String statsText = "Kills: " + parts[1] + "  |  Potions: " + parts[2] + "  |  Deaths: " + parts[3];

                    // Draw Name (Yellow)
                    g.setColor(Color.YELLOW);
                    g.drawString((drawIndex + 1) + ". " + name, boardX + (int)(30 * Game.SCALE), startY + (drawIndex * spacing));

                    // Draw Stats (White)
                    g.setColor(Color.WHITE);
                    g.drawString(statsText, boardX + (int)(150 * Game.SCALE), startY + (drawIndex * spacing));
                }
                drawIndex++;
            }
        }

        // --- EXIT INSTRUCTIONS ---
        g.setFont(new Font("Arial", Font.ITALIC, (int) (10 * Game.SCALE)));
        g.setColor(Color.LIGHT_GRAY);
        String exitText = "Press ESC to return to Menu";
        int exitWidth = g.getFontMetrics().stringWidth(exitText);
        g.drawString(exitText, (Game.GAME_WIDTH / 2) - (exitWidth / 2), boardY + boardHeight - (int)(20 * Game.SCALE));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setGamestate(Gamestate.MENU);
        }
    }

    // Unused overrides
    @Override
    public void mouseclicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
}