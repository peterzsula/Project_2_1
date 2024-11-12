package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Moves.*;
import org.testing.project_2_1.UI.CheckersApp;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;

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
    private Turn currentTurn; 

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
        b = new Board();
        new Pane();
        app.pieceGroup.getChildren().clear();
        //app.capturedPiecesTracker.reset();
        currentTurn = new Turn();
    }

    public boolean isGameOver(Board board){
        if (board.getWhitePieces().size() == 0 || board.getBlackPieces().size() == 0) {
            return true;
        }
        return false;
    }

    public void restartGame(){
        setStandardValues(app);
    }

    //DONE
    private void switchTurn() {
        System.out.println();
        b.toString();
        System.out.println();
        if (b.isWhiteTurn) {
            System.out.println("White's turn");
        }
        else {
            System.out.println("Black's turn");
        }
        printAvailableCaptures(b);
        if (agent != null && agent.isWhite() == b.isWhiteTurn) {
            agent.makeMove();
        }
        if (opponent != null && opponent.isWhite() == b.isWhiteTurn) {
            opponent.makeMove();
        }
    }

    //DONE
    public static ArrayList<Turn> getLegalTurns(Board originalBoard) {
        ArrayList<Move> availableMoves = getLegalMoves(originalBoard);
        if (availableMoves.get(0).isNormal()) {
            return Turn.copyMovesToTurns(availableMoves);
        }
        ArrayList<Turn> availableTurns = new ArrayList<>();
        Set<Piece> pieces = Piece.movesToPieces(availableMoves);
        int maxCaptures = 0;
        for (Piece piece : pieces) {
            ArrayList<Turn> pieceTurns = getLegalTurns(piece, originalBoard);
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

    //DONE
    public static ArrayList<Turn> getLegalTurns(Piece piece, Board originalBoard) {
        Board b = new Board(originalBoard.getBoard(), originalBoard.getIsWhiteTurn());
        ArrayList<Move> availableMoves = getLegalMoves(originalBoard);
        if (availableMoves.get(0).isNormal()) {
            return Turn.copyMovesToTurns(availableMoves);
        }
        DepthFirstSearch.resetMaxCaptures();
        DepthFirstSearch.resetResult();
        Turn initialTurn = new Turn();
        DepthFirstSearch.dfs(b, piece, initialTurn, 0);
        ArrayList<Turn> result = DepthFirstSearch.getResult();
        return result;
    }
    
    //DONE
    public static ArrayList<Move> getLegalMoves(Board b) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Move> currentMoves = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces(b);
        for (Piece piece : pieces) {
            //TODO: instead of iterating over all black tiles, iterate over all tiles where the piece can move
            currentMoves = getLegalMoves(piece, b);
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

    //DONE
    public static ArrayList<Move> getLegalMoves(Piece piece, Board b) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Move> availableCaptures = new ArrayList<>();
        // TODO: instead of iterating over all black tiles, iterate over all tiles where the piece can move
        for (int row = 0; row < SIZE; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < SIZE; col += 2){
                Move move = b.determineMoveType(piece.getX(), piece.getY(), row, col);
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

    //DONE
    public void printAvailableCaptures(Board b){
        ArrayList<Move> availablecaptures = getLegalMoves(b);
        System.out.println("Nuber of available moves: " + availablecaptures.size());
        for (Move move : availablecaptures) {
            System.out.println(move.toString());
        }
    }

    //DONE
    public static ArrayList<Piece> getListOfPieces(Board b) {
        if (b.isWhiteTurn) {
            return b.getWhitePieces();
        }
        else {
            return b.getBlackPieces();
        }
    }

    //DONE
    public boolean takeMove(Move move) {
        Piece piece = move.getPiece();
        if (move.isInvalid()) {
            piece.abortMove();
            return false;
        }
        ArrayList<Turn> legalTurns = getLegalTurns(b);
        if (move.isNormal() && !legalTurns.get(0).isShot()) {
            System.out.println("no available captures, making normal move");
            movePiece(move, b);
            switchTurn();
            return true;
        }
        int i = currentTurn.getMoves().size();
        for (Turn turn : legalTurns) {
            if (turn.getMoves().get(i).equals(move)) {
                movePiece(move, b);
                app.updateCaptureMessage(" ");
                if (turn.getMoves().get(i).isTurnEnding()) {
                    System.out.println("made all available captures");
                    switchTurn(); 
                    return true;
                }
            }
        }
        piece.abortMove();
        app.updateCaptureMessage(piece.getType().color + " must capture!");
        return false;        
    }

    //DONE
    private void movePiece(Move move, Board b) {
        b.move(move);
        if (move.isInvalid()) {
            move.getPiece().abortMove();
        }
        else if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece otherPiece = capture.getCapturedPiece();
            app.pieceGroup.getChildren().remove(otherPiece.getPieceDrawer());
            if (capture.getCapturedPiece().type.color.equals("white")) {
                app.capturedPiecesTracker.capturePiece("Player 2");
            }
            else {
            app.capturedPiecesTracker.capturePiece("Player 1");
            }
        }
    }

    //DONE
    public static Set<Piece> getPiecesTheathenedBy(ArrayList<Piece> pieces, Board b) {
        //TODO: instead of getLegalMoves, iterate over all legal Turns
        Set<Piece> threatenedPieces = new HashSet<>();
        for (Piece piece : pieces) {
            for (Move move : getLegalMoves(piece, b)) {
                if (move.isCapture()) {
                    Capture captureMove = (Capture) move;
                    threatenedPieces.add(captureMove.getCapturedPiece());
                }
            }
        }
        return threatenedPieces;
    }

    public static double evaluateBoard(Board b) {
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

        for (Piece piece : b.getBlackPieces()) {
            if (piece.type == PieceType.BLACK) {
                x1++;
            } else {
                x3++;
            }
        }

        for (Piece piece : b.getWhitePieces()) {
            if (piece.type == PieceType.WHITE) {
                x2++;
            } else {
                x4++;
            }
        }
            
        Set<Piece> threatenedPieces = getPiecesTheathenedBy(b.getWhitePieces(), b);
        x5 = threatenedPieces.size();
        threatenedPieces.clear();
        threatenedPieces = getPiecesTheathenedBy(b.getBlackPieces(), b);
        x6 = threatenedPieces.size();
        return w1 * x1 + w2 * x2 + w3 * x3 + w4 * x4 + w5 * x5 + w6 * x6;
    }

    //DONE
    public void undoLastTurn(Board b) {
        Turn turn = b.getTurnsPlayed().removeLast();
        LinkedList<Move> moves = turn.getMoves();
        for (int i = 0; i < moves.size(); i++) {
            undoLastMove(b);
        }
    }   

    //DONE
    public void undoLastMove(Board b) {
        Move move = b.getMovesPlayed().removeLast();
        if (move.isTurnEnding()) {
            switchTurn();
        }
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = capture.getCapturedPiece();
            if (capturedPiece.getType().color.equals("white")) {
                app.capturedPiecesTracker.decrementWhiteCaptured();
            }
            else {
                app.capturedPiecesTracker.decrementBlackCaptured();
            }
            app.pieceGroup.getChildren().add(capturedPiece.getPieceDrawer());
        }
    }

    //DONE
    public double evaluateTurn(Turn turn, Board originalboard) {
        Board b0 = new Board(originalboard.getBoard(), originalboard.getIsWhiteTurn());
        for (Move move : turn.getMoves()) {
            b0.move(move);
        }
        ArrayList<Turn> opponentsTurns = getLegalTurns(b0);
        double[] evaluations = new double[opponentsTurns.size()];
        for (int i = 0; i < opponentsTurns.size(); i++) {
            Turn oTurn = opponentsTurns.get(i);
            Board b = new Board(b0.getBoard(), b0.getIsWhiteTurn());
            for (Move move : oTurn.getMoves()) {
                b.move(move);
            }
            evaluations[i] = evaluateBoard(b);
            System.out.println("evaluation: " + evaluations[i]);
        }
        double evaluation = 0;
        if (originalboard.getIsWhiteTurn()) {
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

    public void takeTurn(Turn turn) {
        for (Move move : turn.getMoves()) {
            takeMove(move);
        }
    }


}
