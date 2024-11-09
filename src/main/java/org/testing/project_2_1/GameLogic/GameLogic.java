package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Moves.*;
import org.testing.project_2_1.UI.CheckersApp;
import org.testing.project_2_1.UI.PlayerTimer;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.layout.Pane;

public class GameLogic {
    private Tile[][] board;
    public static boolean isWhiteTurn;
    public int turnCounter;
    public static ArrayList<Piece> whitePieces;
    public static ArrayList<Piece> blackPieces;
    public ArrayList<Move> availableCaptures;
    public CheckersApp app;
    public Agent agent;
    public Agent opponent;
    public ArrayList<Move> movesPlayed;
    public ArrayList<Turn> turns;
    public MoveLogic moveLogic;

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
        isWhiteTurn = true;
        turnCounter = 0;
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        availableCaptures = new ArrayList<>();
        movesPlayed = new ArrayList<Move>();
        turns = new ArrayList<Turn>();
        moveLogic = new MoveLogic();
        new Board();
        this.board = Board.board;
        this.moveLogic = new MoveLogic();
        new Pane();
    }

    public boolean isGameOver(){
        if (whitePieces.size() == 0 || blackPieces.size() == 0) {
            return true;
        }
        return false;
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
            new Board();
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
        availableCaptures = getAvailableCaptures();
        printAvailableCaptures();
        if (agent != null && agent.isWhite() == isWhiteTurn) {
            agent.makeMove();
        }
        if (opponent != null && agent.isWhite() != isWhiteTurn) {
            opponent.makeMove();
        }
    }

    public ArrayList<Move> getAvailableCaptures() {
    //check all available captures for current player
        // TODO: improve complexity: improved to O(n^3)/2 from O(n^3)
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        for (Piece piece : pieces) {
            for (int row = 0; row < board.length; row++) {
                int startCol = (row % 2 == 0) ? 1 : 0;
                for (int col = startCol; col < board.length; col += 2){
                    Move move = MoveLogic.determineMoveType(piece, row, col);
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
    public ArrayList<Move> checkAvailableCaptures(Piece piece) {
        // TODO: improve complexity: improved to O(n^3)/2 from O(n^3)
        ArrayList<Move> availableCaptures = new ArrayList<>();
        for (int row = 0; row < board.length; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < board.length; col += 2){
                Move move = moveLogic.determineMoveType(piece, row, col);
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
        if (capturesAvailable) {
            return getAvailableCaptures();
        }
        else {
            System.out.println("no available captures line 153");
            for (Piece piece : pieces) {
                //TODO: instead of iterating over all black tiles, iterate over all tiles where the piece can move
                for (int row = 0; row < board.length; row++) {
                    int startCol = (row % 2 == 0) ? 1 : 0;
                    for (int col = startCol; col < board.length; col += 2){
                        Move move = moveLogic.determineMoveType(piece, row, col);
                        if (move.getType() == MoveType.NORMAL) {
                            availableMoves.add(move);
                        }
                    }
                }
            }
        }      
        return availableMoves;
    }

    public ArrayList<Move> getLegalMoves(Piece piece) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        boolean capturesAvailable = hasAvailableCaptures();
        if (capturesAvailable) {
            availableCaptures = checkAvailableCaptures(piece);
        }
        else {
            for (int row = 0; row < board.length; row++) {
                int startCol = (row % 2 == 0) ? 1 : 0;
                for (int col = startCol; col < board.length; col += 2){
                    Move move = moveLogic.determineMoveType(piece, row, col);
                    if (capturesAvailable && move.getType() == MoveType.CAPTURE) {
                        availableMoves.add(move);
                    }
                    else if (!capturesAvailable && move.getType() == MoveType.NORMAL) {
                        availableMoves.add(move);
                    }
                }
            }
        }
        return availableMoves;
    }

    public boolean hasAvailableCaptures(){
        availableCaptures = getAvailableCaptures();
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
            return false;
        }        
        // If you have available captures with any piece, you must make a capture
        if (hasAvailableCaptures()) {
            System.out.println("has available captures");
            // If the move is a capture, make the move and check for more captures
            if (move.getType() == MoveType.CAPTURE) {
                movePiece(move);
                getAvailableCaptures();
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
        if (lastMove.getType() == MoveType.NORMAL) {
            if (promotedLastMove(lastMove)) {
                piece.demoteToNormal();   
            }
            piece.x = lastMove.getFromX();
            piece.y = lastMove.getFromY();
            board[lastMove.getToX()][lastMove.getToY()].setPiece(null);
            board[lastMove.getFromX()][lastMove.getFromY()].setPiece(piece);
            piece.pieceDrawer.move(lastMove.getFromX(), lastMove.getFromY());
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
        if (piece.getType() == PieceType.BLACK && lastMove.getFromX() == SIZE - 1) {
            return true;
        } else if (piece.getType() == PieceType.WHITE && lastMove.getFromY() == 0) {
            return true;
        }
        return false;
    }

    public void printAvailableCaptures(){
        System.out.println("available captures: " + getAvailableCaptures().size());
        for (Move move : getAvailableCaptures()) {
            System.out.println(move.toString());
        }
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

    public double evaluateMove(Move move) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluateMove'");
    }


}
