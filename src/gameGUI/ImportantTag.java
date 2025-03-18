package gameGUI;

import gameObjects.Character;

public class ImportantTag extends DialogueTag{
    public ImportantTag(Character tagged){
        super(tagged, "assets/art/ui/importantIndicator.png");
    }
}
