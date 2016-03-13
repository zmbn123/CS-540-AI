
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jmishra
 */
public class Node {
    
    private GameState gs;
    private boolean player;
    private int value;
    private List<Node> successors;
    private int depth;
    private int move;

    public Node(GameState gs, boolean player, int value, List<Node> successors, int depth, int move) {
        this.gs = gs;
        this.player = player;
        this.value = value;
        this.successors = successors;
        this.depth = depth;
        this.move = move;
    }        

    public GameState getGs() {
        return gs;
    }

    public void setGs(GameState gs) {
        this.gs = gs;
    }

    public boolean isPlayer() {
        return player;
    }

    public void setPlayer(boolean player) {
        this.player = player;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<Node> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<Node> successors) {
        this.successors = successors;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getMove()
    {
        return move;
    }

    public void setMove(int move)
    {
        this.move = move;
    }       
    
}
