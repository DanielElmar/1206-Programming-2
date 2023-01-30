package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * ScoreScene;
 * loads local Scores,
 * manages new entries to local scores,
 * manages writing local scores,
 * manages requesting and parsing online scores,
 * displaying scores to the Player in a scene with animations
 */
public class ScoresScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);

    private Game game;
    private BorderPane mainPane;

    private static final Communicator communicator = new Communicator("ws://discord.ecs.soton.ac.uk:9700");

    // SimpleListProperty used to bind Properties to localScores and remoteScores
    private final ArrayList<Pair<String, Integer>> pairsArrayList = new ArrayList<>();
    private final ObservableList<Pair<String, Integer>> pairObservableList = FXCollections.observableArrayList(pairsArrayList);
    private final SimpleListProperty<Pair<String, Integer>> localScores = new SimpleListProperty<>(pairObservableList);

    private final ArrayList<Pair<String, Integer>> onlinePairsArrayList = new ArrayList<>();
    private final ObservableList<Pair<String, Integer>> onlinePairsObservableList = FXCollections.observableArrayList(onlinePairsArrayList);
    private final SimpleListProperty<Pair<String, Integer>> remoteScores = new SimpleListProperty<>(onlinePairsObservableList);

    // used to Compare scores in Arrays or Lists and sort them in decending order according to the Score that player achieved
    private static Comparator comparatorTo = new Comparator<Pair<String, Integer>>() {
        @Override
        public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
            if (o1.getValue() > o2.getValue()){ return -1; }
            else if (o1.getValue() < o2.getValue()){ return 1; }
            else{ return 0; }
        }
    };

    // holds all the FadeIn Transitions when reveling teh scores
    private ArrayList<FadeTransition> scoreFadeIns;

    private String userName;
    private Integer userScore;


    //private static SimpleListProperty<ObservableList> = new ;


    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;

        // add listener for logging
        localScores.addListener((ListChangeListener<? super Pair<String, Integer>>) ( e ) -> { logger.info("localScores ADDED:" + e.toString() ); });
    }

    /**
     * Initialise the scene, scene listeners need to go here
     */
    @Override
    public void initialise() {

        scene.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
            }
        });
    }
    /**
     * Build the scene layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        userScore = game.getScoreProperty().getValue();

        // set up main scene root and layout
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        root.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());

        var scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add("menu-background");
        root.getChildren().add(scoresPane);

        mainPane = new BorderPane();
        scoresPane.getChildren().add(mainPane);


        // Scores Part
        var scoresList = new ScoresList();

        scoresList.getScoresList().bind(localScores);
        scoresList.getRemoteScores().bind(remoteScores);

        loadScores();

        // if game is instance of MultiplayerGame skip logic for prompting the player for their name if necessary
        if ( game instanceof MultiplayerGame ){ loadOnlineScores(); }
        else {

            if (localScores.size() < 10) {
                promptUserForName();

            } else {

                try {
                    if (userScore > localScores.get(localScores.size() - 1).getValue()) {
                        promptUserForName();

                    } else {
                        loadOnlineScores();
                    }

                } catch (NullPointerException e) {
                    loadOnlineScores();
                }
            }
        }
    }

    /**
     * Prompts the User to enter their name and stores in userName
     */
    private void promptUserForName() {

        logger.info("prompting player for Name");

        // Tetrecs Image
        String titleImagePath = ScoresScene.class.getResource("/images/TetrECS.png").toExternalForm();

        ImageView titleImageView = null;
        try {
            Image titleImage = new Image( titleImagePath );
            titleImageView = new ImageView(titleImage);

            titleImageView.setFitHeight(455);
            titleImageView.setFitWidth(500);
            titleImageView.setPreserveRatio(true);

            mainPane.setTop(titleImageView);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Centre VBox
        var centreVBox = new VBox();
        mainPane.setCenter(centreVBox);
        centreVBox.setAlignment(Pos.CENTER);

        // Score Title
        var scoreTitle = new Text("Scores");
        scoreTitle.getStyleClass().add("title");
        centreVBox.getChildren().add(scoreTitle );

        // Enter name Text
        var enterName = new Text("Please Enter Your Name?");
        enterName.getStyleClass().add("heading");
        centreVBox.getChildren().add(enterName );

        // text field
        var textBox = new TextField("Name");
        centreVBox.getChildren().add(textBox );

        // Submit Button
        var submit = new Text("Submit?");
        submit.getStyleClass().add("menuItem");
        centreVBox.getChildren().add(submit );

        submit.setOnMouseClicked( (e) -> { userName = textBox.getText(); addUsersScore( textBox.getText() ); loadOnlineScores(); } );

    }

    /**
     * Adds a Users Score to localScores, then writes the new Scores using writeScores()
     * @param userName
     */
    private void addUsersScore(String userName) {
        logger.info("Adding Players Score to localScores");

        localScores.add(new Pair<String, Integer>(userName, userScore ));
        writeScores();
    }

    /**
     * Loads the javafx nods to display the Scores
     */
    private void loadScoresView(){

        logger.info("Loading Scores View");

        // Tetrecs Image
        String titleImagePath = ScoresScene.class.getResource("/images/TetrECS.png").toExternalForm();

        ImageView titleImageView = null;
        try {
            Image titleImage = new Image( titleImagePath );
            titleImageView = new ImageView(titleImage);

            titleImageView.setFitHeight(455);
            titleImageView.setFitWidth(500);
            titleImageView.setPreserveRatio(true);

            mainPane.setTop(titleImageView);

        } catch (Exception ignored) { }

        //Centre VBox
        var centreVBox = new VBox();
        centreVBox.setAlignment(Pos.CENTER);
        mainPane.setCenter(centreVBox);

        // Score Title
        var scoreTitle = new Text("Scores");
        scoreTitle.getStyleClass().add("title");
        centreVBox.getChildren().add(scoreTitle );

        //ScoreList HBox
        var scoreListHBox = new HBox();
        centreVBox.getChildren().add(scoreListHBox);
        scoreListHBox.setAlignment(Pos.TOP_CENTER);

        // LocalScores VBox
        var localScoresVBox = new VBox();
        scoreListHBox.getChildren().add(localScoresVBox);
        localScoresVBox.setAlignment(Pos.TOP_CENTER);
        localScoresVBox.setPadding( new Insets( 0, 50, 0, 0));

        // Online Scores VBox
        var onlineScoresVBox = new VBox();
        scoreListHBox.getChildren().add(onlineScoresVBox);
        onlineScoresVBox.setAlignment(Pos.TOP_CENTER);


        // Score Fade In Animations
        scoreFadeIns = new ArrayList<>();

        // used to set a Styling for only the high score
        var highScore = true;

        for (int i = 0; i < 10; i++) {

            // HBox to hold the onlines players name and score
            var onlineScore = new HBox();
            onlineScore.setOpacity(0);

            // HBox to hold the local players name and score
            var localScore = new HBox();
            localScore.setOpacity(0);

            try {
                // Local Score
                var localScoreName = new Text( localScores.get(i).getKey() );
                localScoreName.getStyleClass().add("heading");

                var localScoreValue = new Text(" " + localScores.get(i).getValue());

                // add styling
                if (highScore) {
                    localScoreValue.getStyleClass().add("highscoreitem");
                } else {
                    localScoreValue.getStyleClass().add("scoreitem");
                }

                // add nodes to the layout
                localScore.getChildren().add(localScoreName);
                localScore.getChildren().add(localScoreValue);
                localScore.setAlignment(Pos.CENTER);
                localScoresVBox.getChildren().add(localScore);

            }catch (Exception ignored){ }

            // Online Score
            try {

                var onlineScoreName = new Text(remoteScores.get(i).getKey());
                onlineScoreName.getStyleClass().add("heading");

                var onlineScoreValue = new Text(" " + remoteScores.get(i).getValue());

                // add styling
                if (highScore) {
                    onlineScoreValue.getStyleClass().add("highscoreitem");
                } else {
                    onlineScoreValue.getStyleClass().add("scoreitem");
                }

                // add nodes to the layout
                onlineScore.getChildren().add(onlineScoreName);
                onlineScore.getChildren().add(onlineScoreValue);
                onlineScore.setAlignment(Pos.CENTER);
                onlineScoresVBox.getChildren().add(onlineScore);
            }catch (Exception ignored){ }

            highScore = false;


            // create Local Score Fade In
            FadeTransition localScoreFadeIn = new FadeTransition(Duration.millis(700), localScore);

            localScoreFadeIn.setFromValue(0);
            localScoreFadeIn.setToValue(1);

            scoreFadeIns.add(localScoreFadeIn);

            // set on finished to trigger the next 2 FadeIn Transitions from the Array
            localScoreFadeIn.setOnFinished( (e)-> { nextScoreFadeIn( scoreFadeIns.indexOf(localScoreFadeIn) + 2 ); });


            // create online Score Fade In
            FadeTransition onlineScoreFadeIn = new FadeTransition(Duration.millis(700), onlineScore);

            onlineScoreFadeIn.setFromValue(0);
            onlineScoreFadeIn.setToValue(1);

            scoreFadeIns.add(onlineScoreFadeIn);

        }

        reveal();
    }

    /**
     * used to trigger the FadeIn Transitions on the Scores via nextScoreFadeIn()
     */
    private void reveal(){
        logger.info("reveling Scores");

        nextScoreFadeIn(0);
    }

    /**
     * Plays a pair of FadeIn Transitions on the Scores via the given index on the scoreFadeIns Array
     * @param index
     */
    private void nextScoreFadeIn(int index){

        try {
            scoreFadeIns.get(index).play();
            scoreFadeIns.get(index+1).play();
        }catch (Exception ignored){}
    }

    /**
     * Loads the localScores Array with either the multiplayer scores from a MultiplayerGame instance or from a text file if not
     */
    private void loadScores(){

        logger.info("Loading Scores");

        // game instanceof MultiplayerGame load localScores from the MultiplayerGame game
        if (game instanceof MultiplayerGame){

            var multiplayerScores = ((MultiplayerGame) game).getMultiplayerScores();


            for (int i = 0; i < multiplayerScores.size() ; i++) {

                var thisScore = (ArrayList<String>) multiplayerScores.get(i);

                var pairToAdd = new Pair<String, Integer>(thisScore.get(0), Integer.parseInt(thisScore.get(1)));

                localScores.add(pairToAdd);
                localScores.sort(comparatorTo);
            }

        }

        // else read localScores from a text file
        else {

            try {

                URL resource = getClass().getClassLoader().getResource("scores.txt");
                BufferedReader reader = new BufferedReader(new FileReader( new File( resource.toURI() ) ));

                String strCurrentLine;
                while ((strCurrentLine = reader.readLine()) != null) {
                    var splitCurrentLine = strCurrentLine.split(":",2);

                    var pairToAdd = new Pair<String, Integer>( splitCurrentLine[0] , Integer.parseInt( splitCurrentLine[1] ));

                    localScores.add(pairToAdd);
                }

            } catch (IOException | URISyntaxException ignored) { }
        }
    }

    /**
     * loads online scores via using a communicator into the remoteScores Array
     */
    private void loadOnlineScores(){

        logger.info("Loading Online Scores");

        // send communicator message to retrieve the High Scores
        communicator.send("HISCORES");
        communicator.addListener( (message) -> {

            if ( message.startsWith("HISCORES ")) {

                // for each score received
                for (String score : message.split(" ", 2)[1].split("\n")) {

                    var splitScore = score.split(":");

                    var pairToAdd = new Pair<String, Integer>(splitScore[0], Integer.parseInt(splitScore[1]));
                    remoteScores.add(pairToAdd);
                }

                // Check if user has beaten the lowest score
                if (remoteScores.get( remoteScores.getSize() - 1 ).getValue() < userScore) {
                    // add this Players score to remoteScores
                    var pairToAdd = new Pair<String, Integer>(userName, userScore);
                    remoteScores.add(pairToAdd);
                    remoteScores.sort(comparatorTo);

                    // notify the server That I HAVE BEATEN them ( scores a new HighScore )
                    Platform.runLater(() -> {
                        writeHighScore();
                    });

                }

                // run loadScoresView later so the server has a time to responds and populate the remoteScores Array
                Platform.runLater(() -> {
                    loadScoresView();
                });
            }
        });
    }

    /**
     * writes Scores from localScores Array to a text file named 'scores.txt' (sorts Array first)
     */
    private void writeScores(){

        logger.info("Writing Scores");

        // sort the Array befor writing
        localScores.sort(comparatorTo);

        PrintWriter reader = null;
        try {
            URL resource = getClass().getClassLoader().getResource("scores.txt");
            reader = new PrintWriter(new FileWriter( new File( resource.toURI() ) ));

            for ( Pair<String, Integer> pair: localScores) {
                reader.print(pair.getKey() + ":" + pair.getValue() + "\n");
                reader.flush();
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message via the communicator to indicate a new highScore has need achieved
     */
    private void writeHighScore(){
        logger.info("writing New HighScore!!");
        communicator.send("HISCORE " + userName + ":" + userScore);
    }




}
