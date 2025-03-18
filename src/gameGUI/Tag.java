package gameGUI;

import javax.swing.JLayeredPane;

import gameMath.Vector;
import gameObjects.Character;

public abstract class Tag extends GameImage {
    protected Character tagged;

    public Tag(Character tagged, String imgDirectory, Vector position){
        super(imgDirectory, tagged.getPosition().add(position));
        this.tagged = tagged;
    }

    @Override
    public Integer getLayer(){
        return JLayeredPane.POPUP_LAYER;
    }



}
