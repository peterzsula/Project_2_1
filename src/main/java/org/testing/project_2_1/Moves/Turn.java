package org.testing.project_2_1.Moves;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Turn {
    private List<Move> moves;
    private boolean isShot;
    private double evaluation;

    public Turn() {
        moves = new LinkedList<Move>();
        evaluation = 0;
    }

    public Turn(LinkedList<Move> moves, boolean isShot) {
        moves = new LinkedList<Move>();
        isShot = false;
        evaluation = 0;
    }

    public Turn(Turn currentTurn) {
        isShot = true;
        this.moves = new LinkedList<Move>();
        for (Move move : currentTurn.getMoves()) {
            this.moves.add(move);
        }
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
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

    public double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    public void removeLastMove() {
        if (!moves.isEmpty()) {
            moves.remove(moves.size() - 1);
        }
    }

    @Override
    public String toString() {
        return "Turn [noOfMoves=" + moves.size() + ", isShot=" + isShot + moves.toString() + "]";
    }
    
    public static ArrayList<Turn> copyMovesToTurns(ArrayList<Move> moves) {
        ArrayList<Turn> turns = new ArrayList<Turn>();
        for (Move move : moves) {
                Turn turn = new Turn();
                turn.addMove(move);
                turns.add(turn);
            }
        return turns;
    }

    public void removeMove(Move move) {
        moves.remove(move);
        if (moves.isEmpty()) {
            
        }
    }

    public Move getLast() {
        return moves.get(moves.size() - 1);
    }

    public boolean isEmpty() {
        return moves.isEmpty();
    }

}
