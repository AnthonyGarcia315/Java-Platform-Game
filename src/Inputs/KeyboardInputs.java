package Inputs;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import GameStates.Gamestate;
import Main.GamePanel;

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
        // 🌟 THE TRIPLE MODE CYCLE SWITCH (F11)
        if (e.getKeyCode() == KeyEvent.VK_F11) {
            Main.GameWindow windowManager = gamePanel.getGame().getGameWindow();
            Main.GameWindow.WindowMode current = windowManager.getCurrentMode();

            // Cycle routine logic path: Windowed -> Maximized -> Fullscreen -> Loop back
            if (current == Main.GameWindow.WindowMode.WINDOWED) {
                windowManager.setWindowMode(Main.GameWindow.WindowMode.MAXIMIZED_WINDOWED);
            } else if (current == Main.GameWindow.WindowMode.MAXIMIZED_WINDOWED) {
                windowManager.setWindowMode(Main.GameWindow.WindowMode.EXCLUSIVE_FULLSCREEN);
            } else {
                windowManager.setWindowMode(Main.GameWindow.WindowMode.WINDOWED);
            }
            return;
        }

        // Your existing routing logic...
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().keyPressed(e);
            case PLAYING -> gamePanel.getGame().getPlaying().keyPressed(e);
            case OPTIONS -> gamePanel.getGame().getGameOptions().keyPressed(e);
            case STATS -> gamePanel.getGame().getStats().keyPressed(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not In Use
    }
}