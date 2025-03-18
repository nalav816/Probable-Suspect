package gameGUI;

import gameObjects.Character;

public class ClueTag extends DialogueTag{
    public ClueTag(Character tagged){
        super(tagged, "assets/art/ui/clueIndicator.png");
    }
}
