package gameObjects;

import java.util.HashMap;

import javax.swing.SwingWorker;

import game.Game;
import game.GameState;
import gameEvents.dialogue.PageListener;
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
        dialogueStage = CONFIRMATION_STAGE;
    }

    @Override
    public DialogueBook chooseDialogue() {
        DialogueBook book;
        Page A, AA, AAA, B;
        switch (dialogueStage) {
            case INTRODUCTION_STAGE:
                book = new DialogueBook(
                        "Hey. About time you showed up. A murder just happened and I need your help gathering clues.");
                A = book.add(book.getFirst(),
                        "So far, we know that our suspect's name is " + getClue().getVariation().getName() + ".",
                        "Alright",
                        new PageListener() {
                            public void pageOpened() {
                                giveClue();
                            }
                        });
                AA = book.add(A, "We need info on what they look like and what they used to commit the murder.",
                        "What else do we need?");
                AAA = book.add(AA, "Talk to the civillians and the doctor. Maybe they know something.",
                        "What should I do?");

                book.add(AAA, "Come talk to me when your done so we can go identify the suspect.", "Will do!",
                        new PageListener() {
                            @Override
                            public void pageClosed() {
                                Game.getActiveGame().startInteractionPhase();
                                newDialogueNeeded = true;
                                dialogueStage = INSUFFICIENT_CLUE_COUNT_STAGE;
                            }
                        });
                return book;
            case INSUFFICIENT_CLUE_COUNT_STAGE:
                book = new DialogueBook(
                        "It looks like you haven't gathered all the clues yet. Perhaps you should ask around some more.");
                return book;
            case CONFIRMATION_STAGE:
                book = new DialogueBook(
                        "You got all the information? Great job! Are you ready to go identify the suspect now?");
                A = book.add(book.getFirst(), "Sweet! Let's go!", "Yes", new PageListener() {
                    @Override
                    public void pageClosed() {
                        SwingWorker<Void, Void> worker = new SwingWorker<Void,Void>(){
                            public Void doInBackground(){
                                Game.getActiveGame().startTransitionPhase(1.5, () -> {Game.getActiveGame().setState(GameState.LINEUPPHASE);});
                                Game.getActiveGame().startLineupPhase();
                                return null;
                            }
                        };

                        worker.execute();
                    }
                });
                B = book.add(book.getFirst(), "Alright. Talk to me again when you're ready.", "No");
                return book;
            default:
                return null;
        }
    }

    

    /// DIALOGUE LIBRARY ///
}
