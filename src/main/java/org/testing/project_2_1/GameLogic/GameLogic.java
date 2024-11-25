package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Moves.*;
import org.testing.project_2_1.UI.CheckersApp;
import org.testing.project_2_1.UI.PieceDrawer;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.ArrayList;
import java.util.HashSet;
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
        g.setPossibleTurns(g.getLegalTurns()); // should be in GameState constructor
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
        if (g.isGameOver()) {
            System.out.println("Game over");
            return;
        }
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
        List<Turn> availableTurns = g.getLegalTurns();
        System.out.println("Nuber of available moves: " + availableTurns.size());
        for (Turn turn : availableTurns) {
            System.out.println(turn.getMoves().getFirst().toString());
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
            g.setPossibleTurns(g.getLegalTurns());
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
        if (g.getTurnsPlayed().isEmpty()) {
            return;
        }
        Move move = g.getTurnsPlayed().getLast().getLast();
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

    public void undoLastTurn(GameState g) {
        Turn turn = g.getTurnsPlayed().removeLast();
        List<Move> moves = turn.getMoves();
        for (int i = 0; i < moves.size(); i++) {
            undoLastMove(g);
        }
    }

    public static List<Piece> getMovablePieces(GameState g) {
    List<Piece> movablePieces = new ArrayList<>();
    List<Piece> allPieces = g.getAllPieces();

    for (Piece piece : allPieces) {
        List<Move> moves = g.getPossibleMoves(piece);
        if (!moves.isEmpty()) {
            movablePieces.add(piece);
        }
    }

    return movablePieces;
    }

}
