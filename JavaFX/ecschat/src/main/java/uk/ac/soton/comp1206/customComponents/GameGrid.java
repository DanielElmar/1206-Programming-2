package uk.ac.soton.comp1206.customComponents;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import uk.ac.soton.comp1206.listeners.BlockClickedListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GameGrid extends GridPane {


    private ArrayList<BlockClickedListener> listeners = new ArrayList<BlockClickedListener>();

    public GameGrid(Grid grid, int width, int height, Paint[] colors) {

        var columns = grid.getColumns();
        var rows = grid.getRows();

        setMaxWidth(width);
        setMaxHeight(height);
        setGridLinesVisible(true);


        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                var block = new GameBlock(x, y, width / columns, height / rows, colors);
                add(block,x,y);

                block.valueProperty().bind(grid.getGridProperty(x,y));

                block.setOnMouseClicked((e)->blockClicked(block));
            }
        }
    }

    public void addListener(BlockClickedListener listener){this.listeners.add(listener);}

    private void blockClicked(GameBlock block){
        for ( BlockClickedListener listener : listeners) {
            listener.blockClicked(block);
        }
    }




}
