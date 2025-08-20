package gameObjects;

import java.util.HashMap;
import java.util.Random;

import gameGUI.*;
import gameGUI.DialogueBook.Page;
import gameObjects.CharComponent.ComponentType;

public class Witness extends Character {
    // Constances for dialogue stages
    // Influences what type of dialogue the character could give you
    private final static int CLUE_GIVING_STAGE = 1;
    private final static int JUST_GAVE_CLUE_STAGE = 2;
    private final static int FED_UP_STAGE = 3;
    private final static int NON_RESPONDENT_STAGE = 4;

    private static boolean dialoguesLoaded = false;

    private int dialogueStage;

    public Witness(int posX, int posY) {
        super(posX, posY);
        initModel(null);
        this.dialogue = null;
        this.dialogueStage = CLUE_GIVING_STAGE;

        if (!dialoguesLoaded) {
            dialoguesLoaded = true;
        }
    }

    @Override
    public void giveClue(){
        super.giveClue();
        newDialogueNeeded = true;
        dialogueStage = JUST_GAVE_CLUE_STAGE;
    }
    
}
