package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        // build the scene base layout
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);


        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        mainPane.setTop(title);

        // Image title
        String titleImagePath = MenuScene.class.getResource("/images/TetrECS.png").toExternalForm();

        ImageView titleImageView = null;
        try {
            Image titleImage = new Image( titleImagePath );  //new FileInputStream(
            titleImageView = new ImageView(titleImage);

            titleImageView.setFitHeight(455);
            titleImageView.setFitWidth(500);
            titleImageView.setPreserveRatio(true);
            titleImageView.setRotate(-20);

            mainPane.setCenter(titleImageView);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Image rotation 'Animation'
        RotateTransition rt = new RotateTransition(Duration.millis(6000), titleImageView);
        rt.setByAngle(40);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setAutoReverse(true);

        rt.play();


        /// Menu Box
        var menuBox = new VBox();
        mainPane.setBottom(menuBox);
        menuBox.setAlignment(Pos.TOP_CENTER);
        menuBox.getStyleClass().add("menu");

        // Single Player
        var singlePlayer = new Text("Single Player");
        singlePlayer.getStyleClass().add("menuItem");
        menuBox.getChildren().add(singlePlayer);
        //Bind the button action to the startGame method in the menu
        singlePlayer.setOnMouseClicked(this::startGame);


        // Multi Player
        var multiPlayer = new Text("Multi Player");
        multiPlayer.getStyleClass().add("menuItem");
        menuBox.getChildren().add(multiPlayer);
        //Bind the button action to the startMultiplayer method in the menu
        multiPlayer.setOnMouseClicked(this::startMultiplayer);

        // How to Play
        var howToPlay = new Text("How to Play");
        howToPlay.getStyleClass().add("menuItem");
        menuBox.getChildren().add(howToPlay);
        //Bind the button action to the StartInstructions method in the menu
        howToPlay.setOnMouseClicked(this::startInstructions);

        // Exit
        var exit = new Text("Exit");
        exit.getStyleClass().add("menuItem");
        menuBox.getChildren().add(exit);
        //Bind the button action to the startExit method in the menu
        exit.setOnMouseClicked(this::startExit);


        // start Music
        Multimedia media = new Multimedia();
        media.playMusic("menu.mp3");

    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(Event event) {
        gameWindow.startChallenge();
    }

    /**
     * Handle when a exit event is triggered
     * @param event event
     */
    private void startExit(Event event) {
        gameWindow.startExit();
    }

    /**
     * Handle when the Instructions button is pressed
     * @param event event
     */
    private void startInstructions(Event event) {
        gameWindow.startInstructions();
    }

    /**
     * Handle when the Multiplayer button is pressed
     * @param event event
     */
    private void startMultiplayer(Event event) { gameWindow.startMultiplayerLobby(); }
}
