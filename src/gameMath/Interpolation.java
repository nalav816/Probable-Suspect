package gameMath;

import java.util.concurrent.CopyOnWriteArrayList;

import game.Game;
import gameEvents.interpolation.InterpolationListener;
import gameMath.interpolationEnums.EasingDirection;

public class Interpolation<V> {
    public static Thread interpolationThread = null;
    public static CopyOnWriteArrayList<Interpolation<?>> activeInterpolations = new CopyOnWriteArrayList<Interpolation<?>>();

    private InterpolationListener listener;
    private SetterFunction setFunction;
    private EasingDirection direction;
    private V start, goal;
    private double speed, progress;
    private boolean finished, looped;

    public interface SetterFunction {
        public void set(Object value);
    }

    public Interpolation(SetterFunction setProperty, V start, V goal, EasingDirection direction, double speed) {
        this.setFunction = setProperty;
        this.start = start;
        this.goal = goal;
        this.speed = speed;
        this.progress = 0;
        this.finished = false;
        this.listener = null;
        this.direction = direction;
    }

    public void play() {
        activeInterpolations.add(this);
        if (interpolationThread == null || !interpolationThread.isAlive()) {
            interpolationThread = new Thread(() -> run());
            interpolationThread.start();
        }
    }

    public void stop() {
        activeInterpolations.remove(this);
    }

    public double getProgress(){
        return progress;
    }

    public void setProgress(double newProgress){
        synchronized(this){
            this.progress = newProgress;
        }
    }

    public void setNewParams(V newStart, V newGoal, double newSpeed){
        start = newStart;
        goal = newGoal;
        speed = newSpeed;
        progress = 0;
    }

    public V getGoal() {
        return goal;
    }

    public void setListener(InterpolationListener listener) {
        this.listener = listener;
    }

    public boolean isPlaying(){
        return activeInterpolations.contains(this);
    }

    public Vector lerp(Vector a, Vector b) {
        return new Vector(lerp(a.getX(), b.getX()), (int) lerp(a.getY(), b.getY()));
    }

    public double lerp(double a, double b) {
        double interpolated = a + (b - a) * quadraticEasing();
        if (progress != 1) {
            increment();
        } else if(!finished) {
            finished = true;
        }
        return interpolated;
    }

    private void increment() {
        synchronized(this){
            progress += (1.0 / Game.FRAME_RATE) / speed;
            progress = Math.min(progress, 1);
        }
    }

    private double quadraticEasing() {
        switch (direction) {
            case IN:
                return Math.pow(progress, 2);
            case OUT:
                return 1 - Math.pow((1 - progress), 2);
            case INOUT:
                return progress < .5 ? 2 * Math.pow(progress, 2) : 1 - 2 * Math.pow((1 - progress), 2);
            default:
                return progress;
        }
    }

    private static void run() {
        while (activeInterpolations.size() > 0) {
            try {
                Thread.sleep(1000 / Game.FRAME_RATE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(Interpolation<?> interpolation : activeInterpolations){
                Object startValue = interpolation.start;
                Object goalValue = interpolation.goal;
                if (startValue instanceof Vector) {
                    interpolation.setFunction.set(interpolation.lerp((Vector) startValue, (Vector) goalValue));
                } else if (startValue instanceof Number) {
                    interpolation.setFunction.set(interpolation.lerp((double) startValue, (double) goalValue));
                }
                if(interpolation.finished){
                    if(interpolation.listener != null){ interpolation.listener.finished(); }
                    activeInterpolations.remove(interpolation);
                }
            }
        }
    }

}
