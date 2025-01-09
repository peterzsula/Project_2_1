package org.testing.project_2_1.Moves;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The `Turn` class represents a series of moves made by a player during their turn in the game.
 * It keeps track of the moves, whether a capture (shot) occurred, and the evaluation score of the turn.
 */
public class Turn {
    private List<Move> moves; // List of moves in the turn
    private boolean isShot; // Indicates if the turn involves a capture (shot)
    private double evaluation; // Evaluation score of the turn
    private boolean isRandomChoice;

    /**
     * Default constructor initializing an empty turn.
     */
    public Turn() {
        moves = new LinkedList<>();
        evaluation = 0;
        isRandomChoice = false;
    }

    /**
     * Constructor to initialize a turn with a given list of moves and shot status.
     *
     * @param moves  The list of moves in the turn.
     * @param isShot Indicates if the turn involves a capture (shot).
     */
    public Turn(LinkedList<Move> moves, boolean isShot) {
        this.moves = new LinkedList<>(moves);
        this.isShot = isShot;
        evaluation = 0;
        isRandomChoice = false;
    }

    /**
     * Copy constructor to create a new turn as a copy of an existing turn.
     *
     * @param currentTurn The turn to copy.
     */
    public Turn(Turn currentTurn) {
        this.isShot = currentTurn.isShot;
        this.moves = new LinkedList<>();
        for (Move move : currentTurn.getMoves()) {
            this.moves.add(move);
        }
        this.evaluation = currentTurn.evaluation;
        this.isRandomChoice = currentTurn.isRandomChoice;
    }

    /**
     * Gets the list of moves in the turn.
     *
     * @return The list of moves.
     */
    public List<Move> getMoves() {
        return moves;
    }

    /**
     * Sets the list of moves for the turn.
     *
     * @param moves The new list of moves.
     */
    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    /**
     * Checks if the turn involves a capture (shot).
     *
     * @return True if a capture occurred, false otherwise.
     */
    public boolean isShot() {
        return isShot;
    }

    /**
     * Sets the shot status for the turn.
     *
     * @param isShot True if the turn involves a capture, false otherwise.
     */
    public void setShot(boolean isShot) {
        this.isShot = isShot;
    }

    /**
     * Adds a move to the turn.
     *
     * @param move The move to add.
     */
    public void addMove(Move move) {
        moves.add(move);
    }

    /**
     * Gets the evaluation score of the turn.
     *
     * @return The evaluation score.
     */
    public double getEvaluation() {
        return evaluation;
    }

    /**
     * Sets the evaluation score for the turn.
     *
     * @param evaluation The new evaluation score.
     */
    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    /**
     * Removes the last move from the turn.
     */
    public void removeLastMove() {
        if (!moves.isEmpty()) {
            moves.remove(moves.size() - 1);
        }
    }

    public boolean isRandomChoice() {
        return isRandomChoice;
    }

    public void setRandomChoice(boolean isRandomChoice) {
        this.isRandomChoice = isRandomChoice;
    }

    /**
     * Converts the turn to a string representation for debugging purposes.
     *
     * @return The string representation of the turn.
     */
    @Override
    public String toString() {
        return "Turn [noOfMoves=" + moves.size() + ", isShot=" + isShot + moves.toString() + "]";
    }

    /**
     * Creates a list of turns from a list of moves, where each move becomes its own turn.
     *
     * @param moves The list of moves to convert.
     * @return A list of single-move turns.
     */
    public static ArrayList<Turn> copyMovesToTurns(ArrayList<Move> moves) {
        ArrayList<Turn> turns = new ArrayList<>();
        for (Move move : moves) {
            Turn turn = new Turn();
            turn.addMove(move);
            turns.add(turn);
        }
        return turns;
    }

    /**
     * Removes a specific move from the turn.
     *
     * @param move The move to remove.
     */
    public void removeMove(Move move) {
        moves.remove(move);
    }

    /**
     * Gets the last move in the turn.
     *
     * @return The last move.
     */
    public Move getLast() {
        return moves.get(moves.size() - 1);
    }

    /**
     * Checks if the turn contains any moves.
     *
     * @return True if the turn has no moves, false otherwise.
     */
    public boolean isEmpty() {
        return moves.isEmpty();
    }
}
