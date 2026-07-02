package util;

import java.awt.geom.Rectangle2D;

import Main.Game;
import Objects.Projectile;

public class HelpMethods {

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        if (!IsSolid(x, y, lvlData))
            if (!IsSolid(x + width, y + height, lvlData))
                if (!IsSolid(x + width, y, lvlData))
                    if (!IsSolid(x, y + height, lvlData))
                        return true;
        return false;
    }

    private static boolean IsSolid(float x, float y, int[][] lvlData) {
        // 🌟 SAFETY: If the lvlData array is null or completely empty, skip check
        if (lvlData == null || lvlData.length == 0 || lvlData[0].length == 0)
            return true;

        int maxWidth = lvlData[0].length * Game.TILES_SIZE;
        if (x < 0 || x >= maxWidth)
            return true;
        if (y < 0 || y >= Game.GAME_HEIGHT)
            return true;

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        return IsTileSolid((int) xIndex, (int) yIndex, lvlData);
    }

    public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData) {
        return IsSolid(p.getHitbox().x + p.getHitbox().width / 2, p.getHitbox().y + p.getHitbox().height / 2, lvlData);
    }

    public static boolean IsEntityInWater(Rectangle2D.Float hitbox, int[][] lvlData) {
        // Will only check if entity touch top water. Can't reach bottom water if not
        // touched top water.
        if (GetTileValue(hitbox.x, hitbox.y + hitbox.height, lvlData) != 48)
            if (GetTileValue(hitbox.x + hitbox.width, hitbox.y + hitbox.height, lvlData) != 48)
                return false;
        return true;
    }

    private static int GetTileValue(float xPos, float yPos, int[][] lvlData) {
        // 🌟 SAFETY: Prevent null pointer/empty map issues
        if (lvlData == null || lvlData.length == 0 || lvlData[0].length == 0)
            return 0;

        int xCord = (int) (xPos / Game.TILES_SIZE);
        int yCord = (int) (yPos / Game.TILES_SIZE);

        // 🌟 BOUNDS DEFENSE: If index is out of bounds due to level swapping thread delay,
        // return a safe fallback default value (0) instead of throwing an Exception
        if (yCord < 0 || yCord >= lvlData.length || xCord < 0 || xCord >= lvlData[yCord].length) {
            return 0;
        }

        return lvlData[yCord][xCord];
    }

    public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
        // 🌟 SAFETY: Secure the array access boundaries
        if (lvlData == null || lvlData.length == 0 || lvlData[0].length == 0)
            return true;

        // 🌟 DEFENSE: Double check the raw integers before looking them up in memory
        if (yTile < 0 || yTile >= lvlData.length || xTile < 0 || xTile >= lvlData[yTile].length) {
            return true;
        }

        int value = lvlData[yTile][xTile];

        switch (value) {
            case 11, 48, 49:
                return false;
            default:
                return true;
        }
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        int currentTile = (int) (hitbox.x / Game.TILES_SIZE);
        if (xSpeed > 0) {
            // Right
            int tileXPos = currentTile * Game.TILES_SIZE;
            int xOffset = (int) (Game.TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1;
        } else
            // Left
            return currentTile * Game.TILES_SIZE;
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
        int currentTile = (int) (hitbox.y / Game.TILES_SIZE);
        if (airSpeed > 0) {
            // Falling - touching floor
            int tileYPos = currentTile * Game.TILES_SIZE;
            int yOffset = (int) (Game.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        } else
            // Jumping
            return currentTile * Game.TILES_SIZE;

    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
            if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
                return false;
        return true;
    }

    public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
        if (xSpeed > 0)
            return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
        else
            return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
    }

    public static boolean IsFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
            if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
                return false;
        return true;
    }

    public static boolean CanCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
        int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

        if (firstXTile > secondXTile)
            return IsAllTilesClear(secondXTile, firstXTile, yTile, lvlData);
        else
            return IsAllTilesClear(firstXTile, secondXTile, yTile, lvlData);
    }

    public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData) {
        for (int i = 0; i < xEnd - xStart; i++)
            if (IsTileSolid(xStart + i, y, lvlData))
                return false;
        return true;
    }

    public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
        if (IsAllTilesClear(xStart, xEnd, y, lvlData))
            for (int i = 0; i < xEnd - xStart; i++) {
                if (!IsTileSolid(xStart + i, y + 1, lvlData))
                    return false;
            }
        return true;
    }

    // Player can sometimes be on an edge and in sight of enemy.
    // The old method would return false because the player x is not on edge.
    // This method checks both player x and player x + width.
    // If tile under playerBox.x is not solid, we switch to playerBox.x +
    // playerBox.width;
    // One of them will be true, because of prior checks.

    public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float enemyBox, Rectangle2D.Float playerBox, int yTile) {
        int firstXTile = (int) (enemyBox.x / Game.TILES_SIZE);

        int secondXTile;
        if (IsSolid(playerBox.x, playerBox.y + playerBox.height + 1, lvlData))
            secondXTile = (int) (playerBox.x / Game.TILES_SIZE);
        else
            secondXTile = (int) ((playerBox.x + playerBox.width) / Game.TILES_SIZE);

        if (firstXTile > secondXTile)
            return IsAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
        else
            return IsAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);
    }

    public static boolean IsSightClear_OLD(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
        int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

        if (firstXTile > secondXTile)
            return IsAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
        else
            return IsAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);
    }
}