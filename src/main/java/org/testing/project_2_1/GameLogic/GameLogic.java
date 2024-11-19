package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Moves.*;
import org.testing.project_2_1.UI.CheckersApp;
import org.testing.project_2_1.UI.PieceDrawer;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javafx.scene.layout.Pane;

public class GameLogic {
    public CheckersApp app;
    public Agent agent;
    public Agent opponent;
    public GameState g;   


    public GameLogic(CheckersApp app) {
        this.agent = null;
        this.opponent = null;
        setStandardValues(app);
    }

    public GameLogic(CheckersApp app, Agent agent) {
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
        g = new GameState();
        new Pane();
    }

    public boolean isGameOver(GameState board){
        if (board.getWhitePieces().size() == 0 || board.getBlackPieces().size() == 0) {
            return true;
        }
        return false;
    }

    public Tile[][] getBoard() {
        return g.getBoard();
    }
    
    public void restartGame(){
        setStandardValues(app);
    }

    private void askForMove() {
        app.updateGlows();
        app.updateEvaluationBar();
        System.out.println();
        if (g.isWhiteTurn) {
            System.out.println("White's turn");
        }
        else {
            System.out.println("Black's turn");
        }
        // printAvailableCaptures(g);
        if (agent != null && agent.isWhite() == g.isWhiteTurn) {
            agent.makeMove();
        }
        if (opponent != null && opponent.isWhite() == g.isWhiteTurn) {
            opponent.makeMove();
        }
    }

    public void printAvailableCaptures(GameState g){
        ArrayList<Turn> availableTurns = getLegalTurns(g);
        System.out.println("Nuber of available moves: " + availableTurns.size());
        for (Turn turn : availableTurns) {
            System.out.println(turn.getMoves().getFirst().toString());
        }
    }

    public static ArrayList<Turn> getLegalTurns(GameState g) {
        ArrayList<Move> availableMoves = getPossibleMoves(g);
        if (availableMoves.isEmpty()) {
            g.setGameOver(true);
            return new ArrayList<>();
        }
        if (availableMoves.get(0).isNormal()) {
            return Turn.copyMovesToTurns(availableMoves);
        }
        ArrayList<Turn> availableTurns = new ArrayList<>();
        Set<Piece> pieces = Piece.movesToPieces(availableMoves);
        int maxCaptures = 0;
        for (Piece piece : pieces) {
            ArrayList<Turn> pieceTurns = getLegalTurns(piece, g);
            for (Turn turn : pieceTurns) {
                //TODO: add 2 kings rule
                if (turn.getMoves().size() > maxCaptures) {
                    availableTurns.clear();
                    availableTurns.add(turn);
                    maxCaptures = turn.getMoves().size();
                }
                else if (turn.getMoves().size() == maxCaptures) {
                    availableTurns.add(turn);
                }
            }
        }

        return availableTurns;
    }

    public static ArrayList<Turn> getLegalTurns(Piece piece, GameState originalGS) {
        GameState g = new GameState(originalGS);
        ArrayList<Move> availableMoves = getPossibleMoves(piece, g);
        if (availableMoves.get(0).isNormal()) {
            return Turn.copyMovesToTurns(availableMoves);
        }
        DepthFirstSearch.resetMaxCaptures();
        DepthFirstSearch.resetResult();
        Turn initialTurn = new Turn();
        DepthFirstSearch.dfs(g, piece, initialTurn, 0);
        ArrayList<Turn> result = DepthFirstSearch.getResult();
        return result;
    }

    public static ArrayList<Move> getCaptures(GameState g) {
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces(g);
        for (Piece piece : pieces) {
            availableCaptures.addAll(getCaptures(piece, g));
        }
        return availableCaptures;
    }
    
    public static ArrayList<Move> getCaptures(Piece piece, GameState g) {
        ArrayList<Move> availableCaptures = new ArrayList<>();
        // TODO: instead of iterating over all black tiles, iterate over all tiles where the piece can move
        for (int row = 0; row < SIZE; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < SIZE; col += 2){
                Move move = g.determineMoveType(piece.getX(), piece.getY(), row, col);
                if (move.isCapture()) {
                    availableCaptures.add(move);
                }
            }
        }
        return availableCaptures;
    }

    private static ArrayList<Move> getPossibleMoves(GameState b) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Move> currentMoves = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces(b);
        for (Piece piece : pieces) {
            //TODO: instead of iterating over all black tiles, iterate over all tiles where the piece can move
            currentMoves = getPossibleMoves(piece, b);
            if (currentMoves.isEmpty()) {
                continue;
            } else if (currentMoves.get(0).isCapture()) {
                availableCaptures.addAll(currentMoves);
            } else {
                availableMoves.addAll(currentMoves);
            }
        }   
        if (!availableCaptures.isEmpty()) {
            return availableCaptures;
        } 
        return availableMoves;
    }

    private static ArrayList<Move> getPossibleMoves(Piece piece, GameState g) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Move> availableCaptures = new ArrayList<>();
        // TODO: instead of iterating over all black tiles, iterate over all tiles where the piece can move
        for (int row = 0; row < SIZE; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < SIZE; col += 2){
                Move move = g.determineMoveType(piece.getX(), piece.getY(), row, col);
                if (move.isNormal() && availableCaptures.isEmpty()) { 
                    availableMoves.add(move);
                } else if (move.isCapture()) {
                    availableCaptures.add(move);
                }
            }
        }
        if (availableCaptures.size() > 0) {
            return availableCaptures;
        } 
        return availableMoves;
    }

    public static List<Move> getLegalMoves(Piece piece, GameState g) {
        List<Turn> availableTurns = getLegalTurns(piece, g); // Fetch all legal turns for the piece
        List<Move> availableMoves = new ArrayList<>();
    
        for (Turn turn : availableTurns) {
            availableMoves.add(turn.getMoves().getFirst()); // Add the first move of each turn
        }
    
        return availableMoves;
    }
    
    public static List<Move> getLegalMoves(GameState g) {
        List<Turn> availableTurns = getLegalTurns(g); 
        List<Move> availableMoves = new ArrayList<>(); 
        for (Turn turn : availableTurns) {
            availableMoves.add(turn.getMoves().getFirst());
        }
        return availableMoves;
    }

    public static ArrayList<Piece> getListOfPieces(GameState b) {
        if (b.isWhiteTurn) {
            return b.getWhitePieces();
        }
        else {
            return b.getBlackPieces();
        }
    }
    
    public void takeTurn(Turn turn) {
        for (Move move : turn.getMoves()) {
            takeMove(move);
        }
    }

    public boolean takeMove(Move move) {
        Piece piece = g.getPieceAt(move.getFromX(), move.getFromY());
        if (move.isInvalid()) {
            piece.abortMove();
            askForMove();
            return false;
        }
        
        if (g.getCurrentTurn().isEmpty()) {
            g.setPossibleTurns(getLegalTurns(g));
        }
        else if (piece != g.getPieceAt(g.getCurrentTurn().getLast().getToX(), g.getCurrentTurn().getLast().getToY())) {
            piece.abortMove();
            askForMove();
            return false;  
        }
        List<Turn> legalTurns = g.getPossibleTurns();
        
        // Handle normal moves when no captures are available
        if (move.isNormal() && !legalTurns.getFirst().isShot()) {
            System.out.println("No available captures, making normal move");
            movePiece(move);
            askForMove();
            return true;
        }
    
        int i = g.getCurrentTurn().getMoves().size();
        // Handle capture moves
        for (Turn turn : legalTurns) {
            Move curMove = turn.getMoves().get(i);
            if (curMove.equals(move)) {
                if (curMove.isTurnEnding()) {
                    move.setTurnEnding(true);
                }
                movePiece(move);
                app.updateCaptureMessage(" ");
    
                // If the turn ends after the move
                if (curMove.isTurnEnding()) {
                    System.out.println("Made all available captures");
                    askForMove();
                    return true;
                } else {
                    // Handle additional captures in the same turn
                    System.out.println("Made capture, can take again");
                    askForMove();
                    return true;
                }
            }
        }
    
        // Handle cases where the current turn is empty
        if (g.getCurrentTurn().isEmpty()) {
            piece.abortMove();
            app.updateCaptureMessage(piece.getType().color + " must capture!");
            askForMove();
            return false;
        }
    
        // Handle cases where additional captures are available
        if (g.getCurrentTurn().getLast().isCapture()) {
            piece.abortMove();
            app.updateCaptureMessage(" ");
            System.out.println("Can take again");
            askForMove();
            return true;
        }
    
        // Default case: no valid move
        piece.abortMove();
        app.updateCaptureMessage(piece.getType().color + " must capture!");
        askForMove();
        return false;
    }

    private void movePiece(Move move) {
        if (move.isInvalid()) {
            move.getPiece().abortMove();
        }
        else if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = g.getPieceAt(capture.getCaptureAtX(), capture.getCaptureAtY());
            System.out.println(capturedPiece.toString());
            app.pieceGroup.getChildren().remove(capturedPiece.getPieceDrawer());
            if (capture.getCapturedPiece().type.color.equals("white")) {
                app.capturedPiecesTracker.capturePiece("Player 2");
            }
            else {
            app.capturedPiecesTracker.capturePiece("Player 1");
            }
        }
        g.move(move);
    }

    public void undoLastMove(GameState g) {
        if (g.getMovesPlayed().isEmpty()) {
            return;
        }
        Move move = g.getMovesPlayed().getLast();
        g.undoMove(move);
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = g.getPieceAt(capture.getCaptureAtX(), capture.getCaptureAtY());
            capturedPiece.setPieceDrawer(new PieceDrawer(capturedPiece, app));
            if (capturedPiece.getType().color.equals("white")) {
                app.capturedPiecesTracker.decrementWhiteCaptured();
            }
            else {
                app.capturedPiecesTracker.decrementBlackCaptured();
            }
        }
        askForMove();
    }

    public static Set<Piece> getPiecesTheathenedBy(ArrayList<Piece> pieces, GameState g) {
        //TODO: instead of getLegalMoves, iterate over all legal Turns
        Set<Piece> threatenedPieces = new HashSet<>();
        for (Piece piece : pieces) {
            for (Move move : getPossibleMoves(piece, g)) {
                if (move.isCapture()) {
                    Capture captureMove = (Capture) move;
                    threatenedPieces.add(captureMove.getCapturedPiece());
                }
            }
        }
        return threatenedPieces;
    }

    public static double evaluateBoard(GameState g) {
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

        for (Piece piece : g.getBlackPieces()) {
            if (piece.type == PieceType.BLACK) {
                x1++;
            } else {
                x3++;
            }
        }

        for (Piece piece : g.getWhitePieces()) {
            if (piece.type == PieceType.WHITE) {
                x2++;
            } else {
                x4++;
            }
        }
            
        Set<Piece> threatenedPieces = getPiecesTheathenedBy(g.getWhitePieces(), g);
        x5 = threatenedPieces.size();
        threatenedPieces.clear();
        threatenedPieces = getPiecesTheathenedBy(g.getBlackPieces(), g);
        x6 = threatenedPieces.size();
        return w1 * x1 + w2 * x2 + w3 * x3 + w4 * x4 + w5 * x5 + w6 * x6;
    }

    public void undoLastTurn(GameState g) {
        Turn turn = g.getTurnsPlayed().removeLast();
        LinkedList<Move> moves = turn.getMoves();
        for (int i = 0; i < moves.size(); i++) {
            undoLastMove(g);
        }
    }   

    public double evaluateTurn(Turn turn, GameState originalGS) {
        GameState g0 = new GameState(originalGS);
        for (Move move : turn.getMoves()) {
            g0.move(move);
        }
        if (isGameOver(g0)) {
            if (g0.getIsWhiteTurn()) {
                return -100;
            }
            else {
                return +100;
            }
        }
        ArrayList<Turn> opponentsTurns = getLegalTurns(g0);
        double[] evaluations = new double[opponentsTurns.size()];
        for (int i = 0; i < opponentsTurns.size(); i++) {
            Turn oTurn = opponentsTurns.get(i);
            GameState g = new GameState(g0);
            for (Move move : oTurn.getMoves()) {
                g.move(move);
                if (isGameOver(g0)) {
                    if (g0.getIsWhiteTurn()) {
                        return -100;
                    }
                    else {
                        return +100;
                    }
                }
            }
            evaluations[i] = evaluateBoard(g);
        }
        double evaluation = 0;
        if (originalGS.getIsWhiteTurn()) {
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

    public static List<Piece> getMovablePieces(GameState g) {
    List<Piece> movablePieces = new ArrayList<>();
    List<Piece> allPieces = g.getAllPieces();

    for (Piece piece : allPieces) {
        List<Move> moves = getPossibleMoves(piece, g);
        if (!moves.isEmpty()) {
            movablePieces.add(piece);
        }
    }

    return movablePieces;
}

}
