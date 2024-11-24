package org.testing.project_2_1.Test;

import org.testing.project_2_1.Agents.AlphaBetaAgent;
import org.testing.project_2_1.GameLogic.GameState;

public class AlphaBetaAgentTest {

    public static void main(String[] args) {
        GameState testStateLowBranching = createLowBranchingState();
        GameState testStateHighBranching = createHighBranchingState();
        GameState testStateEdgeCase = createEdgeCaseState();

        int[] depths = {1, 2, 3, 4, 5}; // Test various depths

        AlphaBetaAgent agent = new AlphaBetaAgent(true, 0);

        System.out.println("Testing AlphaBetaAgent with Alpha-Beta Pruning...");
        runScenarios(agent, testStateLowBranching, testStateHighBranching, testStateEdgeCase, depths, true);

        System.out.println("\nTesting AlphaBetaAgent with Plain Minimax...");
        runScenarios(agent, testStateLowBranching, testStateHighBranching, testStateEdgeCase, depths, false);
    }

    private static void runScenarios(AlphaBetaAgent agent, GameState low, GameState high, GameState edge, int[] depths, boolean usePruning) {
        for (int depth : depths) {
            System.out.println("\nDepth: " + depth);
            agent.resetNodeCounter();

            System.out.println("Scenario: Low Branching Factor");
            runTest(agent, low, depth, usePruning);

            System.out.println("Scenario: High Branching Factor");
            runTest(agent, high, depth, usePruning);

            System.out.println("Scenario: Edge Case");
            runTest(agent, edge, depth, usePruning);
        }
    }

    private static void runTest(AlphaBetaAgent agent, GameState gameState, int depth, boolean usePruning) {
        Runtime runtime = Runtime.getRuntime();
    
        runtime.gc(); 
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
    
        long startTime = System.nanoTime(); 
        if (usePruning) {
            agent.minimaxPruning(gameState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        } else {
            agent.minimaxPlain(gameState, depth, true); 
        }
        long endTime = System.nanoTime(); 
    
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;
 
        double executionTimeInMilliseconds = (endTime - startTime) / 1_000_000.0;
    
        // Display results
        System.out.println("Nodes Visited: " + agent.getNodesVisited());
        System.out.println("Memory Used: " + memoryUsed + " bytes");
        System.out.println("Execution Time: " + executionTimeInMilliseconds + " milliseconds");
    }
    
    // I don't know why but if this is not implemented the time is 0, so ignore for now. 
    // But in phase 3, we have to take a look to make sure that it works properly.
    private static GameState createLowBranchingState() {
        GameState state = new GameState();
        // Add minimal pieces to create few legal moves
        return state;
    }

    private static GameState createHighBranchingState() {
        GameState state = new GameState();
        // Add many pieces for a high branching factor
        return state;
    }

    private static GameState createEdgeCaseState() {
        GameState state = new GameState();
        // Configure the state to have no moves or mark it as game over
        return state;
    }
}
