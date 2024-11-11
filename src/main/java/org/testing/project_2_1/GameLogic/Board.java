package org.testing.project_2_1.GameLogic;
import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.ArrayList;

import org.testing.project_2_1.Moves.Capture;
import org.testing.project_2_1.Moves.Move;

public class Board {
    private MoveLogic moveLogic;
    private Tile[][] board;
    private boolean isWhiteTurn;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private ArrayList<Move> movesPlayed;

    public Board(){
        isWhiteTurn = true;
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

    public Board(Tile[][] board, boolean isWhiteTurn) {
        this.isWhiteTurn = isWhiteTurn;
        whitePieces = new ArrayList<Piece>();
        blackPieces = new ArrayList<Piece>();
        movesPlayed = new ArrayList<Move>();
        Tile[][] boardCopy = new Tile[SIZE][SIZE];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                boardCopy[row][col] = new Tile(row, col);
                if (board[row][col].hasPiece()) {
                    Piece originalPiece = board[row][col].getPiece();
                    Piece piece = new Piece(originalPiece.type, row, col);
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
        if (move.isTurnEnding()) {
            isWhiteTurn = !isWhiteTurn;
        }
        Piece piece = board[move.getFromX()][move.getFromY()].getPiece();
        if (move.isNormal()) {
            movesPlayed.add(move);
            board[move.getFromX()][move.getFromY()].setPiece(null);
            board[move.getToX()][move.getToY()].setPiece(piece);
            piece.movePiece(move);
            System.out.println(piece.toString());
        }
        else if (move.isCapture()) {
            movesPlayed.add(move);
            piece.movePiece(move);
            Capture capture = (Capture) move;
            Piece capturedPiece = board[capture.getCapturedPiece().getX()][capture.getCapturedPiece().getY()].getPiece();
            board[move.getFromX()][move.getFromY()].setPiece(null);
            board[move.getToX()][move.getToY()].setPiece(piece);
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(null);
        }
        return board;
    }

    public Tile[][] undoMove(Move move){
        Piece piece = board[move.getFromX()][move.getFromY()].getPiece();
        if (move.isNormal()) {
            board[move.getFromX()][move.getFromY()].setPiece(piece);
            board[move.getToX()][move.getToY()].setPiece(null);
            piece.undoMove(move);
        }
        else if (move.isCapture()) {
            piece.undoMove(move);
            Capture capture = (Capture) move;
            Piece capturedPiece = board[capture.getCapturedPiece().getX()][capture.getCapturedPiece().getY()].getPiece();
            board[move.getFromX()][move.getFromY()].setPiece(piece);
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

    public boolean getIsWhiteTurn() {
        return isWhiteTurn;
    }
    
}
