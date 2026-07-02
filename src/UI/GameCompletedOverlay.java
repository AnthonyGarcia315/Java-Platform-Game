package UI;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import GameStates.Gamestate;
import GameStates.Playing;
import Main.Game;
import util.LoadSave;
import util.SaveManager; // Make sure this is imported!

public class GameCompletedOverlay {
    private Playing playing;
    private BufferedImage img;
    private MenuButton quit, credit;
    private int imgX, imgY, imgW, imgH;
    private String playerName = "";
    private final int MAX_NAME_LENGTH = 10;
    private boolean isSaved = false;

    public GameCompletedOverlay(Playing playing) {
        this.playing = playing;
        createImg();
        createButtons();
    }

    private void createButtons() {
        quit = new MenuButton(Game.GAME_WIDTH / 2, (int) (270 * Game.SCALE), 2, Gamestate.MENU);
        credit = new MenuButton(Game.GAME_WIDTH / 2, (int) (200 * Game.SCALE), 3, Gamestate.CREDITS);
    }

    private void createImg() {
        img = LoadSave.GetSpriteAtlas(LoadSave.GAME_COMPLETED);
        imgW = (int) (img.getWidth() * Game.SCALE);
        imgH = (int) (img.getHeight() * Game.SCALE);
        imgX = Game.GAME_WIDTH / 2 - imgW / 2;
        imgY = (int) (100 * Game.SCALE);

    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.drawImage(img, imgX, imgY, imgW, imgH, null);

        credit.draw(g);
        quit.draw(g);
        g.setFont(new Font("Arial", Font.BOLD, (int) (14 * Game.SCALE)));
        int textY = (int)(50 * Game.SCALE); // Positions it below your buttons

        if (!isSaved) {
            g.setColor(Color.WHITE);
            String prompt = "Enter Name to Save Stats:";
            int promptWidth = g.getFontMetrics().stringWidth(prompt);
            g.drawString(prompt, (Game.GAME_WIDTH / 2) - (promptWidth / 2), textY);

            // Draw Name with Blinking Cursor
            String displayString = playerName;
            if (System.currentTimeMillis() / 500 % 2 == 0) {
                displayString += "_";
            }
            int nameWidth = g.getFontMetrics().stringWidth(displayString);
            g.setColor(Color.YELLOW);
            g.drawString(displayString, (Game.GAME_WIDTH / 2) - (nameWidth / 2), textY + (int)(25 * Game.SCALE));

            // Enter instruction
            g.setFont(new Font("Arial", Font.ITALIC, (int) (10 * Game.SCALE)));
            g.setColor(Color.LIGHT_GRAY);
            String enterPrompt = "Press ENTER to save";
            int enterWidth = g.getFontMetrics().stringWidth(enterPrompt);
            g.drawString(enterPrompt, (Game.GAME_WIDTH / 2) - (enterWidth / 2), textY + (int)(50 * Game.SCALE));
        } else {
            // Success Message
            g.setColor(Color.GREEN);
            String savedPrompt = "Stats Saved Successfully!";
            int savedWidth = g.getFontMetrics().stringWidth(savedPrompt);
            g.drawString(savedPrompt, (Game.GAME_WIDTH / 2) - (savedWidth / 2), textY + (int)(20 * Game.SCALE));
        }
    }

    public void update() {
        credit.update();
        quit.update();
    }

    private boolean isIn(MenuButton b, MouseEvent e) {
        return b.getBounds().contains(e.getX(), e.getY());
    }

    public void mouseMoved(MouseEvent e) {
        credit.setMouseOver(false);
        quit.setMouseOver(false);

        if (isIn(quit, e))
            quit.setMouseOver(true);
        else if (isIn(credit, e))
            credit.setMouseOver(true);
    }

    public void mouseReleased(MouseEvent e) {
        if (isIn(quit, e)) {
            if (quit.isMousePressed()) {
                resetTypingBox(); // Reset before leaving
                playing.resetAll();
                playing.resetGameCompleted();
                playing.setGamestate(Gamestate.MENU);

            }
        } else if (isIn(credit, e))
            if (credit.isMousePressed()) {
                resetTypingBox(); // Reset before leaving
                playing.resetAll();
                playing.resetGameCompleted();
                playing.setGamestate(Gamestate.CREDITS);
            }

        quit.resetBools();
        credit.resetBools();
    }
    private void resetTypingBox() {
        playerName = "";
        isSaved = false;
    }

    public void mousePressed(MouseEvent e) {
        if (isIn(quit, e))
            quit.setMousePressed(true);
        else if (isIn(credit, e))
            credit.setMousePressed(true);
    }
    public void keyPressed(KeyEvent e){
        // If already saved, don't let them type anymore
        if (isSaved) return;

        // Handle Backspace
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (playerName.length() > 0) {
                playerName = playerName.substring(0, playerName.length() - 1);
            }
        }
        // Handle Enter (Submit)
        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (playerName.length() > 0) {
                util.StatsTracker tracker = playing.getGame().getStatsTracker();
                SaveManager.saveScore(playerName, tracker.getTotalEnemiesDefeated(), tracker.getPotionsCollected(), tracker.getTotalDeaths());
                playing.getGame().getStats().refreshLeaderboard();
                isSaved = true; // Lock the typing box
            }
        }
        // Handle Typing (Letters and Numbers only)
        else if (playerName.length() < MAX_NAME_LENGTH) {
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c)) {
                playerName += c;
            }
        }
    }
}
