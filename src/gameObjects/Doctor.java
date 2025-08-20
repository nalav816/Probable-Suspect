package gameObjects;

import java.util.HashMap;

import game.Game;
import gameEvents.interaction.*;
import gameGUI.*;
import gameGUI.DialogueBook.Page;
import gameObjects.CharComponent.ComponentType;

public class Doctor extends Character implements InteractionListener {
    // Dialogue stage constants
    private final static int INSTRUCTION_STAGE = 0;
    private final static int JUST_GAVE_INSTRUCTION_STAGE = 1;
    private final static int CLUE_GIVING_STAGE = 2;
    private final static int JUST_GAVE_CLUE_STAGE = 3;

    private int dialogueStage;

    public Doctor(int posX, int posY) {
        super(posX, posY);
        initModel(generateProperties());
        this.dialogueStage = INSTRUCTION_STAGE;
        InteractionHandler.addListener(this);
    }

    protected HashMap<ComponentType, String> generateProperties(){
        HashMap<ComponentType, String> props = new HashMap<ComponentType, String>();
        props.put(ComponentType.SKINCOLOR, "doctor");
        props.put(ComponentType.SHIRT, "doctor");
        props.put(ComponentType.SHOES, "doctor");
        props.put(ComponentType.NAME, "Dr. Brown");
        props.put(ComponentType.MURDERWEAPON, null);
        return props;
    }

    @Override
    public void autopsyConducted() {
        dialogueStage = CLUE_GIVING_STAGE;
        newDialogueNeeded = true;
        dialogueIndicator = new ClueTag(this);
        Game.getActiveGame().add(dialogueIndicator);
    }

    

}
