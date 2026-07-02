package Inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import GameStates.Gamestate;
import Main.Game;
import Main.GamePanel;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private GamePanel gamePanel;

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void mouseDragged(MouseEvent e) {
        MouseEvent scaled = getScaledEvent(e);
        switch (Gamestate.state) {
            case PLAYING -> gamePanel.getGame().getPlaying().mouseDragged(scaled);
            case OPTIONS -> gamePanel.getGame().getGameOptions().mouseDragged(scaled);
        }
    }
    // 🌟 THE TRANSLATION FIX: Converts real screen clicks back to game dimensions
    private MouseEvent getScaledEvent(MouseEvent e) {
        // Calculate scale ratios between actual window and virtual game resolution
        float scaleX = (float) Game.GAME_WIDTH / gamePanel.getWidth();
        float scaleY = (float) Game.GAME_HEIGHT / gamePanel.getHeight();

        // Generate a new temporary mouse event mapped back into original constraints
        return new MouseEvent(
                e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
                (int)(e.getX() * scaleX), (int)(e.getY() * scaleY),
                e.getClickCount(), e.isPopupTrigger(), e.getButton()
        );
    }
    @SuppressWarnings("incomplete-switch")
    @Override
    public void mouseMoved(MouseEvent e) {
        MouseEvent scaled = getScaledEvent(e); // Scale it first so hover effects light up!
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().mouseMoved(scaled);
            case PLAYING -> gamePanel.getGame().getPlaying().mouseMoved(scaled);
            case OPTIONS -> gamePanel.getGame().getGameOptions().mouseMoved(scaled);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void mouseClicked(MouseEvent e) {
        switch (Gamestate.state) {
            case PLAYING -> gamePanel.getGame().getPlaying().mouseClicked(e);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void mousePressed(MouseEvent e) {
        MouseEvent scaled = getScaledEvent(e); // Scale it first!
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().mousePressed(scaled);
            case PLAYING -> gamePanel.getGame().getPlaying().mousePressed(scaled);
            case OPTIONS -> gamePanel.getGame().getGameOptions().mousePressed(scaled);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void mouseReleased(MouseEvent e) {
        MouseEvent scaled = getScaledEvent(e); // Scale it first!
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().mouseReleased(scaled);
            case PLAYING -> gamePanel.getGame().getPlaying().mouseReleased(scaled);
            case OPTIONS -> gamePanel.getGame().getGameOptions().mouseReleased(scaled);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not In use
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not In use
    }

}