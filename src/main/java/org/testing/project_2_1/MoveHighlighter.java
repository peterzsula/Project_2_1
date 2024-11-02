package org.testing.project_2_1;

import java.util.ArrayList;
import java.util.List;

public class MoveHighlighter {

    private final int BOARD_SIZE = 10;
    private GameLogic gameLogic;

    public MoveHighlighter(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    public List<NormalMove> getAvailableMoves(Piece piece) {
        List<NormalMove> availableMoves = new ArrayList<>();
        
        int x = piece.x;
        int y = piece.y;
        
        if (piece.getType() == PieceType.BLACK || piece.getType() == PieceType.WHITE) {
            int direction = piece.getType().moveDir;
            addIfValid(piece, x + 1, y + direction, availableMoves);
            addIfValid(piece, x - 1, y + direction, availableMoves);
        } else if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            addIfValid(piece, x + 1, y + 1, availableMoves);
            addIfValid(piece, x - 1, y + 1, availableMoves);
            addIfValid(piece, x + 1, y - 1, availableMoves);
            addIfValid(piece, x - 1, y - 1, availableMoves);
        }
        
        return availableMoves;
    }

    private void addIfValid(Piece piece, int toX, int toY, List<NormalMove> moves) {
        if (isValidPosition(toX, toY) && !gameLogic.hasPieceAt(toX, toY)) {
            moves.add(new NormalMove(piece, toX, toY));
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }
}
