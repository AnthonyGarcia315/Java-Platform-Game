package entities;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import GameStates.Playing;
import Levels.Level;
import util.LoadSave;
import static util.Constants.EnemyConstants.*;

public class EnemyManager {

    private Playing playing;
    private BufferedImage[][] crabbyArr, pinkstarArr, sharkArr;
    private Level currentLevel;

    public EnemyManager(Playing playing) {
        this.playing = playing;
        loadEnemyImgs();
    }

    public void loadEnemies(Level level) {
        this.currentLevel = level;
    }

    public void update(int[][] lvlData) {
        boolean isAnyActive = false;
        for (Crabby c : currentLevel.getCrabs())
            if (c.isActive()) {
                c.update(lvlData, playing);
                isAnyActive = true;
            }

        for (Pinkstar p : currentLevel.getPinkstars())
            if (p.isActive()) {
                p.update(lvlData, playing);
                isAnyActive = true;
            }

        for (Shark s : currentLevel.getSharks())
            if (s.isActive()) {
                s.update(lvlData, playing);
                isAnyActive = true;
            }

        if (!isAnyActive)
            playing.setLevelCompleted(true);
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawCrabs(g, xLvlOffset);
        drawPinkstars(g, xLvlOffset);
        drawSharks(g, xLvlOffset);
    }

    private void drawSharks(Graphics g, int xLvlOffset) {
        for (Shark s : currentLevel.getSharks()) {
            if (s.isActive()) {
                g.drawImage(sharkArr[s.getState()][s.getAniIndex()],
                        (int) s.getHitbox().x - xLvlOffset - SHARK_DRAWOFFSET_X + s.flipX(),
                        (int) s.getHitbox().y - SHARK_DRAWOFFSET_Y + (int) s.getPushDrawOffset(),
                        SHARK_WIDTH * s.flipW(), SHARK_HEIGHT, null);

                // 🌟 DRAW HEALTH BAR 🌟
                if (s.getState() != DEAD) {
                    g.setColor(Color.RED);
                    int healthWidth = (int)((s.getCurrentHealth() / (float)s.getMaxHealth()) * 30);
                    g.fillRect((int) s.getHitbox().x - xLvlOffset, (int) s.getHitbox().y - 10, healthWidth, 4);
                    g.setColor(Color.BLACK);
                    g.drawRect((int) s.getHitbox().x - xLvlOffset, (int) s.getHitbox().y - 10, 30, 4);
                }
            }
        }
    }

    private void drawPinkstars(Graphics g, int xLvlOffset) {
        for (Pinkstar p : currentLevel.getPinkstars()) {
            if (p.isActive()) {
                g.drawImage(pinkstarArr[p.getState()][p.getAniIndex()],
                        (int) p.getHitbox().x - xLvlOffset - PINKSTAR_DRAWOFFSET_X + p.flipX(),
                        (int) p.getHitbox().y - PINKSTAR_DRAWOFFSET_Y + (int) p.getPushDrawOffset(),
                        PINKSTAR_WIDTH * p.flipW(), PINKSTAR_HEIGHT, null);

                // 🌟 DRAW HEALTH BAR 🌟
                if (p.getState() != DEAD) {
                    g.setColor(Color.RED);
                    int healthWidth = (int)((p.getCurrentHealth() / (float)p.getMaxHealth()) * 30);
                    g.fillRect((int) p.getHitbox().x - xLvlOffset, (int) p.getHitbox().y - 10, healthWidth, 4);
                    g.setColor(Color.BLACK);
                    g.drawRect((int) p.getHitbox().x - xLvlOffset, (int) p.getHitbox().y - 10, 30, 4);
                }
            }
        }
    }

    private void drawCrabs(Graphics g, int xLvlOffset) {
        for (Crabby c : currentLevel.getCrabs()) {
            if (c.isActive()) {
                // Draw the Crab
                g.drawImage(crabbyArr[c.getState()][c.getAniIndex()],
                        (int) c.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X + c.flipX(),
                        (int) c.getHitbox().y - CRABBY_DRAWOFFSET_Y + (int) c.getPushDrawOffset(),
                        CRABBY_WIDTH * c.flipW(), CRABBY_HEIGHT, null);

                // 🌟 DRAW HEALTH BAR 🌟
                if (c.getState() != DEAD) {
                    g.setColor(Color.RED);
                    int healthWidth = (int)((c.getCurrentHealth() / (float)c.getMaxHealth()) * 30); // 30 is max bar width
                    g.fillRect((int) c.getHitbox().x - xLvlOffset, (int) c.getHitbox().y - 10, healthWidth, 4);
                    g.setColor(Color.BLACK);
                    g.drawRect((int) c.getHitbox().x - xLvlOffset, (int) c.getHitbox().y - 10, 30, 4);
                }
            }
        }
    }

    public boolean checkEnemyHit(Rectangle2D.Float attackBox, int damageAmount) {
        // --- ADD THIS CHECK ---
        boolean fireBladeUnlocked = playing.getStatsTracker().hasFireBlade();

        for (Crabby c : currentLevel.getCrabs())
            if (c.isActive() && c.getState() != DEAD && c.getState() != HIT)
                if (attackBox.intersects(c.getHitbox())) {
                    performPlayerAttack(c,damageAmount);

                    // 🌟 TRIGGER FIRE
                    if (fireBladeUnlocked) c.setOnFire();

                    if (c.getCurrentHealth() <= 0) {
                        playing.getStatsTracker().recordEnemyDefeated();
                    }
                    return true;
                }

        for (Pinkstar p : currentLevel.getPinkstars())
            if (p.isActive() && p.getState() != DEAD && p.getState() != HIT)
                if (!(p.getState() == ATTACK && p.getAniIndex() >= 3))
                    if (attackBox.intersects(p.getHitbox())) {
                        performPlayerAttack(p,damageAmount);

                        // 🌟 TRIGGER FIRE
                        if (fireBladeUnlocked) p.setOnFire();

                        if (p.getCurrentHealth() <= 0) {
                            playing.getStatsTracker().recordEnemyDefeated();
                        }
                        return true;
                    }

        for (Shark s : currentLevel.getSharks())
            if (s.isActive() && s.getState() != DEAD && s.getState() != HIT)
                if (attackBox.intersects(s.getHitbox())) {
                    performPlayerAttack(s,damageAmount);

                    // 🌟 TRIGGER FIRE
                    if (fireBladeUnlocked) s.setOnFire();

                    if (s.getCurrentHealth() <= 0) {
                        playing.getStatsTracker().recordEnemyDefeated();
                    }
                    return true;
                }

        return false;
    }
    // Add this new method in EnemyManager.java
// Inside EnemyManager.java
    public void performPlayerAttack(Enemy e, int damageAmount) {
        // Pass 'playing' to the new hurt method
        e.hurt(damageAmount, playing);

        // Trigger fire ONLY for player attacks
        if (playing.getStatsTracker().hasFireBlade()) {
            e.setOnFire();
        }
    }

    private void loadEnemyImgs() {
        crabbyArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE), 9, 5, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
        pinkstarArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.PINKSTAR_ATLAS), 8, 5, PINKSTAR_WIDTH_DEFAULT, PINKSTAR_HEIGHT_DEFAULT);
        sharkArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.SHARK_ATLAS), 8, 5, SHARK_WIDTH_DEFAULT, SHARK_HEIGHT_DEFAULT);
    }

    private BufferedImage[][] getImgArr(BufferedImage atlas, int xSize, int ySize, int spriteW, int spriteH) {
        BufferedImage[][] tempArr = new BufferedImage[ySize][xSize];
        for (int j = 0; j < tempArr.length; j++)
            for (int i = 0; i < tempArr[j].length; i++)
                tempArr[j][i] = atlas.getSubimage(i * spriteW, j * spriteH, spriteW, spriteH);
        return tempArr;
    }

    public void resetAllEnemies() {
        for (Crabby c : currentLevel.getCrabs())
            c.resetEnemy();
        for (Pinkstar p : currentLevel.getPinkstars())
            p.resetEnemy();
        for (Shark s : currentLevel.getSharks())
            s.resetEnemy();
    }

}