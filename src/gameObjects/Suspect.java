package gameObjects;

import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

import game.Game;
import game.GameState;
import gameGUI.Dialogue;
import gameGUI.DialogueBook;
import gameObjects.CharComponent.ComponentType;

public class Suspect extends Character {
    private String number;

    public Suspect(int posX, int posY, int accurateProps, int suspectNumber) {
        super(posX, posY);
        this.number = toString(suspectNumber);
        initModel(generateProperties(accurateProps));
        setDialogueStage(stage);
    }

    private HashMap<ComponentType, String> generateProperties(int accurateProps) {
        // Chooses what properties to make accurate
        Random numGen = new Random();
        HashMap<ComponentType, CharComponent> clueTemplate = Game.getActiveGame().getAvailableClues();
        HashMap<ComponentType, String> properties = new HashMap<ComponentType, String>();
        ArrayList<ComponentType> remainingProps = new ArrayList<ComponentType>(Arrays.asList(ComponentType.values()));

        while (remainingProps.size() > Game.getActiveGame().getAvailableClueCount() - accurateProps) {
            int roll = numGen.nextInt(remainingProps.size());
            ComponentType type = remainingProps.remove(roll);
            properties.put(type, clueTemplate.get(type).getVariation().getName());
        }

        // Choose randoms for rest of the props that are NOT accurate
        for (ComponentType type : remainingProps) {
            CharComponent randomProp = new CharComponent(type, "Suspect");
            while (randomProp.equals(clueTemplate.get(type))) {
                randomProp = new CharComponent(type, "Suspect");
            }
            properties.put(type, randomProp.getVariation().getName());
        }

        return properties;
    }

    private String toString(int num) {
        switch (num) {
            case 1:
                return "one";
            case 2:
                return "two";
            case 3:
                return "three";
            case 4:
                return "four";
            case 5:
                return "five";
            default:
                return null;
        }
    }

    @Override
    public DialogueBook chooseDialogue() {
        return new DialogueBook("You have chosen suspect number " + number + ".")
                .setBranch("...", page1 -> {
                    page1.setText("Their name is " + features.get(ComponentType.NAME).getVariation().getName()
                            + " and they have a "
                            + features.get(ComponentType.SKINCOLOR).getVariation().getNameParsed() + " skin complexion.");
                    page1.setBranch("...", page2 -> {
                        page2.setText("Additionally, they are wearing a "
                                + features.get(ComponentType.SHIRT).getVariation().getName()
                                + " shirt and " + features.get(ComponentType.SHOES).getVariation().getName()
                                + " shoes.");
                        page2.setBranch("...", page3 -> {
                            page3.setText("Lastly, they were found in posession of a "
                                    + features.get(ComponentType.MURDERWEAPON).getVariation().getName()
                                    + ". Are you confident this is the murderer?");
                            page3.setBranch("Yes", page4 -> {
                                page4.setText("Retrieving suspect number " + number + "..................");
                                page4.setOnPageOpen(() -> {
                                    Thread newThread = new Thread(() -> {
                                        Game.getActiveGame().startTransitionPhase(3, () -> {
                                            Dialogue.getActiveDialogue().destroy();
                                            Game.getActiveGame().setState(GameState.WINLOSS);
                                        });
                                        Game.getActiveGame().endLineupPhase(Suspect.this);
                                    });
                                    newThread.start();
                                });
                            });
                            page3.setBranch("No", page4 -> {
                                page4.setText("Take some more time to consider.");
                            });
                        });
                    });
                });
    }

}
