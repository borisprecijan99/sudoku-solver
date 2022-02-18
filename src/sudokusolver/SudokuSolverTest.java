package sudokusolver;

public class SudokuSolverTest {

	public static void main(String[] args) {
		int[][] board = new int[][] {
			{0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 7, 2, 0, 6, 0, 1, 0, 0},
			{0, 0, 5, 1, 0, 0, 0, 8, 2},
			{0, 8, 0, 0, 0, 1, 3, 0, 0},
			{4, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 3, 7, 0, 9, 0, 0, 1, 0},
			{0, 0, 0, 0, 2, 3, 8, 0, 0},
			{5, 0, 4, 0, 0, 9, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 7, 9, 0}
		};
		State initialState = new State(board);
		System.out.println("Initial state");
		initialState.print();
		Solver solver = new Solver(initialState);
		State goalState = solver.search();
		if (goalState != null) {
			System.out.println("Goal state");
			goalState.print();
		} else {
			System.err.println("Cannot be solved!");
		}
	}

}
