package gameObjects;

import static gameObjects.Character.DialogueStage.*;

import java.util.HashMap;

import javax.swing.SwingWorker;

import game.Game;
import game.GameState;
import gameEvents.interaction.*;
import gameGUI.DialogueBook;
import gameObjects.CharComponent.ComponentType;

public class Detective extends Character implements InteractionListener {
    public Detective(int posX, int posY, CharComponent clue) {
        super(posX, posY);
        initModel(generateProperties());
        setImportantInfo(clue);
        setDialogueStage(INTRODUCTION);
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
        setDialogueStage(CONFIRMATION);
    }

    @Override
    public DialogueBook chooseDialogue() {
        return switch(stage) {
            case INTRODUCTION -> new DialogueBook("Hey. About time you showed up. A murder just happened and I need your help gathering clues.")
                .setBranch("Alright", page1 -> {
                    page1.setText("So far, we know that our suspect's name is " + getClue().getVariation().getName() + ".");
                    page1.setOnPageOpen(() -> {
                        giveClue();
                    });
                    page1.setBranch("What else do we need?", page2 -> {
                        page2.setText("We need info on what they look like and what they used to commit the murder.");
                        page2.setBranch("What should I do?", page3 -> {
                            page3.setText("Talk to the civillians and the doctor. Maybe they know something.");
                            page3.setBranch("Will do!", page4 -> {
                                page4.setText("Come talk to me when your done so we can go identify the suspect.");
                                page4.setOnPageClosed(() -> {
                                    setDialogueStage(INSUFFICIENT_CLUES);
                                    Game.getActiveGame().startInteractionPhase();
                                });
                             });
                        });
                    });
                });
            case INSUFFICIENT_CLUES -> new DialogueBook("It looks like you haven't gathered all the clues yet. Perhaps you should ask around some more."); 
            case CONFIRMATION -> new DialogueBook("You got all the information? Great job! Are you ready to go identify the suspect now?")
                .setBranch("Yes", page1 -> {
                    page1.setText("Sweet! Let's go!");
                    page1.setOnPageClosed(() -> {
                        SwingWorker<Void, Void> worker = new SwingWorker<Void,Void>(){
                            @Override
                            public Void doInBackground(){
                                Game.getActiveGame().startTransitionPhase(1.5, () -> Game.getActiveGame().setState(GameState.LINEUPPHASE));
                                Game.getActiveGame().startLineupPhase();
                                return null;
                            }
                        };

                        worker.execute();
                    });
                })
                .setBranch("No", page1 -> {
                    page1.setText("Alright. Talk to me again when you're ready.");
                });
            default -> new DialogueBook("Default text.");
        };
    } 

}
