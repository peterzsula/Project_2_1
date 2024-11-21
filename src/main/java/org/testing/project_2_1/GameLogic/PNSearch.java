package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.List;

public class PNSearch { // Regular Proof-Number Search (see "Winands and van den Herik, 2008")
    private boolean isWhite;
    public class Node {
        public static final int INFINITY = Integer.MAX_VALUE;
        public static final int AND_NODE = 1;
        public static final int OR_NODE = 2;
        public static final int TRUE = 1;
        public static final int FALSE = 2;
        public static final int UNKNOWN = 3;

        public int type;
        public int value;
        public int proof;
        public int disproof;
        public boolean expanded;
        public List<Node> children;
        public Node parent;
        public Move moveFromParent;
        public GameState state;

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
    }

    public PNSearch(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void PN(Node root, int maxNodes) {
        evaluate(root);
        setProofAndDisproofNumbers(root);
        int nodeCount = 1;
        Node currentNode = root;

        while (root.proof != 0 && root.disproof != 0 && nodeCount <= maxNodes) {
            Node mostProvingNode = selectMostProvingNode(currentNode);
            expandNode(mostProvingNode);
            nodeCount += mostProvingNode.children.size();
            currentNode = updateAncestors(mostProvingNode, root);
        }
    }

    public void evaluate(Node node) {
        int result = node.state.evaluate();

        if (result == 1) {
            node.value = Node.TRUE;
        } else if (result == -1) {
            node.value = Node.FALSE;
        } else {
            node.value = Node.UNKNOWN;
        }
    }

    public void setProofAndDisproofNumbers(Node node) {
        if (node.expanded) { // AND NODE
            if (node.type == Node.AND_NODE) {
                node.proof = 0;
                node.disproof = Node.INFINITY;
                for (Node n : node.children) {
                    node.proof += n.proof;
                    if (n.disproof < node.disproof) {
                        node.disproof = n.disproof;
                    }
                }
            } else { // OR_NODE
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

    public Node selectMostProvingNode(Node node) {
        while (node.expanded) {
            Node nextNode = null;
            if (node.type == Node.OR_NODE) {
                for (Node child : node.children) {
                    if (child.proof == node.proof) {
                        nextNode = child;
                        break;
                    }
                }
            } else { // AND_NODE
                for (Node child : node.children) {
                    if (child.disproof == node.disproof) {
                        nextNode = child;
                        break;
                    }
                }
            }
            if (nextNode == null) {
                break;
            }
            node = nextNode;
        }
        return node;
    }

    public void expandNode(Node node) {
        generateAllChildren(node);
        for (Node n : node.children) {
            evaluate(n);
            setProofAndDisproofNumbers(n);
            if ((node.type == Node.OR_NODE && n.proof == 0) ||
                    (node.type == Node.AND_NODE && n.disproof == 0)) {
                break;
            }
        }
        node.expanded = true;
    }

    public void generateAllChildren(Node node) {
        List<Turn> possibleTurns = GameLogic.getLegalTurns(node.state);
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

    public Node updateAncestors(Node node, Node root) {
        do {
            int oldProof = node.proof;
            int oldDisproof = node.disproof;
            setProofAndDisproofNumbers(node);
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

    public void deleteSubtree(Node node) {
        node.children.clear();
        node.expanded = false;
    }
}

