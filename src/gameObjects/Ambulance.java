package gameObjects;

import gameGUI.GameImage;
import gameMath.Vector;


public class Ambulance extends GameObject{
    private GameImage model;
   
    public Ambulance(int posX, int posY){
        this.model = new GameImage("assets/art/ambulance.png", new Vector(posX, posY));
    }

    public GameImage[] getModel(){
        return new GameImage[]{model};
    } 
}
