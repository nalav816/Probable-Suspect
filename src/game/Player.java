package game;

import java.awt.Point;
import java.awt.MouseInfo;

import gameEvents.interaction.InteractionHandler;
import gameObjects.CharComponent;
import gameMath.Vector;

import java.util.ArrayList;

public class Player {
    private static Player currentPlayer;

    private Game game;
    private ArrayList<CharComponent> obtainedClues;

    public Player(Game g) {
        this.game = g;
        this.obtainedClues = new ArrayList<CharComponent>();
        
    }

    public void obtainClue(CharComponent clue){
        obtainedClues.add(clue);
        if(obtainedClues.size() == Game.getActiveGame().getAvailableClueCount()){
            InteractionHandler.fireAllCluesCollected();
        }
    }

    public Vector getMouseLocation() {
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        game.toGameCoordinates(mousePoint);
        return new Vector(mousePoint.getX(), mousePoint.getY());
    }

    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static void setCurrentPlayer(Player newPlayer) {
        currentPlayer = newPlayer;
    }

}
