package gameObjects;

import javax.swing.JLayeredPane;

import gameGUI.GameImage;
import gameMath.Vector;

public class Light extends GameObject {
    private GameImage model;

    public Light(int posX, int posY){
        this.model = new GameImage("assets/art/light.png", new Vector(posX, posY));
    }

    @Override
    public Integer getLayer(){
        return JLayeredPane.POPUP_LAYER;
    }

    public GameImage[] getModel(){
        return new GameImage[]{model};
    }
}
