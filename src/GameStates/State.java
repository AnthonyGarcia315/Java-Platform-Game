package GameStates;

import java.awt.event.MouseEvent;

import audio.AudioPlayer;
import Main.Game;
import UI.MenuButton;

public class State {

    protected Game game;

    public State(Game game) {
        this.game = game;
    }

    public boolean isIn(MouseEvent e, UI.MenuButton b) {
        // 1. Get the current scale factor of the stretched window
        float scaleX = game.getMouseXScale();
        float scaleY = game.getMouseYScale();

        // 2. Map the physical screen mouse pixels back to virtual game pixels
        int gameMouseX = (int) (e.getX() / scaleX);
        int gameMouseY = (int) (e.getY() / scaleY);

        // 3. Check if the normalized coordinate falls inside the button bounds
        return b.getBounds().contains(gameMouseX, gameMouseY);
    }

    public Game getGame() {
        return game;
    }

    @SuppressWarnings("incomplete-switch")
    public void setGamestate(Gamestate state) {
        switch (state) {
            case MENU -> game.getAudioPlayer().playSong(AudioPlayer.MENU_1);
            case PLAYING -> game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLevelIndex());
        }

        Gamestate.state = state;
    }

}