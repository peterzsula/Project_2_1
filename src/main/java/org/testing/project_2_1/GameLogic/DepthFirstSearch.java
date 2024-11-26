package org.testing.project_2_1.GameLogic;

import java.util.ArrayList;

import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

/**
 * Implements a Depth-First Search (DFS) algorithm to determine
 * the optimal sequence of captures in a game of checkers.
 * Tracks the maximum number of captures and associated turns.
 */
public class DepthFirstSearch {

    private static int maxCaptures; // Tracks the highest number of captures in a sequence
    private static ArrayList<Turn> result; // Stores the optimal turns with the maximum captures

    /**
     * Performs a Depth-First Search to explore all possible capture sequences.
     * Updates the optimal turn sequence with the highest number of captures.
     *
     * @param g The current game state.
     * @param piece The piece to move.
     * @param currentTurn The current turn being constructed.
     * @param captureCount The number of captures made in the current sequence.
     */
    public static void dfs(GameState g, Piece piece, Turn currentTurn, int captureCount) {
        ArrayList<Move> captures = g.getCaptures(piece); // Get possible capture moves for the piece
        if (captures.isEmpty()) { // Base case: no more captures available
            if (!currentTurn.getMoves().isEmpty()) { // Ensure moves exist
                currentTurn.getMoves().get(currentTurn.getMoves().size() - 1).setTurnEnding(true);
            }
            // TODO: Add logic for the "2 kings" rule
            if (captureCount > maxCaptures) {
                result.clear(); // New maximum captures found, clear existing results
                result.add(new Turn(currentTurn)); // Add the current turn to results
                maxCaptures = captureCount; // Update the maximum captures
            } else if (captureCount == maxCaptures) { // If equal to current maximum, add to results
                result.add(new Turn(currentTurn));
            }
            return; // Exit recursion
        }

        // Iterate over each capture move and explore further captures
        for (Move capture : captures) {
            g.move(capture); // Make the move on a board copy
            currentTurn.addMove(capture); // Add the capture to the current turn
            piece = g.getPiece(capture.getToX(), capture.getToY()); // Get the piece at the new position

            // Recursively continue to capture and increment captureCount
            dfs(g, piece, currentTurn, captureCount + 1);

            // Backtrack by undoing the last move and removing it from the current turn
            g.undoMove(capture); // Undo the move
            currentTurn.removeLastMove(); // Remove the last move from the current turn
        }
    }

    /**
     * Retrieves the result containing the optimal turns with the maximum captures.
     * @return A list of turns with the highest number of captures.
     */
    public static ArrayList<Turn> getResult() {
        return result;
    }

    /**
     * Resets the result list to prepare for a new DFS computation.
     */
    public static void resetResult() {
        result = new ArrayList<>();
    }

    /**
     * Resets the maximum captures counter to prepare for a new DFS computation.
     */
    public static void resetMaxCaptures() {
        maxCaptures = 0;
    }
}
