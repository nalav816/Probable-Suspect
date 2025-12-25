package gameObjects;

import static gameObjects.Character.DialogueStage.*;

import java.util.HashMap;

import game.Game;
import gameEvents.interaction.*;
import gameGUI.*;
import gameObjects.CharComponent.ComponentType;

public class Doctor extends Character implements InteractionListener {
    public Doctor(int posX, int posY) {
        super(posX, posY);
        initModel(generateProperties());
        setDialogueStage(AUTOPSY_INSTRUCTION);
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
        dialogueIndicator = new ClueTag(this);
        Game.getActiveGame().add(dialogueIndicator);
        setDialogueStage(CLUE_GIVING);
    }

    @Override
    public DialogueBook chooseDialogue(){
        return switch (stage){
            case AUTOPSY_INSTRUCTION -> new DialogueBook("Hey detective. Awful situation isn't it? Poor guy.")
                .setBranch("Yeah", page1 -> {
                    page1.setText("Let's take a moment of silence.......... Anyway, what can I answer for you?");
                    page1.setBranch("How did he die?", page2 -> {
                        page2.setText("If you drag the body to the ambulance, we can conduct an autopsy and find out.");
                        page2.setBranch("Ok", page3 -> {
                            page3.setText("Come talk to me once its done.");
                            page3.setOnPageClosed(() -> {
                                removeIndicator = true;
                                setDialogueStage(JUST_GAVE_INSTRUCTION);
                                InteractionHandler.fireDocInstructionGiven();
                            });
                        });
                    });
                })
                .setBranch("He deserved it", page1 -> {
                    page1.setText("What!? What do you mean?");
                    page1.setBranch("I don't know", page2 -> {
                        page2.setText("Are you sure you haven't been drinking?");
                        page2.setBranch("Who knows", page3 -> {
                            page3.setText("Wow. Well I guess just try talking to me again when you come to your senses.");
                        });
                    });
                });
            case JUST_GAVE_INSTRUCTION -> new DialogueBook("Did you drag the body to the ambulance yet? Bring it over here so we can conduct an autopsy.");
            case CLUE_GIVING -> new DialogueBook("Hi again. We took a look at the body.")
                .setBranch("What'd you find?", page1 -> {
                    page1.setText("We've determined that the victim was murdered with a " + getClue().getVariation().getName()+ ".");
                    page1.setBranch("Thank you", page2 -> {
                        page2.setText("Just doing my job.");
                        page2.setOnPageClosed(() -> {
                            setDialogueStage(JUST_GAVE_CLUE);
                            removeIndicator = true;
                            giveClue();
                        });
                    });
                });
            case JUST_GAVE_CLUE -> new DialogueBook("Hi again. I don't really have any extra information. Wish I could help more. Sorry.");
            default -> new DialogueBook("Default text");
        };
    }

    

}
