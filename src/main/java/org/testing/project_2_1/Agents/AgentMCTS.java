package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implements an agent using Monte Carlo Tree Search (MCTS) for decision-making.
 * The agent simulates multiple possible game outcomes to determine the optimal move.
 */
public class AgentMCTS implements Agent {
    private GameLogic gameLogic; // Reference to the game logic
    private GameState gameState; // Current game state
    private boolean isWhite; // Indicates if the agent is playing as white
    private static final int SIMULATIONS = 20; // Number of simulations per move
    int numSimulations = Math.min(SIMULATIONS, 100 / (1 + gameState.getDepth()));
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2); // UCB1 constant for exploration
    private Turn currentTurn; // The current turn being executed

    /**
     * Node class representing a state in the MCTS tree.
     */
    private static class Node {
        GameState state; // The game state at this node
        Turn turn; // The move leading to this state
        Node parent; // Parent node
        ArrayList<Node> children; // List of child nodes
        int visits; // Number of visits to this node
        double wins; // Total wins accumulated by this node

        /**
         * Constructs a Node for the MCTS tree.
         * @param state The game state at this node.
         * @param move The move leading to this state.
         * @param parent The parent node.
         */
        Node(GameState state, Turn move, Node parent) {
            this.state = state;
            this.turn = move;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.visits = 0;
            this.wins = 0.0;
        }
    }

    /**
     * Constructs an MCTS agent with the specified player color.
     * @param isWhite Whether the agent plays as white.
     */
    public AgentMCTS(boolean isWhite) {
        this.isWhite = isWhite;
        currentTurn = new Turn();
    }

    /**
     * Constructs an MCTS agent with the specified player color and game state.
     * @param isWhite Whether the agent plays as white.
     * @param gameState The initial game state.
     */
    public AgentMCTS(boolean isWhite, GameState gameState) {
        this.isWhite = isWhite;
        this.gameState = gameState;
        currentTurn = new Turn();
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.gameState = gameLogic.g;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Executes a move using MCTS to determine the best possible action.
     */
    @Override
    public void makeMove() {
        System.out.println("Monte Carlo Tree Search agent making move");

        // Create the root node for the MCTS tree
        Node root = new Node(new GameState(gameState), null, null);

        // Perform simulations
        for (int i = 0; i < numSimulations; i++) {
            Node selectedNode = selection(root); // Select the most promising node
            if (!selectedNode.state.isGameOver()) {
                expansion(selectedNode); // Expand the selected node
                Node nodeToSimulate = selectedNode.children.isEmpty() ? selectedNode : getRandomChild(selectedNode);
                double result = simulation(nodeToSimulate.state); // Simulate the game
                backpropagation(nodeToSimulate, result); // Update the tree with the result
            }
        }

        // Choose the best move
        Node bestChild = selectBestChild(root);
        if (bestChild != null && bestChild.turn != null) {
            if (currentTurn.isEmpty()) {
                currentTurn = bestChild.turn;
            }
            Move move = currentTurn.getMoves().remove(0);
            PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
            pause.setOnFinished(event -> {
                if (gameState.getIsWhiteTurn() == isWhite && !gameState.isGameOver()) {
                    gameLogic.takeMove(move);
                }
            });
            pause.play();
        } else {
            System.out.println("No valid moves found.");
        }
    }

    private Node selection(Node node) {
        while (!node.children.isEmpty()) {
            node = selectWithUCB1(node);
        }
        return node;
    }

    private Node selectWithUCB1(Node node) {
        Node bestNode = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        double logVisits = Math.log(node.visits + 1);

        for (Node child : node.children) {
            double ucb1Value = (child.wins / (child.visits + 1e-6)) +
                    EXPLORATION_CONSTANT * Math.sqrt(logVisits / (child.visits + 1e-6));
            if (ucb1Value > bestValue) {
                bestValue = ucb1Value;
                bestNode = child;
            }
        }

        return bestNode;
    }



    private void expansion(Node node) {
        List<Turn> legalTurns = node.state.getLegalTurns();
        for (Turn turn : legalTurns) {
            GameState newState = new GameState(node.state); // Clone the state for the child
            for (Move move : turn.getMoves()) {
                newState.move(move);
            }
            Node childNode = new Node(newState, turn, node);
            node.children.add(childNode);
        }
    }

    private double simulation(GameState state) {
        GameState simState = new GameState(state);
        Random random = new Random();

        while (!simState.isGameOver()) {
            List<Turn> legalTurns = simState.getLegalTurns();
            if (legalTurns.isEmpty()) {
                break;
            }

            // Prioritize better moves with heuristics
            Turn bestTurn = null;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (Turn turn : legalTurns) {
                GameState testState = new GameState(simState);
                for (Move move : turn.getMoves()) {
                    testState.move(move);
                }

                double heuristicScore = testState.evaluateBoard() +
                        (isWhite ? testState.getWhiteAdvantage() : testState.getBlackAdvantage());

                if (heuristicScore > bestScore) {
                    bestScore = heuristicScore;
                    bestTurn = turn;
                }
            }

            Turn selectedTurn = (bestTurn != null) ? bestTurn : legalTurns.get(random.nextInt(legalTurns.size()));
            for (Move move : selectedTurn.getMoves()) {
                simState.move(move);
            }
        }

        return simState.evaluateBoard();
    }


    private void backpropagation(Node node, double result) {
        while (node != null) {
            node.visits++;
            node.wins += result;
            result = -result; // Invert result for opponent
            node = node.parent;
        }
    }

    private Node selectBestChild(Node node) {
        Node bestChild = null;
        int maxVisits = -1;

        for (Node child : node.children) {
            if (child.visits > maxVisits) {
                maxVisits = child.visits;
                bestChild = child;
            }
        }

        return bestChild;
    }

    private Node getRandomChild(Node node) {
        if (node.children.isEmpty()) {
            return node;
        }
        Random random = new Random();
        return node.children.get(random.nextInt(node.children.size()));
    }

    @Override
    public Agent reset() {
        return new AgentMCTS(isWhite);
    }

    @Override
    public void simulate() {
        Node root = new Node(new GameState(gameState), null, null);

        // Perform simulations
        for (int i = 0; i < numSimulations; i++) {
            Node selectedNode = selection(root);
            if (!selectedNode.state.isGameOver()) {
                expansion(selectedNode);
                Node nodeToSimulate = selectedNode.children.isEmpty() ? selectedNode : getRandomChild(selectedNode);
                double result = simulation(nodeToSimulate.state);
                backpropagation(nodeToSimulate, result);
            }
        }

        // Execute the best move
        Node bestChild = selectBestChild(root);
        if (bestChild != null && bestChild.turn != null) {
            if (currentTurn.isEmpty()) {
                currentTurn = bestChild.turn;
            }
            while (!currentTurn.isEmpty() && gameState.getWinner() == 0) {
                List<Move> moves = currentTurn.getMoves();
                if (!moves.isEmpty()) {
                    gameState.move(moves.remove(0));
                }
            }
        }
    }

    private void performSimulations(Node root, long timeLimitMillis) {
        long startTime = System.currentTimeMillis();
        int simulations = 0;

        while (simulations < SIMULATIONS && (System.currentTimeMillis() - startTime) < timeLimitMillis) {
            Node selectedNode = selection(root);
            if (!selectedNode.state.isGameOver()) {
                expansion(selectedNode);
                Node nodeToSimulate = selectedNode.children.isEmpty() ? selectedNode : getRandomChild(selectedNode);
                double result = simulation(nodeToSimulate.state);
                backpropagation(nodeToSimulate, result);
            }
            simulations++;
        }
    }


    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }
}
