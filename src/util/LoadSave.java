package util;

import entities.PlayerCharacters;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;

public class LoadSave {

    // --- Image File Constants ---
    public static final String PLAYER_PIRATE = "player_sprites.png", PLAYER_ORC = "player_orc.png", PLAYER_SOLDIER = "player_soldier.png";
    public static final String LEVEL_ATLAS = "outside_sprites.png", MENU_BUTTONS = "button_atlas.png", MENU_BACKGROUND = "menu_background.png";
    public static final String PAUSE_BACKGROUND = "pause_menu.png", SOUND_BUTTONS = "sound_button.png", URM_BUTTONS = "urm_buttons.png";
    public static final String VOLUME_BUTTONS = "volume_buttons.png", MENU_BACKGROUND_IMG = "background_menu.png", PLAYING_BG_IMG = "playing_bg_img.png";
    public static final String BIG_CLOUDS = "big_clouds.png", SMALL_CLOUDS = "small_clouds.png", CRABBY_SPRITE = "crabby_sprite.png";
    public static final String STATUS_BAR = "health_power_bar.png", COMPLETED_IMG = "completed_sprite.png", POTION_ATLAS = "potions_sprites.png";
    public static final String CONTAINER_ATLAS = "objects_sprites.png", TRAP_ATLAS = "trap_atlas.png", CANNON_ATLAS = "cannon_atlas.png";
    public static final String CANNON_BALL = "ball.png", DEATH_SCREEN = "death_screen.png", OPTIONS_MENU = "options_background.png";
    public static final String PINKSTAR_ATLAS = "pinkstar_atlas.png", QUESTION_ATLAS = "question_atlas.png", EXCLAMATION_ATLAS = "exclamation_atlas.png";
    public static final String SHARK_ATLAS = "shark_atlas.png", CREDITS = "credits_list.png", GRASS_ATLAS = "grass_atlas.png";
    public static final String TREE_ONE_ATLAS = "tree_one_atlas.png", TREE_TWO_ATLAS = "tree_two_atlas.png", GAME_COMPLETED = "game_completed.png";
    public static final String RAIN_PARTICLE = "rain_particle.png", WATER_TOP = "water_atlas_animation.png", WATER_BOTTOM = "water.png";
    public static final String SHIP = "ship.png", COIN_SPRITE = "coin_Sheet.png";

    public static BufferedImage[][] loadAnimations(PlayerCharacters pc) {
        BufferedImage img = GetSpriteAtlas(pc.playerAtlas);
        BufferedImage[][] animations = new BufferedImage[pc.rowA][pc.colA];
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(i * pc.spriteW, j * pc.spriteH, pc.spriteW, pc.spriteH);
            }
        }
        return animations;
    }

    public static BufferedImage GetSpriteAtlas(String fileName) {
        // Use try-with-resources to automatically close the stream safely
        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {
            if (is == null) throw new IOException("File not found: " + fileName);
            return ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("LoadSave Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Returning null here alerts the calling class to handle the error
    }

    public static BufferedImage[] GetAllLevels() {
        URL url = LoadSave.class.getResource("/lvls");
        if (url == null) return new BufferedImage[0];

        try {
            File file = new File(url.toURI());
            File[] files = file.listFiles((dir, name) -> name.endsWith(".png"));
            if (files == null) return new BufferedImage[0];

            BufferedImage[] imgs = new BufferedImage[files.length];
            // Simple sorting by name
            for (int i = 0; i < files.length; i++) {
                int levelIndex = Integer.parseInt(files[i].getName().replace(".png", "")) - 1;
                imgs[levelIndex] = ImageIO.read(files[i]);
            }
            return imgs;
        } catch (URISyntaxException | IOException | NumberFormatException e) {
            e.printStackTrace();
            return new BufferedImage[0];
        }
    }
}