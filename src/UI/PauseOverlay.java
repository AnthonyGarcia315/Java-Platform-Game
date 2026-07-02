package UI;

import GameStates.Gamestate;
import GameStates.Playing;
import Main.Game;
import util.LoadSave;
import static util.Constants.UI.PauseButtons.*;
import static util.Constants.UI.URMButtons.*;
import static util.Constants.UI.VolumeButtons.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PauseOverlay {
    private BufferedImage backgroundImg;
    private int x,y,width,height;
    private UrmButton menuB,replayB,unpauseB;
    private Playing playing;
    private AudioOptions audioOptions;
    public PauseOverlay(Playing playing){
        this.playing= playing;
        loadBackGround();
        audioOptions = playing.getGame().getAudioOptions();
        createUrmButtons();
    }



    private void createUrmButtons() {
        int menuX = (int)(313*Game.SCALE);
        int replayX = (int)(387*Game.SCALE);
        int unpauseX = (int)(462*Game.SCALE);
        int bY=(int)(325*Game.SCALE);

        menuB = new UrmButton(menuX,bY,URM_SIZE,URM_SIZE,2);
        replayB = new UrmButton(replayX,bY,URM_SIZE,URM_SIZE,1);
        unpauseB = new UrmButton(unpauseX,bY,URM_SIZE,URM_SIZE,0);
    }



    private void loadBackGround() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        width = (int)(backgroundImg.getWidth()* Game.SCALE);
        height = (int)(backgroundImg.getHeight()* Game.SCALE);
        x = Game.GAME_WIDTH/2 - width/2;
        y=(int)(25*Game.SCALE);
    }

    public void update(){

        menuB.update();
        replayB.update();
        unpauseB.update();
        audioOptions.update();

    }
    public void draw(Graphics g){
        g.drawImage(backgroundImg,x,y,width,height,null);

                //URM Buttons
        menuB.draw(g);
        replayB.draw(g);
        unpauseB.draw(g);
        audioOptions.draw(g);


    }
    public void mouseDragged(MouseEvent e){
        audioOptions.mouseDragged(e);
    }


    public void mouseMoved(MouseEvent e){

        menuB.setMouseOver(false);
        replayB.setMouseOver(false);
        unpauseB.setMouseOver(false);
        if (isIn(e, menuB)) {
            menuB.setMouseOver(true);
        }else if (isIn(e,replayB)){
            replayB.setMouseOver(true);
        } else if (isIn(e,unpauseB)) {
            unpauseB.setMouseOver(true);
        } else
            audioOptions.mouseMoved(e);

    }

    public void mouseclicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        if(isIn(e,menuB))
            menuB.setMousePressed(true);
        else if (isIn(e,replayB))
            replayB.setMousePressed(true);
        else if (isIn(e,unpauseB))
            unpauseB.setMousePressed(true);
        else
            audioOptions.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
         if (isIn(e, menuB)){
            if (menuB.isMousePressed()) {
                playing.resetAll();
                playing.setGamestate(Gamestate.MENU);
                playing.unpauseGame();
            }
        } else if (isIn(e, replayB)){
            if (replayB.isMousePressed()) {
                playing.resetAll();
                playing.unpauseGame();
                //System.out.println("REPLAY LVL!");
            }
        } else if (isIn(e, unpauseB)){
            if (unpauseB.isMousePressed())
                playing.unpauseGame();
        }else audioOptions.mouseReleased(e);

        menuB.resetBools();
        replayB.resetBools();
        unpauseB.resetBools();

    }
    private boolean isIn(MouseEvent e, PauseButton b){
        return (b.getBounds().contains(e.getX(),e.getY()));
    }

}
