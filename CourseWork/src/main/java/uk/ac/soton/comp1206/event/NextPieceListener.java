package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The NextPiece Listener  is used for listening for when a next Piece is triggered.
 */
public interface NextPieceListener {

    /**
     * Handle an nextPiece triggered by game logic
     * @param nextPiece the Game Piece that is next up to play
     * @param followingPiece the Game Piece that is following up the current/next piece
     */
    public void nextPiece (GamePiece nextPiece, GamePiece followingPiece);

}
