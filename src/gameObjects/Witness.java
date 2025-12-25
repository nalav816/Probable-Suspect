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

        public static Persona choose() {
            Persona[] personas = Persona.values();
            Random numGen = new Random();
            return personas[numGen.nextInt(personas.length)];
        }
    }

    private Persona persona;

    public Witness(int posX, int posY) {
        super(posX, posY);
        initModel(null);
        this.persona = Persona.choose();
    }

    @Override
    public void setClue(CharComponent clue) {
        super.setClue(clue);
        setDialogueStage(CLUE_GIVING);
    }

    @Override
    public DialogueBook chooseDialogue() {
        return switch (stage) {
            case CLUE_GIVING -> {
                ComponentType clueType = getClue().getType();
                String clueInfo = clue.getVariation().getNameParsed();
                yield switch (persona) {
                    case CORDIAL -> {
                        if (clueType == ComponentType.SHIRT) {
                            yield new DialogueBook("Hey detective. Is that guy gonna be okay?")
                                .setBranch("No, he's dead", page1 -> {
                                    page1.setText("May he rest in peace. Cruel world we live in.");
                                    page1.setBranch("You know anything?", page2 -> {
                                        page2.setText("I saw someone talking to the guy not too long ago. He was wearing a " + clueInfo + " shirt.");
                                        page2.setBranch("Appreciate it", page3 -> {
                                            page3.setText("No problem. May justice be served!");
                                            page3.setOnPageClosed(() -> {
                                                giveClue();
                                                setDialogueStage(JUST_GAVE_CLUE);
                                            });
                                        });
                                    });
                                });
                        } else if (clueType == ComponentType.SKINCOLOR) {
                            yield new DialogueBook("Hi detective! How are you doing?")
                                .setBranch("Good", page1 -> {
                                    page1.setText("That's good to hear. What can I do for you?");
                                    page1.setBranch("Tell me what you know", page2 -> {
                                        page2.setText("I don't know much, but I saw someone running away. He had a " + clueInfo + " skin complexion.");
                                        page2.setBranch("Thank you", page3 -> {
                                            page3.setText("No problem. Good luck with your investigation!");
                                            page3.setOnPageClosed(() -> {
                                                giveClue();
                                                setDialogueStage(JUST_GAVE_CLUE);
                                            });
                                        });
                                    });
                                })
                                .setBranch("Not great", page1 -> {
                                    page1.setText("That's unfortunate to hear. Would telling you what I know cheer you up?");
                                    page1.setBranch("Yeah", page2 -> {
                                        page2.setText("I don't know much, but I saw someone running away. He had a " + clueInfo + " skin complexion.");
                                        page2.setBranch("Thank you", page3 -> {
                                            page3.setText("No worries. Good luck with the rest of your investigation.");
                                            page3.setOnPageClosed(() -> {
                                                giveClue();
                                                setDialogueStage(JUST_GAVE_CLUE);
                                            });
                                        });
                                    });
                                });
                        } else if (clueType == ComponentType.SHOES) {
                            yield new DialogueBook("Good evening sir!")
                                .setBranch("Be sad. Someone died", page1 -> {
                                    page1.setText("Apologies sir. I did not mean to undermine the situation.");
                                    page1.setBranch("What do you know?", page2 -> {
                                        page2.setText("I saw someone suspicious wearing " + clueInfo + " shoes.");
                                        page2.setBranch("I see", page3 -> {
                                            page3.setText("I hope you catch him sir.");
                                            page3.setOnPageClosed(() -> {
                                                giveClue();
                                                setDialogueStage(JUST_GAVE_CLUE);
                                            });
                                        });
                                    });
                                });
                        } else {
                            yield null;
                        }
                    }
                };
            }
            case JUST_GAVE_CLUE -> new DialogueBook("You again? I already told you everything I know. Can I go now?")
                    .setBranch("No", page1 -> {
                        page1.setText("Ugh.");
                        page1.setOnPageClosed(() -> {
                            setDialogueStage(FED_UP);
                        });
                    });
            case FED_UP -> { 
                yield new DialogueBook("Stop talking to me!")
                    .setBranch("Why?", page1 -> {
                        page1.setText("You're annoying!");
                        page1.setOnPageClosed(() -> {
                            setDialogueStage(NON_RESPONDENT);
                        });
                    });
            }
            case NON_RESPONDENT -> new DialogueBook(".....")
                    .setBranch("Hello?", page1 -> {
                        page1.setText("..........");
                    });
            default -> new DialogueBook("Default text.");
        };
    }

}
