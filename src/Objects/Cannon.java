package Objects;

import Main.Game;

public class Cannon extends GameObject{

    private int tileY;
    private int cooldownTick = 0;
    private final int COOLDOWN_MAX = 120; // 120 frames = ~2 seconds of pure rest between shots
    private boolean onCooldown = false;

    public Cannon(int x, int y, int objType) {
        super(x, y, objType);
        tileY=y/ Game.TILES_SIZE;
        initHitbox(40,26);
        hitbox.x-=(int)(4*Game.SCALE);
        hitbox.y+=(int)(6*Game.SCALE);
    }
    public void update(){
        if (onCooldown) {
            cooldownTick++;
            if (cooldownTick >= COOLDOWN_MAX) {
                cooldownTick = 0;
                onCooldown = false; // Cooldown finished! Ready to track player again
            }
            return; // Skip animation steps while resting
        }
        if (doAnimation)
            updateAnimationTick();
    }
    public int getTileY(){
        return tileY;
    }
    public boolean isOnCooldown() { return onCooldown; }
    public void setOnCooldown(boolean onCooldown) { this.onCooldown = onCooldown; }
    public void resetAni() {
        aniTick = 0;
        aniIndex = 0;
    }
}
