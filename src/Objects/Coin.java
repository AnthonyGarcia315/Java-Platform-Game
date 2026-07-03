package Objects;

import Main.Game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Coin extends GameObject {

    private float hoverOffset;
    private int maxHoverOffset, hoverDir = 1;

    public Coin(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = true;

        // Setup a 20x20 hitbox in the center of the tile
        initHitbox(14, 14);
        hitbox.x += (int)(9 * Game.SCALE);
        hitbox.y += (int)(9 * Game.SCALE);

        xDrawOffset = (int) (0 * Game.SCALE);
        yDrawOffset = (int) (0 * Game.SCALE);
        maxHoverOffset = (int) (10 * Game.SCALE);
    }

    public void update() {
        updateAnimationTick();
        updateHover();
    }

    private void updateHover() {
        hoverOffset += (0.075f * Game.SCALE * hoverDir);
        if (hoverOffset >= maxHoverOffset)
            hoverDir = -1;
        else if (hoverOffset < 0)
            hoverDir = 1;

        hitbox.y = y + hoverOffset;
    }

}