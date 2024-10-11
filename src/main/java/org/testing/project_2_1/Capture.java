package org.testing.project_2_1;

public class Capture {
    public Piece piece;
    public Piece capturedPiece;
    public int toX;
    public int toY;

    public Capture(Piece piece, Piece capturedPiece, int toX, int toY) {
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.toX = toX;
        this.toY = toY;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        Capture other = (Capture) obj;
        if (piece == null) {
            if (other.piece != null)
                return false;
        } else if (!piece.equals(other.piece))
            return false;
        if (capturedPiece == null) {
            if (other.capturedPiece != null)
                return false;
        } else if (!capturedPiece.equals(other.capturedPiece))
            return false;
        if (toX != other.toX)
            return false;
        if (toY != other.toY)
            return false;
        return true;
    }

    @Override
    public String toString() {
            return "Capture [fromX=" + piece.x + ", fromY=" + piece.y + ", toX=" + toX + ", toY=" + toY + "]";
    }

    public static void main(String[] args) {
        Piece piece = new Piece(PieceType.BLACK, 0, 0);
        Piece capturedPiece = new Piece(PieceType.WHITE, 1, 1);
        Capture capture = new Capture(piece, capturedPiece, 1, 1);
        Capture capture2 = new Capture(piece, capturedPiece, 1, 1);
        System.out.println(capture.equals(capture2));
    }

    
}
