package org.testing.project_2_1;

import static org.testing.project_2_1.CheckersApp.SIZE;
import java.util.ArrayList;

import javafx.scene.layout.Pane;

public class GameLogic {
    public Tile[][] board = new Tile[SIZE][SIZE];
    public boolean isWhiteTurn;
    public int turnCounter;
    public ArrayList<Piece> whiteList;
    public ArrayList<Piece> blackList;
    public ArrayList<Capture> availableCaptures;
    public CheckersApp app;

    public GameLogic(CheckersApp app) {
        this.app = app;
        new Pane();
        isWhiteTurn = true;
        turnCounter = 0;
        whiteList = new ArrayList<>();
        blackList = new ArrayList<>();
        availableCaptures = new ArrayList<>();
        setUpBoard();
    }

    private void setUpBoard(){
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = new Tile(x, y);
                board[x][y] = tile;

                if (y <= 3 && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.BLACK, x, y);
                    tile.setPiece(piece);
                    blackList.add(piece);
                } else if (y >= 6 && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.WHITE, x, y);
                    tile.setPiece(piece);
                    whiteList.add(piece);
                }

            }
        }
    }

    private void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
        if (isWhiteTurn) {
            turnCounter++;    
        }
        availableCaptures = checkAvailableCaptures();
    }

    public ArrayList<Capture> checkAvailableCaptures() {
        ArrayList<Capture> availableCaptures = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        for (Piece piece : pieces) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    if (tryMove(piece, i, j).getType() == MoveType.CAPTURE) {
                        availableCaptures.add(new Capture(piece, i, j));
                        
                    }
                }
            }
        }
        return availableCaptures;
    }

    public boolean hasAvailableCaptures(){
        availableCaptures = checkAvailableCaptures();
        if (availableCaptures.size() > 0) {
            return true;
        }
        return false;
    }

    public ArrayList<Piece> getListOfPieces() {
        if (isWhiteTurn) {
            return whiteList;
        }
        else {
            return blackList;
        }
    }

    public boolean takeTurn(Piece piece, int newX, int newY) {
        MoveResult result;
        // Keep trying to move until a valid move is made
        result = tryMove(piece, newX, newY);
        System.out.println(result.toString());
        if (result.getType() == MoveType.INVALID) {
            System.out.println("Invalid fucking move");
            piece.pieceDrawer.abortMove();
            return false;
        }
            // If you have available captures, you must make a capture
            System.out.println(checkAvailableCaptures().size());
            if (hasAvailableCaptures()) {
                // while you can capture, keep capturing until you can't
                while (hasAvailableCaptures()) {
                    for (Capture capture : availableCaptures) {
                        if (result.getType() == MoveType.CAPTURE) {
                            if (capture.equals(result.getCapture())) {
                                movePiece(result, piece, newX, newY);
                            }
                        }
                    }
                }
            }
            else { // If you don't have available captures, make a normal move
                movePiece(result, piece, newX, newY);
            }
        switchTurn();
        return true;
    }

    private void movePiece(MoveResult result, Piece piece, int newX, int newY) {
            switch (result.getType()) {
                case INVALID:
                System.out.println("Invalid move");
                    piece.pieceDrawer.abortMove();
                    break;
                case NORMAL:
                    System.out.println("Normal move");
                    board[piece.x][piece.y].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    piece.pieceDrawer.move(newX, newY);
                    piece.x = newX;
                    piece.y = newY;
                    handleKingPromotion(piece, newY);
                    break;
                case CAPTURE:
                    System.out.println("Capture");
                    piece.pieceDrawer.move(newX, newY);
                    board[piece.x][piece.y].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    Piece otherPiece = result.getPieceTaken();
                    board[otherPiece.x][otherPiece.y].setPiece(null);
                    app.pieceGroup.getChildren().remove(otherPiece.pieceDrawer);
                    piece.x = newX;
                    piece.y = newY;
                    handleKingPromotion(piece, newY);
                    break;
            }
    }

    private void handleKingPromotion(Piece piece, int newY) {
        if (piece.getType() == PieceType.BLACK && newY == SIZE - 1) {
            piece.promoteToKing();
            piece.pieceDrawer.promoteToKing();
        } else if (piece.getType() == PieceType.WHITE && newY == 0) {
            piece.promoteToKing();
            piece.pieceDrawer.promoteToKing();
        }
    }

    public MoveResult tryMove(Piece piece, int newX, int newY) {
        int x0 = piece.x;
        int y0 = piece.y;
        Tile tile = board[newX][newY];

        // Check if it's the correct player's turn
        if (isWhiteTurn != piece.getType().color.equals("white")) {return new MoveResult(MoveType.INVALID);}

        // Check if the tile is empty
        if (tile.hasPiece()) {return new MoveResult(MoveType.INVALID);}

        // Check if the tile is black
        if (!tile.isBlack()) {return new MoveResult(MoveType.INVALID);}

        boolean isKing = piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING;

        // If the piece is a king, allow multi-tile diagonal moves and captures
        if (isKing) {
            // Check for normal diagonal move (multi-tile)
            if (isMoveDiagonal(x0, y0, newX, newY) && isPathClear(x0, y0, newX, newY)) {
                return new MoveResult(MoveType.NORMAL);
            }

            // Check for diagonal capture for king
            if (Math.abs(newX - x0) >= 2 && Math.abs(newY - y0) >= 2 && isCapturePath(x0, y0, newX, newY)) {
                Piece capturedPiece = getCapturedPieceOnPath(x0, y0, newX, newY);
                Capture capture = new Capture(capturedPiece, newX, newY);
                return new MoveResult(MoveType.CAPTURE, capture);
            }
        }

        else {
            // Normal diagonal move for regular pieces
        if (isMoveDiagonalNormal(x0, y0, newX, newY) && piece.getType().moveDir == (newY - y0)) {return new MoveResult(MoveType.NORMAL);}


        // Horizontal capture logic for normal pieces
        if (newY == y0 && Math.abs(newX - x0) == 4) {
            int x1 = (newX + x0) / 2;
            Tile halfWay = board[x1][y0];
            if (halfWay.hasPiece() && !halfWay.getPiece().getType().color.equals(piece.getType().color)) {
                Capture capture = new Capture(halfWay.getPiece(), newX, newY);
                return new MoveResult(MoveType.CAPTURE, capture);
            }
        }

        // Vertical capture logic for normal pieces
        if (newX == x0 && Math.abs(newY - y0) == 4) {
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x0][y1];
            if (halfWay.hasPiece() && !halfWay.getPiece().getType().color.equals(piece.getType().color)) {
                Capture capture = new Capture(halfWay.getPiece(), newX, newY);
                return new MoveResult(MoveType.CAPTURE, capture);
            }
        }

        // Diagonal capture logic for normal pieces
        if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == 2) {
            int x1 = (newX + x0) / 2;
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x1][y1];
            if (halfWay.hasPiece() && !halfWay.getPiece().getType().color.equals(piece.getType().color)) {
                Capture capture = new Capture(halfWay.getPiece(), newX, newY);
                return new MoveResult(MoveType.CAPTURE, capture);
            }
        }
    }

    return new MoveResult(MoveType.INVALID);
    }

    public boolean isGameOver(){
        if (whiteList.size() == 0 || blackList.size() == 0) {
            return true;
        }
        return false;
    }

    // Helper method to check if move is diagonal for king
    private boolean isMoveDiagonal(int x0, int y0, int newX, int newY) {
        return Math.abs(newX - x0) == Math.abs(newY - y0);
    }

    // Helper method to check if move is diagonal for normal pieces
    private boolean isMoveDiagonalNormal(int x0, int y0, int newX, int newY) {
        return Math.abs(newX - x0) == 1 && Math.abs(newY - y0) == 1;
    }

    // Check if the path for king movement (diagonal, horizontal, vertical) is clear
    private boolean isPathClear(int x0, int y0, int newX, int newY) {
        int dx = Integer.signum(newX - x0);
        int dy = Integer.signum(newY - y0);

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
    private boolean isCapturePath(int x0, int y0, int newX, int newY) {
        int dx = Integer.signum(newX - x0);
        int dy = Integer.signum(newY - y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null && board[x][y].getPiece().getType() != board[x0][y0].getPiece().getType()) {
                    capturedPiece = board[x][y].getPiece();  // Store the opponent piece for capture
                } else {
                    return false;  // Path is blocked by more than one piece
                }
            }
            x += dx;
            y += dy;
        }

        return capturedPiece != null;  // Return true if there's exactly one piece to capture
    }

    // Return the piece to capture along the path
    private Piece getCapturedPieceOnPath(int x0, int y0, int newX, int newY) {
        int dx = Integer.signum(newX - x0);
        int dy = Integer.signum(newY - y0);

        int x = x0 + dx;
        int y = y0 + dy;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece() && board[x][y].getPiece().getType() != board[x0][y0].getPiece().getType()) {
                return board[x][y].getPiece();  // Return the capturable piece
            }
            x += dx;
            y += dy;
        }

        return null;  // No capturable piece found
    }

    public static void main(String[] args) {
        CheckersApp app = new CheckersApp();
        GameLogic game = new GameLogic(app);
        Piece piece = game.whiteList.get(0);
        game.takeTurn(piece, 2, 5);
        game.takeTurn(piece, 3, 4);
        System.out.println(game.hasAvailableCaptures());
    }
}
