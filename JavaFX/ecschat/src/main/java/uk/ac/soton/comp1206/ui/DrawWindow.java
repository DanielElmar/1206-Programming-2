package uk.ac.soton.comp1206.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.customComponents.WhiteBoard;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.network.PaintMessage;

import java.awt.*;

public class DrawWindow {

    private Scene scene;
    private GraphicsContext gc;
    private PaintMessage pm;
    private Communicator communicator;
    private Color selected = Color.BLACK;
    private static final Logger logger = LogManager.getLogger(DrawWindow.class);

    public DrawWindow(App app, Communicator communicator){

        var pane = new BorderPane();
        this.scene = new Scene(pane,640,480);
        this.communicator = communicator;

        communicator.addListener((message) -> Platform.runLater(() -> {
            if (message.startsWith("DRAW")){
                this.receiveDraw(message);
            }
        }));

        pane.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1 1 1 1");

        WhiteBoard whiteBoard = new WhiteBoard();

        pane.setCenter(whiteBoard);


        HBox hBox = new HBox();

        var pen1 = new Pane();
        var pen2 = new Pane();
        var pen3 = new Pane();
        var pen4 = new Pane();
        var pen5 = new Button();
        var pen6 = new Button();
        var pen7 = new Button();
        var pen8 = new Button();
        var pen9 = new Button();
        var pen10 = new Button();
        var pen11 = new Button();
        var pen12 = new Button();
        var pen13 = new Button();
        var pen14 = new Button();

        pen1.setMinSize(100,100);
        pen2.setMinSize(100,100);
        pen3.setMinSize(100,100);

        pen1.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1 1 1 1");
        pen2.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1 1 1 1");
        pen3.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1 1 1 1");
        pen4.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1 1 1 1");



        hBox.getChildren().add(pen1);
        hBox.getChildren().add(pen2);
        hBox.getChildren().add(pen3);
        hBox.getChildren().add(pen4);


        pane.setBottom(hBox);


        whiteBoard.widthProperty().bind(pane.widthProperty());
        whiteBoard.heightProperty().bind(pane.heightProperty());










        // Listeners
        gc = whiteBoard.getGraphicsContext2D();
        gc.setLineWidth(3);

        whiteBoard.setOnMousePressed((e)->{
            pm = new PaintMessage();
            pm.setColour(selected);
            gc.setStroke(selected);
            gc.beginPath();
            paint(e.getX(), e.getY());
        });

        whiteBoard.setOnMouseDragged((e)->{
            paint(e.getX(), e.getY());
            gc.stroke();

        });

        whiteBoard.setOnMouseReleased((e)->{
            paint(e.getX(), e.getY());
            gc.stroke();
            gc.closePath();
            communicator.send(pm.encode());
        });

    }

    public Scene getScene(){
        return scene;
    }

    public void paint(double x ,double y){
        gc.lineTo(x, y);
        pm.addPoint(x, y);
    }

    public void receiveDraw(String message) {
        logger.info("Received Drawing");
        String drawing = message.replace("DRAW ", "");
        var paintDrawing = new PaintMessage(drawing);
        paintDrawing.paint(gc);
    }
}
