package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.component.RunnersUpBoard;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiplayerScene extends ChallengeScene and manages
 * building extra components in the scene such as
 * versus leaderboard, the runner ups GameBoard and the chat box.
 *
 * It also manages interaction with some of these components via keyPressed methods
 *
 * @see ChallengeScene
 */
public class MultiplayerScene extends ChallengeScene{


    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);

    // SimpleListProperty used to bind Properties to localScores
    private final ArrayList<Pair<String, Integer>> pairsArrayList = new ArrayList<>();
    private final ObservableList<Pair<String, Integer>> pairObservableList = FXCollections.observableArrayList(pairsArrayList);
    private final SimpleListProperty<Pair<String, Integer>> localScores = new SimpleListProperty<>(pairObservableList);

    private static final Leaderboard leaderboard = new Leaderboard();
    private static Communicator communicator;

    private final Text chatText = new Text();
    private final TextField chatTextField = new TextField();

    private static String myName;


    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow, Communicator communicator, String myName) {
        super(gameWindow);
        MultiplayerScene.communicator = communicator;
        MultiplayerScene.myName = myName;

    }

    @Override
    public void setupGame() {
        logger.info("Starting a new challenge");
        logger.info(myName);

        //Start new MultiplayerGame game
        game = new MultiplayerGame(5, 5, leaderboard, communicator, myName);

        // adds listener to the multiplayer scores to re-build the side top VBox with the new scores
        ((MultiplayerGame) game).getMultiplayerScores().addListener((ListChangeListener) (e) -> {
            Platform.runLater( () -> { buildSideTopVBox( ((MultiplayerGame) game).getMultiplayerScores()); } ); });

        //adds listener to the last message from MultiplayerGame and updates the chatText node
        ((MultiplayerGame) game).getLastMessage().addListener( (ChangeListener) (observable, oldValue, newValue) -> {
            Platform.runLater( () -> {    chatText.setText(newValue.toString());   });  });


    }

    /**
     * Creates the necessary javafx nodes to display the multiplayer Scores in the Scene
     * @param multiplayerScores  Scores to display
     */
    private void buildSideTopVBox(List<ArrayList<String>> multiplayerScores) {

        logger.info("Building side Top Vbox");

        sideTopVBox.getChildren().clear();

        // heading
        var versusText = new Text("Versus");
        versusText.getStyleClass().add("heading");

        // Content (will be filled with players name and score)
        var versusPlayers = new VBox();

        //for each score in multiplayerScores
        for ( ArrayList<String> array : multiplayerScores) {

            var player = new Text(array.get(0) + ": " + array.get(1) );
            player.getStyleClass().add("heading");

            // set Styling based on lives remaining
            try {
                switch (Integer.parseInt(array.get(2))) {
                    case 0 -> {
                        player.getStyleClass().add("zeroLives");
                    }
                    case 1 -> {
                        player.getStyleClass().add("oneLives");
                    }
                    case 2 -> {
                        player.getStyleClass().add("twoLives");
                    }
                    case 3 -> {
                        player.getStyleClass().add("threeLives");
                    }
                }
            }catch ( NumberFormatException e){ player.getStyleClass().add("deadLives"); }

            versusPlayers.getChildren().add(player);
        }

        // add Scores to side top VBox
        sideTopVBox.getChildren().add(versusText);
        sideTopVBox.getChildren().add(versusPlayers);

    }

    /**
     * Builds the RunnersUpVBox by adding the necessary javafx components to display and update game boards of the runners up in a multiplayer match
     */
    private void buildRunnersUpVBox(){

        logger.info("Building Runners up Vbox");

        // Add 1st adn 2nd place Boards to centre HBox ( via a VBox (1 for each Board)) ( 1st and 2nd place excuding ME)
        runnerUpsVBox.setMaxWidth(200);
        runnerUpsVBox.setAlignment(Pos.CENTER);
        runnerUpsVBox.setSpacing(20);

        // first place
        var firstPlacePlayerName = new Text();
        firstPlacePlayerName.setOpacity(0);
        firstPlacePlayerName.getStyleClass().add("heading");

        var firstPlaceBoard = new RunnersUpBoard( ((MultiplayerGame) game).getFirstPlaceBoardGrid(), 100, 100);
        firstPlaceBoard.setOpacity(0);

        // second place
        var secondPlacePlayerName = new Text();
        secondPlacePlayerName.setOpacity(0);
        secondPlacePlayerName.getStyleClass().add("heading");

        var secondPlaceBoard = new RunnersUpBoard( ((MultiplayerGame) game).getSecondPlaceBoardGrid(), 100, 100);
        secondPlaceBoard.setOpacity(0);

        // add content to runnerUpsVBox
        runnerUpsVBox.getChildren().add(firstPlacePlayerName);
        runnerUpsVBox.getChildren().add(firstPlaceBoard);
        runnerUpsVBox.getChildren().add(secondPlacePlayerName);
        runnerUpsVBox.getChildren().add(secondPlaceBoard);

        // add listener on runnerUps SimpleListProperty to trigger updateRunnerUpBoards() on change
        ((MultiplayerGame)game).getRunnerUps().addListener((ListChangeListener) (e) -> { updateRunnerUpBoards(); });


    }

    @Override
    protected void buildExtra() {

        // Add chat Functionality
        chatText.setText("Press T to trash talk your opponents");
        chatText.getStyleClass().add("heading");

        mainBoardVBox.getChildren().add(chatText);

        // stlye adn add TextField
        chatTextField.setOpacity(1);
        chatTextField.setFocusTraversable(false);
        mainBoardVBox.getChildren().add(chatTextField);//*/

        buildRunnersUpVBox();
    }

    /**
     * Used to update the runner ups game boards Text to the User, as well as reveling them ( The Boards Content is not updated here )
     */
    private void updateRunnerUpBoards(){

        var runnersUpList = ((MultiplayerGame)game).getRunnerUps();

        // Update UI to display the RunnerUpsBoards if they are present in runnersUpList ( only 1 opponent will only show 1 boards,
        // 2 opponents will display both Boards) ( The Boards Content is not updated here )
        try {
            //revel the 1st runner up Board, and set Player Text
            if (runnersUpList.get(0) != null) {
                runnerUpsVBox.getChildren().get(0).setOpacity(1);
                ((Text) runnerUpsVBox.getChildren().get(0)).setText( "" + runnersUpList.get(0) );
                runnerUpsVBox.getChildren().get(1).setOpacity(1);
            }
            //revel the 2nd runner up Board, and set Player Text
            if (runnersUpList.get(1) != null) {
                runnerUpsVBox.getChildren().get(2).setOpacity(1);
                ((Text) runnerUpsVBox.getChildren().get(2)).setText( "" + runnersUpList.get(1));
                runnerUpsVBox.getChildren().get(3).setOpacity(1);
            }
        }catch (IndexOutOfBoundsException ignored){}

    }

    @Override
    protected void keyPressed(KeyEvent e) {

        // if player focuses the chat box the scene setOnKeyPressed is changed to allow the player to use enter to send and escape the chat focus
        if (e.getCode() == KeyCode.T){
            scene.setOnKeyPressed( this::keyPressedInChat );

            // escapes the chat focus and allows key presses to trigger KeyEvents
            Platform.runLater( () -> { chatTextField.requestFocus(); } );

        }else { super.keyPressed(e); }
    }

    //Update the UI to include chat and the leaderboard and remove less important elements if needed.

    /**
     * This is called when a key is pressed while the user is typing in chat in a multiplayer game, it allows the player
     * to escape the focus of the TextField nad return to play
     */
    private void keyPressedInChat(KeyEvent e){

        var code = e.getCode();

        switch (code) {

            // close Game
            case ESCAPE -> {
                game.close();
                gameWindow.startMenu();
            }

            // send Chat message with TextField contents
            case ENTER -> {
                communicator.send("MSG " + chatTextField.getText());
                mainBoardVBox.requestFocus();
                chatTextField.clear();

                logger.info("Set KeyPressed");
                scene.setOnKeyPressed(this::keyPressed);
            }
        }
    }
}
