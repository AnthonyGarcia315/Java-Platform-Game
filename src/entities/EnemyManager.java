package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import GameStates.Playing;
import Levels.Level;
import effects.DamageNumber;
import util.LoadSave;
import static util.Constants.EnemyConstants.*;

public class EnemyManager {

    private Playing playing;
    private BufferedImage[][] crabbyArr, pinkstarArr, sharkArr;
    private Level currentLevel;
    private java.util.ArrayList<DamageNumber> damageNumbers = new java.util.ArrayList<>();

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
        for (int i = damageNumbers.size() - 1; i >= 0; i--) {
            DamageNumber dn = damageNumbers.get(i);
            dn.update();
            if (!dn.isActive()) {
                damageNumbers.remove(i);
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawCrabs(g, xLvlOffset);
        drawPinkstars(g, xLvlOffset);
        drawSharks(g, xLvlOffset);
        for (DamageNumber dn : damageNumbers) {
            dn.draw(g, xLvlOffset);
        }
    }

    private void drawSharks(Graphics g, int xLvlOffset) {
        for (Shark s : currentLevel.getSharks())
            if (s.isActive()) {
                g.drawImage(sharkArr[s.getState()][s.getAniIndex()], (int) s.getHitbox().x - xLvlOffset - SHARK_DRAWOFFSET_X + s.flipX(),
                        (int) s.getHitbox().y - SHARK_DRAWOFFSET_Y + (int) s.getPushDrawOffset(), SHARK_WIDTH * s.flipW(), SHARK_HEIGHT, null);
//				s.drawHitbox(g, xLvlOffset);
//				s.drawAttackBox(g, xLvlOffset);
                if (s.getState() != DEAD) {
                    drawEnemyHealthBar(g, s, xLvlOffset);
                }
            }
    }

    private void drawPinkstars(Graphics g, int xLvlOffset) {
        for (Pinkstar p : currentLevel.getPinkstars())
            if (p.isActive()) {
                g.drawImage(pinkstarArr[p.getState()][p.getAniIndex()], (int) p.getHitbox().x - xLvlOffset - PINKSTAR_DRAWOFFSET_X + p.flipX(),
                        (int) p.getHitbox().y - PINKSTAR_DRAWOFFSET_Y + (int) p.getPushDrawOffset(), PINKSTAR_WIDTH * p.flipW(), PINKSTAR_HEIGHT, null);
//				p.drawHitbox(g, xLvlOffset);
                if (p.getState() != DEAD) {
                    drawEnemyHealthBar(g, p, xLvlOffset);
                }
            }
    }

    private void drawCrabs(Graphics g, int xLvlOffset) {
        for (Crabby c : currentLevel.getCrabs())
            if (c.isActive()) {

                g.drawImage(crabbyArr[c.getState()][c.getAniIndex()], (int) c.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X + c.flipX(),
                        (int) c.getHitbox().y - CRABBY_DRAWOFFSET_Y + (int) c.getPushDrawOffset(), CRABBY_WIDTH * c.flipW(), CRABBY_HEIGHT, null);

//				c.drawHitbox(g, xLvlOffset);
//				c.drawAttackBox(g, xLvlOffset);
                if (c.getState() != DEAD) {
                    drawEnemyHealthBar(g, c, xLvlOffset);
                }
            }

    }

    public void checkEnemyHit(Rectangle2D.Float attackBox,int damageAmount) {
        for (Crabby c : currentLevel.getCrabs())
            if (c.isActive())
                if (c.getState() != DEAD && c.getState() != HIT)
                    if (attackBox.intersects(c.getHitbox())) {
                        c.hurt(damageAmount);
                        if (c.getCurrentHealth() <= 0) {
                            playing.getGame().getStatsTracker().recordEnemyDefeated();
                        }
                        damageNumbers.add(new DamageNumber(c.getHitbox().x + (c.getHitbox().width / 2), c.getHitbox().y, damageAmount));
                        return;
                    }

        for (Pinkstar p : currentLevel.getPinkstars())
            if (p.isActive()) {
                if (p.getState() == ATTACK && p.getAniIndex() >= 3)
                    return;
                else {
                    if (p.getState() != DEAD && p.getState() != HIT)
                        if (attackBox.intersects(p.getHitbox())) {
                            p.hurt(damageAmount);
                            if (p.getCurrentHealth() <= 0) {
                                playing.getGame().getStatsTracker().recordEnemyDefeated();
                            }
                            damageNumbers.add(new DamageNumber(p.getHitbox().x + (p.getHitbox().width / 2), p.getHitbox().y, damageAmount));
                            return;
                        }
                }
            }

        for (Shark s : currentLevel.getSharks())
            if (s.isActive()) {
                if (s.getState() != DEAD && s.getState() != HIT)
                    if (attackBox.intersects(s.getHitbox())) {
                        s.hurt(damageAmount);
                        if (s.getCurrentHealth() <= 0) {
                            playing.getGame().getStatsTracker().recordEnemyDefeated();
                        }
                        damageNumbers.add(new DamageNumber(s.getHitbox().x + (s.getHitbox().width / 2), s.getHitbox().y, damageAmount));
                        return;
                    }
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
        damageNumbers.clear(); // 🌟 Clear old popups on restart
        for (Crabby c : currentLevel.getCrabs())
            c.resetEnemy();
        for (Pinkstar p : currentLevel.getPinkstars())
            p.resetEnemy();
        for (Shark s : currentLevel.getSharks())
            s.resetEnemy();
    }
    private void drawEnemyHealthBar(Graphics g, Enemy e, int xLvlOffset) {
        // 1. Define the dimensions of our tiny health bar overlay
        int barWidth = (int) (30 * Main.Game.SCALE);
        int barHeight = (int) (3 * Main.Game.SCALE);

        // 2. Position it centered right above the enemy's bounding box
        int xPos = (int) (e.getHitbox().x - xLvlOffset + (e.getHitbox().width / 2) - (barWidth / 2));
        int yPos = (int) (e.getHitbox().y - (8 * Main.Game.SCALE)); // 8 pixels above head

        // 3. Calculate remaining health width percentage
        float healthRatio = (float) e.getCurrentHealth() / e.getMaxHealth();
        int currentHealthWidth = (int) (barWidth * healthRatio);

        // 4. Draw Background/Missing Health (Dark Red/Black)
        g.setColor(java.awt.Color.BLACK);
        g.fillRect(xPos, yPos, barWidth, barHeight);

        // 5. Draw Current Health (Bright Crimson Red or Green)
        g.setColor(java.awt.Color.RED);
        g.fillRect(xPos, yPos, currentHealthWidth, barHeight);

        // 6. Optional: Draw a thin black border layout outline around it for visibility
        g.setColor(new java.awt.Color(50, 50, 50));
        g.drawRect(xPos, yPos, barWidth, barHeight);
    }

}