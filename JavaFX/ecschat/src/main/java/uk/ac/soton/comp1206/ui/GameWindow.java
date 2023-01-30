package uk.ac.soton.comp1206.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.customComponents.GameBlock;
import uk.ac.soton.comp1206.customComponents.GameGrid;
import uk.ac.soton.comp1206.customComponents.Grid;
import uk.ac.soton.comp1206.customComponents.ScoreList;
import uk.ac.soton.comp1206.network.Communicator;

import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.DoubleToIntFunction;

public class GameWindow {


    private Scene scene;
    private Communicator communicator;
    private App app;
    private static final Logger logger = LogManager.getLogger(uk.ac.soton.comp1206.ui.DrawWindow.class);
    private Grid grid;
    static Paint[] colors = {Color.BLUE, Color.YELLOW, Color.PINK, Color.RED, Color.GREEN, Color.MAGENTA};
    private Timer gameTimer;
    private ScoreList scoreBar;
    private SimpleIntegerProperty currentScore = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty currentColor = new SimpleIntegerProperty(0);
    private Random random = new Random();
    private ProgressBar barTimer;
    private int gameLoopCounter = 1;

    public GameWindow(App app, Communicator communicator){

        // Scene Creation
        var pane = new BorderPane();
        this.scene = new Scene(pane,650,500);
        this.communicator = communicator;
        this.app = app;

        String css = this.getClass().getResource("/game.css").toExternalForm();
        scene.getStylesheets().add(css);

        grid = new Grid(6,6, colors);
        GameGrid gameGrid = new GameGrid(grid, 350,350, colors);
        scoreBar = new ScoreList();

        // TOP
        GameBlock currentColorBlock = new GameBlock(-1,-1, 80,80, colors);
        barTimer = new ProgressBar(0);
        VBox topContainer = new VBox();

        topContainer.setAlignment(Pos.TOP_CENTER);

        topContainer.getChildren().add(currentColorBlock);
        topContainer.getChildren().add(barTimer);

        pane.setCenter(gameGrid);
        pane.setRight(scoreBar);
        pane.setTop(topContainer);



        // Binding and Listening
        currentColorBlock.valueProperty().bind(currentColor);

        scoreBar.getScoreProperty().bind(currentScore);

        gameGrid.addListener(this::blockClicked);

        communicator.addListener((message) -> {
            if (!message.startsWith("SCORES")) return;
            Platform.runLater(()->{this.scoreBar.updateScores(message);});
        });

        communicator.send("SCORES");

        // Timer

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                gameLoop();
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                timerBarUpdate();
            }
        };

        gameTimer = new Timer("Timer");
        gameTimer.schedule(task,0,2000);
        gameTimer.schedule(task2,0,20);

    }

    private void blockClicked(GameBlock gameBlock) {
        var gridBlock = grid.getGridProperty(gameBlock.getX(), gameBlock.getY());

        var temp = currentColor.get();
        var temp2 = random.nextInt(colors.length);

        while (temp == temp2){temp2 = random.nextInt(colors.length);}

        if (currentColor.get() == gridBlock.get()){
            logger.info("{} is {} scoring 5 points",currentColor.get(), gridBlock.get());
            currentScore.set(currentScore.get() + 5);
            gridBlock.setValue(temp2);
            logger.info("Score is {}", currentScore.get());
        }else{
            logger.info("{} is {} taking 1 points",currentColor.get(), gridBlock.get());
            currentScore.set(currentScore.get() - 1);
            currentScore.set((currentScore.get() < 0) ? 0 : currentScore.get());
            logger.info("Score is {}", currentScore.get());
            logger.info(scoreBar.getScoreProperty().get());
        }
    }

    public Scene getScene(){
        return scene;
    }

    public void receiveScore(String message) {
        logger.info("Received New High Scores");


    }

    public void timerBarUpdate(){
        if (!(gameLoopCounter <= 30)){return;}
        barTimer.setProgress( barTimer.getProgress() + 0.01);
    }

    public void gameLoop(){
        if (gameLoopCounter <= 30) {
            var temp = currentColor.get();
            var temp2 = random.nextInt(colors.length);

            while (temp == temp2) {
                temp2 = random.nextInt(colors.length);
            }

            currentColor.set(temp2);
            barTimer.setProgress(0);

            gameLoopCounter++;
        }else{
            barTimer.setProgress(1);
            communicator.send("SCORE " + app.getUsername() + " " + currentScore.get());
            gameTimer.cancel();
        }
    }

}
