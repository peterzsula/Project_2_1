package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.Simulation;
import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Moves.*;
import org.testing.project_2_1.UI.CheckersApp;
import org.testing.project_2_1.UI.PieceDrawer;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the core game logic for a checkers game.
 * Manages game state, agents, and player interactions.
 */
public class GameLogic {
    public CheckersApp app; // Reference to the UI application
    public Agent agent; // Agent controlling one side of the game
    public Agent opponent; // Opponent agent, if applicable
    public GameState g; // The current game state

    /**
     * Constructs a new GameLogic instance for Human vs. Human games.
     * @param app Reference to the CheckersApp UI.
     */
    public GameLogic(CheckersApp app) {
        setStandardValues(app);
        this.agent = null;
        this.opponent = null;
    }

    /**
     * Constructs a new GameLogic instance for Human vs. AI games.
     * @param app Reference to the CheckersApp UI.
     * @param agent The AI agent playing the game.
     */
    public GameLogic(CheckersApp app, Agent agent) {
        setStandardValues(app);
        this.agent = agent.reset();
        this.opponent = null;
        this.agent.setGameLogic(this);
    }

    /**
     * Constructs a new GameLogic instance for AI vs. AI games.
     * @param app Reference to the CheckersApp UI.
     * @param agent1 The first AI agent.
     * @param agent2 The second AI agent.
     */
    public GameLogic(CheckersApp app, Agent agent1, Agent agent2) {
        setStandardValues(app);
        this.agent = agent1.reset();
        this.opponent = agent2.reset();
        this.agent.setGameLogic(this);
        this.opponent.setGameLogic(this);
    }

    /**
     * Initializes the game with standard starting values.
     * Sets up the game state and initializes the UI Pane.
     * @param app Reference to the CheckersApp UI.
     */
    public void setStandardValues(CheckersApp app) {
        this.app = app;
        g = new GameState(); // Initialize the game state
        // new Pane(); // Initialize the graphical Pane (needed for some reason)
        g.setPossibleTurns(g.getLegalTurns()); // Initialize possible turns
    }

    /**
     * Checks if the game is over based on the current board state.
     * @param board The current game state.
     * @return True if the game is over, otherwise false.
     */
    public boolean isGameOver(GameState board) {
        return board.getWhitePieces().isEmpty() || board.getBlackPieces().isEmpty();
    }

    /**
     * Retrieves the current board state as a 2D array of tiles.
     * @return The current game board.
     */
    public Tile[][] getBoard() {
        return g.getBoard();
    }

    /**
     * Restarts the game by resetting to the standard initial values.
     */
    public void restartGame() {
        setStandardValues(app);
    }

    /**
     * Requests the next move in the game, either from the user or an agent.
     * Updates the UI and prints the current turn status.
     */
    private void askForMove() {
        int result = g.getWinner();
        if (result != 0) {
            //System.out.println("Game over");
            Simulation.simsRan++;
            switch (result) {
                case 1:
                    Simulation.whiteWins++;
                    System.out.println("White wins");
                    break;
                case -1:
                    Simulation.blackWins++;
                    System.out.println("Black wins");
                    break;
                case 100:
                    Simulation.draws++;
                    System.out.println("Draw");
                    break;
                default:
                    break;
            }
            if (Simulation.SIMULATIONS > Simulation.simsRan) {
                app.autoRestart();
            }
            else {
                System.out.println("White wins: " + Simulation.whiteWins + ", Black wins: " + Simulation.blackWins + ", Draws: " + Simulation.draws);
            }
            
        }
        app.updateGlows(); // Update highlights for possible moves
        app.updateEvaluationBar(); // Update the UI evaluation bar
        //System.out.println();
        if (g.isWhiteTurn) {
            //System.out.println("White's turn");
        } else {
            //System.out.println("Black's turn");
        }
        // Delegate move-making to agents if applicable
        if (agent != null && agent.isWhite() == g.isWhiteTurn && !isGameOver(g)) {
            agent.makeMove();
        }
        if (opponent != null && opponent.isWhite() == g.isWhiteTurn && !isGameOver(g)) {
            opponent.makeMove();
        }
        try {
            app.stop();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Prints all available captures for the current turn.
     * @param g The current game state.
     */
    public void printAvailableCaptures(GameState g) {
        List<Turn> availableTurns = g.getLegalTurns();
        System.out.println("Number of available moves: " + availableTurns.size());
        for (Turn turn : availableTurns) {
            System.out.println(turn.getMoves().get(0).toString());
        }
    }

    /**
     * Executes a given turn by processing all moves in it.
     * @param turn The turn to execute.
     */
    public void takeTurn(Turn turn) {
        for (Move move : turn.getMoves()) {
            takeMove(move);
        }
    }

    /**
     * Processes a move in the game.
     * @param move The move to execute.
     * @return True if the move was successfully executed, false otherwise.
     */
    public boolean takeMove(Move move) {
        Piece piece = g.getPieceAt(move.getFromX(), move.getFromY());
        if (move.isInvalid()) {
            piece.abortMove();
            askForMove();
            return false;
        }
        if (g.getCurrentTurn().isEmpty()) {
            g.setPossibleTurns(g.getLegalTurns());
        } else if (piece != g.getPieceAt(g.getCurrentTurn().getLast().getToX(), g.getCurrentTurn().getLast().getToY())) {
            piece.abortMove();
            askForMove();
            return false;
        }
        List<Turn> legalTurns = g.getPossibleTurns();

        // Handle normal moves when no captures are available
        if (move.isNormal() && !legalTurns.get(0).isShot()) {
            //System.out.println("No available captures, making normal move");
            movePiece(move);
            askForMove();
            return true;
        }

        int i = g.getCurrentTurn().getMoves().size();

        // Handle capture moves
        for (Turn turn : legalTurns) {
            int moveIndex = Math.min(g.getCurrentTurn().getMoves().size(), turn.getMoves().size() - 1);

            // Check if the move index is out of bounds
            if (moveIndex >= turn.getMoves().size()) {
                continue; // Skip to the next turn
            }

            Move curMove = turn.getMoves().get(moveIndex);

            if (curMove.equals(move)) {
                if (curMove.isTurnEnding()) {
                    move.setTurnEnding(true);
                }
                movePiece(move);
                app.updateCaptureMessage(" ");

                // Reset `g.getCurrentTurn()` to avoid invalid `moveIndex`
                g.getCurrentTurn().getMoves().clear();

                // Get updated legal turns
                List<Turn> updatedLegalTurns = g.getLegalTurns();

                boolean canAnyPieceCapture = updatedLegalTurns.stream().anyMatch(Turn::isShot);

                if (curMove.isTurnEnding() || !canAnyPieceCapture) {
                    g.setPossibleTurns(g.getLegalTurns()); // Update turns for the next player
                    askForMove();
                    return true; // End the current player's turn
                } else {
                    askForMove(); // Prompt the player to continue capturing
                    return true;
                }
            }
        }

        //return false; // No matching move found in legal turns
    
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
            //System.out.println("Can take again");
            askForMove();
            return true;
        }

        // Default case: no valid move
        piece.abortMove();
        app.updateCaptureMessage(piece.getType().color + " must capture!");
        askForMove();
        return false;
    }

    /**
     * Executes a piece move and updates the game state.
     * @param move The move to execute.
     */
    private void movePiece(Move move) {
        if (move.isInvalid()) {
            move.getPiece().abortMove();
        } else if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = g.getPieceAt(capture.getCaptureAtX(), capture.getCaptureAtY());
            //System.out.println(capturedPiece.toString());
            app.pieceGroup.getChildren().remove(capturedPiece.getPieceDrawer());
            if (capture.getCapturedPiece().type.color.equals("white")) {
                app.capturedPiecesTracker.capturePiece("Player 2");
            } else {
                app.capturedPiecesTracker.capturePiece("Player 1");
            }
        }
        g.move(move);
    }

    /**
     * Undoes the last move made in the game.
     * @param g The current game state.
     */
    public void undoLastMove(GameState g) {
        if (g.getTurnsPlayed().isEmpty()) {
            return;
        }
        Move move = g.getTurnsPlayed().get(g.getTurnsPlayed().size() - 1).getLast();
        g.undoMove(move);
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = g.getPieceAt(capture.getCaptureAtX(), capture.getCaptureAtY());
            capturedPiece.setPieceDrawer(new PieceDrawer(capturedPiece, app));
            if (capturedPiece.getType().color.equals("white")) {
                app.capturedPiecesTracker.decrementWhiteCaptured();
            } else {
                app.capturedPiecesTracker.decrementBlackCaptured();
            }
        }
        askForMove();
    }

    /**
     * Undoes the last turn played in the game.
     * @param g The current game state.
     */
    public void undoLastTurn(GameState g) {
        Turn turn = g.getTurnsPlayed().remove(g.getTurnsPlayed().size() - 1);
        List<Move> moves = turn.getMoves();
        for (int i = 0; i < moves.size(); i++) {
            undoLastMove(g);
        }
    }

    /**
     * Retrieves all pieces that can legally move in the current state.
     * @param g The current game state.
     * @return A list of pieces that can make legal moves.
     */
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