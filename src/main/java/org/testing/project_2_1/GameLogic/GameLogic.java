package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Moves.*;
import org.testing.project_2_1.UI.CheckersApp;
import org.testing.project_2_1.UI.PlayerTimer;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javafx.scene.layout.Pane;

public class GameLogic {
    public CheckersApp app;
    public Agent agent;
    public Agent opponent;
    public Board b;
    public Tile[][] board;
    public MoveLogic moveLogic;
    public boolean isWhiteTurn = b.getIsWhiteTurn();
    public static ArrayList<Piece> whitePieces;
    public static ArrayList<Piece> blackPieces;
    public ArrayList<Move> movesPlayed;
    public ArrayList<Turn> turnsPlayed;
    

    public GameLogic(CheckersApp app) {
        this.agent = null;
        this.opponent = null;
        setStandardValues(app);
    }

    public GameLogic(CheckersApp app, Agent agent, boolean isAgentWhite) {
        this.agent = agent.reset();
        this.opponent = null;
        this.agent.setGameLogic(this);
        setStandardValues(app); 
    }

    public GameLogic(CheckersApp app, Agent agent1, Agent agent2) {
        this.agent = agent1.reset();
        this.opponent = agent2.reset();
        this.agent.setGameLogic(this);
        this.opponent.setGameLogic(this);
        setStandardValues(app);
    }

    public void setStandardValues(CheckersApp app) {
        this.app = app;
        this.b = new Board();
        this.board = new Board().getBoard();
        this.moveLogic = new MoveLogic(b);
        whitePieces = b.getWhitePieces();
        blackPieces = b.getBlackPieces();
        isWhiteTurn = b.getIsWhiteTurn();
        movesPlayed = new ArrayList<Move>();
        turnsPlayed = new ArrayList<Turn>();
        new Pane();
    }

    public boolean isGameOver(){
        if (whitePieces.size() == 0 || blackPieces.size() == 0) {
            return true;
        }
        return false;
    }

    public void restartGame(){
        setStandardValues(app);
    }

    private void switchTurn() {
        System.out.println();

        if (isWhiteTurn) {
            System.out.println("White's turn");
        }
        else {
            System.out.println("Black's turn");
        }
        printAvailableCaptures();
        if (agent != null && agent.isWhite() == isWhiteTurn) {
            agent.makeMove();
        }
        if (opponent != null && opponent.isWhite() == isWhiteTurn) {
            opponent.makeMove();
        }
    }

    public ArrayList<Move> getAvailableCaptures() {
        // TODO: improve complexity: improved to O(n^3)/2 from O(n^3)
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        for (Piece piece : pieces) {
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
        }
        return availableCaptures;
    }

    public ArrayList<Move> getAvailableCaptures(MoveLogic moveLogic) {
        Tile[][] board = moveLogic.getBoard();
        // TODO: improve complexity
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        for (Piece piece : pieces) {
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
        }
        return availableCaptures;
    }

    public ArrayList<Move> getAvailableCaptures(Piece piece) {
        // TODO: improve complexity
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

    public ArrayList<Move> getAvailableCaptures(Piece piece, MoveLogic moveLogic) {
        Tile[][] board = moveLogic.getBoard();
        // TODO: improve complexity
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
    
    public ArrayList<Turn> getLegalTurns() {
        ArrayList<Turn> availableTurns = new ArrayList<>();
        ArrayList<Move> availableMoves = getLegalMoves();
        if (availableMoves.get(0).isNormal()) {
            for (Move move : availableMoves) {
                Turn turn = new Turn();
                turn.addMove(move);
                availableTurns.add(turn);
            }
            return availableTurns;
        }
        for (Move move : availableMoves) {
            Turn turn = new Turn();
            turn.addMove(move);
            dfs(b, move.getPiece(), turn, 1);
        }
        return availableTurns;
    }    

    public ArrayList<Turn> getLegalTurns(MoveLogic moveLogic) {
        Board b = new Board(moveLogic.getBoard(), moveLogic.getIsWhiteTurn());
        ArrayList<Turn> availableTurns = new ArrayList<>();
        ArrayList<Move> availableMoves = getLegalMoves(moveLogic);
        if (availableMoves.get(0).isNormal()) {
            for (Move move : availableMoves) {
                move.setTurnEnding(true);
                Turn turn = new Turn();
                turn.addMove(move);
                availableTurns.add(turn);
            }
            return availableTurns;
        }
        for (Move move : availableMoves) {
            Turn turn = new Turn();
            turn.addMove(move);
            dfs(b, move.getPiece(), turn, 1);
        }
        return availableTurns;
    }

    private int maxCaptures;  // Keeps track of the maximum captures found so far
    private ArrayList<Turn> result;  

    public ArrayList<Turn> getLegalTurns(Piece piece) {
        maxCaptures = 0;
        result = new ArrayList<>();
        
        Turn initialTurn = new Turn();
        dfs(b, piece, initialTurn, 0);
        
        return result;
    }

    public ArrayList<Turn> getLegalTurns(Piece piece, Board b) {
        maxCaptures = 0;
        result = new ArrayList<>();
        
        Turn initialTurn = new Turn();
        dfs(b, piece, initialTurn, 0);
        
        return result;
    }

    private void dfs(Board b, Piece piece, Turn currentTurn, int captureCount) {
        MoveLogic moveLogic = new MoveLogic(b);
        // Get all available captures for the current state
        ArrayList<Move> captures = getAvailableCaptures(piece, moveLogic);
        
        if (captures.isEmpty()) {
            currentTurn.getMoves().getLast().setTurnEnding(true);
            // No more captures possible, so check if this turn is one of the max-capturing ones
            if (captureCount > maxCaptures) {
                result.clear();
                result.add(new Turn(currentTurn));  // Create a new turn to add to result
                maxCaptures = captureCount;
            } else if (captureCount == maxCaptures) {
                result.add(new Turn(currentTurn));
            }
            return;
        }
        
        // Iterate over each capture move and explore further captures
        for (Move capture : captures) {
            b.movePiece(capture);  // Make the move on a board copy
            currentTurn.addMove(capture);

            // Recursively continue to capture and increment captureCount
            dfs(b, piece, currentTurn, captureCount + 1);
            
            // Backtrack by undoing the last move and removing it from the current turn
            b.undoMove(capture);
            currentTurn.removeLastMove();
        }
    }

    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        boolean capturesAvailable = hasAvailableCaptures();
        if (capturesAvailable) {
            return getAvailableCaptures();
        }
        else {
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
    
    public ArrayList<Move> getLegalMoves(MoveLogic moveLogic) {
        Board b = moveLogic.getBoardObj();
        Tile[][] board = b.getBoard();
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces(moveLogic);
        boolean capturesAvailable = hasAvailableCaptures(moveLogic);
        if (capturesAvailable) {
            return getAvailableCaptures(moveLogic);
        }
        else {
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
            availableMoves = getAvailableCaptures(piece);
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

    public ArrayList<Move> getLegalMoves(Piece piece, MoveLogic moveLogic) {
        Board b = moveLogic.getBoardObj();
        Tile[][] tiles = b.getBoard();
        ArrayList<Move> availableMoves = new ArrayList<>();
        boolean capturesAvailable = hasAvailableCaptures(moveLogic);
        if (capturesAvailable) {
            availableMoves = getAvailableCaptures(piece, moveLogic);
        }
        else {
            for (int row = 0; row < tiles.length; row++) {
                int startCol = (row % 2 == 0) ? 1 : 0;
                for (int col = startCol; col < tiles.length; col += 2){
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
        if (getAvailableCaptures().size() > 0) {
            return true;
        }
        return false;
    }

    public boolean hasAvailableCaptures(MoveLogic moveLogic){
        if (getAvailableCaptures(moveLogic).size() > 0) {
            return true;
        }
        return false;
    }

    public boolean hasAvailableCaptures(Piece piece){
        if (getAvailableCaptures(piece).size() > 0) {
            return true;

        }
        return false;
    }

    public boolean hasAvailableCaptures(Piece piece, MoveLogic moveLogic){
        if (getAvailableCaptures(piece, moveLogic).size() > 0) {
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

    public ArrayList<Piece> getListOfPieces() {
        if (isWhiteTurn) {
            return whitePieces;
        }
        else {
            return blackPieces;
        }
    }

    public ArrayList<Piece> getListOfPieces(MoveLogic moveLogic) {
        Board b = moveLogic.getBoardObj();
        if (isWhiteTurn) {
            return b.getWhitePieces();
        }
        else {
            return b.getBlackPieces();
        }
    }

    public boolean takeTurn(Turn turn){
        turnsPlayed.add(turn);
        LinkedList<Move> moves = turn.getMoves();
        while (!moves.isEmpty()) {
            if (!takeMove(moves.removeFirst())) {
                return false;
            }
        }
        return true;
    }

    public boolean takeMove(Move move) {
        Piece piece = move.getPiece();
        // print statements for debugging
        System.out.println(move.toString());
        if (move.getType() == MoveType.CAPTURE) {
            System.out.println(move.toString());
        }
        // if the move is invalid, abort the move, return false
        if (move.getType() == MoveType.INVALID) {
            piece.getPieceDrawer().abortMove();
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
                
                // If you don't have any more available captures, switch turns
                if (!hasAvailableCaptures(move.getPiece())) {
                    System.out.println("made all available captures");
                    switchTurn();
                    move.setTurnEnding(true);
                    return true;        
                }
                // If you have more available captures, make another capture
                else {
                    System.out.println("made a capture, must make another");
                    return false;
                }
            }
            // If the move is not a capture, abort the move and return false
            else {
                System.out.println("abort move");
                app.updateCaptureMessage(piece.getType().color + " must capture!");

                piece.getPieceDrawer().abortMove();
                return false;
            }
        }
        // If you don't have available captures, make a normal move
        else { 
            System.out.println("no available captures");
            movePiece(move);
            move.setTurnEnding(true);
            switchTurn();
            return true;
        }
    }

    private void movePiece(Move move) {
        Piece piece = board[move.getFromX()][move.getFromY()].getPiece();
        int oldX = move.getFromX();
        int oldY = move.getFromY();
        int newX = move.getToX();
        int newY = move.getToY();
        switch (move.getType()) {
            case INVALID:
                piece.getPieceDrawer().abortMove();
                break;
            case NORMAL:
                movesPlayed.add(move);
                board[oldX][oldY].setPiece(null);
                board[newX][newY].setPiece(piece);
                board[newX][newY].getPiece().movePiece(move);
                break;
            case CAPTURE:
                movesPlayed.add(move);
                Capture capture = (Capture) move;

                if (capture.getCapturedPiece().type.color.equals("white")) {
                    whitePieces.remove(capture.getCapturedPiece());
                    app.capturedPiecesTracker.capturePiece("Player 2");
                }
                else {
                    blackPieces.remove(capture.getCapturedPiece());
                    app.capturedPiecesTracker.capturePiece("Player 1");
                }
                
                board[oldX][oldY].setPiece(null);
                board[newX][newY].setPiece(piece);
                board[newX][newY].getPiece().movePiece(move);
                Piece otherPiece = capture.getCapturedPiece();
                board[otherPiece.getX()][otherPiece.getY()].setPiece(null);
                app.pieceGroup.getChildren().remove(otherPiece.getPieceDrawer());
                break;
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

    public Set<Piece> getPiecesTheathenedBy(ArrayList<Piece> pieces, MoveLogic moveLogic) {
        Set<Piece> threatenedPieces = new HashSet<>();
        for (Piece piece : pieces) {
            for (Move move : getLegalMoves(piece, moveLogic)) {
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

    public double evaluateBoard(Board board) {
        int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0;
        double w1 = -1, w2 = 1, w3 = -3, w4 = 3, w5 = 1, w6 = -1;

        for (Piece piece : board.getBlackPieces()) {
            if (piece.type == PieceType.BLACK) {
                x1++;
            } else {
                x3++;
            }
        }

        for (Piece piece : board.getWhitePieces()) {
            if (piece.type == PieceType.WHITE) {
                x2++;
            } else {
                x4++;
            }
        }
            
        Set<Piece> threatenedPieces = getPiecesTheathenedBy(board.getWhitePieces());
        x5 = threatenedPieces.size();
        threatenedPieces.clear();
        threatenedPieces = getPiecesTheathenedBy(board.getBlackPieces());
        x6 = threatenedPieces.size();
        return w1 * x1 + w2 * x2 + w3 * x3 + w4 * x4 + w5 * x5 + w6 * x6;
    }

    public double evaluateMove(Turn turn) {
        turnsPlayed.add(turn);
        LinkedList<Move> moves = turn.getMoves();
        while (!moves.isEmpty()) {
            movePiece(moves.removeFirst());
        }
        double evaluation = evaluateBoard();
        undoLastTurn();
        return evaluation;
    }

    private void undoLastTurn() {
        Turn turn = turnsPlayed.remove(turnsPlayed.size() - 1);
        LinkedList<Move> moves = turn.getMoves();
        while (!moves.isEmpty()) {
            undoMove(moves.removeLast());
        }
        switchTurn();
    }   

    private void undoMove(Move move) {
        Piece piece = move.getPiece();
        if (move.getType() == MoveType.NORMAL) {
            piece.undoMove(move);
            board[move.getToX()][move.getToY()].setPiece(null);
            board[move.getFromX()][move.getFromY()].setPiece(piece);
            movesPlayed.remove(move);
        }
        else if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = capture.getCapturedPiece();
            if (capturedPiece.getType().color.equals("white")) {
                whitePieces.add(capturedPiece);
                app.capturedPiecesTracker.decrementWhiteCaptured();
            }
            else {
                blackPieces.add(capturedPiece);
                app.capturedPiecesTracker.decrementBlackCaptured();
            }
            piece.undoMove(move);
            board[move.getToX()][move.getToY()].setPiece(null);
            board[move.getFromX()][move.getFromY()].setPiece(piece);
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(capturedPiece);
            app.pieceGroup.getChildren().add(capturedPiece.getPieceDrawer());
            movesPlayed.remove(move);
        }
    }

    public void undoLastMove() {
        Move lastMove = movesPlayed.get(movesPlayed.size() - 1);
        undoMove(lastMove);
    }

    public double evaluateTurn(Turn turn) {
        Board b0 = new Board(board, isWhiteTurn);
        MoveLogic m0 = new MoveLogic(b0);
        LinkedList<Move> moves = turn.getMoves();
        for (Move move : moves) {
            b0.movePiece(move);
        }
        Tile[][] board0 = b0.getBoard();
        ArrayList<Turn> opponentsTurns = getLegalTurns(m0);
        double[] evaluations = new double[opponentsTurns.size()];
        for (int i = 0; i < opponentsTurns.size(); i++) {
            Turn oTurn = opponentsTurns.get(i);
            Board b = new Board(board0, b0.getIsWhiteTurn());
        
            // Process each move in the current turn
            for (Move move : oTurn.getMoves()) {
                b.movePiece(move);
            }
        
            // Evaluate the board after processing the moves of this turn
            evaluations[i] = evaluateBoard(b);
            System.out.println("Evaluation: " + evaluations[i]);
        }
        double evaluation = 0;
        if (b.getIsWhiteTurn()) {
            evaluation = findMax(evaluations);
        }
        else {
            evaluation = findMin(evaluations);
        }
        return evaluation;
    }

    public static double findMin(double[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array must not be null or empty.");
        }
        double min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static double findMax(double[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array must not be null or empty.");
        }

        double max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }


}
