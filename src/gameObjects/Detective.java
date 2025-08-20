package gameObjects;

import java.util.HashMap;

import javax.swing.SwingWorker;

import game.Game;
import game.GameState;
import gameEvents.interaction.*;
import gameGUI.DialogueBook;
import gameGUI.DialogueBook.Page;
import gameObjects.CharComponent.ComponentType;

public class Detective extends Character implements InteractionListener {
    // Constances for dialogue stages
    private final static int INTRODUCTION_STAGE = 0;
    private final static int INSUFFICIENT_CLUE_COUNT_STAGE = 1;
    private final static int CONFIRMATION_STAGE = 2;

    private int dialogueStage;

    public Detective(int posX, int posY, CharComponent clue) {
        super(posX, posY);
        initModel(generateProperties());
        setImportantInfo(clue);
        this.dialogueStage = INTRODUCTION_STAGE;
        InteractionHandler.addListener(this);
    }

    protected HashMap<ComponentType, String> generateProperties() {
        HashMap<ComponentType, String> props = new HashMap<ComponentType, String>();
        props.put(ComponentType.SKINCOLOR, "detective");
        props.put(ComponentType.SHIRT, "detective");
        props.put(ComponentType.SHOES, "detective");
        props.put(ComponentType.NAME, "Dt. Tony");
        props.put(ComponentType.MURDERWEAPON, null);
        return props;
    }

    @Override
    public void allCluesCollected() {
        setImportantInfo(null);
        newDialogueNeeded = true;
        dialogueStage = CONFIRMATION_STAGE;
    }


    

    /// DIALOGUE LIBRARY ///
}
