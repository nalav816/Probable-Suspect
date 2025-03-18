package gameGUI;

import javax.swing.JComponent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import game.Game;
import game.GameState;
import game.Player;
import gameMath.BoundingBox;
import gameMath.Vector;
import gameObjects.Victim;

import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GameImage implements Renderable {
    protected JLabel component;
    protected BufferedImage image, displayImage;
    protected double alpha;
    // Position is stored relative to the original size of the game frame, not
    // scaled size. Display position stores actual position
    protected Vector position;
    protected BoundingBox collisionBox;
    protected boolean touchedLastTick;

    public GameImage(String directory, Vector position) {
        try {
            //Copy it to make sure it is in ARGB format
            this.image = ImageIO.read(new File(directory));
            this.displayImage = scaleImage(image);
            this.position = position;
            this.component = new JLabel();
            this.collisionBox = computeBoundingBox();
            this.touchedLastTick = false;
            this.alpha = 1.0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JComponent[] getJComponents() {
        return new JComponent[] { component };
    }

    // Runs per tick logic
    public void tick() {
        // Mouse Logic
        MouseListener[] listeners = component.getMouseListeners();
        // Each commponent should only have one mouse logic listener
        MouseLogic listener = listeners.length >= 1 ? (MouseLogic) listeners[0] : null;
        if (!touchedLastTick && mouseTouched() && listener != null && !Victim.dragActive() && Game.getActiveGame().getState() != GameState.TRANSITION) {
            listener.mouseEntered();
            touchedLastTick = true;
        } else if (touchedLastTick && !mouseTouched() && listener != null && !Victim.dragActive()) {
            listener.mouseExited();
            touchedLastTick = false;
        }
    }

    // Updates the jcomponent for rendering
    public void render() {

        component.setIcon(new ImageIcon(displayImage));
        component.setSize(displayImage.getWidth(), displayImage.getHeight());
        component.setLocation((int)(position.getX() * Game.SCALE), (int)(position.getY() * Game.SCALE));
        component.setDoubleBuffered(true);
        // component.repaint();

    }

    public void playEffectOnAdd(){}

    public void setMouseLogic(MouseLogic adapter) {
        SwingUtilities.invokeLater(() -> {
            for (MouseListener m : component.getMouseListeners()) {
                component.removeMouseListener(m);
            }
            component.addMouseListener(adapter);
        });
    }

    public void setPosition(Vector pos) {
        position = pos;
        collisionBox = computeBoundingBox();
        //render();
        SwingUtilities.invokeLater(() -> render());
    }

    public void setTransparency(double transparency){
        alpha = transparency;
        displayImage = scaleImage(copyImage(image));
        SwingUtilities.invokeLater(() -> render());
    }

    public Integer getLayer() {
        return JLayeredPane.PALETTE_LAYER;
    }

    /*
     * @Return The view position of the image
     */
    public Vector getViewPosition() {
        return position.scale(Game.SCALE);
    }

    /*
     * @Return Position of image without game scaling applied
     */
    public Vector getPosition() {
        return position;
    }

    /*
     * @Return The view size of the image
     */
    public Vector getViewSize() {
        return new Vector(displayImage.getWidth(), displayImage.getHeight());
    }

    /*
     * @Return Size of the image without game scaling applied
     */
    public Vector getSize() {
        return new Vector(image.getWidth(), image.getHeight());
    }

    protected BufferedImage scaleImage(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth() * Game.SCALE,
                image.getHeight() * Game.SCALE,
                image.getType());
        Graphics2D graphics = newImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(image, 0, 0, image.getWidth() * Game.SCALE,
                image.getHeight() * Game.SCALE, null);
        graphics.dispose();
        return newImage;
    }

    protected BufferedImage copyImage(BufferedImage image) {
        BufferedImage newImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = newImg.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)alpha));
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return newImg;
    }

    // Pixel perfect touch detection
    public boolean mouseTouched() {
        Vector mousePos = Player.getCurrentPlayer().getMouseLocation();
        return touched((int)mousePos.getX(), (int)mousePos.getY());
    }

    public boolean touched(int xToCheck, int yToCheck) {
        for (int x = 0; x < displayImage.getWidth(); x++) {
            for (int y = 0; y < displayImage.getHeight(); y++) {
                // Gets alpha at a given point. If the pixel isn't transparents checks if mouse
                // position is equal to its projected position on screen
                // If so we can instantly return true. If loop ends without result, its false
                int alpha = displayImage.getRGB(x, y) >> 24;
                if (alpha != 0) {
                    int projectedXPos = (int)position.getX() * Game.SCALE + x;
                    int projectedYPos = (int)position.getY() * Game.SCALE + y;
                    if (xToCheck == projectedXPos && yToCheck == projectedYPos) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private BoundingBox computeBoundingBox() {
        int maxX = 0;
        int maxY = 0;
        int minX = displayImage.getWidth() - 1;
        int minY = displayImage.getHeight() - 1;
        for (int x = 0; x < displayImage.getWidth(); x++) {
            for (int y = 0; y < displayImage.getHeight(); y++) {
                // Gets alpha at a given point. If the pixel isn't transparents checks if mouse
                // position is equal to its projected position on screen
                // If so we can instantly return true. If loop ends without result, its false
                int alpha = displayImage.getRGB(x, y) >> 24;
                if (alpha != 0) {
                    if (x > maxX) {
                        maxX = x;
                    }

                    if (y > maxY) {
                        maxY = y;
                    }

                    if (x < minX) {
                        minX = x;
                    }

                    if (y < minY) {
                        minY = y;
                    }
                }
            }
        }
        return new BoundingBox(pixelToWorldCoords(minX, minY), pixelToWorldCoords(maxX, maxY));
    }

    public BoundingBox getBoundingBox() {
        return collisionBox;
    }

    private Vector pixelToWorldCoords(int x, int y) {
        return new Vector(x + getViewPosition().getX(), y + getViewPosition().getY());
    }

    public static abstract class MouseLogic extends MouseAdapter {
        protected GameImage srcImg;

        public MouseLogic(GameImage srcImg) {
            super();
            this.srcImg = srcImg;
        }

        // Custom mouse functions that are fired by pixel-perfect detection, not by
        // being within JComponent bounds
        public void mouseEntered() {
        }

        public void mouseExited() {
        }

    }
}
