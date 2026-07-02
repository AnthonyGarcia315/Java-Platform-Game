package audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {

    public static int MENU_1 = 0;
    public static int LEVEL_1 = 1;
    public static int LEVEL_2 = 2;

    public static int DIE = 0;
    public static int JUMP = 1;
    public static int GAMEOVER = 2;
    public static int LVL_COMPLETED = 3;
    public static int ATTACK_ONE = 4;
    public static int ATTACK_TWO = 5;
    public static int ATTACK_THREE = 6;
    public static final int CANNON_FIRE = 7;

    private Clip[] songs, effects;
    private int currentSongId;
    private float volume = 0.5f;
    private boolean songMute, effectMute;
    private Random rand = new Random();

    public AudioPlayer() {
        loadSongs();
        loadEffects();
        playSong(MENU_1);
    }

    private void loadSongs() {
        String[] names = { "menu", "level1", "level2" };
        songs = new Clip[names.length];
        for (int i = 0; i < songs.length; i++)
            songs[i] = getClip(names[i]);
    }

    private void loadEffects() {
        String[] effectNames = { "die", "jump", "gameover", "lvlcompleted", "attack1", "attack2", "attack3","cannonfire" };
        effects = new Clip[effectNames.length];
        for (int i = 0; i < effects.length; i++)
            effects[i] = getClip(effectNames[i]);

        updateEffectsVolume();

    }

    private Clip getClip(String name) {
        URL url = getClass().getResource("/audio/" + name + ".wav");
        AudioInputStream audio;

        try {
            audio = AudioSystem.getAudioInputStream(url);
            Clip c = AudioSystem.getClip();
            c.open(audio);
            return c;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {

            e.printStackTrace();
        }

        return null;

    }

    public void setVolume(float volume) {
        this.volume = volume;
        updateSongVolume();
        updateEffectsVolume();
    }

    public void stopSong() {
        if (songs[currentSongId].isActive())
            songs[currentSongId].stop();
    }

    public void setLevelSong(int lvlIndex) {
        if (lvlIndex % 2 == 0)
            playSong(LEVEL_1);
        else
            playSong(LEVEL_2);
    }

    public void lvlCompleted() {
        stopSong();
        playEffect(LVL_COMPLETED);
    }

    public void playAttackSound() {
        int start = 4;
        start += rand.nextInt(3);
        playEffect(start);
    }

    public void playEffect(int effect) {
        if (effects[effect] == null) return;

        // 🌟 THE BULLETPROOF AUDIO OVERLAP FIX
        // Instead of fighting the clip's hardware lock, we spin up an instantaneous,
        // independent audio line runner that can overlap seamlessly.
        try {
            // 1. Fetch the exact raw audio stream context from the source clip layout
            URL url = getClass().getResource("/audio/" + getEffectNameByIndex(effect) + ".wav");
            if (url != null) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(url);
                Clip dynamicClip = AudioSystem.getClip();
                dynamicClip.open(stream);

                // 2. Adjust volume to match your global engine sliders
                javax.sound.sampled.FloatControl gainControl =
                        (javax.sound.sampled.FloatControl) dynamicClip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
                javax.sound.sampled.FloatControl baseControl =
                        (javax.sound.sampled.FloatControl) effects[effect].getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(baseControl.getValue());

                // 3. Set a listener to auto-destroy and close the thread line when the sound finishes
                dynamicClip.addLineListener(event -> {
                    if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        dynamicClip.close();
                    }
                });

                dynamicClip.start();
            }
        } catch (Exception ex) {
            // Fallback safety layer: If system lines are busy, try standard reset execution
            effects[effect].stop();
            effects[effect].setMicrosecondPosition(0);
            effects[effect].start();
        }
    }

    // Quick helper method to map your internal integer IDs back to the string filenames
    private String getEffectNameByIndex(int index) {
        return switch (index) {
            case 0 -> "die";
            case 1 -> "jump";
            case 2 -> "gameover";
            case 3 -> "lvlcompleted";
            case 4 -> "attack1";
            case 5 -> "attack2";
            case 6 -> "attack3";
            case 7 -> "cannonfire";
            default -> "jump";
        };
    }

    public void playSong(int song) {
        stopSong();

        currentSongId = song;
        updateSongVolume();
        songs[currentSongId].setMicrosecondPosition(0);
        songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void toggleSongMute() {
        this.songMute = !songMute;
        for (Clip c : songs) {
            BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(songMute);
        }
    }

    public void toggleEffectMute() {
        this.effectMute = !effectMute;
        for (Clip c : effects) {
            BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(effectMute);
        }
        if (!effectMute)
            playEffect(JUMP);
    }

    private void updateSongVolume() {

        FloatControl gainControl = (FloatControl) songs[currentSongId].getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);

    }

    private void updateEffectsVolume() {
        for (Clip c : effects) {
            FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }

}