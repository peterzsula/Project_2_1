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


public class AgentMCTS implements Agent {
    private GameLogic gameLogic;
    private boolean isWhite;
    private static final int SIMULATIONS = 20; // Number of simulations per move
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2); // UCB1 constant
    private Turn currentTurn;

    // Node class for MCTS
    private static class Node {
        GameState state;
        Turn turn; // The move leading to this state
        Node parent;
        ArrayList<Node> children;
        int visits;
        double wins;

        Node(GameState state, Turn move, Node parent) {
            this.state = state;
            this.turn = move;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.visits = 0;
            this.wins = 0.0;
        }
    }

    public AgentMCTS(boolean isWhite) {
        this.isWhite = isWhite;
        currentTurn = new Turn();
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public void makeMove() {
        System.out.println("Monte Carlo Tree Search agent making move");

        // Create the root node
        Node root = new Node(new GameState(gameLogic.g), null, null);

        // Perform MCTS simulations
        for (int i = 0; i < SIMULATIONS; i++) {
            Node selectedNode = selection(root);
            if (!gameLogic.isGameOver(selectedNode.state)) {
                expansion(selectedNode);
                Node nodeToSimulate = selectedNode.children.isEmpty() ? selectedNode : getRandomChild(selectedNode);
                double result = simulation(nodeToSimulate.state);
                backpropagation(nodeToSimulate, result);
            }
        }

        // Choose the best move
        Node bestChild = selectBestChild(root);
        if (bestChild != null && bestChild.turn != null) {
            if (currentTurn.isEmpty()) {
                currentTurn = bestChild.turn;
            }
            Move move = currentTurn.getMoves().removeFirst();
            PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
            pause.setOnFinished(event -> {
            if (gameLogic.g.getIsWhiteTurn() == isWhite && !gameLogic.isGameOver(gameLogic.g)) {
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

        for (Node child : node.children) {
            double ucb1Value = child.wins / (child.visits + 1e-6) +
                    EXPLORATION_CONSTANT * Math.sqrt(Math.log(node.visits + 1) / (child.visits + 1e-6));
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
        GameState simState = new GameState(state); // Deep copy for simulation
        Random random = new Random();

        while (!gameLogic.isGameOver(simState)) {
            List<Turn> legalTurns = simState.getLegalTurns();

            if (legalTurns.isEmpty()) {
                break; // Exit simulation if no legal turns are available
            }

            Turn randomTurn = legalTurns.get(random.nextInt(legalTurns.size())); // Randomly pick a legal move
            for (Move move : randomTurn.getMoves()) {
                simState.move(move);
            }
        }

        // Return a simulated result
        return GameLogic.evaluateBoard(simState) * (isWhite ? 1 : -1);
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
    public Agent resetSimulation() {
        return new AgentMCTS(isWhite, gameState);
    }

    @Override
    public void simulate() {
        throw new UnsupportedOperationException("Unimplemented method 'simulate'");
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    
}


