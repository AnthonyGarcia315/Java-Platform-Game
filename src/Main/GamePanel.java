package Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import Inputs.KeyboardInputs;
import Inputs.MouseInputs;
import static Main.Game.GAME_HEIGHT;
import static Main.Game.GAME_WIDTH;

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private Game game;
    private BufferedImage virtualCanvas;

    public GamePanel(Game game) {
        mouseInputs = new MouseInputs(this);
        this.game = game;
        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        setFocusTraversalKeysEnabled(false);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Initialize our virtual asset engine canvas if it hasn't been built yet
        if (virtualCanvas == null) {
            virtualCanvas = new BufferedImage(Game.GAME_WIDTH, Game.GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
        }

        // 2. Render the actual gameplay calculations onto our tiny crisp layout
        Graphics2D gCanvas = (Graphics2D) virtualCanvas.getGraphics();
        game.render(gCanvas);
        gCanvas.dispose();

        // 3. Draw and stretch our tiny canvas across the whole window size
        Graphics2D g2d = (Graphics2D) g;

        // Nearest neighbor interpolation prevents pixel art blurring when maximized
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Stretches to the exact current dimensions of the maximized window frame
        g2d.drawImage(virtualCanvas, 0, 0, getWidth(), getHeight(), null);
    }

    public Game getGame() {
        return game;
    }

}