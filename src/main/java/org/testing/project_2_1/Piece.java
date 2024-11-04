package org.testing.project_2_1;

public class Piece {
    public PieceType type;
    public int x, y;
    public PieceDrawer pieceDrawer;

    public Piece(PieceType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public PieceType getType() {
        return type;
    }

    public void setPieceDrawer(PieceDrawer pieceDrawer) {
        this.pieceDrawer = pieceDrawer;
    }

    public void promoteToKing() {
        if (this.type == PieceType.BLACK || this.type == PieceType.WHITE ) {
            if (this.type.color.equals("white")){
                this.type = PieceType.WHITEKING;
            }
            else if (this.type.color.equals("black")){
                this.type = PieceType.BLACKKING;
            }
        }
    }

    public void demoteToNormal() {
        if (this.type == PieceType.BLACKKING || this.type == PieceType.WHITEKING ) {
            System.out.println("Demoted to normal");
            if (this.type.color.equals("white")){
                this.type = PieceType.WHITE;
                pieceDrawer.demoteToNormal();
            }
            else if (this.type.color.equals("black")){
                this.type = PieceType.BLACK;
                pieceDrawer.demoteToNormal();
            }
        }
    }
}