package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.PNSearch;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class AlphaBetaAgent implements Agent {
    private GameLogic gameLogic;
    private GameState gameState;

    private boolean isWhite;
    private int maxDepth;
    private final int defaultDepth = 3;

    private int nodesVisited = 0; // Counter for runtime profiling
    private Turn currentTurn;

    public AlphaBetaAgent(boolean isWhite, int maxDepth) {
        this.isWhite = isWhite;
        this.maxDepth = maxDepth;
        this.currentTurn = new Turn();
    }
    public AlphaBetaAgent(boolean isWhite, GameState gameState){
        this.isWhite = isWhite;
        this.gameState = gameState;
        this.currentTurn = new Turn();
        this.maxDepth = defaultDepth;
    }

    public AlphaBetaAgent(boolean isWhite, GameState gameState, int depth) {
        this.isWhite = isWhite;
        this.gameState = gameState;
        this.currentTurn = new Turn();
        this.maxDepth = depth;
    }
    
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.gameState = gameLogic.g;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public void resetNodeCounter() {
        nodesVisited = 0; // Reset the counter for new runs
    }

    @Override
    public void makeMove() {
        System.out.println("Alpha-Beta agent making move");
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
            if (gameState.getIsWhiteTurn() == isWhite && !gameState.isGameOver()) {
                List<Turn> turns = gameState.getLegalTurns();
                if (gameState.isEndgame()) {
                    System.out.println("Endgame detected, using Proof-Number Search");
                    currentTurn = getBestTurnPNSearch();
                } else {
                    currentTurn = getBestTurnABP(turns);
                }
                if (currentTurn != null && !currentTurn.getMoves().isEmpty()) {
                    Move move = currentTurn.getMoves().removeFirst();
                    gameLogic.takeMove(move);
                }
            }
        });
        pause.play();
    }

    private Turn getBestTurnABP(List<Turn> turns) { // get best turn using Alpha-Beta Pruning
        Turn bestTurn = null;
        int bestValue;
        if (isWhite) {
            bestValue = Integer.MIN_VALUE;  // maximize score
        } else {
            bestValue = Integer.MAX_VALUE; // to minimize score
        }

        List<Turn> shuffledTurns = new ArrayList<>(turns);
        Collections.shuffle(shuffledTurns); // Shuffle the turn before evaluation to avoid obtaining exact same outcome every game
        for (Turn turn : shuffledTurns) {
            GameState newState = new GameState(gameState);
            for (Move move : turn.getMoves()) {
                newState.move(move);
            }

            int boardValue = minimaxPruning(newState, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isWhite);

            if (isWhite) {
                if (boardValue > bestValue) {
                    bestValue = boardValue;
                    shuffledTurns.clear();
                    shuffledTurns.add(turn);
                } else if (boardValue == bestValue) {
                    shuffledTurns.add(turn);
                }
            } else {
                if (boardValue < bestValue) {
                    bestValue = boardValue;
                    shuffledTurns.clear();
                    shuffledTurns.add(turn);
                } else if (boardValue == bestValue) {
                    shuffledTurns.add(turn);
                }
            }
        }
        // Randomization between equally-valued moves
        if (!shuffledTurns.isEmpty()) {
            int randomIndex = (int)(Math.random() * shuffledTurns.size());
            bestTurn = shuffledTurns.get(randomIndex);
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

    // Using this for the Complexity tests
    // PLEASE, DO NOT REMOVE ANYTHING
    public void profileMemoryUsage(GameState gameState, int depth, boolean isMaxPlayerWhite) {
        Runtime runtime = Runtime.getRuntime();

        // Trigger garbage collection for clean measurement
        runtime.gc();

        // Memory usage before running the algorithm
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Run the algorithm
        minimaxPruning(gameState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayerWhite);

        // Memory usage after running the algorithm
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        // Calculate memory used
        long memoryUsed = memoryAfter - memoryBefore;
        System.out.println("Memory used: " + memoryUsed + " bytes");
    }

    public int minimaxPlain(GameState gameState, int depth, boolean isMaxPlayerWhite) {
        nodesVisited++; // Increment node counter

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

                int eval = minimaxPlain(newState, depth - 1, isMaxPlayerWhite);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Turn turn : legalTurns) {
                GameState newState = new GameState(gameState);
                for (Move move : turn.getMoves()) {
                    newState.move(move);
                }

                int eval = minimaxPlain(newState, depth - 1, isMaxPlayerWhite);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
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
