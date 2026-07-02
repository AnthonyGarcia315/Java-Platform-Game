package Objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Enemy;
import entities.Player;
import GameStates.Playing;
import Levels.Level;
import Main.Game;
import util.LoadSave;
import static util.Constants.ObjectConstants.*;
import static util.HelpMethods.CanCannonSeePlayer;
import static util.HelpMethods.IsProjectileHittingLevel;
import static util.Constants.Projectiles.*;

public class ObjectManager {

    private Playing playing;
    private BufferedImage[][] potionImgs, containerImgs;
    private BufferedImage[] cannonImgs, grassImgs;
    private BufferedImage[][] treeImgs;
    private BufferedImage spikeImg, cannonBallImg;
    private ArrayList<Potion> potions;
    private ArrayList<GameContainer> containers;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    private Level currentLevel;

    public ObjectManager(Playing playing) {
        this.playing = playing;
        currentLevel = playing.getLevelManager().getCurrentLevel();
        loadImgs();
    }

    public void checkSpikesTouched(Player p) {
        for (Spike s : currentLevel.getSpikes())
            if (s.getHitbox().intersects(p.getHitbox()))
                p.kill();
    }

    public void checkSpikesTouched(Enemy e) {
        for (Spike s : currentLevel.getSpikes())
            if (s.getHitbox().intersects(e.getHitbox()))
                e.hurt(200);
    }

    public void checkObjectTouched(Rectangle2D.Float hitbox) {
        for (Potion p : potions)
            if (p.isActive()) {
                if (hitbox.intersects(p.getHitbox())) {
                    p.setActive(false);
                    playing.getGame().getStatsTracker().recordPotionCollected();
                    applyEffectToPlayer(p);
                }
            }
    }

    public void applyEffectToPlayer(Potion p) {
        if (p.getObjType() == RED_POTION)
            playing.getPlayer().changeHealth(RED_POTION_VALUE);
        else
            playing.getPlayer().changePower(BLUE_POTION_VALUE);
    }

    public void checkObjectHit(Rectangle2D.Float attackbox) {
        for (GameContainer gc : containers)
            if (gc.isActive() && !gc.doAnimation) {
                if (gc.getHitbox().intersects(attackbox)) {
                    gc.setAnimation(true);
                    int type = 0;
                    if (gc.getObjType() == BARREL)
                        type = 1;
                    potions.add(new Potion((int) (gc.getHitbox().x + gc.getHitbox().width / 2), (int) (gc.getHitbox().y - gc.getHitbox().height / 2), type));
                    return;
                }
            }
    }

    public void loadObjects(Level newLevel) {
        if (playing != null && playing.getPlayer() != null) {
            playing.getPlayer().resetAll(); // Or your specific player position reset method
        }
        currentLevel = newLevel;
        potions = new ArrayList<>(newLevel.getPotions());
        containers = new ArrayList<>(newLevel.getContainers());
        projectiles.clear();

    }

    private void loadImgs() {
        BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
        potionImgs = new BufferedImage[2][7];

        for (int j = 0; j < potionImgs.length; j++)
            for (int i = 0; i < potionImgs[j].length; i++)
                potionImgs[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16);

        BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
        containerImgs = new BufferedImage[2][8];

        for (int j = 0; j < containerImgs.length; j++)
            for (int i = 0; i < containerImgs[j].length; i++)
                containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);

        spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);

        cannonImgs = new BufferedImage[7];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);

        for (int i = 0; i < cannonImgs.length; i++)
            cannonImgs[i] = temp.getSubimage(i * 40, 0, 40, 26);

        cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
        treeImgs = new BufferedImage[2][4];
        BufferedImage treeOneImg = LoadSave.GetSpriteAtlas(LoadSave.TREE_ONE_ATLAS);
        for (int i = 0; i < 4; i++)
            treeImgs[0][i] = treeOneImg.getSubimage(i * 39, 0, 39, 92);

        BufferedImage treeTwoImg = LoadSave.GetSpriteAtlas(LoadSave.TREE_TWO_ATLAS);
        for (int i = 0; i < 4; i++)
            treeImgs[1][i] = treeTwoImg.getSubimage(i * 62, 0, 62, 54);

        BufferedImage grassTemp = LoadSave.GetSpriteAtlas(LoadSave.GRASS_ATLAS);
        grassImgs = new BufferedImage[2];
        for (int i = 0; i < grassImgs.length; i++)
            grassImgs[i] = grassTemp.getSubimage(32 * i, 0, 32, 32);
    }

    public void update(int[][] lvlData, Player player) {
        updateBackgroundTrees();
        for (Potion p : potions)
            if (p.isActive())
                p.update();

        for (GameContainer gc : containers)
            if (gc.isActive())
                gc.update();

        updateCannons(lvlData, player);
        updateProjectiles(lvlData, player);

    }

    private void updateBackgroundTrees() {
        for (BackgroundTree bt : currentLevel.getTrees())
            bt.update();
    }

    private void updateProjectiles(int[][] lvlData, Player player) {
        for (Projectile p : projectiles) {
            if (p.isActive()) {
                p.updatePos();

                if (p.getHitbox().intersects(player.getHitbox())) {
                    player.changeHealth(-25);
                    p.setActive(false);
                } else if (checkProjectileHittingEnemies(p)) {
                    // The helper method now handles setting p.setActive(false) and returning true
                    // which stops further checking on this projectile for this frame loop
                    continue;
                } else if (IsProjectileHittingLevel(p, lvlData)) {
                    p.setActive(false);
                }
            }
        }
    }
    private boolean checkProjectileHittingEnemies(Projectile p) {
        entities.EnemyManager enemyManager = playing.getEnemyManager();

        // 1. Check Crabs
        for (entities.Crabby c : currentLevel.getCrabs()) {
            if (c.isActive() && c.getState() != util.Constants.EnemyConstants.DEAD && c.getState() != util.Constants.EnemyConstants.HIT) {
                if (p.getHitbox().intersects(c.getHitbox())) {
                    enemyManager.checkEnemyHit(p.getHitbox(), 40);
                    p.setActive(false); // 🌟 Force kill the projectile instantly
                    return true;        // 🌟 Break out immediately so no other checks happen
                }
            }
        }

        // 2. Check Pinkstars
        for (entities.Pinkstar pink : currentLevel.getPinkstars()) {
            if (pink.isActive() && pink.getState() != util.Constants.EnemyConstants.DEAD && pink.getState() != util.Constants.EnemyConstants.HIT) {
                if (p.getHitbox().intersects(pink.getHitbox())) {
                    enemyManager.checkEnemyHit(p.getHitbox(), 40);
                    p.setActive(false);
                    return true;
                }
            }
        }

        // 3. Check Sharks
        for (entities.Shark s : currentLevel.getSharks()) {
            if (s.isActive() && s.getState() != util.Constants.EnemyConstants.DEAD && s.getState() != util.Constants.EnemyConstants.HIT) {
                if (p.getHitbox().intersects(s.getHitbox())) {
                    enemyManager.checkEnemyHit(p.getHitbox(), 40);
                    p.setActive(false);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isPlayerInRange(Cannon c, Player player) {
        int absValue = (int) Math.abs(player.getHitbox().x - c.getHitbox().x);
        // 🌟 INCREASE RADIUS: Allow tracking across wider hallways/rooms (12 tiles instead of 5)
        return absValue <= Game.TILES_SIZE * 5;
    }

    private boolean isPlayerInfrontOfCannon(Cannon c, Player player) {
        if (c.getObjType() == CANNON_LEFT) {
            if (c.getHitbox().x > player.getHitbox().x) {
                return true;
            }
        } else if (c.getObjType() == CANNON_RIGHT) {
            // Right-facing cannon only shoots if the player's X coordinate
            // is greater than the cannon's X coordinate (player is to the right)
            if (c.getHitbox().x < player.getHitbox().x) {
                return true;
            }
        }
        return false;
    }

    private void updateCannons(int[][] lvlData, Player player) {
        for (Cannon c : currentLevel.getCannons()) {
            if (!c.doAnimation && !c.isOnCooldown()) {
                int yDiff = Math.abs(c.getTileY() - player.getTileY());

                if (yDiff <= 1)
                    if (isPlayerInRange(c, player))
                        if (isPlayerInfrontOfCannon(c, player)) {

                            // 🌟 THE FIX: Offset the starting position based on orientation!
                            // If it's a right-facing cannon, shift the checking hitbox forward by 1 tile
                            // so it clears the solid background wall block safely.
                            Rectangle2D.Float shiftedHitbox = new Rectangle2D.Float(
                                    c.getHitbox().x, c.getHitbox().y, c.getHitbox().width, c.getHitbox().height
                            );

                            if (c.getObjType() == CANNON_RIGHT) {
                                shiftedHitbox.x += Game.TILES_SIZE; // Shift start point 1 tile right!
                            }

                            // Use our safely shifted hitbox for the line-of-sight calculation
                            if (CanCannonSeePlayer(lvlData, player.getHitbox(), shiftedHitbox, c.getTileY()))
                                c.setAnimation(true);
                        }
            }

            c.update();
            if (c.getAniIndex() == 4) {
                shootCannon(c);
                c.setAnimation(false);
                c.resetAni();
                c.setOnCooldown(true);
            }
        }
    }

    private void shootCannon(Cannon c) {
        int dir = 1;
        if (c.getObjType() == CANNON_LEFT)
            dir = -1;

        projectiles.add(new Projectile((int) c.getHitbox().x, (int) c.getHitbox().y, dir));
        playing.getGame().getAudioPlayer().playEffect(audio.AudioPlayer.CANNON_FIRE);
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawPotions(g, xLvlOffset);
        drawContainers(g, xLvlOffset);
        drawTraps(g, xLvlOffset);
        drawCannons(g, xLvlOffset);
        drawProjectiles(g, xLvlOffset);
        drawGrass(g, xLvlOffset);
    }

    private void drawGrass(Graphics g, int xLvlOffset) {
        for (Grass grass : currentLevel.getGrass())
            g.drawImage(grassImgs[grass.getType()], grass.getX() - xLvlOffset, grass.getY(), (int) (32 * Game.SCALE), (int) (32 * Game.SCALE), null);
    }

    public void drawBackgroundTrees(Graphics g, int xLvlOffset) {
        for (BackgroundTree bt : currentLevel.getTrees()) {

            int type = bt.getType();
            if (type == 9)
                type = 8;
            g.drawImage(treeImgs[type - 7][bt.getAniIndex()], bt.getX() - xLvlOffset + GetTreeOffsetX(bt.getType()), (int) (bt.getY() + GetTreeOffsetY(bt.getType())), GetTreeWidth(bt.getType()),
                    GetTreeHeight(bt.getType()), null);
        }
    }

    private void drawProjectiles(Graphics g, int xLvlOffset) {
        // 🌟 FIX: Using a standard indexed loop makes this 100% thread-safe
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if (p.isActive()) {
                g.drawImage(cannonBallImg, (int) (p.getHitbox().x - xLvlOffset), (int) (p.getHitbox().y), CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT, null);
            }
        }
    }

    private void drawCannons(Graphics g, int xLvlOffset) {
        for (Cannon c : currentLevel.getCannons()) {
            int x = (int) (c.getHitbox().x - xLvlOffset);
            int width = CANNON_WIDTH;

            if (c.getObjType() == CANNON_RIGHT) {
                x += width;
                width *= -1;
            }
            g.drawImage(cannonImgs[c.getAniIndex()], x, (int) (c.getHitbox().y), width, CANNON_HEIGHT, null);
        }
    }

    private void drawTraps(Graphics g, int xLvlOffset) {
        for (Spike s : currentLevel.getSpikes())
            g.drawImage(spikeImg, (int) (s.getHitbox().x - xLvlOffset), (int) (s.getHitbox().y - s.getyDrawOffset()), SPIKE_WIDTH, SPIKE_HEIGHT, null);

    }

    private void drawContainers(Graphics g, int xLvlOffset) {
        for (GameContainer gc : containers)
            if (gc.isActive()) {
                int type = 0;
                if (gc.getObjType() == BARREL)
                    type = 1;
                g.drawImage(containerImgs[type][gc.getAniIndex()], (int) (gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset), (int) (gc.getHitbox().y - gc.getyDrawOffset()), CONTAINER_WIDTH,
                        CONTAINER_HEIGHT, null);
            }
    }

    private void drawPotions(Graphics g, int xLvlOffset) {
        for (Potion p : potions)
            if (p.isActive()) {
                int type = 0;
                if (p.getObjType() == RED_POTION)
                    type = 1;
                g.drawImage(potionImgs[type][p.getAniIndex()], (int) (p.getHitbox().x - p.getxDrawOffset() - xLvlOffset), (int) (p.getHitbox().y - p.getyDrawOffset()), POTION_WIDTH, POTION_HEIGHT,
                        null);
            }
    }

    public void resetAllObjects() {
        loadObjects(playing.getLevelManager().getCurrentLevel());
        for (Potion p : potions)
            p.reset();
        for (GameContainer gc : containers)
            gc.reset();
        for (Cannon c : currentLevel.getCannons())
            c.reset();
    }
}