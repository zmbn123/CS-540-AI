import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public DepthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main depth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		LinkedList<State> stack = new LinkedList<State>();

		stack.push(new State(maze.getPlayerSquare(), null, 0, 0));

		while (!stack.isEmpty()) {
			ArrayList<State> Successors;
			State stateExplored = stack.pop();
			explored[stateExplored.getX()][stateExplored.getY()] = true;
			this.noOfNodesExpanded++;
			if (this.maxDepthSearched < stateExplored.getDepth()) {
				maxDepthSearched = stateExplored.getDepth();
			}
			cost = stateExplored.getGValue();
            Successors = stateExplored.getSuccessors(explored, maze);
			if (stateExplored.isGoal(maze)) {
				State parent = stateExplored.getParent();
				while (parent.getGValue() != 0) {
					maze.setOneSquare(parent.getSquare(), '.');
					parent = parent.getParent();
				}
				return true;
			}
		
			for(State child : Successors){
				if(checkList(child, stack) == true){
					stack.push(child);
				}
				
			}
			this.maxSizeOfFrontier = Math.max(stack.size(), this.maxSizeOfFrontier);
		}
		return false;
	}

	private boolean checkList(State child, LinkedList<State> stack) {
		for (State check : stack) {
			if (child.getX() == check.getX() && child.getY() == check.getY()) {
				return false;
			}
		}
		return true;
	}
}