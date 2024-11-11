package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.Moves.Capture;
import org.testing.project_2_1.Moves.InvalidMove;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.NormalMove;

public class MoveLogic {
    private Tile[][] board;
    private Board boardObj;
    private boolean isWhiteTurn;

    public MoveLogic(Board boardObj) {
        this.boardObj = boardObj;
        this.board = boardObj.getBoard();
        this.isWhiteTurn = boardObj.getIsWhiteTurn();
    }

    public Move determineMoveType(Piece piece, int newX, int newY) {
        int x0 = piece.getX();
        int y0 = piece.getY();
        Tile tile = board[newX][newY];

        // Check if it's the correct player's turn
        if (isWhiteTurn != piece.getType().color.equals("white")) {
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // Check if the tile is empty
        if (tile.hasPiece()) {
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // Check if the tile is black
        if (!tile.isBlack()) {
            System.out.println("white tile");
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // If the piece is a king, allow all types of moves and captures
        if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            // Check for normal king move any direction (multi-tile)
            if (isMoveforKing(x0, y0, newX, newY) && isPathClearforKing(x0, y0, newX, newY)) {
                return new NormalMove(x0, y0, piece, newX, newY);
            }

            // Check for king capture any direction
            if (isCapturePathforKing(x0, y0, newX, newY)) {
                Piece capturedPiece = getCapturedPieceOnPathforKing(x0, y0, newX, newY);
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }

        else {
            // Normal diagonal move for regular pieces
        if (isMoveDiagonalNormal(x0, y0, newX, newY) && piece.getType().moveDir == (newY - y0)) {return new NormalMove(x0, y0, piece, newX, newY);}


        // Horizontal capture logic for normal pieces
        if (newY == y0 && Math.abs(newX - x0) == 4) {
            int x1 = (newX + x0) / 2;
            Tile halfWay = board[x1][y0];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }

        // Vertical capture logic for normal pieces
        if (newX == x0 && Math.abs(newY - y0) == 4) {
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x0][y1];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }

        // Diagonal capture logic for normal pieces
        if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == 2) {
            int x1 = (newX + x0) / 2;
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x1][y1];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }
    }

    return new InvalidMove(x0, y0, piece, newX, newY);
    }

    // Helper method to check if move is available for king
    private boolean isMoveforKing(int x0, int y0, int newX, int newY) {
        return (x0 == newX || y0 == newY || Math.abs(newX - x0) == Math.abs(newY - y0));
    }

    // Helper method to check if move is diagonal for normal pieces
    private boolean isMoveDiagonalNormal(int x0, int y0, int newX, int newY) {
        return Math.abs(newX - x0) == 1 && Math.abs(newY - y0) == 1;
    }

    // Check if the path for king movement (diagonal, horizontal, vertical) is clear
    private boolean isPathClearforKing(int x0, int y0, int newX, int newY) {
        int dx = Integer.compare(newX, x0);
        int dy = Integer.compare(newY, y0);
    
        int x = x0 + dx;
        int y = y0 + dy;
    
        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                return false;  // Path is blocked
            }
            x += dx;
            y += dy;
        }
        return true;
    } 

    // Check if there is a capturable piece on the path
    private boolean isCapturePathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveforKing(x0, y0, newX, newY)) {
            return false;  // Not a move for the burger king
        }

        int dx = Integer.compare(newX , x0);
        int dy = Integer.compare(newY , y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  // Found an opponent's piece to capture
                        capturedX=x;
                        capturedY=y;
                    } else {
                        return false;  // Path is blocked by a friendly piece
                    }
                } else {
                    return false;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }
        // Ensure capturing piece can only land 1 square after the captured one
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            // Add error margin for vertical/horizontal captures
            if (Math.abs(newX - landingX) <= 1 && Math.abs(newY - landingY) <= 1) {
                return true;  // Immediately after the captured piece
            } else {
                return false;  // Not immediately after
            }
        }

        return false;  // No capturable piece wis found
    }

    // Return the piece to capture along the path
    private Piece getCapturedPieceOnPathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveforKing(x0, y0, newX, newY)) {
            return null;  // Not a move for the burger king
        }
        int dx = Integer.compare(newX , x0);
        int dy = Integer.compare(newY , y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  //Found an opponent's piece to capture
                        capturedX = x;
                        capturedY = y;
                    } else {
                        return null;  // Path is blocked by a friendly piece
                    }
                } else {
                    return null;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }
        // Ensure capturing piece can only land 1 square after the captured one
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            if (Math.abs(newX - landingX) <= 1 && Math.abs(newY - landingY) <= 1) {
                return capturedPiece;  // Valid capture
            } else {
                return null;  // not immediately after
            }
        }

        return null; // No valid capture is found
    }

}
