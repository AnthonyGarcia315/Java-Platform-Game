package Main;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;

public class GameWindow {
    private JFrame jframe;
    private GamePanel gamePanel;

    // The three window states available
    public enum WindowMode {
        WINDOWED,
        MAXIMIZED_WINDOWED,
        EXCLUSIVE_FULLSCREEN
    }

    private WindowMode currentMode = WindowMode.WINDOWED;

    public GameWindow(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setWindowMode(currentMode);
    }

    public void setWindowMode(WindowMode mode) {
        this.currentMode = mode;

        // 1. If a window framework already exists, tear it down before reconfiguration
        if (jframe != null) {
            jframe.dispose();
        }

        jframe = new JFrame();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/icon.png");
            if (is != null) {
                jframe.setIconImage(javax.imageio.ImageIO.read(is));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 🌟 END ICON BLOCK 🌟

        jframe.add(gamePanel);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        // 2. Configure the frame attributes based on the selected mode
        switch (mode) {
            case WINDOWED -> {
                jframe.setUndecorated(false); // Show borders
                jframe.setResizable(true);    // Allow maximizing/resizing
                gd.setFullScreenWindow(null);  // Release exclusive control
                jframe.pack();
                jframe.setLocationRelativeTo(null); // Center window
            }
            case MAXIMIZED_WINDOWED -> {
                jframe.setUndecorated(false); // Keep borders
                jframe.setResizable(true);
                gd.setFullScreenWindow(null);
                jframe.pack();
                jframe.setExtendedState(Frame.MAXIMIZED_BOTH); // Force maximize layout
            }
            case EXCLUSIVE_FULLSCREEN -> {
                jframe.setUndecorated(true); // Strip ALL borders out
                jframe.setResizable(false);
                jframe.pack();
                try {
                    if (gd.isFullScreenSupported()) {
                        gd.setFullScreenWindow(jframe); // Hardware lock focus
                    } else {
                        jframe.setExtendedState(Frame.MAXIMIZED_BOTH);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        jframe.setVisible(true);
        jframe.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                // When you click back into the game, make sure the keyboard works immediately
                gamePanel.requestFocusInWindow();
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                // When you click OUT of the game, tell the Game class to stop all movement!
                gamePanel.getGame().windowFocusLost();
            }
        });

        // 3. Safety listeners: kill auto-movement arrays on changes & maintain focus loops
        jframe.addWindowStateListener(e -> resetPlayerInputFlags());
        jframe.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });

        gamePanel.requestFocusInWindow();
    }

    private void resetPlayerInputFlags() {
        if (GameStates.Gamestate.state == GameStates.Gamestate.PLAYING) {
            gamePanel.getGame().getPlaying().getPlayer().resetDirBooleans();
        }
        gamePanel.requestFocusInWindow();
    }

    public WindowMode getCurrentMode() {
        return currentMode;
    }
}