package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.Set;

/**
 * The LineCleared Listener is used for listening for when a Set of Game Blocks are cleared in a Game
 */
public interface LineClearedListener {

    /**
     * Handle an lineCleared from Game logic
     * @param gameBlockCoordinateSet the Set of GameBlockCoordinate that were cleared from the board
     */
    public void lineCleared ( Set<GameBlockCoordinate> gameBlockCoordinateSet );

}
