package uk.ac.soton.comp1206.game;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.Leaderboard;

import uk.ac.soton.comp1206.network.Communicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * MultiplayerGame extends Game and adds functionality such as sending game state updates to the server via a communicator
 * as well as receiving messages and handling messages from the server such as Score updates, opponents Board States and lives
 *
 * Over all extends Game to adjust game logic to conform to online play and report back to the server
 *
 * @see Game
 */
public class MultiplayerGame extends Game{



    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
    private static Communicator communicator;

    private final SimpleIntegerProperty pieceValue = new SimpleIntegerProperty();
    private Boolean pieceUpdatedFromServer = false;

    private final SimpleStringProperty lastMessage = new SimpleStringProperty();

    private final ChangeListener LivesPropertyListener;
    private final ChangeListener scorePropertyListener;

    // multiplayerScores SimpleListProperty holds the scores from all players in teh online game
    private final ArrayList<ArrayList<String>> scorePairsArrayList = new ArrayList<>();
    private final ObservableList<ArrayList<String>> scorePairObservableList = FXCollections.observableArrayList(scorePairsArrayList);
    private final SimpleListProperty<ArrayList<String>> multiplayerScores = new SimpleListProperty<>(scorePairObservableList);

    private final Multimedia audioPlayer = new Multimedia();

    // the 2 boards of the 2 possible runner ups
    private Grid firstPlaceBoardGrid;
    private Grid secondPlaceBoardGrid;

    // runnerUps SimpleListProperty holds teh names of the runner ups
    private final ArrayList<String> runnerUpsArrayList = new ArrayList<>();
    private final ObservableList<String> runnerUpsObservableList = FXCollections.observableArrayList(runnerUpsArrayList);
    private final SimpleListProperty<String> runnerUps = new SimpleListProperty<>(runnerUpsObservableList);
    private static String myName;

    /**
     * Used to compare multiplayer scores to sort the SimpleListProperty is score descending order
     */
    private static final Comparator compareTo = new Comparator<ArrayList<String>>() {
        @Override
        public int compare(ArrayList<String> o1, ArrayList<String> o2) {
            if (Integer.parseInt(o1.get(1)) > Integer.parseInt(o2.get(1))){ return -1; }
            else if (Integer.parseInt(o1.get(1)) < Integer.parseInt(o2.get(1))){ return 1; }
            else{ return 0; }
        }
    };

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows, Leaderboard leaderboardInput, Communicator communicator, String myName) {
        super(cols, rows);
        // sets class communicator and player Name
        MultiplayerGame.communicator = communicator;
        MultiplayerGame.myName = myName;
        communicator.addListener(this::receiveMessage);

        // bind Leaderboard to multiplayerScores
        leaderboardInput.getRemoteScores().bind(multiplayerScores);

        // Score and Lives listeners and runnable
        scorePropertyListener = (observableValue, o, t1) -> communicator.send("SCORE " + getScoreProperty().get());

        LivesPropertyListener = (observableValue, o, t1) -> communicator.send("LIVES " + getLivesProperty().get());

        getScoreProperty().addListener( scorePropertyListener );
        getLivesProperty().addListener( LivesPropertyListener );

        // set up the runner ups Grids
        firstPlaceBoardGrid = new Grid(5,5);
        secondPlaceBoardGrid = new Grid(5,5);

    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    @Override
    public void blockClicked(GameBlock gameBlock) {

        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();


        // check if this piece can be played here
        var playable = grid.canPlayPiece(x,y, currentPiece);

        if (playable){
            grid.playPiece(x,y, currentPiece);


            // construct and send a BOARD command to tell the server the board has been updated
            var boardStateString = "";

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    boardStateString = boardStateString + getGrid().get(j,i);
                }
            }
            communicator.send("BOARD " + boardStateString);

            nextPiece();
            afterPiece();
        }
    }

    /**
     * Uses the communicator to request a new piece form the server
     * @return returns the GamePiece received from the server
     */
    @Override
    public GamePiece spawnPiece() {
        // askt eh server for teh next piece
        communicator.send("PIECE");

        // wait for a response from teh server to update teh piece
        while (!pieceUpdatedFromServer){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) { }
        }

        // return the next GamePiece given by the server
        pieceUpdatedFromServer = false;
        return GamePiece.createPiece(pieceValue.get());
    }


    /**
     * removes any listeners on certain properties, cancels Timers aswell as calling super
     */
    @Override
    public void close() {

        logger.info("Closing Game");

        // remove listeners from the Game
        getScoreProperty().removeListener( scorePropertyListener );
        getLivesProperty().removeListener( LivesPropertyListener );

        // notify the server player is leaving the session
        communicator.send("DIE");

        super.close();
    }

    /**
     * Takes a message from a Communicator and parses the incoming message and executed the corresponding code
     * @param message String from communicator to be parsed
     */
    private void receiveMessage(String message) {


        var parts = message.split( " ", 2);

        // Identify what command to run based off message beginning
        switch (parts[0]){

            case "PIECE" -> { pieceValue.set( Integer.parseInt( parts[1] )); pieceUpdatedFromServer = true; }

            case "BOARD" -> {


                var boardsParts = parts[1].split(":");
                var lastRunnerUps = runnerUps;
                runnerUps.clear();

                //get new Runner Ups from multiplayerScores so we can compare with the previous runner ups
                var count = 0;
                for (int i = 0; i < multiplayerScores.size() ; i++) {
                    if (count == 2){ break;}
                    else if(!(multiplayerScores.get(i).get(0).equals(myName))){

                        runnerUps.add(multiplayerScores.get(i).get(0));

                        count ++ ;
                    }
                }

                if (runnerUps.size() == 1){
                    logger.info("Upddate Board for runner up " + runnerUps.get(0));
                    updateRunnerUpGrid(firstPlaceBoardGrid, boardsParts[1]);
                }

                else {

                    // if runner ups have switched places
                    if (lastRunnerUps.get(0).equals(runnerUps.get(1)) && lastRunnerUps.get(1).equals(runnerUps.get(0))) {

                        // Uppdate Boards
                        if (boardsParts[0].equals(runnerUps.get(0))) {

                            logger.info("Upddate Board for runner up " + runnerUps.get(0));
                            updateRunnerUpGrid(secondPlaceBoardGrid, firstPlaceBoardGrid);
                            updateRunnerUpGrid(firstPlaceBoardGrid, boardsParts[1]);

                        } else if (boardsParts[0].equals(runnerUps.get(1))) {

                            logger.info("Upddate Board for runner up " + runnerUps.get(1));
                            updateRunnerUpGrid(firstPlaceBoardGrid, secondPlaceBoardGrid);
                            updateRunnerUpGrid(secondPlaceBoardGrid, boardsParts[1]);
                        }
                    }


                    // if ONLY last runner ups has switched with a previously non-runner up
                    else if (lastRunnerUps.get(0).equals(runnerUps.get(0)) && !lastRunnerUps.get(1).equals(runnerUps.get(1))) {

                        // Uppdate Boards normaly
                        if (boardsParts[0].equals(runnerUps.get(0))) {

                            logger.info("Upddate Board for runner up " + runnerUps.get(0));
                            updateRunnerUpGrid(firstPlaceBoardGrid, boardsParts[1]);

                        } else if (boardsParts[0].equals(runnerUps.get(1))) {

                            logger.info("Upddate Board for runner up " + runnerUps.get(1));
                            updateRunnerUpGrid(secondPlaceBoardGrid, boardsParts[1]);
                        }
                    }


                    // if a previously non-runner hits big and jumps to first place
                    else if (lastRunnerUps.get(0).equals(runnerUps.get(1)) && !lastRunnerUps.get(1).equals(runnerUps.get(0))) {

                        // Uppdate Boards normaly
                        if (boardsParts[0].equals(runnerUps.get(0))) {

                            logger.info("Upddate Board for runner up " + runnerUps.get(0));
                            updateRunnerUpGrid(secondPlaceBoardGrid, firstPlaceBoardGrid);
                            updateRunnerUpGrid(firstPlaceBoardGrid, boardsParts[1]);

                        } else if (boardsParts[0].equals(runnerUps.get(1))) {

                            // We dont have the Grid for the first place Board yet, it will stick to the last player on it untill the Board is sent to us
                            logger.info("Upddate Board for runner up " + runnerUps.get(1));
                            updateRunnerUpGrid(secondPlaceBoardGrid, boardsParts[1]);
                        }
                    }

                    // default case
                    else {

                        // Uppdate Boards
                        if (boardsParts[0].equals(runnerUps.get(0))) {

                            logger.info("Upddate Board for runner up " + runnerUps.get(0));
                            updateRunnerUpGrid(firstPlaceBoardGrid, boardsParts[1]);

                        } else if (boardsParts[0].equals(runnerUps.get(1))) {

                            logger.info("Upddate Board for runner up " + runnerUps.get(1));
                            updateRunnerUpGrid(secondPlaceBoardGrid, boardsParts[1]);
                        }
                    }
                }
            }

            case "SCORES" -> {

                // Update LeaderBoard / multiplayerScores
                multiplayerScores.clear();

                for ( String score: parts[1].split("\n")) {
                    var scoreParts = score.split(":");
                    multiplayerScores.add(new ArrayList<String>(Arrays.asList(scoreParts[0],scoreParts[1],scoreParts[2])));
                }
                // sort multiplayerScores
                multiplayerScores.sort(compareTo);
            }

            case "MSG" -> { audioPlayer.playAudio("message.wav"); lastMessage.set(parts[1]); }
        }
    }

    /**
     * Copies the grid encoded by a string to a given Grid
     *
     * @param grid Grid to be updated
     * @param boardEncoded what to update the grid with
     */
    private void updateRunnerUpGrid(Grid grid, String boardEncoded){

        var boardEncodeSplit = boardEncoded.split(" ");
        for (int i = 0; i < boardEncodeSplit.length; i++) {

            grid.set( (i / 5), (i % 5), Integer.parseInt(boardEncodeSplit[i]));
        }
    }

    /**
     * Copies one grid to another given Grid
     *
     * @param grid Grid to be updated
     * @param grid2 what to update the Grid with
     */
    private void updateRunnerUpGrid(Grid grid, Grid grid2){

        for (int i = 0; i < 25; i++) {

            grid.set( (i / 5), (i % 5), grid2.get((i / 5), (i % 5)));
        }
    }

    /**
     * @return returns a SimpleListProperty of multiplayer scores
     */
    public SimpleListProperty getMultiplayerScores() {
        return multiplayerScores;
    }

    /**
     * @return returns a SimpleStringProperty containing the last message sent
     */
    public SimpleStringProperty getLastMessage() {
        return lastMessage;
    }

    /**
     * @return returns a Grid of the first place player excluding this player
     */
    public Grid getFirstPlaceBoardGrid() {
        return firstPlaceBoardGrid;
    }

    /**
     * @return returns a Grid of the second place player excluding this player
     */
    public Grid getSecondPlaceBoardGrid() {
        return secondPlaceBoardGrid;
    }

    /**
     * @return returns a SimpleListProperty containing the names of the top 2 players excluding this player
     */
    public SimpleListProperty getRunnerUps(){ return runnerUps; }
}
