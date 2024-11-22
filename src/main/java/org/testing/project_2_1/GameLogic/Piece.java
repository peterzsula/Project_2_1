package org.testing.project_2_1.GameLogic;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.UI.PieceDrawer;

public class Piece {
    public PieceType type;
    private int x, y;
    private PieceDrawer pieceDrawer;
    private int consequtiveMovesPerKing;

    public Piece(PieceType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.pieceDrawer = null;
    }

    public PieceType getType() {
        return type;
    }

    public PieceDrawer getPieceDrawer() {
        return pieceDrawer;
    }

    public void setPieceDrawer(PieceDrawer pieceDrawer) {
        this.pieceDrawer = pieceDrawer;
    }

    public void abortMove() {
        if (pieceDrawer != null) {
            pieceDrawer.abortMove();
        }
    }

    public void movePiece(Move move){
        x = move.getToX();
        y = move.getToY(); 
        if (pieceDrawer != null) {
            pieceDrawer.move(move.getToX(), move.getToY());
        }
        handleKingPromotion(move);
    }

    public void undoMove(Move move){
        x = move.getFromX();
        y = move.getFromY();
        if (pieceDrawer != null) {
            pieceDrawer.move(move.getFromX(), move.getFromY()); 
        }
        handleKingDemotion(move);
    }  

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

    private void handleKingDemotion(Move move) {
        if (move.isPromotion()) {
            demoteToNormal();
            move.setPromotion(false);
        }
    }

    public void promoteToKing() {
        if (type == PieceType.WHITE ) {
            if (type.color.equals("white")){
                type = PieceType.WHITEKING;
                if (pieceDrawer != null) {
                    pieceDrawer.promoteToKing(); 
                }
            }
        }
        else if (type.color.equals("black")){
            type = PieceType.BLACKKING;
            if (pieceDrawer != null) {
                pieceDrawer.promoteToKing(); 
            }
        }
    }

    public void demoteToNormal() {
        if (type == PieceType.BLACKKING || this.type == PieceType.WHITEKING ) {
            if (type.color.equals("white")){
                type = PieceType.WHITE;
                if (pieceDrawer != null) {
                    pieceDrawer.demoteToNormal();   
                }
            }
            else if (type.color.equals("black")){
                type = PieceType.BLACK;
                if (pieceDrawer != null) {
                    pieceDrawer.demoteToNormal();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Piece [type=" + type + ", x=" + x + ", y=" + y + "]";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static Set<Piece> movesToPieces(List<Move> availableMoves){
        Set<Piece> pieces = new HashSet<>();
        for (Move move : availableMoves) {
            pieces.add(move.getPiece());
        }
        return pieces;
    }

}