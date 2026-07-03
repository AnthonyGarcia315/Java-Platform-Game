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
    private BufferedImage[] coinImgs;
    // 🌟 1. COIN LIST
    private ArrayList<Coin> coins = new ArrayList<>();

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
                e.hurt(200,playing );
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

        // 🌟 2. COIN COLLISION
        for (Coin c : coins) {
            if (c.isActive()) {
                if (hitbox.intersects(c.getHitbox())) {
                    c.setActive(false);
                    playing.getGame().getStatsTracker().addCoins(COIN_VALUE);
                    // playing.getGame().getAudioPlayer().playEffect(audio.AudioPlayer.COIN_PICKUP);
                }
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
            playing.getPlayer().resetAll();
        }
        currentLevel = newLevel;
        potions = new ArrayList<>(newLevel.getPotions());
        containers = new ArrayList<>(newLevel.getContainers());
        projectiles.clear();

        // 🌟 3. LOAD COINS FROM LEVEL (We will add this to Level.java next!)
        coins = new ArrayList<>(newLevel.getCoins());
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
        BufferedImage coinSprite = LoadSave.GetSpriteAtlas(LoadSave.COIN_SPRITE);
        coinImgs = new BufferedImage[10];
        for (int i = 0; i < coinImgs.length; i++) {
            // Cuts the image into 16x16 squares
            coinImgs[i] = coinSprite.getSubimage(i * COIN_WIDTH_DEFAULT, 0, COIN_WIDTH_DEFAULT, COIN_HEIGHT_DEFAULT);
        }
    }

    public void update(int[][] lvlData, Player player) {
        updateBackgroundTrees();
        for (Potion p : potions)
            if (p.isActive())
                p.update();

        for (GameContainer gc : containers)
            if (gc.isActive())
                gc.update();

        // 🌟 4. UPDATE COINS
        for (Coin c : coins)
            if (c.isActive())
                c.update();

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
                    continue;
                } else if (IsProjectileHittingLevel(p, lvlData)) {
                    p.setActive(false);
                }
            }
        }
    }

    private boolean checkProjectileHittingEnemies(Projectile p) {
        entities.EnemyManager enemyManager = playing.getEnemyManager();

        for (entities.Crabby c : currentLevel.getCrabs()) {
            if (c.isActive() && c.getState() != util.Constants.EnemyConstants.DEAD && c.getState() != util.Constants.EnemyConstants.HIT) {
                if (p.getHitbox().intersects(c.getHitbox())) {
                    c.hurt(40,playing);
                    p.setActive(false);
                    return true;
                }
            }
        }

        for (entities.Pinkstar pink : currentLevel.getPinkstars()) {
            if (pink.isActive() && pink.getState() != util.Constants.EnemyConstants.DEAD && pink.getState() != util.Constants.EnemyConstants.HIT) {
                if (p.getHitbox().intersects(pink.getHitbox())) {
                    pink.hurt(40,playing);
                    p.setActive(false);
                    return true;
                }
            }
        }

        for (entities.Shark s : currentLevel.getSharks()) {
            if (s.isActive() && s.getState() != util.Constants.EnemyConstants.DEAD && s.getState() != util.Constants.EnemyConstants.HIT) {
                if (p.getHitbox().intersects(s.getHitbox())) {
                    s.hurt(40,playing);
                    p.setActive(false);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPlayerInRange(Cannon c, Player player) {
        int absValue = (int) Math.abs(player.getHitbox().x - c.getHitbox().x);
        return absValue <= Game.TILES_SIZE * 5;
    }

    private boolean isPlayerInfrontOfCannon(Cannon c, Player player) {
        if (c.getObjType() == CANNON_LEFT) {
            if (c.getHitbox().x > player.getHitbox().x) {
                return true;
            }
        } else if (c.getObjType() == CANNON_RIGHT) {
            if (c.getHitbox().x < player.getHitbox().x) {
                return true;
            }
        }
        return false;
    }

    private void updateCannons(int[][] lvlData, Player player) {
        for (Cannon c : currentLevel.getCannons()) {
            if (!c.doAnimation && !c.isOnCooldown()) {

                // 🌟 THE FIX: Must be on the exact same Y-Tile!
                if (c.getTileY() == player.getTileY()) {

                    if (isPlayerInRange(c, player)) {
                        if (isPlayerInfrontOfCannon(c, player)) {

                            Rectangle2D.Float shiftedHitbox = new Rectangle2D.Float(
                                    c.getHitbox().x, c.getHitbox().y, c.getHitbox().width, c.getHitbox().height
                            );

                            if (c.getObjType() == CANNON_RIGHT) {
                                shiftedHitbox.x += Game.TILES_SIZE;
                            }

                            if (CanCannonSeePlayer(lvlData, player.getHitbox(), shiftedHitbox, c.getTileY()))
                                c.setAnimation(true);
                        }
                    }
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

        // 🌟 5. DRAW COINS
        drawCoins(g, xLvlOffset);

        drawGrass(g, xLvlOffset);
    }

    // 🌟 ADDED DRAW METHOD FOR COINS
    private void drawCoins(Graphics g, int xLvlOffset) {
        for (Coin c : coins) {
            if (c.isActive()) {
                g.drawImage(coinImgs[c.getAniIndex()],
                        (int) (c.getHitbox().x - c.getxDrawOffset() - xLvlOffset),
                        (int) (c.getHitbox().y - c.getyDrawOffset()),
                        COIN_WIDTH, COIN_HEIGHT, null);
            }
        }
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

        // 🌟 6. RESET COINS
        for (Coin c : coins)
            c.reset();
    }
}