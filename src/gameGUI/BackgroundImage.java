package gameGUI;

import javax.swing.JLayeredPane;

import gameMath.*;

public class BackgroundImage extends GameImage {
    public BackgroundImage(String fileName, Vector position){
        super(fileName, position);
    }

    @Override
    public Integer getLayer(){
        return JLayeredPane.DEFAULT_LAYER;
    }

}
