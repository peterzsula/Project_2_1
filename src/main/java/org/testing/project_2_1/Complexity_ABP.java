package org.testing.project_2_1;

import org.testing.project_2_1.Agents.AlphaBetaAgent;
import org.testing.project_2_1.GameLogic.GameState;

/**
 * A test class for evaluating the performance and correctness of the AlphaBetaAgent.
 * This class tests the agent under various game scenarios with both Alpha-Beta pruning
 * and plain minimax algorithms at different depths.
 */
public class Complexity_ABP {

    /**
     * Entry point for the test program.
     * 
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Initialize test states for different scenarios
        GameState testStateLowBranching = createLowBranchingState();
        GameState testStateHighBranching = createHighBranchingState();
        GameState testStateEdgeCase = createEdgeCaseState();

        // Depths to test the agent at
        int[] depths = {1, 2, 3, 4, 5};

        // Initialize the AlphaBetaAgent
        AlphaBetaAgent agent = new AlphaBetaAgent(true, 0);

        // Test the agent with Alpha-Beta Pruning
        System.out.println("Testing AlphaBetaAgent with Alpha-Beta Pruning...");
        runScenarios(agent, testStateLowBranching, testStateHighBranching, testStateEdgeCase, depths, true);

        // Test the agent with Plain Minimax
        System.out.println("\nTesting AlphaBetaAgent with Plain Minimax...");
        runScenarios(agent, testStateLowBranching, testStateHighBranching, testStateEdgeCase, depths, false);
    }

    /**
     * Runs the test scenarios for the given agent and game states.
     * 
     * @param agent         The AlphaBetaAgent to test.
     * @param low           The game state with low branching factor.
     * @param high          The game state with high branching factor.
     * @param edge          The edge case game state.
     * @param depths        The depths to test the agent at.
     * @param usePruning    True to use Alpha-Beta pruning, false to use plain minimax.
     */
    private static void runScenarios(AlphaBetaAgent agent, GameState low, GameState high, GameState edge, int[] depths, boolean usePruning) {
        for (int depth : depths) {
            System.out.println("\nDepth: " + depth);
            agent.resetNodeCounter(); // Reset the node counter for each depth

            // Test low branching scenario
            System.out.println("Scenario: Low Branching Factor");
            runTest(agent, low, depth, usePruning);

            // Test high branching scenario
            System.out.println("Scenario: High Branching Factor");
            runTest(agent, high, depth, usePruning);

            // Test edge case scenario
            System.out.println("Scenario: Edge Case");
            runTest(agent, edge, depth, usePruning);
        }
    }

    /**
     * Runs a single test scenario for a specific game state and depth.
     * 
     * @param agent      The AlphaBetaAgent to test.
     * @param gameState  The game state to test.
     * @param depth      The depth of the search tree.
     * @param usePruning True to use Alpha-Beta pruning, false to use plain minimax.
     */
    private static void runTest(AlphaBetaAgent agent, GameState gameState, int depth, boolean usePruning) {
        Runtime runtime = Runtime.getRuntime();

        // Perform garbage collection before measuring memory usage
        runtime.gc(); 
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Measure execution time and run the algorithm
        long startTime = System.nanoTime();
        if (usePruning) {
            agent.minimaxPruning(gameState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        } else {
            agent.minimaxPlain(gameState, depth, true);
        }
        long endTime = System.nanoTime();

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;

        // Calculate execution time in milliseconds
        double executionTimeInMilliseconds = (endTime - startTime) / 1_000_000.0;

        // Display results
        System.out.println("Nodes Visited: " + agent.getNodesVisited());
        System.out.println("Memory Used: " + memoryUsed + " bytes");
        System.out.println("Execution Time: " + executionTimeInMilliseconds + " milliseconds");
    }

    /**
     * Creates a game state with a low branching factor (few legal moves).
     * 
     * @return A GameState instance with minimal branching.
     */
    private static GameState createLowBranchingState() {
        GameState state = new GameState();
        // Add minimal pieces to create few legal moves
        return state;
    }

    /**
     * Creates a game state with a high branching factor (many legal moves).
     * 
     * @return A GameState instance with high branching.
     */
    private static GameState createHighBranchingState() {
        GameState state = new GameState();
        // Add many pieces for a high branching factor
        return state;
    }

    /**
     * Creates an edge case game state (e.g., no legal moves or game over).
     * 
     * @return A GameState instance representing an edge case.
     */
    private static GameState createEdgeCaseState() {
        GameState state = new GameState();
        // Configure the state to have no moves or mark it as game over
        return state;
    }
}
