package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Displays the ECS!!! Logo with a loading sounds effect
 */
public class LoadingScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(LoadingScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LoadingScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * Initialise the scene, scene listeners need to go here
     */
    @Override
    public void initialise() {

    }
    /**
     * Build the scene layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        // set up main scene root and layout
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var backGround = new StackPane();
        backGround.setMaxWidth(gameWindow.getWidth());
        backGround.setMaxHeight(gameWindow.getHeight());
        backGround.getStyleClass().add("menu-background");
        root.getChildren().add(backGround);

        // Loading image
        String imagePath = MenuScene.class.getResource("/images/ECSGames.png").toExternalForm();

        ImageView loadingImageView = null;
        try {
            Image titleImage = new Image( imagePath );  //new FileInputStream(
            loadingImageView = new ImageView(titleImage);

            loadingImageView.setFitHeight(455);
            loadingImageView.setFitWidth(500);
            loadingImageView.setPreserveRatio(true);

            backGround.getChildren().add(loadingImageView);

        } catch (Exception e) {
            e.printStackTrace();
        }


        // Loading image Fade in
        FadeTransition LoadingImageFadeIn = new FadeTransition(Duration.millis(4000), loadingImageView);
        LoadingImageFadeIn.setFromValue(0);
        LoadingImageFadeIn.setToValue(1);

        LoadingImageFadeIn.play();

        LoadingImageFadeIn.setOnFinished( (e) -> { gameWindow.startMenu(); } );

        // start Music
        Multimedia media = new Multimedia();
        media.playAudio("intro.mp3");
    }
}
