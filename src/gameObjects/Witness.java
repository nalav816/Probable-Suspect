package gameObjects;

import static gameObjects.Character.DialogueStage.*;

import java.util.Random;

import gameGUI.*;
import gameObjects.CharComponent.ComponentType;

public class Witness extends Character {
    private enum Persona {
        CORDIAL;
       // COOL,
       // ANNOYED;

        public static Persona choose(){
            Persona[] personas = Persona.values();
            Random numGen = new Random();
            return personas[numGen.nextInt(personas.length)];
        }
    }

    private Persona persona;

    public Witness(int posX, int posY) {
        super(posX, posY);
        initModel(null);
        setDialogueStage(CLUE_GIVING);
        this.persona = Persona.choose();
    }

    @Override
    public void giveClue(){
        super.giveClue();
    }

    @Override
    public DialogueBook chooseDialogue() {
        ComponentType clueType = getClue().getType();
        return switch(stage) {
            case CLUE_GIVING -> switch(persona) {
                case CORDIAL -> {
                    if(clueType == ComponentType.SHIRT){

                    } else if(clueType == ComponentType.SKINCOLOR){

                    } else if(clueType == ComponentType.SHOES){

                    }
                    yield null;
                }
                /* case COOL -> {
                    if(clueType == ComponentType.SHIRT){

                    } else if(clueType == ComponentType.SKINCOLOR){

                    } else if(clueType == ComponentType.SHOES){

                    }
                    yield null;
                }
                case ANNOYED -> {
                    if(clueType == ComponentType.SHIRT){

                    } else if(clueType == ComponentType.SKINCOLOR){

                    } else if(clueType == ComponentType.SHOES){

                    }
                    yield null;
                } */
            };
            
            case JUST_GAVE_CLUE -> new DialogueBook("You again? I already told you everything I know. Can I go now?")
                .setBranch("No", page1 -> {
                    page1.setText("Ugh.");
                });
            case FED_UP -> new DialogueBook("Stop talking to me!");
            case NON_RESPONDENT -> new DialogueBook(".....")
                .setBranch("Hello?", page1 -> {
                    page1.setText("..........");
                });
            default -> new DialogueBook("Default text.");
        };
    }
    
}
