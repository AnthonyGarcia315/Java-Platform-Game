package Inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import GameStates.Gamestate;
import Main.GamePanel;
import Main.GameWindow;

public class KeyboardInputs implements KeyListener {

    private GamePanel gamePanel;

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void keyReleased(KeyEvent e) {
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().keyReleased(e);
            case PLAYING -> gamePanel.getGame().getPlaying().keyReleased(e);
            case CREDITS -> gamePanel.getGame().getCredits().keyReleased(e);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F11) {
            GameWindow gw = gamePanel.getGame().getGameWindow();
            // Toggle logic: If mode is not FULLSCREEN, make it FULLSCREEN. Otherwise, make it WINDOWED.
            if (gw.getCurrentMode() == GameWindow.WindowMode.EXCLUSIVE_FULLSCREEN) {
                gw.setWindowMode(GameWindow.WindowMode.WINDOWED);
            } else {
                gw.setWindowMode(GameWindow.WindowMode.EXCLUSIVE_FULLSCREEN);
            }
        }
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().keyPressed(e);
            case PLAYING -> gamePanel.getGame().getPlaying().keyPressed(e);
            case OPTIONS -> gamePanel.getGame().getGameOptions().keyPressed(e);
            case CREDITS -> gamePanel.getGame().getCredits().keyPressed(e);

            // 🌟 ADD THIS LINE TO FIX YOUR ESCAPE KEY! 🌟
            case STATS -> gamePanel.getGame().getStats().keyPressed(e);

            // If you added PlayerSelection to this file too, make sure it's here:
            case PLAYER_SELECTION -> gamePanel.getGame().getPlayerSelection().keyPressed(e);
            case SHOP -> gamePanel.getGame().getShop().keyPressed(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not In Use
    }
}