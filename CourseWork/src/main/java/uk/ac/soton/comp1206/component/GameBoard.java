package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.Grid;

import java.util.Set;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;
    private RightClickedListener rightClickedListener;

    // SimpleIntegerProperty holds the X and Y coordinate of the current hovered block, allow for binding and listeners
    private final SimpleIntegerProperty hoveredBlockX = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty hoveredBlockY = new SimpleIntegerProperty(0);

    private GameBlock currentHover;
    private  GameBlock previousHover;

    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}",cols,rows);

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        // populates Board with GameBlocks
        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x,y);
            }
        }
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;


        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block,x,y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> {
            if (e.getButton() == MouseButton.PRIMARY){ blockClicked(e, block); }
            if (e.getButton() == MouseButton.SECONDARY){ rightClicked(e); }
        });

        //Add a mouse move listener to the block to trigger GameBoard hoverMoved method
        block.setOnMouseMoved((e)->{ hoveredBlockX.set(block.getX()); hoveredBlockY.set(block.getY()); });
        hoveredBlockX.addListener(((observableValue, oldValue, newValue) -> { hoverMoved(); }));
        hoveredBlockY.addListener(((observableValue, oldValue, newValue) -> { hoverMoved(); }));

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Uses currentHover and previousHover to clear the previous hovered Block and paint a hover on the new hovered Block
     */
    protected void hoverMoved(  ){

        // Clear old Hover
        try{
            previousHover.paint();
        }catch (NullPointerException e){}

        // Paint new Hover
        blocks[hoveredBlockX.get()][hoveredBlockY.get()].paintHovered();

        previousHover = currentHover;
        currentHover = blocks[hoveredBlockX.get()][hoveredBlockY.get()];
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    protected void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);

        if(blockClickedListener != null) {
            blockClickedListener.blockClicked(block);
        }
    }

    /**
     * Sets a rightClickedListener to to triggered on a right click of the GameBoard
     * @param listener the RightClickedListener to be triggered
     */
    public void setOnRightClick(RightClickedListener listener) {
        this.rightClickedListener = listener;
    }

    /**
     * triggered on a right click of the GameBoard, and triggers rightClickedListener if set
     * @param event MouseEvent that triggered this event
     */
    private void rightClicked(MouseEvent event ) {
        logger.info("Right click triggered on Board");

        if(rightClickedListener != null) {
            rightClickedListener.rightClicked(event);
        }
    }

    /**
     * Set the grid value to 0 and triggers the fadeOut() animation for every GameBlockCoordinate given
     * @param gameBlockCoordinateSet a Set of GameBlockCoordinate to be cleared from the grid and GameBoard
     */
    public void fadeOut( Set<GameBlockCoordinate> gameBlockCoordinateSet ){

        logger.info("Fading out Cleared Blocks");
        // get gid to 0 and call dafeOut() on every Block
        for ( GameBlockCoordinate gameCoordinate : gameBlockCoordinateSet ) {
            grid.set( gameCoordinate.getX(), gameCoordinate.getY(), 0);
            blocks[gameCoordinate.getX()][gameCoordinate.getY()].fadeOut();
        }
    }

    /**
     * @return returns a SimpleIntegerProperty representing the X value of the current hovered Block
     */
    public SimpleIntegerProperty getHoveredBlockX() {
        return hoveredBlockX;
    }

    /**
     * @return returns a SimpleIntegerProperty representing the Y value of the current hovered Block
     */
    public SimpleIntegerProperty getHoveredBlockY() {
        return hoveredBlockY;
    }
}
