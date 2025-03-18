package gameEvents.interaction;

import java.util.ArrayList;

import game.Game;
import gameGUI.Dialogue;

//Handles events related to player game interaction
public class InteractionHandler {
    private static ArrayList<InteractionListener> listeners = new ArrayList<InteractionListener>();

    public static void fireDocInstructionGiven() {
        for (InteractionListener i : listeners) {
            i.docInstructionGiven();
        }
    }

    public static void fireAutopsyConducted() {
        for (InteractionListener i : listeners) {
            i.autopsyConducted();
        }
    }

    public static void fireAllCluesCollected() {
        // Waits until dialogue is closed (if it is opened) before firing event
        Thread t = new Thread(() -> {
            while (Dialogue.getActiveDialogue() != null) {
                try {
                    Thread.sleep(1000 / Game.FRAME_RATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (InteractionListener i : listeners) {
                 i.allCluesCollected();
            }
        });

        t.start();
    }

    public static void addListener(InteractionListener li) {
        listeners.add(li);
    }

    public static void removeListener(InteractionListener li) {
        listeners.remove(li);
    }

}
