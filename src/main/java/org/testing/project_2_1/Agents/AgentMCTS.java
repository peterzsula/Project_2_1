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
    private static final int SIMULATIONS = 100; // Number of simulations per move
    private static final double EXPLORATION_CONSTANT = 0.2; // UCB1 constant for exploration
    private Turn currentTurn; // The current turn being executed
    private double[] coefficients = {1, -1, 3, -3, 1, -1, 2, -2};

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

    /**
     * Constructs an MCTS agent with coefficients for evaluation.
     * This constructor allows the agent's evaluation strategy to be customized 
     * based on provided coefficients, enabling fine-tuning for different scenarios.
     *
     * @param isWhite      Indicates if the agent is playing as white.
     * @param gameState    The initial game state for the agent.
     * @param coefficients An array of coefficients used for board evaluation.
     */
    public AgentMCTS(boolean isWhite, GameState gameState, double[] coefficients) {
        this.isWhite = isWhite; // Set the color of the agent.
        this.gameState = gameState; // Initialize the game state.
        currentTurn = new Turn(); // Initialize the current turn.
        this.coefficients = coefficients; // Store the evaluation coefficients.
    }

    /**
     * Returns whether the agent is playing as white.
     *
     * @return True if the agent is playing as white, false otherwise.
     */
    @Override
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Sets the game logic for the agent.
     * This method links the agent to the game logic, enabling it to interact 
     * with the game and make moves.
     *
     * @param gameLogic The game logic instance to be used by the agent.
     */
    @Override
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic; // Assign the game logic instance.
        this.gameState = gameLogic.g; // Synchronize the game state with the logic.
    }

    /**
     * Updates the current game state of the agent.
     * This method allows external components to set a new game state, 
     * ensuring the agent has up-to-date information about the game.
     *
     * @param gameState The new game state to be set.
     */
    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState; // Update the game state.
    }

    /**
     * Executes a move using MCTS to determine the best possible action.
     */
    @Override
    public void makeMove() {
        // Create the root node for the MCTS tree
        Node root = new Node(new GameState(gameState), null, null);

        int numSimulations = Math.min(SIMULATIONS, 100 / (1 + gameState.getDepth()));
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
                if (gameState.isWhiteTurn() == isWhite && !gameState.isGameOver()) {
                    gameLogic.takeMove(move);
                }
            });
            pause.play();
        } else {
            System.out.println("No valid moves found.");
        }
    }

    /**
     * Selects the most promising node in the MCTS tree using the UCB1 algorithm.
     * This method traverses the tree down to a leaf node by iteratively selecting 
     * child nodes with the highest Upper Confidence Bound for Trees (UCB1) value.
     *
     * @param node The starting node for selection.
     * @return The most promising node to expand or simulate.
     */
    private Node selection(Node node) {
        // Traverse the tree while the current node has children.
        while (!node.children.isEmpty()) {
            node = selectWithUCB1(node); // Select the best child using UCB1.
        }
        return node; // Return the leaf node for further expansion or simulation.
    }

    /**
     * Selects the best child node of a given node based on the UCB1 formula.
     * The UCB1 formula balances exploration (visiting less-visited nodes) 
     * and exploitation (choosing nodes with higher win rates).
     *
     * @param node The parent node whose children are evaluated.
     * @return The child node with the highest UCB1 value.
     */
    private Node selectWithUCB1(Node node) {
        Node bestNode = null; // Initialize the best node as null.
        double bestValue = Double.NEGATIVE_INFINITY; // Initialize the best value as negative infinity.
        double logVisits = Math.log(Math.max(1, node.visits)); // Precompute the logarithm of the parent visits.

        // Iterate over all child nodes to calculate their UCB1 values.
        for (Node child : node.children) {
            // Calculate the UCB1 value for the child node.
            double ucb1Value = (child.visits > 0 ?
                    (child.wins / child.visits) : Double.POSITIVE_INFINITY) + // Exploitation term
                    EXPLORATION_CONSTANT * Math.sqrt(logVisits / (1 + child.visits)); // Exploration term

            // Update the best node if the current UCB1 value is higher.
            if (ucb1Value > bestValue) {
                bestValue = ucb1Value;
                bestNode = child;
            }
        }

        return bestNode; // Return the child node with the highest UCB1 value.
    }

    /**
     * Expands a given node by generating its child nodes.
     * Each child node corresponds to a possible turn (legal sequence of moves) 
     * from the current game state. The child's state is derived by applying the turn.
     *
     * @param node The node to expand.
     */
    private void expansion(Node node) {
        // Retrieve all legal turns (sequences of moves) for the current game state.
        List<Turn> legalTurns = node.state.getLegalTurns();

        // Create child nodes for each legal turn.
        for (Turn turn : legalTurns) {
            GameState newState = new GameState(node.state); // Clone the current state for the child.

            // Apply all moves in the turn to the cloned state.
            for (Move move : turn.getMoves()) {
                newState.move(move);
            }

            // Create a new child node with the updated state and add it to the parent node's children.
            Node childNode = new Node(newState, turn, node);
            node.children.add(childNode);
        }
    }


    /**
     * Expands a given node selectively by considering only the top-ranked legal turns.
     * The ranking of turns is based on the evaluation of the resulting board state after applying the turn.
     * This selective expansion limits the number of child nodes, focusing on the most promising moves.
     *
     * @param node The node to selectively expand.
     */
    private void selectiveExpansion(Node node) {
        // Retrieve all legal turns (sequences of moves) for the current game state.
        List<Turn> legalTurns = node.state.getLegalTurns();

        // Sort the legal turns based on their evaluated board scores in descending order.
        legalTurns.sort((turn1, turn2) -> {
            GameState testState1 = new GameState(node.state); // Clone the current state for turn1 evaluation.
            turn1.getMoves().forEach(testState1::move); // Apply turn1 moves.
            double score1 = testState1.evaluateBoard(coefficients); // Evaluate the resulting board.

            GameState testState2 = new GameState(node.state); // Clone the current state for turn2 evaluation.
            turn2.getMoves().forEach(testState2::move); // Apply turn2 moves.
            double score2 = testState2.evaluateBoard(coefficients); // Evaluate the resulting board.

            return Double.compare(score2, score1); // Compare scores in descending order.
        });

        // Limit the number of expansions to the top moves.
        int expandLimit = Math.min(legalTurns.size(), 5); // Choose up to 5 top-ranked moves.
        for (int i = 0; i < expandLimit; i++) {
            Turn turn = legalTurns.get(i);
            GameState newState = new GameState(node.state); // Clone the current state for the child node.
            turn.getMoves().forEach(newState::move); // Apply the moves in the turn.
            Node childNode = new Node(newState, turn, node); // Create a new child node.
            node.children.add(childNode); // Add the child node to the parent.
        }
    }

    /**
     * Simulates the game from a given state to evaluate its outcome.
     * The simulation proceeds by selecting turns randomly or based on simple heuristics,
     * until the game ends or a depth limit is reached.
     *
     * @param state The starting game state for the simulation.
     * @return The evaluation score of the board at the end of the simulation.
     */
    private double simulation(GameState state) {
        GameState simState = new GameState(state); // Clone the initial state for simulation.
        Random random = new Random();

        // Adjust the depth limit dynamically based on the remaining pieces on the board.
        int remainingPieces = simState.getWhitePieces().size() + simState.getBlackPieces().size();
        int depthLimit = Math.max(5, 50 - remainingPieces); // Set a minimum depth of 5 and decrease as pieces reduce.

        // Simulate the game until it ends or the depth limit is reached.
        while (!simState.isGameOver() && depthLimit-- > 0) {
            List<Turn> legalTurns = simState.getLegalTurns(); // Retrieve legal turns.
            if (legalTurns.isEmpty()) {
                break; // End simulation if no legal moves are available.
            }

            // Select the best turn based on board evaluation or fallback to a random turn.
            Turn bestTurn = legalTurns.stream()
                    .max((turn1, turn2) -> {
                        GameState testState1 = new GameState(simState); // Clone for turn1 evaluation.
                        turn1.getMoves().forEach(testState1::move); // Apply turn1 moves.
                        double score1 = testState1.evaluate(); // Evaluate the resulting board.

                        GameState testState2 = new GameState(simState); // Clone for turn2 evaluation.
                        turn2.getMoves().forEach(testState2::move); // Apply turn2 moves.
                        double score2 = testState2.evaluate(); // Evaluate the resulting board.

                        return Double.compare(score1, score2); // Compare scores to select the better turn.
                    }).orElse(legalTurns.get(random.nextInt(legalTurns.size()))); // Fallback to a random turn if no comparison.

            // Apply the selected turn's moves to the simulation state.
            bestTurn.getMoves().forEach(simState::move);
        }

        // Return the evaluation score of the final board state.
        return simState.evaluateBoard(coefficients);
    }
    
    /**
     * Backpropagates the result of a simulation up the MCTS tree.
     * This method updates the win statistics and visit counts for all nodes
     * in the path from the given node to the root.
     *
     * @param node   The node where backpropagation starts.
     * @param result The result of the simulation (positive for a win, negative for a loss).
     */
    private void backpropagation(Node node, double result) {
        while (node != null) {
            node.visits++; // Increment the visit count for the node.
            node.wins += result; // Update the wins with the result of the simulation.
            result = -result; // Invert the result for the opponent's perspective.
            node = node.parent; // Move up to the parent node.
        }
    }

    /**
     * Selects the best child node of a given node based on the number of visits.
     * The child with the highest visit count is considered the best.
     *
     * @param node The parent node whose children are evaluated.
     * @return The child node with the highest visit count.
     */
    private Node selectBestChild(Node node) {
        Node bestChild = null; // Initialize the best child as null.
        int maxVisits = -1; // Initialize the maximum visit count as -1.

        // Iterate over all child nodes to find the one with the highest visit count.
        for (Node child : node.children) {
            if (child.visits > maxVisits) {
                maxVisits = child.visits; // Update the maximum visit count.
                bestChild = child; // Update the best child.
            }
        }

        return bestChild; // Return the child with the highest visits.
    }

    /**
     * Selects a random child node of a given node.
     * If the node has no children, it returns the node itself.
     *
     * @param node The parent node.
     * @return A randomly selected child node, or the node itself if it has no children.
     */
    private Node getRandomChild(Node node) {
        if (node.children.isEmpty()) {
            return node; // Return the node itself if it has no children.
        }
        Random random = new Random(); // Create a random number generator.
        return node.children.get(random.nextInt(node.children.size())); // Return a random child.
    }

    /**
     * Resets the agent by creating a new instance of the same type.
     * This method allows the agent to start fresh with the same settings.
     *
     * @return A new instance of the AgentMCTS class with the same color configuration.
     */
    @Override
    public Agent reset() {
        return new AgentMCTS(isWhite); // Create and return a new instance of the agent.
    }

    /**
     * Simulates multiple games from the current game state to determine the best move.
     * This method builds the MCTS tree by performing selection, expansion, simulation, 
     * and backpropagation for a specified number of simulations.
     */
    @Override
    public void simulate() {
        // Create the root node for the MCTS tree.
        Node root = new Node(new GameState(gameState), null, null);

        // Determine the number of simulations to perform.
        int numSimulations = Math.min(SIMULATIONS, 100 / (1 + gameState.getDepth()));

        // Perform the simulations.
        for (int i = 0; i < numSimulations; i++) {
            Node selectedNode = selection(root); // Select the most promising node.
            if (!selectedNode.state.isGameOver()) {
                selectiveExpansion(selectedNode); // Expand the selected node selectively.
                Node nodeToSimulate = selectedNode.children.isEmpty() ? selectedNode : getRandomChild(selectedNode);
                double result = simulation(nodeToSimulate.state); // Simulate the game from the node.
                backpropagation(nodeToSimulate, result); // Backpropagate the result.
            }
        }

        // Execute the best move found.
        Node bestChild = selectBestChild(root); // Select the best child node based on visits.
        if (bestChild != null && bestChild.turn != null) {
            if (currentTurn.isEmpty()) {
                currentTurn = bestChild.turn; // Set the current turn to the best turn found.
            }
            // Execute all moves in the selected turn until the game ends or the turn is empty.
            while (!currentTurn.isEmpty() && gameState.getWinner() == 0) {
                List<Move> moves = currentTurn.getMoves();
                if (!moves.isEmpty()) {
                    gameState.move(moves.remove(0)); // Apply the move to the game state.
                }
            }
        }
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public Turn findBetterTurn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findBetterTurn'");
    }
}
