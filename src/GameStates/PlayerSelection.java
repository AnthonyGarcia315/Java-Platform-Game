package GameStates;

import entities.PlayerCharacters;
import Main.Game;
import UI.MenuButton;
import util.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static util.Constants.ANI_SPEED;
import static util.Constants.PlayerConstants.IDLE;

public class PlayerSelection extends State implements Statemethods {

    private BufferedImage backgroundImg, backgroundImgPink;
    private int menuX, menuY, menuWidth, menuHeight;
    private MenuButton playButton;
    private int playerIndex = 0;

    private CharacterAnimation[] characterAnimations;


    public PlayerSelection(Game game) {
        super(game);

        loadButtons();
        loadBackground();
        backgroundImgPink = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG);

        loadCharAnimations();
    }

    private void loadCharAnimations() {
        characterAnimations = new CharacterAnimation[3];
        int i = 0;
        characterAnimations[i++] = new CharacterAnimation(PlayerCharacters.PIRATE);
        characterAnimations[i++] = new CharacterAnimation(PlayerCharacters.ORC);
        characterAnimations[i++] = new CharacterAnimation(PlayerCharacters.SOLDIER);

    }

    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
        menuWidth = (int) (backgroundImg.getWidth() * Game.SCALE);
        menuHeight = (int) (backgroundImg.getHeight() * Game.SCALE);
        menuX = Game.GAME_WIDTH / 2 - menuWidth / 2;
        menuY = (int) (25 * Game.SCALE);
    }

    private void loadButtons() {

        playButton = new MenuButton(Game.GAME_WIDTH / 2, (int) (340 * Game.SCALE), 0, Gamestate.PLAYING);

    }

    @Override
    public void update() {
        playButton.update();
        for (CharacterAnimation ca : characterAnimations)
            ca.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImgPink, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        g.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);

        playButton.draw(g);


        //Center
        drawChar(g, playerIndex, menuX + menuWidth / 2, menuY + menuHeight / 2);

        //Left
        drawChar(g, playerIndex - 1, menuX, menuY + menuHeight / 2);

        //Left
        drawChar(g, playerIndex + 1, menuX + menuWidth, menuY + menuHeight / 2);

    }

    @Override
    public void mouseclicked(MouseEvent e) {

    }

    private void drawChar(Graphics g, int playerIndex, int x, int y) {
        if (playerIndex < 0)
            playerIndex = characterAnimations.length - 1;
        else if (playerIndex >= characterAnimations.length)
            playerIndex = 0;
        characterAnimations[playerIndex].draw(g, x, y);
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (isIn(e, playButton))
            playButton.setMousePressed(true);


    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isIn(e, playButton)) {
            if (playButton.isMousePressed()) {

                // 1. Set the character they chose
                game.getPlaying().setPlayerCharacter(characterAnimations[playerIndex].getPc());

                // 🌟 2. THE FIX: Load the saved level before we start!
                game.getPlaying().resumeGame();

                // 3. Set the music for whatever level we just loaded
                game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLevelIndex());

                // 4. Finally, switch the screen to the game!
                playButton.applyGamestate();
            }
        }

        resetButtons();
    }

    private void resetButtons() {
        playButton.resetBools();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        playButton.setMouseOver(false);


        if (isIn(e, playButton))
            playButton.setMouseOver(true);


    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            deltaIndex(1);
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            deltaIndex(-1);
    }

    private void deltaIndex(int i) {
        playerIndex += i;
        if (playerIndex < 0)
            playerIndex = characterAnimations.length - 1;
        else if (playerIndex >= characterAnimations.length)
            playerIndex = 0;
    }


    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    public class CharacterAnimation {
        private final PlayerCharacters pc;
        private int aniTick, aniIndex;
        private final BufferedImage[][] animations;
        private int scale;

        public CharacterAnimation(PlayerCharacters pc) {
            this.pc = pc;
            this.scale = (int) (Game.SCALE + 6);
            animations = LoadSave.loadAnimations(pc);
        }

        public void draw(Graphics g, int drawX, int drawY) {
            g.drawImage(animations[pc.getRowIndex(IDLE)][aniIndex],
                    drawX - pc.spriteW * scale / 2,
                    drawY - pc.spriteH * scale / 2,
                    pc.spriteW * scale,
                    pc.spriteH * scale,
                    null);
        }

        public void update() {
            aniTick++;
            if (aniTick >= ANI_SPEED) {
                aniTick = 0;
                aniIndex++;
                if (aniIndex >= pc.getSpriteAmount(IDLE)) {
                    aniIndex = 0;

                }
            }
        }

        public PlayerCharacters getPc() {
            return pc;
        }
    }
}