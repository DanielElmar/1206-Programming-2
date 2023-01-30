package uk.ac.soton.comp1206.event;

import javafx.scene.input.MouseEvent;

/**
 * The MouseEvent Listener is used for listening to right clicks on objects
 */
public interface RightClickedListener {

    /**
     * Handle an right click on a javafx obj
     * @param event the Mouse Event that was received
     */
    public void rightClicked (MouseEvent event);

}
