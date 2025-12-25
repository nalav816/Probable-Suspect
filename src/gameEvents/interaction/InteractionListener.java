package gameEvents.interaction;

public interface InteractionListener {
    public default void docInstructionGiven(){}
    public default void autopsyConducted(){}
    public default void allCluesCollected(){}
}
