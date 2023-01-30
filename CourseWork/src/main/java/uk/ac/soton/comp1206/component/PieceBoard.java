package uk.ac.soton.comp1206.component;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * PieceBoard extends GameBoard with a 3x3 Board and added methods to set a GamePiece to be displayed on the Board
 *
 * @see GameBoard
 */
public class PieceBoard extends GameBoard{

    private GamePiece gamePiece;
    private int pieceVal;

    /**
     * Creates a 3x3 Board to display a single Game Piece, with a centre indicator
     * @param width width of the PieceBoard
     * @param height height of the PieceBoard
     */
    public PieceBoard( double width, double height) {
        super(3, 3, width, height);

        // add circle indicator to the centre of the PieceBoard
        Circle circle = new Circle(0, 0, width/6);

        circle.setFill(Color.DARKCYAN);
        circle.setOpacity(0.5);

        var canvas = new Canvas();

        var gc = canvas.getGraphicsContext2D();

        add(circle,1,1);

        // set on Click Event for the circle as it covers the block below it
        circle.setOnMouseClicked( ( e ) -> { blockClicked( e , blocks[1][1]); } );

    }
    // removed hover functionality form PieceBoard
    @Override
    protected void hoverMoved() {
    }

    /**
     * Displays a GamePiece with the corresponding Value to that what was given
     * @param pieceVal value of the GamePiece to set this Board to display
     */
    public void setPiece( int pieceVal ){
        this.pieceVal = pieceVal;

        // clear board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.grid.set(i,j,0);
            }
        }

        // play piece
        this.grid.playPiece(1,1, GamePiece.createPiece(this.pieceVal));
    }
    /**
     * Displays a GamePiece that was given
     * @param gamePiece GamePiece to set this Board to display
     */
    public void setPiece( GamePiece gamePiece ){
        this.gamePiece = gamePiece;

        // clear board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.grid.set(i,j,0);
            }
        }

        // play piece
        this.grid.playPiece(1,1, this.gamePiece);
    }

}
