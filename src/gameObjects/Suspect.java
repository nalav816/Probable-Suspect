package gameObjects;

import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

import game.Game;
import game.GameState;
import gameEvents.dialogue.PageListener;
import gameGUI.Dialogue;
import gameGUI.DialogueBook;
import gameGUI.DialogueBook.Page;
import gameObjects.CharComponent.ComponentType;

public class Suspect extends Character {
    private String number;
    public Suspect(int posX, int posY, int accurateProps, int suspectNumber) {
        super(posX, posY);
        this.number = toString(suspectNumber);
        initModel(generateProperties(accurateProps));
    }

    private HashMap<ComponentType, String> generateProperties(int accurateProps) {
       //Chooses what properties to make accurate
        Random numGen = new Random();
        HashMap<ComponentType, CharComponent> clueTemplate = Game.getActiveGame().getAvailableClues();
        HashMap<ComponentType, String> properties = new HashMap<ComponentType, String>();
        ArrayList<ComponentType> remainingProps = new ArrayList<ComponentType>(Arrays.asList(ComponentType.values()));

        
        while(remainingProps.size() > Game.getActiveGame().getAvailableClueCount() - accurateProps){
            int roll = numGen.nextInt(remainingProps.size());
            ComponentType type = remainingProps.remove(roll);
            properties.put(type, clueTemplate.get(type).getVariation().getName());
        }
        
        //Choose randoms for rest of the props that are NOT accurate
        for(ComponentType type : remainingProps){
            CharComponent randomProp = new CharComponent(type, "Suspect");
            while(randomProp.equals(clueTemplate.get(type))){
                randomProp = new CharComponent(type, "Suspect");
            }
            properties.put(type, randomProp.getVariation().getName());
        }
        
        return properties;
    }

    private String toString(int num){
        switch(num){
            case 1: return "one";
            case 2: return "two";
            case 3: return "three";
            case 4: return "four";
            case 5: return "five";
            default: return null;
        }
    }

     @Override
    public DialogueBook chooseDialogue() {
        DialogueBook book;
        Page A, AA, AAA, B;
        book = new DialogueBook("You have chosen suspect number " + number + ".");
        A = book.add(book.getFirst(), "Their name is " + features.get(ComponentType.NAME).getVariation().getName() + " and they have a " 
        + features.get(ComponentType.SKINCOLOR).getVariation().getName() + " skin complexion.");
        AA = book.add(A, "Additionally, they are wearing a " + features.get(ComponentType.SHIRT).getVariation().getName()
        + " shirt and " + features.get(ComponentType.SHOES).getVariation().getName() + " shoes.");
        AAA = book.add(AA, "Lastly, they were found in posession of a " + features.get(ComponentType.MURDERWEAPON).getVariation().getName() + ". Are you confident this is the murderer?");
        book.add(AAA, "Retrieving suspect number " + number + "..................", "Yes", new PageListener(){
            @Override
            public void pageOpened(){
                Thread newThread = new Thread(() -> {
                    Game.getActiveGame().startTransitionPhase(3, () -> {
                        Dialogue.getActiveDialogue().destroy();
                        Game.getActiveGame().setState(GameState.WINLOSS);});
                    Game.getActiveGame().endLineupPhase(Suspect.this);
                });
                newThread.start();
            }
        });
        book.add(AAA, "Take some more time to consider.", "No");
        return book;
    }
}
