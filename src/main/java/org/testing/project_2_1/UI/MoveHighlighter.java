package org.testing.project_2_1.UI;


import java.util.ArrayList;
import java.util.List;

import org.testing.project_2_1.GameLogic.Piece;
import org.testing.project_2_1.GameLogic.PieceType;
import org.testing.project_2_1.Moves.NormalMove;

public class MoveHighlighter {

    private final int BOARD_SIZE = 8;

    public List<NormalMove> getAvailableMoves(Piece piece) {
        List<NormalMove> availableMoves = new ArrayList<>();
        
        int x = piece.getX();
        int y = piece.getY();
        
        // Determine moves based on piece type
        if (piece.getType() == PieceType.BLACK || piece.getType() == PieceType.WHITE) {
            // Regular piece moves only in one direction
            int direction = piece.getType().moveDir;
            checkAndAddMove(piece, x + 1, y + direction, availableMoves);
            checkAndAddMove(piece, x - 1, y + direction, availableMoves);
        } else if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            // King piece moves in multiple steps along both diagonals
            checkAndAddMultipleMoves(piece, x, y, 1, 1, availableMoves);  
            checkAndAddMultipleMoves(piece, x, y, -1, 1, availableMoves); 
            checkAndAddMultipleMoves(piece, x, y, 1, -1, availableMoves); 
            checkAndAddMultipleMoves(piece, x, y, -1, -1, availableMoves); 
        }
        
        return availableMoves;
    }

    private void checkAndAddMove(Piece piece, int toX, int toY, List<NormalMove> moves) {
        if (isValidPosition(toX, toY)) {
            // TODO: FIX THIS
            // moves.add(new NormalMove(piece, toX, toY));
        }
    }

    private void checkAndAddMultipleMoves(Piece piece, int startX, int startY, int dx, int dy, List<NormalMove> moves) {
        int x = startX + dx;
        int y = startY + dy;
        while (isValidPosition(x, y)) {
            // TODO: FIX THIS
            // moves.add(new NormalMove(piece, x, y));
            // Move one step further along the direction
            x += dx;
            y += dy;
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }
}
