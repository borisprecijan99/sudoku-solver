package sudokusolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class State {

	// redovi na tabli
	private static final String ROWS = "ABCDEFGHI";
	// kolone na tabli
	private static final String COLS = "123456789";
	// oznake polja (A1, A2, ... , I8, I9)
	private static String[] squares;

	// mapa (A1, A2, ...) -> ((red), (kolona), (blok))
	private Map<String, List<Set<String>>> units;
	// mapa (A1, A2, ...) -> (red, kolona, blok bez duplikata)
	private Map<String, Set<String>> peers;
	// tabla
	private int[][] board;
	// da li je moguce resiti
	private boolean solvable;
	// mapa (A1, A2, ...) -> (1, 2, ..., 9)
	private Map<String, String> possibleValues;

	static {
		squares = new String[ROWS.length() * COLS.length()];
		for (int i = 0; i < ROWS.length(); i++) {
			for (int j = 0; j < COLS.length(); j++) {
				squares[i * 9 + j] = String.valueOf(ROWS.charAt(i)) + String.valueOf(COLS.charAt(j));
			}
		}
	}

	public State(int[][] board) {
		this.board = board;
		this.solvable = true;
		this.peers = new TreeMap<>();
		this.units = new TreeMap<>();
		this.possibleValues = new TreeMap<>();

		for (int i = 0; i < ROWS.length(); i++) {
			for (int j = 0; j < COLS.length(); j++) {
				String square = squares[i * 9 + j];

				Set<String> row = new TreeSet<>();
				Set<String> column = new TreeSet<>();
				Set<String> block = new TreeSet<>();

				for (int k = 0; k < 9; k++) {
					row.add(squares[i * 9 + k]);
					column.add(squares[j + k * 9]);
				}

				int top = i / 3;
				int left = j / 3;
				for (int a = 0; a < 3; a++) {
					int index1 = top * 3 + a;
					for (int b = 0; b < 3; b++) {
						int index2 = left * 3 + b;
						block.add(squares[index1 * 9 + index2]);
					}
				}

				List<Set<String>> units = new ArrayList<>();
				units.add(row);
				units.add(column);
				units.add(block);
				this.units.put(square, units);

				Set<String> peers = new TreeSet<>();
				peers.addAll(row);
				peers.addAll(column);
				peers.addAll(block);
				peers.remove(square);
				this.peers.put(square, peers);
			}
		}

		for (int i = 0; i < squares.length; i++) {
			String square = squares[i];
			possibleValues.put(square, COLS);
		}

		for (int i = 0; i < ROWS.length() && solvable; i++) {
			for (int j = 0; j < COLS.length(); j++) {
				if (board[i][j] != 0) {
					if (assign(squares[i * 9 + j], String.valueOf(board[i][j])) == null) {
						this.solvable = false;
						break;
					}
				}
			}
		}
	}

	public boolean isSolvable() {
		return solvable;
	}

	public int[][] getBoard() {
		return board;
	}

	public Map<String, String> getPossibleValues() {
		return possibleValues;
	}

	private int getRow(String square) {
		int row = 0;
		for (int i = 0; i < ROWS.length(); i++) {
			if (square.charAt(0) == ROWS.charAt(i)) {
				break;
			} else {
				row++;
			}
		}
		return row;
	}

	private int getColumn(String square) {
		int column = 0;
		for (int i = 0; i < COLS.length(); i++) {
			if (square.charAt(1) == COLS.charAt(i)) {
				break;
			} else {
				column++;
			}
		}
		return column;
	}

	private State eliminate(String square, String value) {
		if (!possibleValues.get(square).contains(value)) {
			return this;
		}

		String otherPossibleValues = possibleValues.get(square).replace(value, "");
		possibleValues.put(square, otherPossibleValues);

		if (possibleValues.get(square).length() == 0) {
			return null;
		} else if (possibleValues.get(square).length() == 1) {
			String valueTmp = possibleValues.get(square);
			for (String peer : peers.get(square)) {
				if (eliminate(peer, valueTmp) == null) {
					return null;
				}
			}
		}

		for (Set<String> unit : units.get(square)) {
			Set<String> placesForValue = new TreeSet<>();
			for (String element : unit) {
				if (possibleValues.get(element).contains(value)) {
					placesForValue.add(element);
				}
			}
			if (placesForValue.size() == 0) {
				return null;
			} else if (placesForValue.size() == 1) {
				if (assign(((TreeSet<String>) placesForValue).first(), value) == null) {
					return null;
				}
			}
		}
		return this;
	}

	public State assign(String square, String value) {
		String otherPossibleValues = possibleValues.get(square).replace(value, "");
		for (int i = 0; i < otherPossibleValues.length(); i++) {
			String currentValue = String.valueOf(otherPossibleValues.charAt(i));
			if (eliminate(square, currentValue) == null) {
				return null;
			}
		}
		int row = getRow(square);
		int column = getColumn(square);
		board[row][column] = Integer.parseInt(value);
		return this;
	}

	@Override
	public State clone() {
		int[][] board = new int[9][9];
		for (int i = 0; i < 9; i++) {
			board[i] = Arrays.copyOf(this.board[i], 9);
		}
		State clone = new State(board);
		clone.possibleValues = new TreeMap<>(this.possibleValues);
		clone.peers = new TreeMap<>(this.peers);
		clone.units = new TreeMap<>(this.units);
		clone.solvable = this.solvable;
		return clone;
	}

	public void print() {
		for (int i = 0; i < board.length; i++) {
			if (i % 3 == 0 && i != 0) {
				System.out.println("------+-------+------");
			}
			for (int j = 0; j < board[0].length; j++) {
				if (j % 3 == 0 && j != 0) {
					System.out.print("| ");
				}
				if (board[i][j] == 0) {
					System.out.print(". ");
				} else {
					System.out.print(board[i][j] + " ");
				}
			}
			System.out.println();
		}
	}

	public static String[] getSquares() {
		return squares;
	}

}
