package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * @param x location on board of the centre of the piece to play
     * @param y location on board of the centre of the piece to play
     * @param gamePiece the piece to play
     * @return returns a Boolean weather this piece can be played at this location without violating game rules
     */
    public boolean canPlayPiece(int x, int y, GamePiece gamePiece){

        var blocks = gamePiece.getBlocks();

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {

                try {
                    // checks if a block of this GamePiece colides with a block on the Grid that isn't empty
                    if (grid[x - 1 + j][y - 1 + i].get() != 0 && blocks[i][j] != 0) {

                        logger.info("Cant Play Piece Here");
                        return false;
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    // Checks if index outside the GameBoard is apart of the Game Piece or not
                    if (blocks[i][j] != 0){ logger.info("Cant Play Piece Here"); return false; };
                }
            }
        }
        return true;
    }

    /**
     * plays a piece on the grid with the given parameters
     * @param x location on board of the centre of the piece to play
     * @param y location on board of the centre of the piece to play
     * @param gamePiece the piece to play
     */
    public void playPiece(int x, int y, GamePiece gamePiece){
        var blocks = gamePiece.getBlocks();

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                // any blocks in teh given GamePiece that arent empty are added to the grid
                if (blocks[i][j] != 0) {
                    grid[x - 1 + j][y - 1 + i].set(blocks[i][j]);
                }
            }
        }
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value or -1 if no such index
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

}
