package org.testing.project_2_1.Moves;

import java.util.LinkedList;

public class Turn {
    private LinkedList<Move> moves;
    private boolean isShot;

    public Turn(LinkedList<Move> moves, boolean isShot) {
        moves = new LinkedList<Move>();
        isShot = false;
    }

    public LinkedList<Move> getMoves() {
        return moves;
    }

    public void setMoves(LinkedList<Move> moves) {
        this.moves = moves;
    }

    public boolean isShot() {
        return isShot;
    }

    public void setShot(boolean isShot) {
        this.isShot = isShot;
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    @Override
    public String toString() {
        return "Turn [noOfMoves=" + moves.size() + ", isShot=" + isShot + "]";
    }
    

}
