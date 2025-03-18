package game;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import gameGUI.*;
import gameMath.*;
import gameObjects.*;
import gameObjects.CharComponent.ComponentType;
import gameObjects.Character;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.util.Random;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Game {
    public final static Object mainThreadLock = new Object();
    public final static int FRAME_RATE = 60;

    // Game assets are pixel art so it needs to be scaled up so it's playable
    public final static int SCALE = 3;
    public final static int SCENE_FLOOR_HEIGHT = 26;
    public final static Vector SCENE_SIZE = new Vector(160, 90);

    // Game renderables constatns
    private final static int CHARACTERS = 0;
    private final static int MISC_GAME_OBJECTS = 2;
    private final static int USER_INTERFACE = 3;

    private static Game activeGame;

    private JFrame gameFrame;
    private JLayeredPane gameLayers;
    // private GameImage currentBackground;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Renderable>> renderables;
    private HashMap<ComponentType, CharComponent> availableClues;
    private GameState state;

    public Game() throws Exception {
        activeGame = this;
        this.gameFrame = new JFrame();
        this.gameLayers = new JLayeredPane();
        this.renderables = new CopyOnWriteArrayList<CopyOnWriteArrayList<Renderable>>();
        this.availableClues = new HashMap<ComponentType, CharComponent>();
        this.state = GameState.TITLE;
        SwingUtilities.invokeLater(() -> {
            //gameFrame.setUndecorated(true);
            gameFrame.setResizable(false);
            gameFrame.setLayout(null);
            gameFrame.setVisible(true);
            gameFrame.setSize((int)SCENE_SIZE.getX() * SCALE + gameFrame.getInsets().left + gameFrame.getInsets().right,
            (int)SCENE_SIZE.getY() * SCALE + gameFrame.getInsets().top + gameFrame.getInsets().bottom);
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (state == GameState.TITLE) {
                            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    startTransitionPhase(1.5, () -> {state = GameState.INTRODUCTIONPHASE;});
                                    startGame();
                                    return null;
                                }
                            };

                            worker.execute();
                        } else if (state == GameState.WINLOSS) {
                            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    startTransitionPhase(1.5, () -> {state = GameState.TITLE;});
                                    clearScene();
                                    add(new BackgroundImage("assets/art/scenes/titleScene.png", new Vector(0, 0)));
                                    return null;
                                }

                            };

                            worker.execute();
                        }
                    }
                }
            });
            gameLayers.setLayout(null);
            gameLayers.setSize(gameFrame.getSize());
            gameFrame.add(gameLayers);
        });

        for (int i = 0; i < 4; i++) {
            renderables.add(new CopyOnWriteArrayList<Renderable>());
        }
        add(new BackgroundImage("assets/art/scenes/titleScene.png", new Vector(0, 0)));
        Player.setCurrentPlayer(new Player(this));
    }

    private void startGame() {
        clearScene();
        generateClues();
        loadCharacters();
        add(new BackgroundImage("assets/art/scenes/mainScene.png", new Vector(0, 0)));
        add(new Ambulance(0, 39));
        add(new Light(27, 10));
        tick();
    }

    private void tick() {
        while (state != GameState.TITLE && state != GameState.WINLOSS) {
            try {
                Thread.sleep(1000 / FRAME_RATE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (this) {
                // Using iterator because objects might add or remove others within this loop
                for (CopyOnWriteArrayList<Renderable> renderGroups : renderables) {
                    for (Renderable r : renderGroups) {
                        r.tick();
                    }
                }
            }
        }
    }

    public CopyOnWriteArrayList<Renderable> getGameCharacters() {
        return renderables.get(CHARACTERS);
    }

    public CopyOnWriteArrayList<Renderable> getMiscGameObjects() {
        return renderables.get(MISC_GAME_OBJECTS);
    }

    public CopyOnWriteArrayList<Renderable> getGameUI() {
        return renderables.get(USER_INTERFACE);
    }

    public int getAvailableClueCount() {
        return availableClues.size();
    }

    public HashMap<ComponentType, CharComponent> getAvailableClues() {
        return availableClues;
    }

    public JFrame getMainFrame() {
        return gameFrame;
    }

    public GameState getState() {
        return state;
    }

    public void toGameCoordinates(Point screenCoordinates) {
        SwingUtilities.convertPointFromScreen(screenCoordinates, gameLayers);
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void add(GameImage UI) {
        
        add(UI, UI.getLayer());
    }

    public void add(GameImage UI, Integer layer) {
        CopyOnWriteArrayList<Renderable> userInterface = renderables.get(USER_INTERFACE);
        userInterface.add(UI);
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> {
                UI.render();
                UI.playEffectOnAdd();
                for (JComponent j : UI.getJComponents()) {
                    gameLayers.add(j, layer);
                }

                gameFrame.revalidate();
                gameFrame.repaint();
            });
        } else {
            UI.render();
            UI.playEffectOnAdd();
            for (JComponent j : UI.getJComponents()) {
                gameLayers.add(j, layer);
            }
            gameFrame.revalidate();
            gameFrame.repaint();
        }
    }

    public void add(GameObject g) {
        CopyOnWriteArrayList<Renderable> objects = g instanceof Character ? renderables.get(CHARACTERS)
                : renderables.get(MISC_GAME_OBJECTS);
        objects.add(g);
        for (GameImage img : g.getModel()) {
            add(img, g.getLayer());
        }
    }

    public void remove(GameImage UI) {
        CopyOnWriteArrayList<Renderable> userInterface = renderables.get(USER_INTERFACE);
        userInterface.remove(UI);
        for (JComponent j : UI.getJComponents()) {
            gameLayers.remove(j);
        }
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> {
                UI.render();
                for (JComponent j : UI.getJComponents()) {
                    gameLayers.remove(j);
                }
            });
            gameFrame.revalidate();
            gameFrame.repaint();
        } else {
            UI.render();
            for (JComponent j : UI.getJComponents()) {
                gameLayers.remove(j);
            }
            gameFrame.revalidate();
            gameFrame.repaint();
        }
    }

    public void remove(GameObject g) {
        CopyOnWriteArrayList<Renderable> objects = g instanceof Character ? renderables.get(CHARACTERS)
                : renderables.get(MISC_GAME_OBJECTS);
        objects.remove(g);
        for (GameImage img : g.getModel()) {
            remove(img);
        }
    }

    public void clearScene() {
        for (CopyOnWriteArrayList<Renderable> renderGroup : renderables) {
            for (Renderable r : renderGroup) {
                if (r instanceof GameObject) {
                    remove((GameObject) r);
                } else if (r instanceof GameImage && !(r instanceof TransitionScreen)) {
                    remove((GameImage) r);
                }
            }
        }
    }

    public void startTransitionPhase(double speed, TransitionScreen.OnEffectCompletion completionEvent){
        state = GameState.TRANSITION;
        add(new TransitionScreen(speed, () -> {
            synchronized(mainThreadLock){
                mainThreadLock.notify();
            }
        }, completionEvent));

        synchronized(mainThreadLock){
            try{
                mainThreadLock.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startInteractionPhase() {
        state = GameState.INTERACTIONPHASE;
        Random numGen = new Random();
        ArrayList<CharComponent> cluesToGive = new ArrayList<CharComponent>();
        cluesToGive.add(availableClues.get(ComponentType.SKINCOLOR));
        cluesToGive.add(availableClues.get(ComponentType.SHIRT));
        cluesToGive.add(availableClues.get(ComponentType.SHOES));

        // Load clues onto witnesses
        for (Renderable character : getGameCharacters()) {
            if (character instanceof Witness) {
                Witness witness = (Witness) character;
                int chosen = numGen.nextInt(cluesToGive.size());
                witness.setClue(cluesToGive.get(chosen));
                cluesToGive.remove(chosen);
            } else if (character instanceof Doctor) {
                Doctor doc = (Doctor) character;
                doc.setImportantInfo(availableClues.get(ComponentType.MURDERWEAPON));
            }
        }
    }

    public void startLineupPhase() {
        Random numGen = new Random();
        clearScene();
        add(new BackgroundImage("assets/art/scenes/lineupScene.png", new Vector(0, 0)));
        add(new FilterImage("assets/art/scenes/lineupForeground.png", new Vector(0, 0)));
        Integer[] positions = { 0, 1, 2, 3, 4 };
        ArrayList<Integer> possiblePositions = new ArrayList<Integer>(Arrays.asList(positions));
        for (int i = 1; i <= 5; i++) {
            int roll = numGen.nextInt(possiblePositions.size());
            add(new Suspect(46 + 12 * possiblePositions.get(roll), 48, i, possiblePositions.get(roll) + 1));
            possiblePositions.remove(roll);
        }
    }

    public void endLineupPhase(Suspect choice) {
        clearScene();
        if (won(choice)) {
            add(new BackgroundImage("assets/art/scenes/winScene.png", new Vector(0, 0)));
        } else {
            add(new BackgroundImage("assets/art/scenes/loseScene.png", new Vector(0, 0)));
        }
    }

    public boolean won(Suspect choice) {
        HashMap<ComponentType, CharComponent> features = choice.getFeatures();
        for (ComponentType t : features.keySet()) {
            if (!features.get(t).equals(availableClues.get(t))) {
                return false;
            }
        }
        return true;
    }

    private void generateClues() {
        for (ComponentType c : ComponentType.values()) {
            CharComponent clue = new CharComponent(c, "Suspect");
            availableClues.put(c, clue);
            System.out.println(clue.getType() + " " + clue.getVariation().getName());
        }
    }

    private void loadCharacters() {
        add(new Victim(142, 48));
        add(new Detective(61, 47, availableClues.get(ComponentType.NAME)));
        for (int i = 0; i < 3; i++) {
            add(new Witness(104 + i * 12, 48));
        }
        add(new Doctor(42, 46));
    }

    public static Game getActiveGame() {
        return activeGame;
    }
}
