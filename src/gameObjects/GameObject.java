package gameObjects;

import javax.swing.JLayeredPane;

import gameGUI.GameImage;
import gameGUI.Renderable;

public abstract class GameObject implements Renderable {
 
    public void tick(){
        for(GameImage i : getModel()){
            i.tick();
        }
    }

    public void render(){
        for(GameImage i : getModel()){
            i.render();
        }
    }

    public Integer getLayer(){
        return JLayeredPane.PALETTE_LAYER;
    }

    public abstract GameImage[] getModel();
    
    //works but slow. Either use parallel threads or switch to bounding boxes
    public boolean collidedWith(GameObject other){
        for(GameImage img : getModel()){
            for(GameImage otherImg : other.getModel()){
                if(otherImg.getBoundingBox().intersect(img.getBoundingBox())){
                    return true;
                }
            }
        }
        return false;
    };
}
