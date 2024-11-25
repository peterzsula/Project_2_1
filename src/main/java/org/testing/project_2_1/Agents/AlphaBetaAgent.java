package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.PNSearch;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class AlphaBetaAgent implements Agent {
    private GameLogic gameLogic;
    private GameState gameState;
    private boolean isWhite;
    private int maxDepth;
    private final int defaultDepth = 3;
    private Turn currentTurn;

    public AlphaBetaAgent(boolean isWhite, int maxDepth) {
        this.isWhite = isWhite;
        this.maxDepth = maxDepth;
        currentTurn = new Turn();
    }

    public AlphaBetaAgent(boolean isWhite, GameState gameState){
        this.isWhite = isWhite;
        this.gameState = gameState;
        currentTurn = new Turn();
        maxDepth = defaultDepth;
    }

    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void makeMove() {
        System.out.println("Alpha-Beta agent making move");
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
            if (gameLogic.g.getIsWhiteTurn() == isWhite && !gameLogic.g.isGameOver()) {
                List<Turn> turns = gameLogic.g.getLegalTurns();
                 if (isEndgame(gameLogic.g)) {
                    System.out.println("Endgame detected, using Proof-Number Search");
                    currentTurn = getBestTurnPNSearch();
                } else {
                    currentTurn = getBestTurnABP(turns);
                }
                if (currentTurn != null && !currentTurn.getMoves().isEmpty()) {
                    Move move = currentTurn.getMoves().remove(0);
                    gameLogic.takeMove(move);
                }
            }
        });
        pause.play();
    }

    private boolean isEndgame(GameState gameState) {
        int totalPieces = gameState.countPieces();
        int endgameThreshold = 12;
        return totalPieces < endgameThreshold;
    }

    private Turn getBestTurnABP(List<Turn> turns) { // get best turn using Alpha-Beta Pruning
        Turn bestTurn = null;
        int bestValue;
        if (isWhite) {
            bestValue = Integer.MIN_VALUE;  // maximize score
        } else {
            bestValue = Integer.MAX_VALUE; // to minimize score
        }

        List<Turn> bestTurns = new ArrayList<>();
        for (Turn turn : turns) {
            GameState newState = new GameState(gameState);
            for (Move move : turn.getMoves()) {
                newState.move(move);
            }

            int boardValue = minimaxPruning(newState, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isWhite);

            if (isWhite) {
                if (boardValue > bestValue) {
                    bestValue = boardValue;
                    bestTurns.clear();
                    bestTurns.add(turn);
                } else if (boardValue == bestValue) {
                    bestTurns.add(turn);
                }
            } else {
                if (boardValue < bestValue) {
                    bestValue = boardValue;
                    bestTurns.clear();
                    bestTurns.add(turn);
                } else if (boardValue == bestValue) {
                    bestTurns.add(turn);
                }
            }
        }
        // Randomization between equally-valued moves
        if (!bestTurns.isEmpty()) {
            int randomIndex = (int)(Math.random() * bestTurns.size());
            bestTurn = bestTurns.get(randomIndex);
        }
        return bestTurn;
    }

    public int minimaxPruning(GameState gameState, int depth, int alpha, int beta, boolean isMaxPlayerWhite) {
        if (depth == 0 || gameState.isGameOver()) {
            return (int) gameState.evaluateBoard();
        }

        List<Turn> legalTurns = gameState.getLegalTurns();
        boolean maxPlayer = (gameState.getIsWhiteTurn() == isMaxPlayerWhite);

        if (maxPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Turn turn : legalTurns) {
                GameState newState = new GameState(gameState);
                for (Move move : turn.getMoves()) {
                    newState.move(move);
                }

                int eval = minimaxPruning(newState, depth - 1, alpha, beta, isMaxPlayerWhite);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) { // Beta cutoff
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Turn turn : legalTurns) {
                GameState newState = new GameState(gameState);
                for (Move move : turn.getMoves()) {
                    newState.move(move);
                }

                int eval = minimaxPruning(newState, depth - 1, alpha, beta, isMaxPlayerWhite);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) { // Alpha cutoff
                    break;
                }
            }
            return minEval;
        }
    }

    // Implement PN Search in endgame situations
    private Turn getBestTurnPNSearch() {
        PNSearch pnSearch = new PNSearch(isWhite);
        GameState currentState = gameState;
        PNSearch.Node root;
        if (isWhite) {
            root = pnSearch.new Node(null, currentState, PNSearch.Node.OR_NODE);
        } else {
            root = pnSearch.new Node(null, currentState, PNSearch.Node.AND_NODE);
        }
        int maxNodes = 10000;
        pnSearch.PN(root, maxNodes);

        if (root.proof == 0) {
            return findBestTurnPNSearch(root);
        } else if (root.disproof == 0) {
            return findBestTurnPNSearch(root);
        } else {
            List<Turn> turns = currentState.getLegalTurns();
            return getBestTurnABP(turns);
        }
    }

    private Turn findBestTurnPNSearch(PNSearch.Node root) {
        PNSearch.Node node = root;
        Turn bestTurn = new Turn();

        while (node != null && node.children != null && !node.children.isEmpty()) {
            PNSearch.Node bestChild = null;
            if (node.type == PNSearch.Node.OR_NODE) {
                for (PNSearch.Node child : node.children) {
                    if (child.proof == node.proof) {
                        bestChild = child;
                        break;
                    }
                }
            } else {
                for (PNSearch.Node child : node.children) {
                    if (child.disproof == node.disproof) {
                        bestChild = child;
                        break;
                    }
                }
            }
            if (bestChild != null) {
                Move move = bestChild.moveFromParent;
                if (move != null) {
                    bestTurn.addMove(move);
                }
                node = bestChild;
            } else {
                break;
            }
        }
        return bestTurn;
    }

    @Override
    public Agent reset() {
        return new AlphaBetaAgent(isWhite, maxDepth);
    }

    @Override
public void simulate() {
    if (gameState.getIsWhiteTurn() == isWhite && gameState.getWinner() == 0) {
        List<Turn> turns = gameState.getLegalTurns();
        if (!turns.isEmpty()) {
            Turn bestTurn;
                bestTurn = getBestTurnABP(turns);
            if (bestTurn != null && !bestTurn.getMoves().isEmpty()) {
                for (Move move : bestTurn.getMoves()) {
                    gameState.move(move);
                }
            }
        }
    }
}


    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }
}
