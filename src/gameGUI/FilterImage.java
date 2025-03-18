package gameGUI;

import javax.swing.JLayeredPane;
import gameMath.Vector;

public class FilterImage extends GameImage{
    public FilterImage(String fileName, Vector position){
        super(fileName, position);
    }

    @Override
    public Integer getLayer(){
        return JLayeredPane.POPUP_LAYER;
    }
}
