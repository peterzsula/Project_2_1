package org.testing.project_2_1.GameLogic;

import java.util.ArrayList;

import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

public class DepthFirstSearch {
    private static int maxCaptures;
    private static ArrayList<Turn> result;  

    public static void resetMaxCaptures() {
        maxCaptures = 0;
    }
    
    public static void dfs(Board b, Piece piece, Turn currentTurn, int captureCount) {
        // Get all available captures for the current state
        ArrayList<Move> captures = GameLogic.getLegalMoves(piece, b);
        
        if (captures.isEmpty()) {
            currentTurn.getMoves().getLast().setTurnEnding(true);
            //TODO: add 2 kings rule
            if (captureCount > maxCaptures) {
                result.clear();
                result.add(new Turn(currentTurn)); 
                maxCaptures = captureCount;
            } else if (captureCount == maxCaptures) {
                result.add(new Turn(currentTurn));
            }
            return;
        }
        
        // Iterate over each capture move and explore further captures
        for (Move capture : captures) {
            b.move(capture);  // Make the move on a board copy
            currentTurn.addMove(capture);

            // Recursively continue to capture and increment captureCount
            dfs(b, piece, currentTurn, captureCount + 1);
            
            // Backtrack by undoing the last move and removing it from the current turn
            b.undoMove(capture);
            currentTurn.removeLastMove();
        }
    }

    public static ArrayList<Turn> getResult() {
        return result;
    }

    public static void resetResult() {
        result = new ArrayList<Turn>();
    }
}
