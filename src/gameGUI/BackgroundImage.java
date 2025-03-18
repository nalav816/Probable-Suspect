package gameGUI;

import javax.swing.JLayeredPane;


import gameMath.*;
import gameMath.interpolationEnums.*;




public class BackgroundImage extends GameImage {
    public BackgroundImage(String fileName, Vector position){
        super(fileName, position);
       // Interpolation<Double> interpolation = new Interpolation<Double>((a) -> setTransparency((Double) a), (Double)alpha, Double.valueOf(0), EasingDirection.OUT, 10);
        // interpolation.play();
    }

    @Override
    public Integer getLayer(){
        return JLayeredPane.DEFAULT_LAYER;
    }

}
