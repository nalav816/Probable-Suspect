package gameObjects;

import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

import game.Game;
import game.GameState;
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

}
