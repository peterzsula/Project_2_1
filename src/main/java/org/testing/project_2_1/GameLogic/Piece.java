package org.testing.project_2_1.GameLogic;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.UI.PieceDrawer;

/**
 * Represents a game piece in the game. A piece has a type (e.g., BLACK, WHITE, KING),
 * a position on the board, and an associated visual representation (`PieceDrawer`).
 * It includes functionality for moving, promoting, and demoting the piece.
 */
public class Piece {
    public PieceType type; // The type of the piece (e.g., BLACK, WHITE, KING)
    private int x, y; // The x and y coordinates of the piece on the board
    private PieceDrawer pieceDrawer; // The visual representation of the piece

    /**
     * Constructs a `Piece` with the specified type and position.
     *
     * @param type The type of the piece.
     * @param x    The initial x-coordinate of the piece.
     * @param y    The initial y-coordinate of the piece.
     */
    public Piece(PieceType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.pieceDrawer = null;
    }

    public Piece(String string) {
        //TODO Auto-generated constructor stub
    }

    /**
     * Gets the type of the piece.
     *
     * @return The type of the piece.
     */
    public PieceType getType() {
        return type;
    }

    /**
     * Gets the `PieceDrawer` associated with this piece.
     *
     * @return The `PieceDrawer` for this piece, or null if not set.
     */
    public PieceDrawer getPieceDrawer() {
        return pieceDrawer;
    }

    /**
     * Sets the `PieceDrawer` for this piece.
     *
     * @param pieceDrawer The `PieceDrawer` to associate with this piece.
     */
    public void setPieceDrawer(PieceDrawer pieceDrawer) {
        this.pieceDrawer = pieceDrawer;
    }

    /**
     * Aborts a move by reverting the piece's visual representation to its current position.
     */
    public void abortMove() {
        if (pieceDrawer != null) {
            pieceDrawer.abortMove();
        }
    }

    /**
     * Moves the piece to a new position and handles king promotion if applicable.
     *
     * @param move The move object containing the destination coordinates.
     */
    public void movePiece(Move move) {
        x = move.getToX();
        y = move.getToY();
        if (pieceDrawer != null) {
            pieceDrawer.move(move.getToX(), move.getToY());
        }
        handleKingPromotion(move);
    }

    /**
     * Undoes a move, reverting the piece to its previous position and handling king demotion if applicable.
     *
     * @param move The move object containing the previous coordinates.
     */
    public void undoMove(Move move) {
        x = move.getFromX();
        y = move.getFromY();
        if (pieceDrawer != null) {
            pieceDrawer.move(move.getFromX(), move.getFromY());
        }
        handleKingDemotion(move);
    }

    /**
     * Handles promotion of the piece to a king if it reaches the opposite end of the board.
     *
     * @param move The move that potentially results in a promotion.
     */
    private void handleKingPromotion(Move move) {
        int newY = move.getToY();
        if (getType() == PieceType.BLACK && newY == SIZE - 1 && move.isTurnEnding()) {
            move.setPromotion(true);
            promoteToKing();
        } else if (getType() == PieceType.WHITE && newY == 0 && move.isTurnEnding()) {
            promoteToKing();
            move.setPromotion(true);
        }
    }

    /**
     * Handles demotion of the piece from a king to a regular piece if needed.
     *
     * @param move The move that potentially results in a demotion.
     */
    private void handleKingDemotion(Move move) {
        if (move.isPromotion()) {
            demoteToNormal();
            move.setPromotion(false);
        }
    }

    /**
     * Promotes the piece to a king and updates its visual representation.
     */
    public void promoteToKing() {
        if (type.color.equals("white") && type == PieceType.WHITE) {
            type = PieceType.WHITEKING;
        } else if (type.color.equals("black") && type == PieceType.BLACK) {
            type = PieceType.BLACKKING;
        }
        if (pieceDrawer != null) {
            pieceDrawer.promoteToKing();
        }
    }

    /**
     * Demotes the piece from a king to a regular piece and updates its visual representation.
     */
    public void demoteToNormal() {
        if (type.color.equals("white") && type == PieceType.WHITEKING) {
            type = PieceType.WHITE;
        } else if (type.color.equals("black") && type == PieceType.BLACKKING) {
            type = PieceType.BLACK;
        }
        if (pieceDrawer != null) {
            pieceDrawer.demoteToNormal();
        }
    }

    /**
     * Converts the piece to a string representation for debugging.
     *
     * @return A string describing the piece, including its type and position.
     */
    @Override
    public String toString() {
        return "Piece [type=" + type + ", x=" + x + ", y=" + y + "]";
    }

    /**
     * Gets the x-coordinate of the piece.
     *
     * @return The x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the piece.
     *
     * @return The y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Converts a list of moves into a set of unique pieces involved in those moves.
     *
     * @param availableMoves The list of moves to analyze.
     * @return A set of unique pieces involved in the moves.
     */
    public static Set<Piece> movesToPieces(List<Move> availableMoves) {
        Set<Piece> pieces = new HashSet<>();
        for (Move move : availableMoves) {
            pieces.add(move.getPiece());
        }
        return pieces;
    }
}
