package gameGUI;

import javax.swing.JLayeredPane;


import gameMath.Vector;
import gameObjects.Character;

public class DialogueTag extends Tag {
    public DialogueTag(Character tagged, String tagDirectory){
        super(tagged, tagDirectory, new Vector(-5,  tagged.getHeight() - 43));
    }

    @Override
    public Integer getLayer(){
        return JLayeredPane.MODAL_LAYER;
    }
}