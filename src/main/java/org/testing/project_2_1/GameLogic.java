package org.testing.project_2_1;

import static org.testing.project_2_1.CheckersApp.SIZE;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.layout.Pane;

public class GameLogic {
    public Tile[][] board = new Tile[SIZE][SIZE];
    public boolean isWhiteTurn;
    public int turnCounter;
    public ArrayList<Piece> whitePieces;
    public ArrayList<Piece> blackPieces;
    public ArrayList<Capture> availableCaptures;
    public CheckersApp app;
    public Agent agent;
    public Agent opponent;
    public ArrayList<Move> movesPlayed = new ArrayList<Move>();

    public GameLogic(CheckersApp app) {
        this.agent = null;
        setStandardValues(app);
    }

    public GameLogic(CheckersApp app, Agent agent, boolean isAgentWhite) {
        this.agent = agent;
        agent.setGameLogic(this);
        setStandardValues(app); 
    }

    public GameLogic(CheckersApp app, Agent agent1, Agent agent2) {
        this.agent = agent1;
        this.opponent = agent2;
        agent1.setGameLogic(this);
        agent2.setGameLogic(this);
        setStandardValues(app);
    }

    public void setStandardValues(CheckersApp app) {
        this.app = app;
        new Pane(); // for some reason this is needed to avoid a null pointer exception
        isWhiteTurn = true;
        turnCounter = 0;
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        availableCaptures = new ArrayList<>();
        setUpBoard();
    }

    public void setUpBoard(){
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = new Tile(x, y);
                this.board[x][y] = tile;

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

    public void restartGame(){
        isWhiteTurn = true;
        whitePieces.clear();
        blackPieces.clear();
        turnCounter = 0;
        movesPlayed.clear();

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                board[x][y].setPiece(null);
            }
        }
            setUpBoard();
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
        if (agent != null && agent.isWhite() == isWhiteTurn) {
            agent.makeMove();
        }
        if (opponent != null && agent.isWhite() != isWhiteTurn) {
            opponent.makeMove();
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
            return whitePieces;
        }
        else {
            return blackPieces;
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
            piece.pieceDrawer.abortMove();
            return
                    false;
        }        
        // If you have available captures with any piece, you must make a capture
        if (hasAvailableCaptures()) {
            System.out.println("has available captures");
            // If the move is a capture, make the move and check for more captures
            if (move.getType() == MoveType.CAPTURE) {
                movePiece(move);
                checkAvailableCaptures();
                app.updateCaptureMessage(" ");

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
                app.updateCaptureMessage(piece.getType().color + " must capture!");

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
                movesPlayed.add(move);
                board[piece.x][piece.y].setPiece(null);
                board[newX][newY].setPiece(piece);
                piece.pieceDrawer.move(newX, newY);
                piece.x = newX;
                piece.y = newY;
                handleKingPromotion(piece, newY);
                break;
            case CAPTURE:
                movesPlayed.add(move);
                Capture capture = (Capture) move;

                if (capture.getCapturedPiece().type.color.equals("white")) {
                    whitePieces.remove(capture.getCapturedPiece());
                    app.capturedPiecesTracker.capturePiece("Player 2");
                    System.out.println("piece taken: " + capture.getCapturedPiece().toString());
                }
                else {
                    blackPieces.remove(capture.getCapturedPiece());
                    app.capturedPiecesTracker.capturePiece("Player 1");
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

    public void undoLastMove() {
        Move lastMove = movesPlayed.get(movesPlayed.size() - 1);
        Piece piece = lastMove.getPiece();
        if (lastMove.type == MoveType.NORMAL) {
            if (promotedLastMove(lastMove)) {
                piece.demoteToNormal();   
            }
            piece.x = lastMove.fromX;
            piece.y = lastMove.fromY;
            board[lastMove.toX][lastMove.toY].setPiece(null);
            board[lastMove.fromX][lastMove.fromY].setPiece(piece);
            piece.pieceDrawer.move(lastMove.fromX, lastMove.fromY);
            movesPlayed.remove(lastMove);
            turnCounter--;
            switchTurn();
        }
        else if (lastMove.isCapture()) {
            // TODO: implement undo for captures
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

    private boolean promotedLastMove(Move lastMove) {
        Piece piece = lastMove.getPiece();
        if (piece.getType() == PieceType.BLACK && lastMove.fromY == SIZE - 1) {
            return true;
        } else if (piece.getType() == PieceType.WHITE && lastMove.fromY == 0) {
            return true;
        }
        return false;
    }

    public Move determineMoveType(Piece piece, int newX, int newY) {
        int x0 = piece.x;
        int y0 = piece.y;
        Tile tile = board[newX][newY];

        // Check if it's the correct player's turn
        if (isWhiteTurn != piece.getType().color.equals("white")) {
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

        // If the piece is a king, allow all types of moves and captures
        if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            // Check for normal king move any direction (multi-tile)
            if (isMoveforKing(x0, y0, newX, newY) && isPathClearforKing(x0, y0, newX, newY)) {
                return new NormalMove(piece, newX, newY);
            }

            // Check for king capture any direction
            if (isCapturePathforKing(x0, y0, newX, newY)) {
                Piece capturedPiece = getCapturedPieceOnPathforKing(x0, y0, newX, newY);
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
        if (whitePieces.size() == 0 || blackPieces.size() == 0) {
            return true;
        }
        return false;
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
    public boolean isPathClearforKing(int x0, int y0, int newX, int newY) {
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

    public Set<Piece> getPiecesTheathenedBy(ArrayList<Piece> pieces) {
            Set<Piece> threatenedPieces = new HashSet<>();
            for (Piece piece : pieces) {
                for (Move move : getLegalMoves(piece)) {
                    if (move.isCapture()) {
                        Capture captureMove = (Capture) move;
                        threatenedPieces.add(captureMove.getCapturedPiece());
                    }
                }
            }
            return threatenedPieces;
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

    public Piece getPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].getPiece();  // Assuming each tile has a method getPiece()
        }
        return null;
    }

    public double evaluateBoard() {
        // page 8 of Machine Learning by Tom M. Mitchell
        // xl: the number of black pieces on the board 
        // x2: the number of white pieces on the board 
        // x3: the number of black kings on the board 
        // x4: the number of white kings on the board 
        // x5: the number of black pieces threatened by white (i.e., which can be captured on white's next turn) 
        // X6: the number of white pieces threatened by black 
        // w1, w2, w3, w4, w5, w6: weights for the six features
        int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0;
        // evaluation positive for white, negative for black
        double w1 = -1, w2 = 1, w3 = -3, w4 = 3, w5 = 1, w6 = -1;

        for (Piece piece : blackPieces) {
            if (piece.type == PieceType.BLACK) {
                x1++;
            } else {
                x3++;
            }
        }

        for (Piece piece : whitePieces) {
            if (piece.type == PieceType.WHITE) {
                x2++;
            } else {
                x4++;
            }
        }
            
        Set<Piece> threatenedPieces = getPiecesTheathenedBy(whitePieces);
        x5 = threatenedPieces.size();
        threatenedPieces.clear();
        threatenedPieces = getPiecesTheathenedBy(blackPieces);
        x6 = threatenedPieces.size();
        System.out.println("Evaluation: " +  (w1 * x1 + w2 * x2 + w3 * x3 + w4 * x4 + w5 * x5 + w6 * x6) +
         " x1: " + x1 + " x2: " + x2 + " x3: " + x3 + " x4: " + x4 + " x5: " + x5 + " x6: " + x6);
        return w1 * x1 + w2 * x2 + w3 * x3 + w4 * x4 + w5 * x5 + w6 * x6;
        
    }

    // Timer 
    public void processMove(PlayerTimer currentPlayerTimer, PlayerTimer opponentTimer, boolean isLegalMove) {
        currentPlayerTimer.startMove(isLegalMove);  // Stops and increments current player's timer if the move is legal
        
        if (isLegalMove) {
            opponentTimer.startCountdown();  // Start the opponent's timer
        }
    }


}
