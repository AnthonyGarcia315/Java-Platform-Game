package GameStates;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.ArrayList;

import Main.GameWindow;
import entities.EnemyManager;
import entities.Player;
import entities.PlayerCharacters;
import Levels.LevelManager;
import Main.Game;
import Objects.ObjectManager;
import UI.GameCompletedOverlay;
import UI.GameOverOverlay;
import UI.LevelCompletedOverlay;
import UI.PauseOverlay;
import util.LoadSave;
import effects.DialogueEffect;
import effects.Rain;

import static util.Constants.Environment.*;
import static util.Constants.Dialogue.*;

public class Playing extends State implements Statemethods {

    // --- ENTITIES & MANAGERS ---
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;
    private Rain rain;

    // --- UI & OVERLAYS ---
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private GameCompletedOverlay gameCompletedOverlay;
    private LevelCompletedOverlay levelCompletedOverlay;
    private ArrayList<DamageText> damageTexts = new ArrayList<>();
    private ArrayList<DialogueEffect> dialogEffects = new ArrayList<>();

    // --- STATE BOOLEANS ---
    private boolean paused = false;
    private boolean gameOver = false;
    private boolean lvlCompleted = false;
    private boolean gameCompleted = false;
    private boolean playerDying = false;
    private boolean drawRain = false;
    private boolean drawShip = true;

    // --- CAMERA / ENVIRONMENT ---
    private int xLvlOffset;
    private int leftBorder = (int) (0.25 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.75 * Game.GAME_WIDTH);
    private int maxLvlOffsetX;
    private int[] smallCloudsPos;
    private Random rnd = new Random();

    // --- SPRITES ---
    private BufferedImage backgroundImg, bigCloud, smallCloud;
    private BufferedImage[] shipImgs, questionImgs, exclamationImgs;

    // --- SHIP ANIMATION SETTINGS ---
    private int shipAni, shipTick, shipDir = 1;
    private float shipHeightDelta, shipHeightChange = 0.05f * Game.SCALE;

    // --- CONSTRUCTOR ---
    public Playing(Game game) {
        super(game);
        System.out.println("DEBUG: Playing created. Tracker Hash: " + game.getStatsTracker().hashCode());

        initClasses();
        loadEnvironmentImages();
        loadDialogue();
        calcLvlOffset();
        setDrawRainBoolean();
    }

    // --- INITIALIZATION ---
    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        objectManager = new ObjectManager(this);

        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverlay(this);
        gameCompletedOverlay = new GameCompletedOverlay(this);

        rain = new Rain();
    }

    private void loadEnvironmentImages() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);

        smallCloudsPos = new int[8];
        for (int i = 0; i < smallCloudsPos.length; i++) {
            smallCloudsPos[i] = (int) (90 * Game.SCALE) + rnd.nextInt((int) (100 * Game.SCALE));
        }

        shipImgs = new BufferedImage[4];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SHIP);
        for (int i = 0; i < shipImgs.length; i++) {
            shipImgs[i] = temp.getSubimage(i * 78, 0, 78, 72);
        }
    }

    private void loadDialogue() {
        BufferedImage qtemp = LoadSave.GetSpriteAtlas(LoadSave.QUESTION_ATLAS);
        questionImgs = new BufferedImage[5];
        for (int i = 0; i < questionImgs.length; i++)
            questionImgs[i] = qtemp.getSubimage(i * 14, 0, 14, 12);

        BufferedImage etemp = LoadSave.GetSpriteAtlas(LoadSave.EXCLAMATION_ATLAS);
        exclamationImgs = new BufferedImage[5];
        for (int i = 0; i < exclamationImgs.length; i++)
            exclamationImgs[i] = etemp.getSubimage(i * 14, 0, 14, 12);

        for (int i = 0; i < 10; i++)
            dialogEffects.add(new DialogueEffect(0, 0, EXCLAMATION));
        for (int i = 0; i < 10; i++)
            dialogEffects.add(new DialogueEffect(0, 0, QUESTION));

        for (DialogueEffect de : dialogEffects)
            de.deactive();
    }

    // --- CORE LOOP (UPDATE & DRAW) ---
    @Override
    public void update() {
        if (paused) {
            pauseOverlay.update();
        } else if (lvlCompleted) {
            levelCompletedOverlay.update();
        } else if (gameCompleted) {
            gameCompletedOverlay.update();
        } else if (gameOver) {
            gameOverOverlay.update();
        } else if (playerDying) {
            player.update();
        } else {
            updateDialogue();
            if (drawRain) rain.update(xLvlOffset);

            levelManager.update();
            objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLevelData());

            updateDamageText();
            checkCloseToBorder();

            if (drawShip) updateShipAni();
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        drawClouds(g);

        if (drawRain) rain.draw(g, xLvlOffset);

        if (drawShip) {
            g.drawImage(shipImgs[shipAni], (int) (100 * Game.SCALE) - xLvlOffset,
                    (int) ((288 * Game.SCALE) + shipHeightDelta),
                    (int) (78 * Game.SCALE), (int) (72 * Game.SCALE), null);
        }

        levelManager.draw(g, xLvlOffset);
        objectManager.draw(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);

        drawDamageText(g);

        player.render(g, xLvlOffset);
        objectManager.drawBackgroundTrees(g, xLvlOffset);
        drawDialogue(g, xLvlOffset);

        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver) {
            gameOverOverlay.draw(g);
        } else if (lvlCompleted) {
            levelCompletedOverlay.draw(g);
        } else if (gameCompleted) {
            gameCompletedOverlay.draw(g);
        }
    }

    // --- LEVEL / STATE MANAGEMENT ---
    public void loadNextLevel() {
        levelManager.setLevelIndex(levelManager.getLevelIndex() + 1);
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        calcLvlOffset();
        resetAll();
        drawShip = false;
    }

    public void resumeGame() {
        int savedLevel = game.getStatsTracker().getHighestLevel();
        if (savedLevel >= levelManager.getAmountOfLevels()) {
            savedLevel = 0;
            game.getStatsTracker().resetlvl(); // Reset tracker back to 0
        }
        levelManager.setLevelIndex(savedLevel);

        Levels.Level currentLvl = levelManager.getCurrentLevel();
        enemyManager.loadEnemies(currentLvl);
        objectManager.loadObjects(currentLvl);
        player.loadLvlData(currentLvl.getLevelData());
        player.setSpawn(currentLvl.getPlayerSpawn());

        calcLvlOffset();
        drawShip = (savedLevel == 0);
        resetAll();
    }

    public void resetAll() {
        game.getStatsTracker().revertCoins();
        if(game.getPlaying().getLevelManager().getLevelIndex()>4){
            levelManager.setLevelIndex(0);
            game.getStatsTracker().resetlvl();
        }
        //levelManager.loadNextLevel();
        xLvlOffset = 0;
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        playerDying = false;
        drawRain = false;

        setDrawRainBoolean();
        player.resetAll();
        enemyManager.resetAllEnemies();
        objectManager.resetAllObjects();
        dialogEffects.clear();
    }

    // --- LOGIC UPDATES & DRAW HELPERS ---
    private void updateShipAni() {
        shipTick++;
        if (shipTick >= 35) {
            shipTick = 0;
            shipAni++;
            if (shipAni >= 4) shipAni = 0;
        }

        shipHeightDelta += shipHeightChange * shipDir;
        shipHeightDelta = Math.max(Math.min(10 * Game.SCALE, shipHeightDelta), 0);

        if (shipHeightDelta == 0) shipDir = 1;
        else if (shipHeightDelta == 10 * Game.SCALE) shipDir = -1;
    }

    private void updateDialogue() {
        for (DialogueEffect de : dialogEffects)
            if (de.isActive()) de.update();
    }

    private void updateDamageText() {
        for (int i = 0; i < damageTexts.size(); i++) {
            DamageText dt = damageTexts.get(i);
            dt.y -= 1f;
            dt.life--;
            if (dt.life <= 0) {
                damageTexts.remove(i);
                i--;
            }
        }
    }

    private void drawDialogue(Graphics g, int xLvlOffset) {
        for (DialogueEffect de : dialogEffects) {
            if (de.isActive()) {
                if (de.getType() == QUESTION)
                    g.drawImage(questionImgs[de.getAniIndex()], de.getX() - xLvlOffset, de.getY(), DIALOGUE_WIDTH, DIALOGUE_HEIGHT, null);
                else
                    g.drawImage(exclamationImgs[de.getAniIndex()], de.getX() - xLvlOffset, de.getY(), DIALOGUE_WIDTH, DIALOGUE_HEIGHT, null);
            }
        }
    }

    private void drawDamageText(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, (int) (12 * Game.SCALE)));
        for (DamageText dt : damageTexts) {
            g.setColor(Color.BLACK);
            g.drawString(dt.text, (int) dt.x - xLvlOffset + 1, (int) dt.y + 1);
            g.setColor(dt.color);
            g.drawString(dt.text, (int) dt.x - xLvlOffset, (int) dt.y);
        }
    }

    private void drawClouds(Graphics g) {
        for (int i = 0; i < 4; i++)
            g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int) (xLvlOffset * 0.3), (int) (204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);

        for (int i = 0; i < smallCloudsPos.length; i++)
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int) (xLvlOffset * 0.7), smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder) xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder) xLvlOffset += diff - leftBorder;

        xLvlOffset = Math.max(Math.min(xLvlOffset, maxLvlOffsetX), 0);
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
    }

    private void setDrawRainBoolean() {
        if (rnd.nextFloat() >= 0.8f) drawRain = true;
    }

    // --- GAMEPLAY TRIGGERS ---
    public void addDamageText(int x, int y, int amount, Color color) {
        damageTexts.add(new DamageText(x, y, "-" + amount, color));
    }

    public void addDialogue(int x, int y, int type) {
        dialogEffects.add(new DialogueEffect(x, y - (int) (Game.SCALE * 15), type));
        for (DialogueEffect de : dialogEffects) {
            if (!de.isActive() && de.getType() == type) {
                de.reset(x, -(int) (Game.SCALE * 15));
                return;
            }
        }
    }

    public void checkObjectHit(Rectangle2D.Float attackBox) { objectManager.checkObjectHit(attackBox); }
    public void checkPotionTouched(Rectangle2D.Float hitbox) { objectManager.checkObjectTouched(hitbox); }
    public void checkSpikesTouched(Player p) { objectManager.checkSpikesTouched(p); }
    public void checkEnemyHit(Rectangle2D.Float attackBox, int damageAmount) { enemyManager.checkEnemyHit(attackBox, damageAmount); }

    // --- INPUT HANDLING ---
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            paused = !paused;
            return;
        } else if (e.getKeyCode() == KeyEvent.VK_F1) {
            game.getStatsTracker().addCoins(100);
            System.out.println("DEBUG: Added 100 coins!");
        }

        if (gameOver) {
            gameOverOverlay.keyPressed(e);
        } else if (gameCompleted) {
            gameCompletedOverlay.keyPressed(e);
        } else if (!lvlCompleted && !paused) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A -> player.setLeft(true);
                case KeyEvent.VK_D -> player.setRight(true);
                case KeyEvent.VK_SPACE -> player.setJump(true);
                case KeyEvent.VK_ESCAPE -> {
                    if (game.getGameWindow().getCurrentMode() == Main.GameWindow.WindowMode.EXCLUSIVE_FULLSCREEN) {
                        game.getGameWindow().setWindowMode(Main.GameWindow.WindowMode.WINDOWED);
                    } else {
                        Gamestate.state = Gamestate.MENU;
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver && !gameCompleted && !lvlCompleted)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A -> player.setLeft(false);
                case KeyEvent.VK_D -> player.setRight(false);
                case KeyEvent.VK_SPACE -> player.setJump(false);
            }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        game.getGameWindow().getGamePanel().requestFocusInWindow();
        if (gameOver) gameOverOverlay.mousePressed(e);
        else if (paused) pauseOverlay.mousePressed(e);
        else if (lvlCompleted) levelCompletedOverlay.mousePressed(e);
        else if (gameCompleted) gameCompletedOverlay.mousePressed(e);
        else {
            if (e.getButton() == MouseEvent.BUTTON1) player.setAttacking(true);
            else if (e.getButton() == MouseEvent.BUTTON3) player.powerAttack();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gameOver) gameOverOverlay.mouseReleased(e);
        else if (paused) pauseOverlay.mouseReleased(e);
        else if (lvlCompleted) levelCompletedOverlay.mouseReleased(e);
        else if (gameCompleted) gameCompletedOverlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameOver) gameOverOverlay.mouseMoved(e);
        else if (paused) pauseOverlay.mouseMoved(e);
        else if (lvlCompleted) levelCompletedOverlay.mouseMoved(e);
        else if (gameCompleted) gameCompletedOverlay.mouseMoved(e);
    }

    public void mouseDragged(MouseEvent e) {
        if (!gameOver && !gameCompleted && !lvlCompleted && paused)
            pauseOverlay.mouseDragged(e);
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseclicked(MouseEvent e) {}

    // --- GETTERS & SETTERS ---
    public void setPlayerCharacter(PlayerCharacters pc) {
        player = new Player(pc, this);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }

    public void setLevelCompleted(boolean levelCompleted) {
        game.getAudioPlayer().lvlCompleted();
        game.getStatsTracker().lockInCoins();

        int nextLevelIndex = levelManager.getLevelIndex() + 1;
        if (nextLevelIndex >= levelManager.getAmountOfLevels()) {
            gameCompleted = true;
            game.getStatsTracker().exportData();

            // Reset everything for a new game
            game.getStatsTracker().resetlvl();
            player.resetAll();
            levelManager.setLevelIndex(0);
            levelManager.loadNextLevel();
            resetAll();
            return;
        }
        game.getStatsTracker().unlockNextLevel(nextLevelIndex);

        if (nextLevelIndex >= levelManager.getAmountOfLevels()) {
            gameCompleted = true;
            game.getStatsTracker().exportData();
            player.resetAll();
            levelManager.setLevelIndex(0);
            levelManager.loadNextLevel();
            resetAll();
            return;
        }
        this.lvlCompleted = levelCompleted;
    }

    public void setGameCompleted() { this.gameCompleted = true; }
    public void resetGameCompleted() { this.gameCompleted = false; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public void setMaxLvlOffset(int lvlOffset) { this.maxLvlOffsetX = lvlOffset; }
    public void unpauseGame() { paused = false; }
    public void windowFocusLost() { player.resetDirBooleans(); }
    public void setPlayerDying(boolean playerDying) { this.playerDying = playerDying; }

    public Player getPlayer() { return player; }
    public EnemyManager getEnemyManager() { return enemyManager; }
    public ObjectManager getObjectManager() { return objectManager; }
    public LevelManager getLevelManager() { return levelManager; }
    public util.StatsTracker getStatsTracker() { return game.getStatsTracker(); }

    // --- INNER CLASSES ---
    private class DamageText {
        float x, y;
        String text;
        Color color;
        int life = 60;

        public DamageText(float x, float y, String text, Color color) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.color = color;
        }
    }
}