
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * **************************************************************
 * studPlayer.java Implements MiniMax search with A-B pruning and iterative
 * deepening search (IDS). The static board evaluator (SBE) function is simple:
 * the # of stones in studPlayer's mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for
 * educational purposes provided that (1) you do not distribute or publish
 * solutions, (2) you retain the notice, and (3) you provide clear attribution
 * to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his
 * TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu). Some
 * GUI componets are from Mancala Project in Google code.
 */
//################################################################
// studPlayer class
//################################################################
public class MyPlayer extends Player {

    private Node tree;
    private int maxDepth = 0;
    private LinkedList<Node> stack;

    /*Use IDS search to find the best move. The step starts from 1 and increments by step 1.
     *Note that the search can be interrupted by time limit.
     */
    public void move(GameState state) {
        maxDepth = 0;        
        while (true) {
            ++maxDepth;
            tree = new Node(state, true, 0, new ArrayList<Node>(), 0, 0);
            IDS(tree);
            this.move = bestMoveFromValue(maxAction(tree, maxDepth), tree);
        }

    }

    private void IDS(Node node) {
        if ((node.getDepth() < maxDepth) && !(node.getGs().gameOver())) {
            for (int i = 0; i < 6; ++i) {
                if (node.getGs().stoneCount(i) > 0) {
                    GameState successor = new GameState(node.getGs());
                    boolean keepMove = successor.applyMove(i);
                    GameState successorRotated = new GameState(successor);
                    successorRotated.rotate();
                    if (!keepMove) {
                        successor = successorRotated;
                    }
                    Node successorNode = new Node(successor,
                            keepMove ? node.isPlayer() : !node.
                            isPlayer(), 0,
                            new ArrayList<Node>(),
                            node.getDepth() + 1, i);
                    node.getSuccessors().add(successorNode);
                    IDS(successorNode);
                }
            }
        }
    }

    private int bestMoveFromValue(int value, Node parent) {
        if (onlyMove(tree) < 1000) {
            return onlyMove(tree);
        }

        for (Node node : parent.getSuccessors()) {
            if (node.getValue() == value) {
                return node.getMove();
            }
        }
        return 0;
    }

    private int onlyMove(Node node) {
        int count = 0;
        int bin = 0;
        for (int i = 0; i < 6; ++i) {
            if (node.getGs().stoneCount(i) > 0) {
                ++count;
                bin = i;
            }
        }
        if (count > 1) {
            return 1000;
        } else {
            return bin;
        }
    }

    //Return best move for max player. Note that this is a wrapper function created for ease to use.
    public int maxAction(Node node, int maxDepth) {
        return maxAction(node, maxDepth, -1000, 1000);
    }

    //return best move for max player
    public int maxAction(Node node, int maxDepth, int alpha, int beta) {
        int bestValue;

        if ((node.getSuccessors().isEmpty()) || (node.getDepth() == maxDepth)) {
            bestValue = sbe(node);
            node.setValue(bestValue);
        } else {
            bestValue = alpha;
            for (Node child : node.getSuccessors()) {
                int childValue = child.isPlayer() ? maxAction(child, maxDepth,
                        alpha, bestValue) : minAction(child, maxDepth, bestValue,
                                beta);
                child.setValue(childValue);
                bestValue = Math.max(bestValue, childValue);
                if (beta <= bestValue) {
                    break;
                }
            }
        }
        return bestValue;
    }

    //return best move for min player
    public int minAction(Node node, int maxDepth, int alpha, int beta) {
        int bestValue;

        if ((node.getSuccessors().isEmpty()) || (node.getDepth() == maxDepth)) {
            bestValue = sbe(node);
            node.setValue(bestValue);
        } else {
            bestValue = beta;
            for (Node child : node.getSuccessors()) {
                int childValue = child.isPlayer() ? maxAction(child, maxDepth,
                        alpha, bestValue) : minAction(child, maxDepth, bestValue,
                                beta);
                child.setValue(childValue);
                bestValue = Math.min(bestValue, childValue);
                if (bestValue <= alpha) {
                    break;
                }
            }
        }
        return bestValue;
    }

    //the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
    private int sbe(Node node) {
        int sbe;
        if (node.getGs().gameOver()) {
            sbe = 2000;
        } else {
            sbe = node.getGs().stoneCount(6) - node.getGs().stoneCount(13);
        }

        if (node.isPlayer()) {
            return sbe;
        } else {
            return -1 * sbe;
        }
    }

}
