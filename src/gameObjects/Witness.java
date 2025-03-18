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
            loadDialogues();
            dialoguesLoaded = true;
        }
    }

    @Override
    public void giveClue(){
        super.giveClue();
        newDialogueNeeded = true;
        dialogueStage = JUST_GAVE_CLUE_STAGE;
    }

    @Override
    public DialogueBook chooseDialogue() {
        Random numGen = new Random();
        switch (dialogueStage) {
            case CLUE_GIVING_STAGE:
                DialogueBook[] possibilities = clueGivingDialogues.get(clue.getType());
                DialogueBook chosen = possibilities[numGen.nextInt(possibilities.length)];
                chosen.initializeClueDialogues(this);
                return chosen;
            case JUST_GAVE_CLUE_STAGE:
                newDialogueNeeded = true;
                dialogueStage = FED_UP_STAGE;
                return justGaveClueDialogues[numGen.nextInt(justGaveClueDialogues.length)];
            case FED_UP_STAGE:
                newDialogueNeeded = true;
                dialogueStage = NON_RESPONDENT_STAGE;
                return fedUpDialogues[numGen.nextInt(fedUpDialogues.length)];
            case NON_RESPONDENT_STAGE:
                return nonRespondentDialogues[numGen.nextInt(nonRespondentDialogues.length)];
            default:
                return null;
        }
    }

    /// DIALOGUE LIBRARY ///
    private static HashMap<ComponentType, DialogueBook[]> clueGivingDialogues = new HashMap<ComponentType, DialogueBook[]>();
    private static DialogueBook[] skinClueDialogues = new DialogueBook[1];
    private static DialogueBook[] shirtClueDialogues = new DialogueBook[1];
    private static DialogueBook[] shoeClueDialogues = new DialogueBook[1];
    private static DialogueBook[] justGaveClueDialogues = new DialogueBook[1];

    private static DialogueBook[] fedUpDialogues = new DialogueBook[1];
    private static DialogueBook[] nonRespondentDialogues = new DialogueBook[1];

    private static void loadDialogues() {
        // Skin Clue Dialogues
        // 1
        DialogueBook dbook = new DialogueBook("Hi detective! How are you doing?");
        Page A = dbook.add(dbook.getFirst(), "That's good to hear. What can I do for you?", "Good");
        Page AA = dbook.add(A,
                "I don't know much, but I saw someone running away. He had a INSERTCLUEHERE skin complexion.",
                "Tell me what you know.");
        Page AAA = dbook.add(AA, "No problem. Good luck with your investigation!", "Thank you");
        Page B = dbook.add(dbook.getFirst(), "That's unfortunate to hear. Would telling you what I know cheer you up?",
                "Not great");
        Page BA = dbook.add(B,
                "I don't know much, but I saw someone running away. He had a INSERTCLUEHERE skin complexion.",
                "Yeah");
        Page BAA = dbook.add(BA, "No worries. Good luck with the rest of your investigation.", "Thank you");
        skinClueDialogues[0] = dbook;

        // Shirt Clue Dialogues
        // 1
        dbook = new DialogueBook("Leave me alone.");
        A = dbook.add(dbook.getFirst(), "What do you want?", "No");
        AA = dbook.add(A, "Fine. I saw the guy you're looking for, he was wearing a INSERTCLUEHERE shirt.", "Information");
        AAA = dbook.add(AA, "Now go away!", "Appreciate it");
        B = dbook.add(dbook.getFirst(), "What are you still in my face for?", "Ok");
        shirtClueDialogues[0] = dbook;

        // Shoe Clue Dialogues
        // 1
        dbook = new DialogueBook("Sup?");
        A = dbook.add(dbook.getFirst(), "I was nearby when it happened. I could only make out the murderer's shoes. They were INSERTCLUEHERE.", "You know anything?");
        AA = dbook.add(A, "Anytime.", "Thanks");
        shoeClueDialogues[0] = dbook;

        clueGivingDialogues.put(ComponentType.SKINCOLOR, skinClueDialogues);
        clueGivingDialogues.put(ComponentType.SHIRT, shirtClueDialogues);
        clueGivingDialogues.put(ComponentType.SHOES, shoeClueDialogues);
        //Jut Gave Clue Dialogues
        //1
        dbook = new DialogueBook("You again? I already told you everything I know. Can I go now?");
        A = dbook.add(dbook.getFirst(), "Ugh.", "No");
        justGaveClueDialogues[0] = dbook;

        //Fed Up Dialogues
        //1
        dbook = new DialogueBook("Stop talking to me!");
        fedUpDialogues[0] = dbook;

        //Non Respondent Dialogues
        dbook = new DialogueBook(".....");
        A = dbook.add(dbook.getFirst(), "........", "Hello?");
        nonRespondentDialogues[0] = dbook;
    }
}
