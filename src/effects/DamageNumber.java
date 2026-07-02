package effects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import Main.Game;

public class DamageNumber {
    private float x, y;
    private String text;
    private int lifetime = 45; // Lasts for 45 frames (~0.75 seconds)
    private boolean active = true;
    private float speedY = -0.5f * Game.SCALE; // Upward drift speed
    private Color textColor= null;

    public DamageNumber(float x, float y, int amount) {
        this.x = x;
        this.y = y;
        this.text = "-" + amount;
    }
    public DamageNumber(float x, float y, int amount, Color color) {
        this.x = x;
        this.y = y;
        this.text = "-" + amount;
        this.textColor = color;
    }

    public void update() {
        y += speedY; // Drift upwards
        lifetime--;
        if (lifetime <= 0) {
            active = false;
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        // Simple retro text style
        g.setFont(new Font("Arial", Font.BOLD, (int)(10 * Game.SCALE)));

        // Draw crisp shadow drop
        g.setColor(Color.BLACK);
        g.drawString(text, (int)(x - xLvlOffset) + 1, (int)y + 1);

        // 🌟 FIX: Use the custom color if provided, otherwise default to Orange/Yellow
        if (textColor != null) {
            g.setColor(textColor);
        } else {
            g.setColor(new Color(255, 180, 0)); // Default enemy damage color
        }

        g.drawString(text, (int)(x - xLvlOffset), (int)y);
    }

    public boolean isActive() {
        return active;
    }
}