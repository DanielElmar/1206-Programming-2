package uk.ac.soton.comp1206.customComponents;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Paint;


import java.util.Random;

public class Grid {

    private final int columns;
    private final int rows;
    final SimpleIntegerProperty[][] grid;
    private Random random = new Random();

    public Grid(int columns, int rows, Paint[] colors){
        this.columns = columns;
        this.rows = rows;

        grid = new SimpleIntegerProperty[columns][rows];

        for (int y = 0; y < columns; y++) {
            for (int x = 0; x < rows; x++) {
                var temp = random.nextInt(colors.length);
                grid[x][y] = new SimpleIntegerProperty((int) temp);
            }
        }
    }


    public IntegerProperty getGridProperty(int x, int y){ return grid[x][y]; }

    public void set(int x, int y, int value){ grid[x][y].set(value); }

    public int get(int x, int y){ return grid[x][y].get(); }

    public int getColumns() { return columns; }

    public int getRows() { return rows; }
}
