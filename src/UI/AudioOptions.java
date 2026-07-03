package UI;

import static util.Constants.UI.PauseButtons.SOUND_SIZE;
import static util.Constants.UI.VolumeButtons.SLIDER_WIDTH;
import static util.Constants.UI.VolumeButtons.VOLUME_HEIGHT;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import Main.Game;

public class AudioOptions {

    private VolumeButton volumeButton;
    private SoundButton musicButton, sfxButton;

    private Game game;

    public AudioOptions(Game game) {
        this.game = game;
        createSoundButtons();
        createVolumeButton();
    }

    private void createVolumeButton() {
        int vX = (int) (309 * Game.SCALE);
        int vY = (int) (278 * Game.SCALE);
        volumeButton = new VolumeButton(vX, vY, SLIDER_WIDTH, VOLUME_HEIGHT);
    }

    private void createSoundButtons() {
        int soundX = (int) (450 * Game.SCALE);
        int musicY = (int) (140 * Game.SCALE);
        int sfxY = (int) (186 * Game.SCALE);
        musicButton = new SoundButton(soundX, musicY, SOUND_SIZE, SOUND_SIZE);
        sfxButton = new SoundButton(soundX, sfxY, SOUND_SIZE, SOUND_SIZE);
    }

    public void update() {
        musicButton.update();
        sfxButton.update();

        volumeButton.update();
    }

    public void draw(Graphics g) {
        // Sound buttons
        musicButton.draw(g);
        sfxButton.draw(g);

        // Volume Button
        volumeButton.draw(g);
    }

    public void mouseDragged(MouseEvent e) {
        if (volumeButton.isMousePressed()) {

            // 🌟 1. Scale the mouse X coordinate for dragging!
            float scaleX = game.getMouseXScale();
            int gameMouseX = (int) (e.getX() / scaleX);

            float valueBefore = volumeButton.getFloatValue();

            // Pass the SCALED coordinate into the changeX method
            volumeButton.changeX(gameMouseX);

            float valueAfter = volumeButton.getFloatValue();
            if (valueBefore != valueAfter)
                game.getAudioPlayer().setVolume(valueAfter);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (isIn(e, musicButton))
            musicButton.setMousePressed(true);
        else if (isIn(e, sfxButton))
            sfxButton.setMousePressed(true);
        else if (isIn(e, volumeButton))
            volumeButton.setMousePressed(true);
    }

    public void mouseReleased(MouseEvent e) {
        if (isIn(e, musicButton)) {
            if (musicButton.isMousePressed()) {
                musicButton.setMuted(!musicButton.isMuted());
                game.getAudioPlayer().toggleSongMute();
            }

        } else if (isIn(e, sfxButton)) {
            if (sfxButton.isMousePressed()) {
                sfxButton.setMuted(!sfxButton.isMuted());
                game.getAudioPlayer().toggleEffectMute();
            }
        }

        musicButton.resetBools();
        sfxButton.resetBools();

        volumeButton.resetBools();
    }

    public void mouseMoved(MouseEvent e) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);

        volumeButton.setMouseOver(false);

        if (isIn(e, musicButton))
            musicButton.setMouseOver(true);
        else if (isIn(e, sfxButton))
            sfxButton.setMouseOver(true);
        else if (isIn(e, volumeButton))
            volumeButton.setMouseOver(true);
    }

    private boolean isIn(MouseEvent e, PauseButton b) {
        float scaleX = game.getMouseXScale();
        float scaleY = game.getMouseYScale();
        int gameMouseX = (int) (e.getX() / scaleX);
        int gameMouseY = (int) (e.getY() / scaleY);
        return b.getBounds().contains(gameMouseX, gameMouseY);
    }

}