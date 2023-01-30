package uk.ac.soton.comp1206.customComponents;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


public class GameBlock extends Canvas {


    private double width;
    private double height;
    private IntegerProperty value;
    private int x;
    private int y;
    private Paint[] colors;

    public GameBlock(int x, int y, double width, double height, Paint[] colors){

        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.colors = colors;

        setWidth(width);
        setHeight(height);

        value = new SimpleIntegerProperty(2);

        value.addListener((observable, oldValue, newValue) ->  paint());

        paint();

    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public void paint(){

        var gc = getGraphicsContext2D();
        gc.clearRect(0,0,width,height);

        if (value.get() >= 0 ){
            gc.setFill(colors[value.get()]);
            gc.setStroke(Color.BLACK);
            gc.fillRect(0,0,width,height);
            gc.strokeRect(0,0,width,height);
        }


    }

    public int getX() { return x; }

    public int getY() { return y; }
}
