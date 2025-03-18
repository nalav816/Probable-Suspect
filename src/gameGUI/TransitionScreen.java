package gameGUI;

import javax.swing.JLayeredPane;

import game.Game;
import gameEvents.interpolation.InterpolationListener;
import gameMath.Vector;
import gameMath.Interpolation;
import gameMath.interpolationEnums.*;

public class TransitionScreen extends GameImage {
    private double effectSpeed;
    private Interpolation<Double> fadeIn, fadeOut;

    public interface OnFadeInCompletion {
        public void run();
    }

    public interface OnEffectCompletion {
        public void run();
    }

    public TransitionScreen(double speed, OnFadeInCompletion onFadeInCompletion){
        this(speed, onFadeInCompletion, null);
    }

    public TransitionScreen(double speed, OnFadeInCompletion onFadeInCompletion, OnEffectCompletion onEffectCompletion){
        super("assets/art/transitionScreen.png", new Vector(0,0));
        setTransparency(0.0);
        this.effectSpeed = speed;
        this.fadeIn = new Interpolation<Double>((a) -> setTransparency((Double) a), 0.0, 1.0, EasingDirection.OUT, effectSpeed/2);
        this.fadeOut = new Interpolation<Double>((a) -> setTransparency((Double) a), 1.0, 0.0, EasingDirection.IN, effectSpeed/2);
        fadeIn.setListener(new InterpolationListener(){
            @Override
            public void finished(){
                
                if(onFadeInCompletion != null){onFadeInCompletion.run();}
                fadeOut.play();
               
            }
        });

        fadeOut.setListener(new InterpolationListener() {
            @Override
            public void finished(){
                if(onEffectCompletion != null){ onEffectCompletion.run();}
                Game.getActiveGame().remove(TransitionScreen.this);
            }
        });
    }

    @Override
    public void playEffectOnAdd(){
        fadeIn.play();
    }

    @Override
    public Integer getLayer(){
        return JLayeredPane.DRAG_LAYER;
    }

}
