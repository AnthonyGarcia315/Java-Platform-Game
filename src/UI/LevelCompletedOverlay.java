package UI;

import GameStates.Gamestate;
import GameStates.Playing;
import Main.Game;
import util.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static util.Constants.UI.URMButtons.*;

public class LevelCompletedOverlay {
    private Playing playing;
    private UrmButton menu, next;
    private BufferedImage img;
    private int bgX,bgY,bgW,bgH;


    public LevelCompletedOverlay(Playing playing){
        this.playing = playing;
        initImg();
        initButtons();
    }

    private void initButtons() {
        int menuX = (int)(330*Game.SCALE);
        int nextX = (int)(445*Game.SCALE);
        int y = (int)(195*Game.SCALE);
        next = new UrmButton(nextX,y,URM_SIZE,URM_SIZE,0);
        menu = new UrmButton(menuX,y,URM_SIZE,URM_SIZE,2);
    }

    private void initImg() {
        img = LoadSave.GetSpriteAtlas(LoadSave.COMPLETED_IMG);
        bgW = (int)(img.getWidth()* Game.SCALE);
        bgH = (int)(img.getHeight()* Game.SCALE);
        bgX = Game.GAME_WIDTH/2 - bgW/2;
        bgY = (int)(75*Game.SCALE);
    }
    public void draw(Graphics g){
        g.drawImage(img,bgX,bgY,bgW,bgH,null);
        next.draw(g);
        menu.draw(g);

    }
    private boolean isIn(UrmButton b, MouseEvent e){
        float scaleX = playing.getGame().getMouseXScale();
        float scaleY = playing.getGame().getMouseYScale();

        int gameMouseX = (int) (e.getX() / scaleX);
        int gameMouseY = (int) (e.getY() / scaleY);

        return b.getBounds().contains(gameMouseX, gameMouseY);
    }
    public void update(){
        menu.update();
        next.update();
    }
    public void mouseMoved(MouseEvent e){
        next.setMouseOver(false);
        menu.setMouseOver(false);
        if(isIn(menu,e))
            menu.setMouseOver(true);
        else if (isIn(next,e)) {
            next.setMouseOver(true);
        }

    }
    public void mouseReleased(MouseEvent e){
        if(isIn(menu,e)) {
            if (menu.isMousePressed()) {
                playing.resetAll();
                playing.setGamestate(Gamestate.MENU);
                System.out.println("MENU");
            }
        }
        else if (isIn(next,e)) {
            if(next.isMousePressed()) {
                playing.loadNextLevel();
                playing.getGame().getAudioPlayer().setLevelSong(playing.getLevelManager().getLevelIndex());
            }
        }
        menu.resetBools();
        next.resetBools();
    }
    public void mousePressed(MouseEvent e){
        if(isIn(menu,e))
            menu.setMousePressed(true);
        else if (isIn(next,e)) {
            next.setMousePressed(true);
        }
    }
}
