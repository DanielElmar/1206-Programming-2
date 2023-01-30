package uk.ac.soton.comp1206.event;

import javafx.scene.input.MouseEvent;

/**
 * The GameLoop Listener is used for listening for when a game loop is triggered in a Game
 */
public interface GameLoopListener {

    /**
     * Handle an gameLoop triggered by the game loop Timer in a Game
     * @param newDelay the message that was received
     * @param NoOfLives the number of lives the player has
     */
    public void gameLoop ( int newDelay, int NoOfLives);

}
