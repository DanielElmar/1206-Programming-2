package uk.ac.soton.comp1206.scene;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    private static final Multimedia audioPlayer = new Multimedia();

    // SimpleIntegerProperty allows binding to game properties
    private final SimpleIntegerProperty currentScore = new SimpleIntegerProperty();
    private final SimpleIntegerProperty multiplier = new SimpleIntegerProperty();
    private final SimpleIntegerProperty lives = new SimpleIntegerProperty();
    private final SimpleIntegerProperty level = new SimpleIntegerProperty();

    // exsposed javafx nodes
    protected VBox sideTopVBox = new VBox();
    protected VBox mainBoardVBox = new VBox();
    protected VBox runnerUpsVBox = new VBox();
    protected Game game;
    private Canvas barTimer;


    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        // set up main scene root and layout
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        root.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);


        // Centre HBox
        HBox centreHBox = new HBox();
        centreHBox.requestFocus();
        mainPane.setCenter(centreHBox);


        // add runnersUpsVBox to scene
        centreHBox.getChildren().add(runnerUpsVBox);
        runnerUpsVBox.setMaxWidth(0);

        // Add main Board to Centre HBox via VBox
        centreHBox.getChildren().add(mainBoardVBox);

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainBoardVBox.setAlignment(Pos.CENTER);
        mainBoardVBox.getChildren().add(board);

        // set game hover bind to board hover (Bidirectional)
        game.getHoveredBlockX().bindBidirectional(board.getHoveredBlockX());
        game.getHoveredBlockY().bindBidirectional(board.getHoveredBlockY());


        centreHBox.setHgrow(mainBoardVBox, Priority.ALWAYS);
        centreHBox.setHgrow(runnerUpsVBox, Priority.ALWAYS);


        // Top Box
        var topBox = new HBox();
        mainPane.setTop(topBox);

        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPrefWidth(10000);
        topBox.setPadding( new Insets( 10,0,0,0));


        // Top box Score box
        var scoreBox = new VBox();
        scoreBox.setAlignment(Pos.TOP_CENTER);
        var scoreText = new Text("Score");
        var currentScoreText = new Text(""+currentScore.get()+"");

        topBox.getChildren().add(scoreBox);
        scoreBox.getChildren().add( scoreText );
        currentScore.bind(game.getScoreProperty());
        scoreBox.getChildren().add( currentScoreText );

        scoreText.getStyleClass().add("heading");
        currentScoreText.getStyleClass().add("score");

        // add listener
        currentScore.addListener(((observableValue, oldValue, newValue) -> {currentScoreText.textProperty().setValue(currentScore.get()+"");}));

        // Top box Title
        var heading  = new Text("Challenger!!!");
        topBox.getChildren().add( heading );
        heading.getStyleClass().add("title");

        // Top box Multiplier Box
        var multiplierBox = new VBox();
        multiplierBox.setAlignment(Pos.TOP_CENTER);
        var multiplierText = new Text("Multiplier");
        var multiplierValue =  new Text(""+multiplier.get()+"");

        topBox.getChildren().add(multiplierBox);
        multiplierBox.getChildren().add( multiplierText );
        // flush value before bind
        multiplier.set( game.getMultiplierProperty().get() );
        multiplier.bind(game.getMultiplierProperty());
        multiplierBox.getChildren().add( multiplierValue);

        multiplierText.getStyleClass().add("heading");
        multiplierValue.getStyleClass().add("score");

        multiplier.addListener(((observableValue, oldValue, newValue) -> {multiplierValue.textProperty().setValue(multiplier.get()+"");}));

        // topBox nodes Styling
        HBox.setHgrow(scoreBox, Priority.ALWAYS);
        HBox.setHgrow(heading, Priority.ALWAYS);
        HBox.setHgrow(multiplierBox, Priority.ALWAYS);

        // Side box
        var sideBox = new VBox(20);
        mainPane.setRight(sideBox);

        sideBox.setAlignment(Pos.TOP_CENTER);
        sideBox.setPadding( new Insets( 10,10,10,0));

        // Side HighScore or Verus VBox place holder

        sideTopVBox.setAlignment(Pos.CENTER);
        buildSideTopVBox();
        sideBox.getChildren().add(sideTopVBox);


        // Side Lives
        var livesText = new Text("Lives");
        var livesValue = new Text("" + lives.get() + "");

        // flush value before bind
        lives.set( game.getLivesProperty().get() );
        lives.bind(game.getLivesProperty());

        sideBox.getChildren().add(livesText);
        sideBox.getChildren().add(livesValue);

        livesText.getStyleClass().add("heading");
        livesValue.getStyleClass().add("score");

        // add listener to update the UI when lives changes
        lives.addListener(((observableValue, oldValue, newValue) -> {livesValue.textProperty().setValue(lives.get()+"");}));

        // Side Level
        var levelText = new Text("Level");
        var levelValue = new Text("" + level.get() + "");

        level.bind(game.getLevelProperty());

        sideBox.getChildren().add(levelText);
        sideBox.getChildren().add(levelValue);

        levelText.getStyleClass().add("heading");
        levelValue.getStyleClass().add("level");

        level.addListener(((observableValue, oldValue, newValue) -> {levelValue.textProperty().setValue(level.get()+""); audioPlayer.playAudio("level.wav"); }));

        // Side Current Piece Board
        PieceBoard currentPieceBoard = new PieceBoard( 50, 50);
        sideBox.getChildren().add(currentPieceBoard);
        game.addNextPieceListener((gamePiece, followingPiece) -> { currentPieceBoard.setPiece(gamePiece); });

        // Side Following Piece Board
        PieceBoard followingPieceBoard = new PieceBoard( 30, 30);
        sideBox.getChildren().add(followingPieceBoard);
        game.addNextPieceListener((gamePiece, followingPiece) -> { followingPieceBoard.setPiece(followingPiece); });


        // Bottom
        barTimer = new Canvas();
        barTimer.setWidth(gameWindow.getWidth());
        barTimer.setHeight(30);
        mainPane.setBottom(barTimer);


        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClick(this::rightClicked);
        currentPieceBoard.setOnBlockClick((e)->{game.rotateCurrentPieceCW();});
        followingPieceBoard.setOnBlockClick((e)->{game.swapCurrentPiece();});

        board.setOnRightClick( (e) -> { game.rotateCurrentPieceCW(); });

        // set GameLoopListener to handle a game loop
        game.setGameLoopListener( (newDelay, lives) -> {
            if (lives == -1 ){
                gameOver();
            }else{
                restBarTimer(newDelay);
            }
        });
        // add listener to trigger fadeOut on a Set of gameBlockCoordinateSet
        game.addLineClearedListener( (gameBlockCoordinateSet) -> { board.fadeOut(gameBlockCoordinateSet); });

        // piggy back build
        buildExtra();
    }

    /**
     * Triggered at end of build() is overridden in over classes to piggyback a call to build()
     */
    protected void buildExtra(){}

    /**
     * Used to build the Side Top VBox of the display, this displays the High-score
     */
    protected void buildSideTopVBox() {

        logger.info("Building Side Top VBox");

        // add HighScore to beat
        var highScoreText = new Text("HighScore");
        var highScore = new Text("" + getHighScore() + "");

        sideTopVBox.getChildren().add(highScoreText);
        sideTopVBox.getChildren().add(highScore);

        highScoreText.getStyleClass().add("heading");
        highScore.getStyleClass().add("hiscore");

    }

    /**
     * Used to rest the bar timer at the bottom that indicates the time remaining till a live is lost.
     * Also implements the timer using a AnimationTimer to animate the new bar timer
     * @param newDelay the delay of the game loop timer
     */
    private void restBarTimer(int newDelay) {

        var gc = barTimer.getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, barTimer.getWidth(), barTimer.getHeight());

        // animate the bar timer
        final double[] previous = {-1};
        AnimationTimer timer;
        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {

                double total = 0;

                if (previous[0] == -1) {
                    previous[0] = now;
                } else {

                    var gc = barTimer.getGraphicsContext2D();

                    //Clear
                    gc.clearRect(0, 0, barTimer.getWidth(), barTimer.getHeight());

                    // time passed  / goal
                    total += (now - previous[0]);
                    double percentage = (total / 1000000) / (newDelay);

                    // redundant code to stop rouge Timers
                    if ((int) percentage >= 10) {
                        this.stop();
                    }

                    try {
                        //Fill
                        gc.setFill(Color.color(percentage, (1 - percentage), 0));
                        gc.fillRect((barTimer.getWidth() - (barTimer.getWidth() * percentage)), 0, (barTimer.getWidth() * percentage), barTimer.getHeight());
                    } catch (IllegalArgumentException ignored) { }

                    //Border
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(0, 0, barTimer.getWidth(), barTimer.getHeight());
                }
            }
        };

        timer.start();

    }

    /**
     * closes the Game by calling game.dead() and close(), this stops any Timers and Media playing.
     * makes call to gamewindow to start the scores scene
     */
    private void gameOver(){
        logger.info("Game Over");
        audioPlayer.playAudio("fail.wav");

        game.close();
        gameWindow.startScores(game);
    }

    /**
     * attempts to read the scores.txt file to obtain the high score
     * @return returns the high score value or 0 if failed to read file
     */
    private int getHighScore(){

        logger.info("Getting HighScores");

        ArrayList< Pair< String, Integer> > tempScoresArrayList = new ArrayList<>();

        // attempt to read highScore from file
        try {
            URL resource = getClass().getClassLoader().getResource("scores.txt");

            assert resource != null;
            BufferedReader reader = new BufferedReader(new FileReader( new File(resource.toURI()) ));

            String strCurrentLine = reader.readLine();
            if ( strCurrentLine != null )  {
                var splitLine = strCurrentLine.split(":");
                return Integer.parseInt( splitLine[1] );
            }

        } catch (URISyntaxException | IOException ignored) { }
        return 0;
    }

    /**
     * Takes a KeyEvent and executes the corresponding code, mainly game events such as rotating pieces
     * @param e KeyEvent
     */
    protected void keyPressed(KeyEvent e){

        var code = e.getCode();

        // handle the KeyEvent using a switch to run the corresponding code tot eh Key Event
        switch (code){

            case ESCAPE -> {
                game.close();
                gameWindow.startMenu();
            }

            case ENTER, X -> { game.enterPressed(); }

            case SPACE, R -> { game.swapCurrentPiece(); }

            case E, C, CLOSE_BRACKET-> { game.rotateCurrentPieceCW(); }

            case Q, Z, OPEN_BRACKET -> { game.rotateCurrentPieceACW(); }

            case W, UP -> { game.moveHoverBlockUP(); }

            case A, LEFT -> { game.moveHoverBlockLEFT(); }

            case S, DOWN -> { game.moveHoverBlockDOWN(); }

            case D, RIGHT -> { game.moveHoverBlockRIGHT(); }

        }
    }

    /**
     * Takes a MouseEvent and executes the corresponding code, mainly game events such as rotating pieces
     * @param e KeyEvent
     */
    private void rightClicked(MouseEvent e){
        game.rotateCurrentPieceCW();
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    //public void shutDown(){
        /// Tip: You will need to add a method to shutdown the game in the ChallengeScene to end and clean up all parts of the game, before going back - or it'll keep playing!}

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");

        scene.setOnKeyPressed(this::keyPressed);

        game.start();
        restBarTimer(12000);
    }

}
