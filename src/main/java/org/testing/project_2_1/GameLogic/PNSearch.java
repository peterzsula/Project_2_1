package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements Regular Proof-Number Search (PNS) for game tree evaluation.
 * Based on the work "Winands and van den Herik, 2008".
 * PNS determines the proof or disproof of a node in a game tree
 * by iteratively expanding nodes and updating proof/disproof numbers.
 */
public class PNSearch {

    private final Map<GameState, Integer> evaluationCache = new HashMap<>();


    /**
     * Inner class representing a single node in the PNS game tree.
     */
    public class Node {
        // Constants for node types, values, and infinity representation.
        public static final int INFINITY = Integer.MAX_VALUE;
        public static final int AND_NODE = 1;
        public static final int OR_NODE = 2;
        public static final int TRUE = 1;
        public static final int FALSE = 2;
        public static final int UNKNOWN = 3;

        // Node properties
        public int type; // Node type: AND or OR
        public int value; // Evaluation result: TRUE, FALSE, or UNKNOWN
        public int proof; // Proof number for this node
        public int disproof; // Disproof number for this node
        public boolean expanded; // Indicates if this node has been expanded
        public List<Node> children; // List of child nodes
        public Node parent; // Parent node
        public Move moveFromParent; // Move leading to this node
        public GameState state; // Game state represented by this node
        private Node mostProvingChild; // Pointer to the most-proving child


        /**
         * Constructs a new Node with the specified properties.
         * @param parent The parent node of this node.
         * @param state The game state associated with this node.
         * @param type The type of this node (AND or OR).
         */
        public Node(Node parent, GameState state, int type) {
            this.parent = parent;
            this.state = state;
            this.type = type;
            this.value = UNKNOWN;
            this.proof = 1;
            this.disproof = 1;
            this.expanded = false;
            this.children = new java.util.ArrayList<>();
        }

        public Node getMostProvingChild() {
            return this.mostProvingChild;
        }

        public void setMostProvingChild(Node child) {
            this.mostProvingChild = child;
        }
    }


    /**
     * Constructor for PNSearch.
     * @param isWhite Indicates whether the search is for the white player.
     */
    public PNSearch() {
    }

    /**
     * Performs Proof-Number Search starting from the root node.
     * @param root The root node of the search tree.
     * @param maxNodes The maximum number of nodes to process.
     */
    public void PN(Node root, int maxNodes) {
        evaluate(root); // Initial evaluation of the root node
        setProofAndDisproofNumbers(root); // Set initial proof/disproof numbers
        int nodeCount = 1; // Track the number of processed nodes
        Node currentNode = root;

        while (root.proof != 0 && root.disproof != 0 && nodeCount <= maxNodes) {
            Node mostProvingNode = selectMostProvingNode(currentNode);
            expandNode(mostProvingNode);
            nodeCount += mostProvingNode.children.size();
            currentNode = updateAncestors(mostProvingNode, root);
        }
    }

    /**
     * Evaluates a node's game state and assigns its value.
     * @param node The node to evaluate.
     */
    public void evaluate(Node node) {
        Integer cachedResult = evaluationCache.get(node.state);
        if (cachedResult != null) {
            node.value = cachedResult;
        } else {
            int result = node.state.evaluate();
            evaluationCache.put(node.state, result);
            node.value = result;
        }
    }

    /**
     * Sets the proof and disproof numbers for a given node.
     * @param node The node for which to calculate proof/disproof numbers.
     */
    public void setProofAndDisproofNumbers(Node node) {
        if (node.expanded) { // AND or OR Node
            if (node.type == Node.AND_NODE) {
                node.proof = 0;
                node.disproof = Node.INFINITY;
                for (Node n : node.children) {
                    node.proof += n.proof;
                    if (n.disproof < node.disproof) {
                        node.disproof = n.disproof;
                    }
                }
            } else { // OR Node
                node.proof = Node.INFINITY;
                node.disproof = 0;
                for (Node n : node.children) {
                    node.disproof += n.disproof;
                    if (n.proof < node.proof) {
                        node.proof = n.proof;
                    }
                }
            }
        } else { // Leaf node
            switch (node.value) {
                case Node.FALSE:
                    node.proof = Node.INFINITY;
                    node.disproof = 0;
                    break;
                case Node.TRUE:
                    node.proof = 0;
                    node.disproof = Node.INFINITY;
                    break;
                case Node.UNKNOWN:
                    node.proof = 1;
                    node.disproof = 1;
                    break;
            }
        }
    }

    /**
     * Selects the most proving node (a leaf node) by traversing the tree.
     * Optimized to use stored pointers to the most-proving child node.
     * @param node The starting node.
     * @return The most proving node.
     */
    public Node selectMostProvingNode(Node node) {
        while (node.expanded) {
            // Use the stored most-proving child for traversal
            Node nextNode = node.getMostProvingChild();
            if (nextNode == null) {
                break; // Safety check: exit if no valid most-proving child
            }
            node = nextNode;
        }
        return node;
    }


    /**
     * Expands a given node by generating all its children.
     * @param node The node to expand.
     */
    public void expandNode(Node node) {
        generateAllChildren(node);

        Node mostProvingChild = null;
        for (Node child : node.children) {
            evaluate(child);
            setProofAndDisproofNumbers(child);

            // Update the most-proving child based on the node type
            if (node.type == Node.OR_NODE) {
                if (mostProvingChild == null || child.proof < mostProvingChild.proof) {
                    mostProvingChild = child;
                }
            } else if (node.type == Node.AND_NODE) {
                if (mostProvingChild == null || child.disproof < mostProvingChild.disproof) {
                    mostProvingChild = child;
                }
            }

            // Stop early if terminal condition is met
            if ((node.type == Node.OR_NODE && child.proof == 0) ||
                    (node.type == Node.AND_NODE && child.disproof == 0)) {
                break;
            }
        }

        node.setMostProvingChild(mostProvingChild);
        node.expanded = true;
    }



    /**
     * Generates all child nodes for a given node based on possible turns.
     * @param node The node for which to generate children.
     */
    public void generateAllChildren(Node node) {
        List<Turn> possibleTurns = node.state.getLegalTurns();
        int childType = (node.type == Node.AND_NODE) ? Node.OR_NODE : Node.AND_NODE;
        for (Turn turn : possibleTurns) {
            GameState newState = new GameState(node.state);
            Move move = turn.getMoves().get(0); // Assuming single move for simplicity
            newState.move(move);
            Node childNode = new Node(node, newState, childType);
            childNode.moveFromParent = move;
            node.children.add(childNode);
        }
    }

    /**
     * Updates the proof and disproof numbers of a node's ancestors.
     * @param node The node whose ancestors to update.
     * @param root The root of the tree.
     * @return The updated ancestor node.
     */
    public Node updateAncestors(Node node, Node root) {
        do {
            int oldProof = node.proof;
            int oldDisproof = node.disproof;
            setProofAndDisproofNumbers(node);

            // Recalculate the most-proving child if proof-disproof numbers change
            Node mostProvingChild = null;
            for (Node child : node.children) {
                if (node.type == Node.OR_NODE) {
                    if (mostProvingChild == null || child.proof < mostProvingChild.proof) {
                        mostProvingChild = child;
                    }
                } else if (node.type == Node.AND_NODE) {
                    if (mostProvingChild == null || child.disproof < mostProvingChild.disproof) {
                        mostProvingChild = child;
                    }
                }
            }
            node.setMostProvingChild(mostProvingChild);

            if (node.proof == oldProof && node.disproof == oldDisproof) {
                return node;
            }
            if (node.proof == 0 || node.disproof == 0) {
                deleteSubtree(node);
            }
            if (node == root) {
                return node;
            }
            node = node.parent;
        } while (true);
    }


    /**
     * Deletes the subtree rooted at the given node.
     * @param node The root of the subtree to delete.
     */
    public void deleteSubtree(Node node) {
        node.children.clear();
        node.expanded = false;
    }
}
