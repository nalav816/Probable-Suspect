package gameObjects;

import java.util.HashMap;

import game.Game;
import gameEvents.dialogue.PageListener;
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

    @Override
    public DialogueBook chooseDialogue() {
        DialogueBook book = null;
        Page a, aa, aaa, b, ba, baa;

        switch (dialogueStage) {
            case INSTRUCTION_STAGE:
                book = new DialogueBook("Hey detective. Awful situation isn't it? Poor guy.");
                a = book.add(book.getFirst(),
                        "Let's take a moment of silence.......... Anyway, what can I answer for you?",
                        "Yeah");
                aa = book.add(a, "If you drag the body to the ambulance, we can conduct an autopsy and find out.",
                        "How did he die?");
                aaa = book.add(aa, "Come talk to me once its done.", "Ok",
                        new PageListener() {
                            @Override
                            public void pageClosed() {
                                Game.getActiveGame().remove(dialogueIndicator);
                                dialogueIndicator = null;
                                newDialogueNeeded = true;
                                dialogueStage = JUST_GAVE_INSTRUCTION_STAGE;
                                InteractionHandler.fireDocInstructionGiven();
                            }
                        });
                b = book.add(book.getFirst(), "What!? What do you mean?", "He deserved it");
                ba = book.add(b, "Are you sure you haven't been drinking?", "I don't know");
                baa = book.add(ba, "Wow. Well I guess just try talking to me again when you come to your senses.",
                        "Who knows");
                break;
            case CLUE_GIVING_STAGE:
                book = new DialogueBook("Hi again. We took a look at the body.");
                a = book.add(book.getFirst(),
                        "We've determined that the victim was murdered with a " + getClue().getVariation().getName()
                                + ".",
                        "What'd you find?",
                        new PageListener() {
                            @Override
                            public void pageOpened() {
                                giveClue();
                                newDialogueNeeded = true;
                                dialogueStage = JUST_GAVE_CLUE_STAGE;
                            }
                        });
                aa = book.add(a, "Just doing my job.", "Thank you");
                break;
            case JUST_GAVE_INSTRUCTION_STAGE:
                book = new DialogueBook("Did you drag the body to the ambulance yet? Bring it over here so we can conduct an autopsy.");
                break;
            case JUST_GAVE_CLUE_STAGE:
                book = new DialogueBook("Replace later.");
                break;
        }

        return book;
    }

}
