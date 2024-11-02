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
    private Agent agent;
    public boolean isAgentWhite;

    public GameLogic(CheckersApp app) {
        this.agent = null;
        this.app = app;
        new Pane(); // for some reason this is needed to avoid a null pointer exception
        isWhiteTurn = true;
        turnCounter = 0;
        whiteList = new ArrayList<>();
        blackList = new ArrayList<>();
        availableCaptures = new ArrayList<>();
        setUpBoard();
    }

    public GameLogic(CheckersApp app, Agent agent, boolean isAgentWhite) {
        this.isAgentWhite = isAgentWhite;
        this.agent = agent;
        this.app = app;
        new Pane(); // for some reason this is needed to avoid a null pointer exception
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
        System.out.println();

        if (isWhiteTurn) {
            turnCounter++;    
            System.out.println("White's turn");
        }
        else {
            System.out.println("Black's turn");
        }
        availableCaptures = checkAvailableCaptures();
        printAvailableCaptures();
        if (agent != null && isAgentWhite == isWhiteTurn) {
            agent.makeMove();
        }
        
    }

    //check all available captures for current player
    public ArrayList<Capture> checkAvailableCaptures() {
        // TODO: improve complexity: improved to O(n^3)/2 from O(n^3)
        ArrayList<Capture> availableCaptures = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        for (Piece piece : pieces) {
            for (int row = 0; row < board.length; row++) {
                int startCol = (row % 2 == 0) ? 1 : 0;
                for (int col = startCol; col < board.length; col += 2){
                    Move move = determineMoveType(piece, row, col);
                    if (move.getType() == MoveType.CAPTURE) {
                        Capture capture = (Capture) move;
                        availableCaptures.add(capture);
                    }
                }
            }
        }
        return availableCaptures;
    }

    //check all available captures for current piece
    public ArrayList<Capture> checkAvailableCaptures(Piece piece) {
        // TODO: improve complexity: improved to O(n^3)/2 from O(n^3)
        ArrayList<Capture> availableCaptures = new ArrayList<>();
        for (int row = 0; row < board.length; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < board.length; col += 2){
                Move move = determineMoveType(piece, row, col);
                if (move.getType() == MoveType.CAPTURE) {
                    Capture capture = (Capture) move;
                    availableCaptures.add(capture);
                }
            }
        }
        return availableCaptures;
    }

    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        boolean capturesAvailable = hasAvailableCaptures();
                
        for (Piece piece : pieces) {
            //TODO: instead of iterating over all black tiles, iterate over all tiles where the piece can move
            for (int row = 0; row < board.length; row++) {
                int startCol = (row % 2 == 0) ? 1 : 0;
                for (int col = startCol; col < board.length; col += 2){
                    Move move = determineMoveType(piece, row, col);
                    if (capturesAvailable && move.getType() == MoveType.CAPTURE) {
                        availableMoves.add(move);
                    }
                    else if (move.getType() == MoveType.NORMAL) {
                        availableMoves.add(move);
                    }
                }
            }
        }
        return availableMoves;
    }

    public ArrayList<Move> getLegalMoves(Piece piece) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        boolean capturesAvailable = hasAvailableCaptures(piece);
        //TODO: instead of iterating over all black tiles, iterate over all tiles where the piece can move
        for (int row = 0; row < board.length; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < board.length; col += 2){
                Move move = determineMoveType(piece, row, col);
                if (capturesAvailable && move.getType() == MoveType.CAPTURE) {
                    availableMoves.add(move);
                }
                else if (move.getType() == MoveType.NORMAL) {
                    availableMoves.add(move);
                }
            }
        }
        return availableMoves;
    }

    public boolean hasAvailableCaptures(){
        availableCaptures = checkAvailableCaptures();
        if (availableCaptures.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean hasAvailableCaptures(Piece piece){
        availableCaptures = checkAvailableCaptures(piece);
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

    public boolean takeTurn(Move move) {
        Piece piece = move.getPiece();
        // print statements for debugging
        System.out.println(move.toString());
        if (move.getType() == MoveType.CAPTURE) {
            System.out.println(move.toString());
        }
        // if the move is invalid, abort the move, return false
        if (move.getType() == MoveType.INVALID) {
            System.out.println("Invalid move 129");
            piece.pieceDrawer.abortMove();
            return false;
        }        
        // If you have available captures with any piece, you must make a capture
        if (hasAvailableCaptures()) {
            System.out.println("has available captures");
            // If the move is a capture, make the move and check for more captures
            if (move.getType() == MoveType.CAPTURE) {
                movePiece(move);
                checkAvailableCaptures();
                //TODO: check if you are moving the piece as before
                // If you don't have any more available captures, switch turns
                if (!hasAvailableCaptures(move.getPiece())) {
                    System.out.println("made all available captures");
                    switchTurn();
                    return true;        
                }
                // If you have more available captures, make another capture
                else {
                    System.out.println("made a capture, must make another");
                    return true;
                }
            }
            // If the move is not a capture, abort the move and return false
            else {
                System.out.println("abort move");
                piece.pieceDrawer.abortMove();
                return false;
            }
        }
        // If you don't have available captures, make a normal move
        else { 
            System.out.println("no available captures");
            movePiece(move);
            switchTurn();
            return true;
        }
    }

    public void movePiece(Move move) {
        Piece piece = move.getPiece();
        int newX = move.getToX();
        int newY = move.getToY();
        switch (move.getType()) {
            case INVALID:
                piece.pieceDrawer.abortMove();
                break;
            case NORMAL:
                board[piece.x][piece.y].setPiece(null);
                board[newX][newY].setPiece(piece);
                piece.pieceDrawer.move(newX, newY);
                piece.x = newX;
                piece.y = newY;
                handleKingPromotion(piece, newY);
                break;
            case CAPTURE:
                Capture capture = (Capture) move;
                //TODO: increment captured pieces counter
                if (capture.getCapturedPiece().type.color.equals("white")) {
                    whiteList.remove(capture.getCapturedPiece());
                    System.out.println("piece taken: " + capture.getCapturedPiece().toString());
                }
                else {
                    blackList.remove(capture.getCapturedPiece());
                    System.out.println("piece taken: " + capture.getCapturedPiece().toString());                    
                }
                piece.pieceDrawer.move(newX, newY);
                board[piece.x][piece.y].setPiece(null);
                board[newX][newY].setPiece(piece);
                Piece otherPiece = capture.getCapturedPiece();
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

    public Move determineMoveType(Piece piece, int newX, int newY) {
        int x0 = piece.x;
        int y0 = piece.y;
        Tile tile = board[newX][newY];

        // Check if it's the correct player's turn
        if (isWhiteTurn != piece.getType().color.equals("white")) {
            System.out.println("wrong player's turn");
            return new InvalidMove(piece, newX, newY);
        }

        // Check if the tile is empty
        if (tile.hasPiece()) {
            return new InvalidMove(piece, newX, newY);
        }

        // Check if the tile is black
        if (!tile.isBlack()) {
            System.out.println("white tile");
            return new InvalidMove(piece, newX, newY);
        }

        // If the piece is a king, allow multi-tile diagonal moves and captures
        if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            // Check for normal diagonal move (multi-tile)
            if (isMoveDiagonalKing(x0, y0, newX, newY) && isPathClear(x0, y0, newX, newY)) {
                return new NormalMove(piece, newX, newY);
            }

            // Check for diagonal capture for king
            if (Math.abs(newX - x0) >= 2 && Math.abs(newY - y0) >= 2 && isCapturePath(x0, y0, newX, newY)) {
                Piece capturedPiece = getCapturedPieceOnPath(x0, y0, newX, newY);
                return new Capture(piece, capturedPiece, newX, newY);
            }
        }

        else {
            // Normal diagonal move for regular pieces
        if (isMoveDiagonalNormal(x0, y0, newX, newY) && piece.getType().moveDir == (newY - y0)) {return new NormalMove(piece, newX, newY);}


        // Horizontal capture logic for normal pieces
        if (newY == y0 && Math.abs(newX - x0) == 4) {
            int x1 = (newX + x0) / 2;
            Tile halfWay = board[x1][y0];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(piece, capturedPiece, newX, newY);
            }
        }

        // Vertical capture logic for normal pieces
        if (newX == x0 && Math.abs(newY - y0) == 4) {
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x0][y1];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(piece, capturedPiece, newX, newY);
            }
        }

        // Diagonal capture logic for normal pieces
        if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == 2) {
            int x1 = (newX + x0) / 2;
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x1][y1];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(piece, capturedPiece, newX, newY);
            }
        }
    }

    return new InvalidMove(piece, newX, newY);
    }

    public boolean isGameOver(){
        if (whiteList.size() == 0 || blackList.size() == 0) {
            return true;
        }
        return false;
    }

    // Helper method to check if move is diagonal for king
    private boolean isMoveDiagonalKing(int x0, int y0, int newX, int newY) {
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

        while (x != newX && y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  // Found an opponent's piece to capture
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

        if (capturedPiece != null) {
            int landingX = capturedPiece.x + dx;
            int landingY = capturedPiece.y + dy;
            if (landingX == newX && landingY == newY) {
                return true;
            } else {
                return false; // Landing square isnt right after the captured piece
            }
        }

        return false;  // No capturable piece found
    }

    // Return the piece to capture along the path
    private Piece getCapturedPieceOnPath(int x0, int y0, int newX, int newY) {
        int dx = Integer.signum(newX - x0);
        int dy = Integer.signum(newY - y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;



        while (x != newX && y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  // Found an opponent's piece to capture
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

        if (capturedPiece != null) {
            int landingX = capturedPiece.x + dx;
            int landingY = capturedPiece.y + dy;
            if (landingX == newX && landingY == newY) {
                return capturedPiece;
            } else {
                return null;
        }
        }

        return null;  // No capturable piece found
    }

    public void printAvailableCaptures(){
        System.out.println("available captures: " + checkAvailableCaptures().size());
        for (Capture capture : checkAvailableCaptures()) {
            System.out.println(capture.toString());
        }
    }

    // FOR HIGHLIGHTER CLASS
    public boolean hasPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].hasPiece();
        }
        return false;
    }
}
