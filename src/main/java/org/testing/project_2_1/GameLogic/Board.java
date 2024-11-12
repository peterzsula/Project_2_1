package org.testing.project_2_1.GameLogic;
import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.ArrayList;

import org.testing.project_2_1.Moves.Capture;
import org.testing.project_2_1.Moves.InvalidMove;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.NormalMove;
import org.testing.project_2_1.Moves.Turn;

public class Board {
    protected Tile[][] board;
    protected boolean isWhiteTurn;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private ArrayList<Move> movesPlayed;
    private ArrayList<Turn> turnsPlayed;

    public Board(){
        isWhiteTurn = true;
        whitePieces = new ArrayList<Piece>();
        blackPieces = new ArrayList<Piece>();
        movesPlayed = new ArrayList<Move>();
        turnsPlayed = new ArrayList<Turn>();
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
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
                boardCopy[row][col] = new Tile(row, col);
                if (board[row][col].hasPiece()) {
                    Piece originalPiece = board[row][col].getPiece();
                    Piece piece = new Piece(originalPiece.type, row, col);
                    boardCopy[row][col].setPiece(piece);
                    if (piece.type == PieceType.WHITE || piece.type == PieceType.WHITEKING) {
                        whitePieces.add(boardCopy[row][col].getPiece());
                    } 
                    else {
                        blackPieces.add(boardCopy[row][col].getPiece());
                    }
                }
            }
        }
        System.out.println(this.toString());
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

    public ArrayList<Move> getMovesPlayed() {
        return movesPlayed;
    }

    public ArrayList<Turn> getTurnsPlayed() {
        return turnsPlayed;
    }

    public void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
    }

    public boolean move(Move move) {
        Piece piece = board[move.getFromX()][move.getFromY()].getPiece();
        movesPlayed.add(move);
        board[move.getToX()][move.getToY()].setPiece(piece);
        piece.movePiece(move);
        board[move.getFromX()][move.getFromY()].setPiece(null);
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = board[capture.getCapturedPiece().getX()][capture.getCapturedPiece().getY()].getPiece();
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(null);
            removeCapturedPiece(capturedPiece);
        }
        if (move.isTurnEnding()) {
            switchTurn();
        }
        return true;
    }

    private void removeCapturedPiece(Piece piece){
        if (piece.type.color.equals("white")) {
            whitePieces.remove(piece);
        }
        else {
            blackPieces.remove(piece);
        }
    }

    public Tile[][] undoMove(Move move){
        Piece piece = board[move.getToX()][move.getToY()].getPiece();
        if (move.isNormal()) {
            piece.undoMove(move);
            board[move.getFromX()][move.getFromY()].setPiece(piece);
            board[move.getToX()][move.getToY()].setPiece(null);
        }
        else if (move.isCapture()) {
            piece.undoMove(move);
            Capture capture = (Capture) move;
            Piece capturedPiece = board[capture.getCapturedPiece().getX()][capture.getCapturedPiece().getY()].getPiece();
            board[move.getFromX()][move.getFromY()].setPiece(piece);
            board[move.getToX()][move.getToY()].setPiece(null);
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(capturedPiece);
            if (capturedPiece.type.color.equals("white")) {
                whitePieces.add(capturedPiece);
            }
            else {
                blackPieces.add(capturedPiece);
            }
        }
        if (move.isTurnEnding()) {
            switchTurn();
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
    
    public Move determineMoveType(int x0, int y0, int newX, int newY) {
        Piece piece = board[x0][y0].getPiece();
        Tile tile = board[newX][newY];

        // Check if it's the correct player's turn
        if (isWhiteTurn != piece.getType().color.equals("white")) {
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // Check if the tile is empty
        if (tile.hasPiece()) {
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // Check if the tile is black
        if (!tile.isBlack()) {
            System.out.println("white tile");
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // If the piece is a king, allow all types of moves and captures
        if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            // Check for normal king move any direction (multi-tile)
            if (isMoveforKing(x0, y0, newX, newY) && isPathClearforKing(x0, y0, newX, newY)) {
                return new NormalMove(x0, y0, piece, newX, newY);
            }

            // Check for king capture any direction
            if (isCapturePathforKing(x0, y0, newX, newY)) {
                Piece capturedPiece = getCapturedPieceOnPathforKing(x0, y0, newX, newY);
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }

        else {
            // Normal diagonal move for regular pieces
        if (isMoveDiagonalNormal(x0, y0, newX, newY) && piece.getType().moveDir == (newY - y0)) {return new NormalMove(x0, y0, piece, newX, newY);}


        // Horizontal capture logic for normal pieces
        if (newY == y0 && Math.abs(newX - x0) == 4) {
            int x1 = (newX + x0) / 2;
            Tile halfWay = board[x1][y0];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }

        // Vertical capture logic for normal pieces
        if (newX == x0 && Math.abs(newY - y0) == 4) {
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x0][y1];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }

        // Diagonal capture logic for normal pieces
        if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == 2) {
            int x1 = (newX + x0) / 2;
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x1][y1];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }
    }

    return new InvalidMove(x0, y0, piece, newX, newY);
    }

    // Helper method to check if move is available for king
    private boolean isMoveforKing(int x0, int y0, int newX, int newY) {
        return (x0 == newX || y0 == newY || Math.abs(newX - x0) == Math.abs(newY - y0));
    }

    // Helper method to check if move is diagonal for normal pieces
    private boolean isMoveDiagonalNormal(int x0, int y0, int newX, int newY) {
        return Math.abs(newX - x0) == 1 && Math.abs(newY - y0) == 1;
    }

    // Check if the path for king movement (diagonal, horizontal, vertical) is clear
    private boolean isPathClearforKing(int x0, int y0, int newX, int newY) {
        int dx = Integer.compare(newX, x0);
        int dy = Integer.compare(newY, y0);
    
        int x = x0 + dx;
        int y = y0 + dy;
    
        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                return false;  // Path is blocked
            }
            x += dx;
            y += dy;
        }
        return true;
    } 

    // Check if there is a capturable piece on the path
    private boolean isCapturePathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveforKing(x0, y0, newX, newY)) {
            return false;  // Not a move for the burger king
        }

        int dx = Integer.compare(newX , x0);
        int dy = Integer.compare(newY , y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  // Found an opponent's piece to capture
                        capturedX=x;
                        capturedY=y;
                    } else {
                        return false;  // Path is blocked by a friendly piece
                    }
                } else {
                    return false;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }
        // Ensure capturing piece can only land 1 square after the captured one
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            // Add error margin for vertical/horizontal captures
            if (Math.abs(newX - landingX) <= 1 && Math.abs(newY - landingY) <= 1) {
                return true;  // Immediately after the captured piece
            } else {
                return false;  // Not immediately after
            }
        }

        return false;  // No capturable piece wis found
    }

    // Return the piece to capture along the path
    private Piece getCapturedPieceOnPathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveforKing(x0, y0, newX, newY)) {
            return null;  // Not a move for the burger king
        }
        int dx = Integer.compare(newX , x0);
        int dy = Integer.compare(newY , y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  //Found an opponent's piece to capture
                        capturedX = x;
                        capturedY = y;
                    } else {
                        return null;  // Path is blocked by a friendly piece
                    }
                } else {
                    return null;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }
        // Ensure capturing piece can only land 1 square after the captured one
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            if (Math.abs(newX - landingX) <= 1 && Math.abs(newY - landingY) <= 1) {
                return capturedPiece;  // Valid capture
            } else {
                return null;  // not immediately after
            }
        }

        return null; // No valid capture is found
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (!board[x][y].hasPiece())  {
                    sb.append("0");
                }
                else {
                    Piece piece = board[x][y].getPiece();
                    if (piece.getType() == PieceType.BLACK) {
                        sb.append("B");
                    } else if (piece.getType() == PieceType.WHITE) {
                        sb.append("W");
                    } else if (piece.getType() == PieceType.BLACKKING) {
                        sb.append("K");
                    } else if (piece.getType() == PieceType.WHITEKING) {
                        sb.append("Q");
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
