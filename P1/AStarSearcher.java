import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
		
		State startState = new State(maze.getPlayerSquare(), null, 0, 0);
		
		frontier.add(new StateFValuePair(startState,Math.abs(maze.getPlayerSquare().X-maze.getGoalSquare().X)
				+Math.abs(maze.getPlayerSquare().Y-maze.getGoalSquare().Y)));
	
		while (!frontier.isEmpty()) {
			ArrayList <State> Successors;
			StateFValuePair stateExplored = frontier.poll();
			explored[stateExplored.getState().getX()][stateExplored.getState().getY()] = true;
			this.noOfNodesExpanded ++;
			cost = stateExplored.getState().getGValue();
			this.maxDepthSearched = Math.max(stateExplored.getState().getDepth(), this.maxDepthSearched);
			if (stateExplored.getState().isGoal(maze)) {
				State parent = stateExplored.getState().getParent();
				while(parent.getGValue() != 0) {
					maze.setOneSquare(parent.getSquare(), '.');
					parent = parent.getParent();
				}
				return true;
			}
			Successors = stateExplored.getState().getSuccessors(explored, maze);
			for (State neighbours : Successors) {
				if (checkList(neighbours, frontier) == 1) {
					StateFValuePair newNode = new StateFValuePair(neighbours, neighbours.getGValue()+ 
							Math.abs(neighbours.getSquare().X-maze.getGoalSquare().X + 
									Math.abs(neighbours.getSquare().Y- maze.getGoalSquare().Y)));
					frontier.add(newNode);
			}
				else if (checkList(neighbours, frontier) == -1){
						StateFValuePair newNode = new StateFValuePair(neighbours, neighbours.getGValue()+ 
								Math.abs(neighbours.getSquare().X-maze.getGoalSquare().X + 
										Math.abs(neighbours.getSquare().Y- maze.getGoalSquare().Y)));
						frontier.add(newNode);
					}
				else {
					continue;
				}
				this.maxSizeOfFrontier = Math.max(frontier.size(), this.maxSizeOfFrontier);
				}
			}
			return false;	
		}

	
	private int checkList(State neighbours,PriorityQueue<StateFValuePair> frontier) {
		for (StateFValuePair check :frontier) {
			if (neighbours.getGValue()< check.getState().getGValue()&& 
		neighbours.getX() == check.getState().getX()&& 
		neighbours.getY() == check.getState().getY()) {
				frontier.remove(check);
				return 1;
			}
			else if (neighbours.getGValue() > check.getState().getGValue()&& 
					neighbours.getX() == check.getState().getX()&& 
					neighbours.getY() == check.getState().getY()) {
				return 0;
			}
	}
		return -1;
	}
}

