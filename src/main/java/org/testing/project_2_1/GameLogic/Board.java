package org.testing.project_2_1.GameLogic;
import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.ArrayList;

import org.testing.project_2_1.Moves.Capture;
import org.testing.project_2_1.Moves.Move;

public class Board {
    private Tile[][] board;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private ArrayList<Move> movesPlayed;

    public Board(){
        whitePieces = new ArrayList<Piece>();
        blackPieces = new ArrayList<Piece>();
        movesPlayed = new ArrayList<Move>();
        board = new Tile[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = new Tile(x, y);
                board[x][y] = tile;

                if (y <= 3 && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.BLACK, x, y);
                    tile.setPiece(piece);
                    blackPieces.add(piece);
                } else if (y >= 6 && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.WHITE, x, y);
                    tile.setPiece(piece);
                    whitePieces.add(piece);
                }

            }
        }

    }

    public Board(Tile[][] board) {
        whitePieces = new ArrayList<Piece>();
        blackPieces = new ArrayList<Piece>();
        movesPlayed = new ArrayList<Move>();
        Tile[][] boardCopy = new Tile[SIZE][SIZE];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                boardCopy[row][col] = new Tile(row, col);
                if (board[row][col].hasPiece()) {
                    Piece piece = board[row][col].getPiece();
                    boardCopy[row][col].setPiece(new Piece(piece.type, row, col));
                    if (piece.type == PieceType.WHITE || piece.type == PieceType.WHITEKING) {
                        whitePieces.add(boardCopy[row][col].getPiece());
                    } 
                    else {
                        blackPieces.add(boardCopy[row][col].getPiece());
                    }
                }
            }
        }
        this.board = boardCopy;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }

    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }

    public Tile[][] movePiece(Move move) {
        Piece piece = move.getPiece();
        int oldX = move.getFromX();
        int oldY = move.getFromY();
        int newX = move.getToX();
        int newY = move.getToY();
        if (move.isNormal()) {
            movesPlayed.add(move);
            board[move.getFromX()][move.getFromY()].setPiece(null);
            board[move.getToX()][move.getToY()].setPiece(move.getPiece());
            move.getPiece().movePiece(move);
        }
        else if (move.isCapture()) {
            movesPlayed.add(move);
            move.getPiece().movePiece(move);
            Capture capture = (Capture) move;
            Piece capturedPiece = capture.getCapturedPiece();
            board[move.getFromX()][move.getFromY()].setPiece(null);
            board[move.getToX()][move.getToY()].setPiece(move.getPiece());
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(null);
        }
        return board;
    }

    public Tile[][] undoMove(Move move){
        if (move.isNormal()) {
            board[move.getFromX()][move.getFromY()].setPiece(move.getPiece());
            board[move.getToX()][move.getToY()].setPiece(null);
            move.getPiece().undoMove(move);
        }
        else if (move.isCapture()) {
            move.getPiece().undoMove(move);
            Capture capture = (Capture) move;
            Piece capturedPiece = capture.getCapturedPiece();
            board[move.getFromX()][move.getFromY()].setPiece(move.getPiece());
            board[move.getToX()][move.getToY()].setPiece(null);
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(capturedPiece);
        }
        return board;
    }

    public Piece getPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].getPiece();  // Assuming each tile has a method getPiece()
        }
        return null;
    }

    public boolean hasPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].hasPiece();
        }
        return false;
    }
    
}
