package uk.ac.soton.comp1206.customComponents;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;


public class ScoreList extends VBox {

    private static final Logger logger = LogManager.getLogger(uk.ac.soton.comp1206.ui.DrawWindow.class);
    private SimpleIntegerProperty currentScore = new SimpleIntegerProperty();


    private ArrayList<Label> scoreLabels = new ArrayList<>();


    public ScoreList(){

        Label score1 = new Label("");
        Label score2 = new Label("");
        Label score3 = new Label("");
        Label score4 = new Label("");
        Label score5 = new Label("");

        Label scoreText = new Label("Current Score");
        Label currentScoreLabel = new Label(""+currentScore.get()+"");
        Label highScoreLabel = new Label("High Scores");


        getChildren().add(scoreText);
        getChildren().add(currentScoreLabel);
        getChildren().add(highScoreLabel);

        getChildren().add(score1);
        getChildren().add(score2);
        getChildren().add(score3);
        getChildren().add(score4);
        getChildren().add(score5);

        scoreLabels.add(score1);
        scoreLabels.add(score2);
        scoreLabels.add(score3);
        scoreLabels.add(score4);
        scoreLabels.add(score5);

        setAlignment(Pos.TOP_CENTER);

        scoreText.setId("scoreText");
        currentScoreLabel.setId("currentScoreLabel");

        score1.setId("highScore");
        score2.setId("highScore");
        score3.setId("highScore");
        score4.setId("highScore");
        score5.setId("highScore");

        updateScores("null");

        currentScore.addListener(((observableValue, oldValue, newValue) -> {currentScoreLabel.textProperty().setValue(currentScore.get()+"");}));
    }

    public void updateScores(String message){

        String[] parts = message.split("\n");

        for (int i = 0; i < 5; i++) {
            try {
                scoreLabels.get(i).setText(parts[i+1]);
            }catch (Exception e){
                break;
            }

        }
    }

    public SimpleIntegerProperty getScoreProperty(){return currentScore;}

}
