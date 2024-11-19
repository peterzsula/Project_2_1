package org.testing.project_2_1.GameLogic;

import java.util.ArrayList;

import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

public class DepthFirstSearch {
    private static int maxCaptures;
    private static ArrayList<Turn> result;  
    
    public static void dfs(GameState g, Piece piece, Turn currentTurn, int captureCount) {
        ArrayList<Move> captures = GameLogic.getCaptures(piece, g);
        if (captures.isEmpty()) {
            if (!currentTurn.getMoves().isEmpty()) { // Ensure moves exist before setting the turn ending
                currentTurn.getMoves().getLast().setTurnEnding(true);
            }
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
            g.move(capture);  // Make the move on a board copy
            currentTurn.addMove(capture);
            piece = g.getPiece(capture.getToX(), capture.getToY());
            // Recursively continue to capture and increment captureCount
            dfs(g, piece, currentTurn, captureCount + 1);
            
            // Backtrack by undoing the last move and removing it from the current turn
            g.undoMove(capture);
            currentTurn.removeLastMove();
        }
    }

    public static ArrayList<Turn> getResult() {
        return result;
    }

    public static void resetResult() {
        result = new ArrayList<Turn>();
    }
    
    public static void resetMaxCaptures() {
        maxCaptures = 0;
    }
}
