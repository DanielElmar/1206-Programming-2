package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

/**
 * Displays the Instruction image as well as all Game Pieces to the player
 */
public class InstructionsScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * Initialise the scene, scene listeners need to go here
     */
    @Override
    public void initialise() {

        scene.setOnKeyPressed((e) -> {
            gameWindow.startMenu();
        });

    }

    /**
     * Build the scene layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        // set up main scene root and layout
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var backgroundPane = new StackPane();
        backgroundPane.setMaxWidth(gameWindow.getWidth());
        backgroundPane.setMaxHeight(gameWindow.getHeight());
        backgroundPane.getStyleClass().add("menu-background");
        root.getChildren().add(backgroundPane);

        var mainPane = new BorderPane();
        backgroundPane.getChildren().add(mainPane);


        // Title
        var topVbox = new VBox();
        topVbox.setAlignment(Pos.CENTER);

        var instructions = new Text("Instructions");
        instructions.getStyleClass().add("heading");
        instructions.setTextAlignment(TextAlignment.CENTER);

        topVbox.getChildren().add(instructions);
        mainPane.setTop(topVbox);


        // Instruction Image
        String imagePath = MenuScene.class.getResource("/images/instructions.png").toExternalForm();

        ImageView titleImageView = null;
        try {
            Image titleImage = new Image( imagePath );  //new FileInputStream(
            titleImageView = new ImageView(titleImage);

            titleImageView.setFitHeight((gameWindow.getHeight() / 2) + 50);
            titleImageView.setFitWidth(gameWindow.getWidth());
            titleImageView.setPreserveRatio(true);

            mainPane.setCenter(titleImageView);

        } catch (Exception ignored) { }
        
        //Bottom Vbox
        var bottomVBox = new VBox();
        bottomVBox.setAlignment(Pos.CENTER);
        mainPane.setBottom(bottomVBox);
        
        // Pieces Title
        var piecesTitle = new Text("Game Piece's");
        piecesTitle.getStyleClass().add("heading");
        bottomVBox.getChildren().add(piecesTitle);
        
        // Game Piece's Display
        var pieceGridPane = new GridPane();
        bottomVBox.getChildren().add(pieceGridPane);
        pieceGridPane.setAlignment(Pos.CENTER);

        // add GamePieces to the Display
        ArrayList<PieceBoard> pieceBoards = new ArrayList<PieceBoard>();

        for (int i = 0; i < GamePiece.PIECES; i++) {
            var temp = new PieceBoard( 50, 50);
            pieceBoards.add( temp );
            temp.setPiece(i);
            pieceGridPane.setConstraints(temp, (i % 5), ( i / 5));
            pieceGridPane.getChildren().add(temp);
        }

        pieceGridPane.setHgap(10);
        pieceGridPane.setVgap(10);
        pieceGridPane.setPadding( new Insets( 5,0,0,0));

    }
}
