package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;


import java.util.*;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    private static final Random random = new Random();

    // SimpleIntegerProperty that hold game variables allow binding and listeners
    private final SimpleIntegerProperty score = new SimpleIntegerProperty();
    private final SimpleIntegerProperty multiplier = new SimpleIntegerProperty();
    private final SimpleIntegerProperty lives = new SimpleIntegerProperty();
    private final SimpleIntegerProperty level = new SimpleIntegerProperty();

    protected GamePiece currentPiece;
    private GamePiece followingPiece;

    // Multimedia classes
    private final Multimedia musicPlayer = new Multimedia();
    private final Multimedia audioPlayer = new Multimedia();

    private final ArrayList<NextPieceListener> nextPieceListeners = new ArrayList<NextPieceListener>();
    private final ArrayList<LineClearedListener> lineClearedListeners = new ArrayList<LineClearedListener >();

    // SimpleIntegerProperty holds the X and Y coordinate of the current hovered block, allow for binding and listeners
    private final SimpleIntegerProperty hoveredBlockX = new SimpleIntegerProperty(-1);
    private final SimpleIntegerProperty hoveredBlockY = new SimpleIntegerProperty(-1);

    protected Timer gameTimer;
    private GameLoopListener gameLoopListener;

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");

        // set up initial state of the game variables
        followingPiece = spawnPiece();
        nextPiece();

        score.set(0);
        multiplier.set(1);
        lives.set(3);
        level.set(0);

        // play music
        musicPlayer.playMusic("game_start.wav");

        // set up a scheduled task to trigger the game loop
        TimerTask gameLoopTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    logger.info("GameLoopTask Triggered");
                    gameLoop();
                });
            }
        };

        gameTimer = new Timer("FIRST Timer");
        gameTimer.schedule(gameLoopTask,getTimerDelay(), getTimerDelay());
    }


    /**
     * triggered when the game timer runs down, updates game values and triggers the gameLoopListener
     */
    private void gameLoop(){
        logger.info("Game Loop");

        // adjust game variables, play music, trigger GameLoopListener and get next piece
        lives.set( lives.get() - 1 );
        audioPlayer.playAudio("lifelose.wav");
        nextPiece();
        multiplier.set( 1 );
        if ( gameLoopListener != null){ gameLoopListener.gameLoop( getTimerDelay(), lives.get() ); }
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        // check if this piece can be played here
        var playable = grid.canPlayPiece(x,y, currentPiece);

        if (playable){
            grid.playPiece(x,y, currentPiece);
            nextPiece();
            // adjust scored and multipliers
            afterPiece();
        }
    }

    /**
     * Handles when the enter key is pressed by checking if the current piece can be played in this location
     */
    public void enterPressed() {
        //Get the position of this block
        int x = hoveredBlockX.get();
        int y = hoveredBlockY.get();

        // check if this piece can be played here
        var playable = grid.canPlayPiece(x,y, currentPiece);

        if (playable){
            grid.playPiece(x,y, currentPiece);
            nextPiece();
            // adjust scored and multipliers
            afterPiece();
        }
    }

    /**
     * handles game logic after a piece is played, scores, audio, multipliers, Timer resets,
     */
    public void afterPiece(){

        logger.info("After Piece Triggered");

        audioPlayer.playAudio("place.wav");

        // need to store what rows/cols have been completes before deletion to allow for multiple rows/cols to be cleared at once
        ArrayList<Integer> completedRows = new ArrayList<Integer>();
        ArrayList<Integer> completedCols = new ArrayList<Integer>();
        var clearedBlocks = 0;
        int completedLines = 0;


        for (int i = 0; i < 5; i++) {
            // assumes a row and column has been completed for this i value
            var isRowLine = true;
            var isColLine = true;
            for (int j = 0; j < 5; j++) {
                // if this block is empty a row or column has not been completed
                if ( grid.get(i,j) == 0 ){ isColLine = false; }
                if ( grid.get(j,i) == 0 ){ isRowLine = false; }
            }
            // if the row and or column was completed add it th its respective ArrayList
            if (isColLine){ completedCols.add(i); completedLines++; }
            if (isRowLine){ completedRows.add(i); completedLines++; }

        }

        // get number of cleared blocks
        if (completedRows.size() == 0){
            clearedBlocks = 5 * completedCols.size();
        }else{
            clearedBlocks = (5 * completedRows.size() ) + ( ( 5 - completedRows.size() ) * completedCols.size());
        }

        //score points
        score(completedLines, clearedBlocks);


        if ( completedLines > 1 ){ audioPlayer.playAudio("explode.wav"); }
        else if ( completedLines > 0 ){ audioPlayer.playAudio("clear.wav");}

        // Create a set of GameCoordinates for LineCleared Listener
        Set<GameBlockCoordinate> coordinates = new HashSet<>();
        for (int i = 0; i < 5; i++) {

            for ( int col : completedCols) {
                coordinates.add( new GameBlockCoordinate(col, i) );
            }
            for ( int row : completedRows) {
                coordinates.add( new GameBlockCoordinate(i, row) );
            }
        }

        // call line Cleared Listeners to do fadeout on blocks
        triggerLineClearedListeners( coordinates );


        // Adjust multiplier
        if (completedLines > 0 ){
            multiplier.set( multiplier.get() + 1);
        }else{ multiplier.set(1); }

        // Adjust level
        level.set( score.get() / 1000 );

        // Rest Timer
        gameTimer.cancel();
        gameTimer = new Timer("CLONE TIMER");
        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    logger.info("GameLoopTask clone Triggered");
                    gameLoop();
                });
            }
        }, getTimerDelay(), getTimerDelay());

        if ( gameLoopListener != null){ gameLoopListener.gameLoop( getTimerDelay(), lives.get() ); }


    }

    /**
     * @return returns a new random GamePiece
     */
    public GamePiece spawnPiece(){
        return GamePiece.createPiece(random.nextInt(15));
    }

    /**
     * updates current and following piece and triggers NextPieceListeners
     */
    public void nextPiece(){

        // update current and following pieces and trigger Listeners
        currentPiece = followingPiece;
        followingPiece = spawnPiece();

        triggerNextPieceListeners();
    }

    /**
     * returns the appropriates delay for the Game Loop Timers
     * @return time in milli seconds
     */
    private int getTimerDelay(){
        return (Math.max(2500, 12000 - (500 * level.get())));
    }

    /**
     * triggers all the NextPieceListener in the array nextPieceListeners with the current and following pieces
     */
    private void triggerNextPieceListeners(){
        logger.info("Triggering Next Piece Listeners");
        for( NextPieceListener listener : nextPieceListeners ) {
            listener.nextPiece(currentPiece, followingPiece);
        }
    }

    /**
     * triggers all the LineClearedListener in the array lineClearedListeners with a Set of BlockCoordinates
     * @param coordinates the Set of GameBlockCoordinate of all blocks that were cleared from the grid
     */
    private void triggerLineClearedListeners(Set<GameBlockCoordinate> coordinates){
        logger.info("Triggering Line Cleared Listeners");
        for( LineClearedListener listener : lineClearedListeners ) {
            listener.lineCleared( coordinates );
        }
    }

    /**
     * rotates the current GamePiece anti-clockwise, triggers triggerNextPieceListeners and plays audio
     */
    public void rotateCurrentPieceACW(){
        currentPiece.rotate();
        triggerNextPieceListeners();
        audioPlayer.playAudio("rotate.wav");
    }

    /**
     * rotates the current GamePiece clockwise, triggers triggerNextPieceListeners and plays audio
     */
    public void rotateCurrentPieceCW(){
        currentPiece.rotate();
        currentPiece.rotate();
        currentPiece.rotate();
        triggerNextPieceListeners();
        audioPlayer.playAudio("rotate.wav");
    }

    /**
     * swamps the current and following pieces, triggers triggerNextPieceListeners and plays audio
     */
    public void swapCurrentPiece(){

        var temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece = temp;
        triggerNextPieceListeners();
        audioPlayer.playAudio("pling.wav");
    }

    /**
     * adds a NextPieceListener to the nextPieceListeners array
     * @param listener the NextPieceListener to add
     */
    public void addNextPieceListener(NextPieceListener listener){
        nextPieceListeners.add(listener);
    }

    /**
     * adds a LineClearedListener to the lineClearedListeners array
     * @param listener the LineClearedListener to add
     */
    public void addLineClearedListener( LineClearedListener listener ){ lineClearedListeners.add(listener); }

    /**
     * sets the GameLoopListener to be triggered on every game loop
     * @param listener listener to be triggered on every game loop
     */
    public void setGameLoopListener( GameLoopListener listener ){ gameLoopListener = listener; }

    /**
     * updates the score with the according equation
     * @param noOfLines a variable int he equation that calculates the score to be added
     * @param noOfBlocks a variable int he equation that calculates the score to be added
     */
    public void score(int noOfLines, int noOfBlocks){

        score.set( score.get() + ( multiplier.get() * 10 * noOfLines * noOfBlocks) );
    }

    /**
     * adjusts hoveredBlockX or hoveredBlockY to represent this movement
     */
    public void moveHoverBlockUP(){
        if (hoveredBlockY.get() != 0){ hoveredBlockY.set( hoveredBlockY.get() - 1 ); }
    }

    /**
     * adjusts hoveredBlockX or hoveredBlockY to represent this movement
     */
    public void moveHoverBlockLEFT(){
        if (hoveredBlockX.get() != 0){ hoveredBlockX.set( hoveredBlockX.get() - 1 ) ; }
    }

    /**
     * adjusts hoveredBlockX or hoveredBlockY to represent this movement
     */
    public void moveHoverBlockDOWN(){
        if (hoveredBlockY.get() != 4){ hoveredBlockY.set(  hoveredBlockY.get() + 1 ); }
    }

    /**
     * adjusts hoveredBlockX or hoveredBlockY to represent this movement
     */
    public void moveHoverBlockRIGHT(){
        if (hoveredBlockX.get() != 4){ hoveredBlockX.set( hoveredBlockX.get() + 1 ); }
    }

    /**
     * cancels game Timer stops music
     */
    public void close(){

        logger.info("Canceling Timer");
        gameTimer.cancel();
        // stop Music
        musicPlayer.stopMusic();
    }

    /**
     * @return SimpleIntegerProperty representing the score
     */
    public SimpleIntegerProperty getScoreProperty(){ return score; }

    /**
     * @return SimpleIntegerProperty representing multiplier
     */
    public SimpleIntegerProperty getMultiplierProperty(){ return multiplier; }

    /**
     * @return SimpleIntegerProperty representing lives
     */
    public SimpleIntegerProperty getLivesProperty(){ return lives; }

    /**
     * @return SimpleIntegerProperty representing level
     */
    public SimpleIntegerProperty getLevelProperty(){ return level; }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
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

    /**
     * @return SimpleIntegerProperty representing the Y value of the hovered Block
     */
    public SimpleIntegerProperty getHoveredBlockY() {
        return hoveredBlockY;
    }

    /**
     * @return SimpleIntegerProperty representing the X value of the hovered Block
     */
    public SimpleIntegerProperty getHoveredBlockX() {
        return hoveredBlockX;
    }
}
