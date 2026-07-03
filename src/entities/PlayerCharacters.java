package entities;
import Main.Game;
import util.LoadSave;

import static util.Constants.*;
public enum PlayerCharacters {
    PIRATE(5,6,3,1,3,4,8,0,1,2,3,4,5,6,
            LoadSave.PLAYER_PIRATE,7,8,64,40,20,20,21,4),

    ORC(6,8,4,4,6,4,4,0,1,3,4,2,5,6,
            LoadSave.PLAYER_ORC,6,8,100,100,13,15,44,42),
    SOLDIER(6,8,4,4,6,4,4,0,1,3,4,2,5,6,
            LoadSave.PLAYER_SOLDIER,7,8,100,100,12,18,44,39);
    public static final int IDLE = 0;
    public static final int RUNNING = 1;
    public static final int JUMP = 2;
    public static final int FALLING = 3;
    public static final int ATTACK = 4;
    public static final int HIT = 5;
    public static final int DEAD = 6;
    int spriteA_IDLE,spriteA_RUNNING, spriteA_JUMP, spriteA_FALLING, SpriteA_ATTACK, SpriteA_HIT, SpriteA_DEAD;
    int rowIDLE,rowRUNNING,rowJUMP,rowFALLING,rowATTACK,rowHIT,rowDEAD;
    String playerAtlas;
    int rowA,colA,spriteW,spriteH;
    int hitboxW,hitboxH;
    int drawXoffset,drawYoffset;
    PlayerCharacters(int spriteA_IDLE,int spriteA_RUNNING, int spriteA_JUMP, int spriteA_FALLING, int SpriteA_ATTACK,int SpriteA_HIT,int SpriteA_DEAD,
                     int rowIDLE,int rowRUNNING,int rowJUMP, int rowFALLING, int rowATTACK,int rowHIT,int rowDEAD,
                     String playerAtlas, int rowA,int colA,int spriteW,int spriteH,
                     int hitboxW,int hitboxH,int drawXoffset,int drawYoffset) {
        this.spriteA_IDLE = spriteA_IDLE;
        this.spriteA_RUNNING = spriteA_RUNNING;
        this.spriteA_JUMP = spriteA_JUMP;
        this.spriteA_FALLING = spriteA_FALLING;
        this.SpriteA_ATTACK=SpriteA_ATTACK;
        this.SpriteA_HIT=SpriteA_HIT;
        this.SpriteA_DEAD=SpriteA_DEAD;

        this.rowIDLE=rowIDLE;
        this.rowRUNNING=rowRUNNING;
        this.rowJUMP=rowJUMP;
        this.rowFALLING=rowFALLING;
        this.rowATTACK=rowATTACK;
        this.rowHIT=rowHIT;
        this.rowDEAD=rowDEAD;

        this.playerAtlas = playerAtlas;
        this.rowA=rowA;
        this.colA=colA;
        this.spriteW=spriteW;
        this.spriteH=spriteH;

        this.hitboxW=hitboxW;
        this.hitboxH=hitboxH;

        this.drawXoffset= (int)(drawXoffset* Game.SCALE);
        this.drawYoffset= (int) (drawYoffset*Game.SCALE);
    }
    public int GetSpriteAmount(int player_action) {
            switch (player_action) {
                case DEAD:
                    return SpriteA_DEAD;
                case RUNNING:
                    return spriteA_RUNNING;
                case IDLE:
                    return spriteA_IDLE;
                case HIT:
                    return SpriteA_HIT;
                case JUMP:
                    return spriteA_JUMP;
                case ATTACK:
                    return SpriteA_ATTACK;
                case FALLING:
                    return spriteA_FALLING;
                default:
                    return 1;
            }
        }
        public int getRowIndex(int player_action) {
            return switch (player_action) {
                case IDLE->rowIDLE;
                case RUNNING -> rowRUNNING;
                case HIT -> rowHIT;
                case JUMP -> rowJUMP;
                case FALLING -> rowFALLING;
                case DEAD -> rowDEAD;
                case ATTACK -> rowATTACK;
                default -> 0;
            };
        }

    public String getPlayerAtlas() {
        return playerAtlas;
    }

    public int getRowA() {
        return rowA;
    }

    public int getSpriteW() {
        return spriteW;
    }

    public int getSpriteH() {
        return spriteH;
    }

    public int getColA() {
        return colA;
    }
}