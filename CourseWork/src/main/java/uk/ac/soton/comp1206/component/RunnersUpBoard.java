package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import uk.ac.soton.comp1206.game.Grid;

/**
 * RunnersUpBoard extends GameBoard and removes some functionality to limit interaction
 * to be more suited for displaying an online players Board
 *
 * @see GameBoard
 */
public class RunnersUpBoard extends GameBoard{
    public RunnersUpBoard(Grid grid, double width, double height) {
        super(grid, width, height);
    }

    // Removed hover functionally of a runners ups board
    @Override
    protected void hoverMoved() {
    }

    // Removed blockClicked functionally of a runners ups board
    @Override
    protected void blockClicked(MouseEvent event, GameBlock block) {
    }
}
