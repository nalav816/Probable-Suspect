package gameObjects;

import gameEvents.interaction.*;
import gameGUI.Renderable;
import gameGUI.GameImage;
import gameMath.BoundingBox;
import gameMath.Vector;
import gameObjects.CharComponent.ComponentType;

import java.util.HashMap;

import javax.swing.JLayeredPane;

import game.Game;
import game.Player;

public class Victim extends Character implements InteractionListener {
    private static boolean beingDragged = false;
    private Vector defaultPosition, clickOffset;
    private boolean draggable;

    public Victim(int posX, int posY) {
        super(posX, posY);
        initModel(generateProperties());
        this.draggable = false;
        this.defaultPosition = Vector.copy(position);
        // Helps reposition model so that its positioned where the player clicks it
        this.clickOffset = new Vector(0, 0);
        InteractionHandler.addListener(this);
    }

    protected HashMap<ComponentType, String> generateProperties() {
        HashMap<ComponentType, String> props = new HashMap<ComponentType, String>();
        props.put(ComponentType.SKINCOLOR, "dead");
        props.put(ComponentType.SHIRT, "dead");
        props.put(ComponentType.SHOES, "dead");
        props.put(ComponentType.NAME, "Victim");
        props.put(ComponentType.MURDERWEAPON, null);
        return props;
    }

    @Override
    public void tick() {
        if (beingDragged) {
            Vector mousePos = Player.getCurrentPlayer().getMouseLocation();
            Vector newPos = mousePos.subtract(clickOffset);
            for (GameImage g : model) {
                BoundingBox collider = g.getBoundingBox();
                collider = collider.move(newPos.subtract(g.getViewPosition()));
                // Clamps floor collision
                if (collider.getBottomLeftCorner().getY() >= Game.SCENE_SIZE.getY() * Game.SCALE
                        - Game.SCENE_FLOOR_HEIGHT * Game.SCALE) {
                    newPos.setY((Game.SCENE_SIZE.getY() * Game.SCALE - Game.SCENE_FLOOR_HEIGHT * Game.SCALE)
                            - g.getViewSize().getY());
                }
                // Clamps top screen colission
                if (collider.getTopRightCorner().getY() < 0) {
                    newPos.setY(0 - g.getViewSize().getY() + collider.getHeight());
                }
                // Clamps left screen collision
                if (collider.getTopRightCorner().getX() < 0) {
                    newPos.setX(0);
                }
                // Clamps right screen collision
                if (collider.getBottomLeftCorner().getX() > Game.SCENE_SIZE.getX() * Game.SCALE) {
                    newPos.setX((Game.SCENE_SIZE.getX() * Game.SCALE) - g.getViewSize().getX());
                }
            }

            newPos = newPos.scale(1.0 / Game.SCALE);
            setPosition(newPos);
        }
        super.tick();
    }

    @Override
    public Integer getLayer() {
        return JLayeredPane.MODAL_LAYER;
    }

    @Override
    public void docInstructionGiven() {
        draggable = true;
    }

    @Override
    protected void mouseEntered() {
        if (interactable()) {
            super.mouseEntered();
        }
    }

    @Override
    protected void mouseClicked() {
    }

    protected void mousePressed() {
        if (draggable) {
            beingDragged = true;
            clickOffset = Player.getCurrentPlayer().getMouseLocation().subtract(getViewPosition());
            if (nameTag != null) {
                Game.getActiveGame().remove(nameTag);
                nameTag = null;
            }
        }
    }

    @Override
    protected boolean interactable() {
        return super.interactable() && draggable && !beingDragged;
    }

    protected void mouseReleased() {
        if (beingDragged) {
            beingDragged = false;
            for (Renderable r : Game.getActiveGame().getMiscGameObjects()) {
                if (r instanceof Ambulance) {
                    Ambulance ambulance = (Ambulance) r;
                    if (collidedWith(ambulance)) {
                        InteractionHandler.fireAutopsyConducted();
                        Game.getActiveGame().remove(this);
                        return;
                    }
                }
            }
            setPosition(defaultPosition);
        }

    }

    public static boolean dragActive() {
        return beingDragged;
    }
}
