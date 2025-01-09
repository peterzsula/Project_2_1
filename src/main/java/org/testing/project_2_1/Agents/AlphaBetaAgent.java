package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.PNSearch;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Alpha-Beta agent implementation for the checkers game.
 * The agent uses the Alpha-Beta pruning algorithm to evaluate and make optimal moves.
 * Additionally, it switches to Proof-Number Search (PNS) for endgame scenarios.
 */
public class AlphaBetaAgent implements Agent {
    private GameLogic gameLogic; // Reference to the game logic
    private GameState gameState; // Current game state

    private boolean isWhite; // Indicates if the agent is playing as white
    private int maxDepth; // Maximum depth for Alpha-Beta pruning
    private final int defaultDepth = 3; // Default depth for Alpha-Beta pruning

    private int nodesVisited = 0; // Counter for runtime profiling
    private Turn currentTurn; // The current turn being executed by the agent
    public boolean PNS = false; // Whether to use PNS for endgame scenarios

    /**
     * Constructs an Alpha-Beta agent with a specified color and maximum depth.
     * @param isWhite Whether the agent plays as white.
     * @param maxDepth The maximum depth for Alpha-Beta pruning.
     */
    public AlphaBetaAgent(boolean isWhite, int maxDepth) {
        this.isWhite = isWhite;
        this.maxDepth = maxDepth;
        this.currentTurn = new Turn();
    }

    public AlphaBetaAgent(boolean isWhite,GameState gameState, int maxDepth,  boolean PNS) {
        this.isWhite = isWhite;
        this.maxDepth = maxDepth;
        this.gameState = gameState;
        this.currentTurn = new Turn();
        this.PNS = PNS;
    }

    public AlphaBetaAgent(boolean isWhite, int maxDepth, boolean PNS) {
        this.isWhite = isWhite;
        this.maxDepth = maxDepth;
        this.currentTurn = new Turn();
        this.PNS = PNS;
    }

    /**
     * Constructs an Alpha-Beta agent with a specified color and initial game state.
     * @param isWhite Whether the agent plays as white.
     * @param gameState The initial game state.
     */
    public AlphaBetaAgent(boolean isWhite, GameState gameState) {
        this.isWhite = isWhite;
        this.gameState = gameState;
        this.currentTurn = new Turn();
        this.maxDepth = defaultDepth;
    }

    /**
     * Constructs an Alpha-Beta agent with a specified color, game state, and depth.
     * @param isWhite Whether the agent plays as white.
     * @param gameState The initial game state.
     * @param depth The depth for Alpha-Beta pruning.
     */
    public AlphaBetaAgent(boolean isWhite, GameState gameState, int depth) {
        this.isWhite = isWhite;
        this.gameState = gameState;
        this.currentTurn = new Turn();
        this.maxDepth = depth;
    }

    /**
     * Sets the game logic for the agent.
     * @param gameLogic The game's logic object.
     */
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

    /**
     * Retrieves the number of nodes visited during the Alpha-Beta pruning process.
     * @return The number of nodes visited.
     */
    public int getNodesVisited() {
        return nodesVisited;
    }

    /**
     * Resets the node counter for profiling purposes.
     */
    public void resetNodeCounter() {
        nodesVisited = 0; // Reset the counter for new runs
    }

    /**
     * Makes a move by selecting the best turn using either Alpha-Beta pruning or Proof-Number Search (PNS).
     * This method evaluates the current game state and selects the best move.
     */
    @Override
    public void makeMove() {
        //System.out.println("Alpha-Beta agent making move");
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
            if (gameState.getIsWhiteTurn() == isWhite && !gameState.isGameOver()) {
                if (currentTurn.isEmpty()) {
                    List<Turn> turns = gameState.getLegalTurns();
                    if (isEndgame(gameState) && PNS) {
                        currentTurn = getBestTurnPNSearch();
                    } else {
                    currentTurn = getBestTurnABP(turns);
                    }
                }
                Move move = currentTurn.getMoves().removeFirst();
                gameLogic.takeMove(move);
            }
        });
        pause.play();
    }

    /**
     * Checks if the game is in the endgame phase based on the number of pieces.
     * @param gameState The current game state.
     * @return True if the game is in the endgame phase, otherwise false.
     */
    private boolean isEndgame(GameState gameState) {
        int totalPieces = gameState.countPieces();
        int endgameThreshold = 15; // Threshold for determining endgame based on the number of pieces
        return totalPieces <= endgameThreshold;
    }

    /**
     * Selects the best turn using Alpha-Beta pruning from the list of legal turns.
     * @param turns The list of legal turns.
     * @return The best turn based on Alpha-Beta pruning.
     */
    private Turn getBestTurnABP(List<Turn> turns) {
        ArrayList<Turn> bestTurns = new ArrayList<>();
        int bestValue;
        if (isWhite) {
            bestValue = Integer.MIN_VALUE;
        } else {
            bestValue = Integer.MAX_VALUE;
        }
    
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
                }
                if (boardValue == bestValue) {
                    bestTurns.add(turn);
                }
            } else {
                if (boardValue < bestValue) {
                    bestValue = boardValue;
                    bestTurns.clear();
                    bestTurns.add(turn);
                }
                if (boardValue == bestValue) {
                    bestTurns.add(turn);
                }
            }
        }
        int randomIndex = new Random().nextInt(bestTurns.size());
        Turn bestTurn = bestTurns.get(randomIndex);
        bestTurn.setEvaluation(bestValue);
        return bestTurn;
    }

    /**
     * Alpha-Beta pruning implementation of the minimax algorithm to evaluate the best move.
     * @param gameState The current game state.
     * @param depth The remaining depth for the search.
     * @param alpha The best value for the maximizer.
     * @param beta The best value for the minimizer.
     * @param isMaxPlayerWhite Whether the maximizing player is white.
     * @return The evaluation of the current game state.
     */
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

    /**
     * Determines the best turn in endgame situations using Proof-Number Search (PNS).
     * Falls back to Alpha-Beta Pruning if PNS is inconclusive.
     * @return The best turn found using PNS or Alpha-Beta Pruning.
     */
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

        // Check if PNSearch found either a proof or disproof
        if (root.proof == 0 || root.disproof == 0) {
            Turn pnTurn = findBestTurnPNSearch(root);
            if (pnTurn != null && !pnTurn.getMoves().isEmpty()) {
                return pnTurn;
            }
        }

        // Fall back to Alpha-Beta pruning if PNSearch is inconclusive or returns invalid turn
        System.out.println("PNSearch inconclusive, calling back Alpha-Beta Pruning");
        List<Turn> turns = currentState.getLegalTurns();
        return getBestTurnABP(turns);
    }

    /**
     * Traverses the PNS tree to construct the best turn based on the proof or disproof numbers.
     * @param root The root node of the PNS tree.
     * @return The best turn found in the PNS tree.
     */
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

    /**
     * Profiles memory usage of the minimax algorithm for complexity analysis.
     * @param gameState The game state to analyze.
     * @param depth The depth to search in the minimax algorithm.
     * @param isMaxPlayerWhite Whether the maximizing player is white.
     */
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

    /**
     * Implements the plain minimax algorithm without Alpha-Beta pruning.
     * @param gameState The current game state.
     * @param depth The depth of the search.
     * @param isMaxPlayerWhite Whether the maximizing player is white.
     * @return The evaluation of the best possible move.
     */
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

    /**
     * Simulates the agent's decision-making process by playing the best moves
     * using Alpha-Beta Pruning until a winner is determined.
     */
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

    /**
     * Pauses the agent's actions.
     * Currently unimplemented.
     * @throws UnsupportedOperationException If called.
     */
    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }
}
