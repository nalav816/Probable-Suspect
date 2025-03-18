package gameEvents.interaction;

public interface InteractionListener {
    default void docInstructionGiven(){}
    default void autopsyConducted(){}
    default void allCluesCollected(){}
}
