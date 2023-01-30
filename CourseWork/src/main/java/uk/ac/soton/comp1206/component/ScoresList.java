package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleListProperty;

public class ScoresList {

    // SimpleListProperty store 2 score List, can be bounded
    private static SimpleListProperty scoresList = new SimpleListProperty();
    private static SimpleListProperty remoteScores = new SimpleListProperty();

    public ScoresList(){
    }

    public SimpleListProperty getScoresList() {
        return scoresList;
    }

    public SimpleListProperty getRemoteScores() {
        return remoteScores;
    }
}
