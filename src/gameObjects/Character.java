package gameObjects;

import java.awt.event.MouseEvent;

import java.util.HashMap;

import game.Game;
import game.GameState;
import game.Player;
import gameGUI.*;
import gameGUI.GameImage.MouseLogic;
import gameMath.Vector;
import gameObjects.CharComponent.ComponentType;

public abstract class Character extends GameObject {
    protected HashMap<ComponentType, CharComponent> features;
    protected GameImage[] model;
    protected NameTag nameTag;
    protected DialogueTag dialogueIndicator;
    protected Vector position;
    protected CharComponent clue;
    protected DialogueBook dialogue;
    protected boolean selected, hoverToggled, newDialogueNeeded;

    public Character(int posX, int posY) {
        this.position = new Vector(posX, posY);
        this.selected = false;
        this.hoverToggled = false;
        this.nameTag = null;
        this.features = new HashMap<ComponentType, CharComponent>();
        this.model = this instanceof Suspect ? new GameImage[4] : new GameImage[3];
        this.dialogue = null;
        this.newDialogueNeeded = true;
    }

    public void initModel(HashMap<ComponentType, String> modelArgs) {
        if(modelArgs == null){
            modelArgs = new HashMap<ComponentType, String>();
            modelArgs.put(ComponentType.NAME, null);
            modelArgs.put(ComponentType.SKINCOLOR, null);
            modelArgs.put(ComponentType.SHIRT, null);
            modelArgs.put(ComponentType.SHOES, null);
            modelArgs.put(ComponentType.MURDERWEAPON, null);
        }

        String charType = getClass().getSimpleName();
        int i = 0;

        for (ComponentType type : modelArgs.keySet()) {
            CharComponent newComponent = modelArgs.get(type) == null ? new CharComponent(type, charType)
                    : new CharComponent(type, charType, modelArgs.get(type));
            features.put(type, newComponent);
            // Name does not get added to the model
            if (type != ComponentType.NAME && (type != ComponentType.MURDERWEAPON || this instanceof Suspect)) {
                model[i] = newComponent.createImage(position);
                model[i].setMouseLogic(new MouseLogic(model[i]) {
                    @Override
                    public void mouseEntered() {
                        Character.this.mouseEntered();
                    }

                    @Override
                    public void mouseExited() {
                        Character.this.mouseExited();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (mouseTouched()) {
                            Character.this.mouseClicked();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (mouseTouched() && Character.this instanceof Victim) {
                            ((Victim) Character.this).mousePressed();
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (Character.this instanceof Victim) {
                            ((Victim) Character.this).mouseReleased();
                        }
                    }
                });
                i++;
            }
        }
    }

    public String getName() {
        return features.get(ComponentType.NAME).getVariation().getName();
    }

    public HashMap<ComponentType, CharComponent> getFeatures(){
        return features;
    }

    public GameImage[] getModel() {
        return model;
    }

    public Vector getViewPosition() {
        return position.scale(Game.SCALE);
    }

    public Vector getPosition() {
        return position;
    }

    public int getHeight() {
        return (int)model[0].getSize().getY();
    }

    public CharComponent getClue() {
        return clue;
    }

    public void setClue(CharComponent clue) {
        this.clue = clue;
        dialogueIndicator = new ClueTag(this);
        Game.getActiveGame().add(dialogueIndicator);
    }

    public void setImportantInfo() {
        setImportantInfo(null);
    }

    public void setImportantInfo(CharComponent clue) {
        if (clue != null) {
            this.clue = clue;
        }
        dialogueIndicator = new ImportantTag(this);
        Game.getActiveGame().add(dialogueIndicator);
    }

    public void setPosition(Vector newPos) {
        position = newPos;
        for (GameImage g : model) {
            g.setPosition(newPos);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void giveClue() {
        Player.getCurrentPlayer().obtainClue(clue);
        clue = null;
    }

    public void deselect() {
        if (dialogueIndicator != null && clue == null) {
            Game.getActiveGame().remove(dialogueIndicator);
            dialogueIndicator = null;
        }
    }

    protected void mouseEntered() {
        if (interactable() && nameTag == null) {
            nameTag = new NameTag(Character.this);
            Game.getActiveGame().add(nameTag);
        }
    }

    protected void mouseExited() {
        if (nameTag != null && !mouseTouched()) {
            Game.getActiveGame().remove(nameTag);
            nameTag = null;
        }
    }

    protected void mouseClicked() {
        if (interactable()){
            Game.getActiveGame().remove(nameTag);
            nameTag = null;
            if (newDialogueNeeded) {
                newDialogueNeeded = false;
                dialogue = chooseDialogue();
            }
            Game.getActiveGame().add(new Dialogue(this, dialogue));
        }
    }

    protected boolean interactable(){
        GameState state = Game.getActiveGame().getState();
        return Dialogue.getActiveDialogue() == null
        && (this instanceof Detective || state != GameState.INTRODUCTIONPHASE)
        && (state == GameState.INTERACTIONPHASE || state == GameState.INTRODUCTIONPHASE || state == GameState.LINEUPPHASE);
    }

    protected boolean mouseTouched() {
        for (GameImage g : model) {
            if (g.mouseTouched()) {
                return true;
            }
        }
        return false;
    }

    protected DialogueBook chooseDialogue() {
        DialogueBook newDialogue = new DialogueBook(this,
                "This is the default dialogue string. This should be overriden by the Character class' children.");
        return newDialogue;
    }

}
