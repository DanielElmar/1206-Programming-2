package uk.ac.soton.comp1206.customComponents;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class WhiteBoard extends Canvas {

    private Canvas myCanvas;

    public WhiteBoard(){
        var myCanvas = new Canvas();
        GraphicsContext gc = myCanvas.getGraphicsContext2D();
    }

    /*
    @Override
    public double prefWidth(double width) {
        return 0;
    }

    @Override
    public double prefHeight(double height){
        return 0;
    }

    @Override
    public boolean isResizable(){
        return true;
    }
*/

    public double preWidth(double width){
        return 0;
    }


    public Canvas getCanvas(){
        return myCanvas;

    }
}
