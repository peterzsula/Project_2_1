package org.testing.project_2_1;

import java.util.ArrayList;
import java.util.List;

public class MoveHighlighter {

    private final int BOARD_SIZE = 8;

    public List<NormalMove> getAvailableMoves(Piece piece) {
        List<NormalMove> availableMoves = new ArrayList<>();
        
        int x = piece.x;
        int y = piece.y;
        
        // Determine moves based on piece type
        if (piece.getType() == PieceType.BLACK || piece.getType() == PieceType.WHITE) {
            // Regular piece moves only in one direction
            int direction = piece.getType().moveDir;
            checkAndAddMove(piece, x + 1, y + direction, availableMoves);
            checkAndAddMove(piece, x - 1, y + direction, availableMoves);
        } else if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            // King piece moves in both directions
            checkAndAddMove(piece, x + 1, y + 1, availableMoves);
            checkAndAddMove(piece, x - 1, y + 1, availableMoves);
            checkAndAddMove(piece, x + 1, y - 1, availableMoves);
            checkAndAddMove(piece, x - 1, y - 1, availableMoves);
        }
        
        return availableMoves;
    }

    private void checkAndAddMove(Piece piece, int toX, int toY, List<NormalMove> moves) {
        if (isValidPosition(toX, toY)) {
            moves.add(new NormalMove(piece, toX, toY));
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }
}
