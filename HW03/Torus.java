// CS 540 HW 3
// Lec 001
// Yien Xu
// yxu322@wisc.edu

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

class State implements Comparable<State>{
	int[] board;
	State parentPt;
	final int depth;

	// This constructor should only be used as initializing the parent of 
	// the initial state.
	private State() {
		board = new int[9];
		depth = 0;
	}

	public State(int[] arr) {
		this.board = Arrays.copyOf(arr, arr.length);
		this.parentPt = new State();
		this.depth = 0;
	}

	public State(int[] board, State parentPt, int depth) {
		this.board = Arrays.copyOf(board, board.length);
		this.parentPt = parentPt;
		this.depth = depth;
	}

	@Override
	// Whoever has the smaller integer value comes first
	public int compareTo(State o) {
		return getBoard().compareTo(o.getBoard());
	}

	/**
	 * Given a parent state, this method creates a new child state whose two
	 * elements are swapped according to the two indices.
	 * @param boardState the parent state
	 * @param index1 one of the two indices to be swapped
	 * @param index2 one of the two indices to be swapped
	 * @return the child state
	 */
	private State swap(State boardState, int index1, int index2) {
		State newState = new State(boardState.board, boardState, boardState.depth + 1);

		int temp = newState.board[index1];
		newState.board[index1] = newState.board[index2];
		newState.board[index2] = temp;
		return newState;
	}

	// gets the indices of elements to be swapped vertically
	private int[] verticalWrapAround(int index) {
		// if index exceeds maxIndex, wrap around (start from 0)
		int maxIndex = board.length;
		int index1 = index - 3 < 0 ? maxIndex + index - 3 : index - 3;
		int index2 = index + 3 >= maxIndex ? index + 3 - maxIndex : index + 3;
		return new int[] {index1, index2};
	}

	// gets the indices of elements to be swapped horizontally
	private int[] horizontalWrapAround(int index) {
		// if index exceeds largest index of current row, wrap around
		int maxIndex = index + 2 - index % 3;
		// if index is less then smallest index of the current row, wrap around
		int minIndex = index - index % 3;
		int index1 = index + 1 > maxIndex ? index - 2 : index + 1;
		int index2 = index - 1 < minIndex ? index + 2 : index - 1;
		return new int[] {index1, index2};
	}

	// get all four successors and return them in sorted order
	public State[] getSuccessors() {
		State[] successors = new State[4];
		// first, get the index of the empty tile
		int emptyTileIndex = -1;
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 0) {
				emptyTileIndex = i;
				break;
			}
		}
		// then get successors by swapping the empty tile with its neighbors
		successors[0] = swap(this, emptyTileIndex, verticalWrapAround(emptyTileIndex)[0]);
		successors[1] = swap(this, emptyTileIndex, verticalWrapAround(emptyTileIndex)[1]);
		successors[2] = swap(this, emptyTileIndex, horizontalWrapAround(emptyTileIndex)[0]);
		successors[3] = swap(this, emptyTileIndex, horizontalWrapAround(emptyTileIndex)[1]);
		Arrays.sort(successors);
		return successors;
	}

	public void printState(int option) {
		switch (option) {
		case 1:
		case 2:
		case 4:
		case 5:
			System.out.println(getBoard());
			break;
		case 3:
			System.out.println(getBoard() + " parent " + parentPt.getBoard());
			break;
		default:
			break;
		}
	}

	public String getBoard() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			builder.append(this.board[i]).append(" ");
		}
		return builder.toString().trim();
	}

	public boolean isGoalState() {
		for (int i = 0; i < 9; i++) {
			if (this.board[i] != (i + 1) % 9)
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof State)) {
			return false;
		}
		for (int i = 0; i < 9; i++) {
			if (this.board[i] != ((State)o).board[i]) {
				return false;
			}
		}
		return true;
	}

}

public class Torus {

	public static void main(String args[]) {
		if (args.length < 10) {
			System.out.println("Invalid Input");
			return;
		}
		int flag = Integer.valueOf(args[0]);
		int[] board = new int[9];
		for (int i = 0; i < 9; i++) {
			board[i] = Integer.valueOf(args[i + 1]);
		}
		int option = flag / 100;
		int cutoff = flag % 100;
		
		State init = new State(board);
		if (option == 1) {
			State[] successors = init.getSuccessors();
			for (State successor : successors) {
				successor.printState(option);
			}
			return;
		}
		
		Stack<State> stack = new Stack<>();
		List<State> prefix = new ArrayList<>();
		long goalChecked = 0;
		int maxStackSize = Integer.MIN_VALUE;
		boolean done = false;
		while (!done) {
			prefix.clear();
			stack.push(init);
			// Update maxStackSize
			if (maxStackSize < stack.size()) {
				maxStackSize = stack.size();
			}
			while (!stack.isEmpty()) {
				State curr = stack.pop();
				// In option 2 and 3, print the state popped out
				if (option == 2 || option == 3) {
					curr.printState(option);
				}
				// Path-Check. Find parent node in prefix list and delete
				// everything afterwards
				for (int i = 0; i < prefix.size(); i++) {
					if (prefix.get(i).equals(curr.parentPt)) {
						while (prefix.size() > i + 1) {
							prefix.remove(prefix.size() - 1);
						}
						break;
					}
				}
				prefix.add(curr);
				// if the curr depth is about to exceed cutoff
				// break the loop and print out option 4
				if (option == 4 && curr.depth == cutoff) {
					break;
				}
				// Goal test.
				goalChecked++;
				if (curr.isGoalState()) {
					done = true;
					break;
				}
				// Get successors, and if depth is within cutoff and not in
				// prefix list, push into stack.
				if (curr.depth < cutoff) {
					State[] successors = curr.getSuccessors();
					for (State successor : successors) {
						if (!prefix.contains(successor)) {
							stack.push(successor);
						}
					}
				}
				// Update maxStackSize
				if (maxStackSize < stack.size()) {
					maxStackSize = stack.size();
				}
			}
			// Iterative deepening option: increment the cutoff and start again
			if (option == 5) {
				cutoff++;
			// else we are done (all other options)
			} else {
				done = true;
			}
		}
		if (option == 4 || option == 5) {
			// in option 4 & 5, print out the current path
			for (State state : prefix) {
				state.printState(option);
			}
			// additionally in option 5, print out goal checks and stack size
			if (option == 5) {
				System.out.println("Goal-check " + goalChecked);
				System.out.println("Max-stack-size " + maxStackSize);
			}
		}
	}
	
}
