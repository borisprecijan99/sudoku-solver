package sudokusolver;

public class Solver {

	private State initialState;

	public Solver(State initialState) {
		this.initialState = initialState;
	}

	public State search() {
		return search(initialState);
	}

	private State search(State currentState) {
		if (currentState == null || !currentState.isSolvable()) {
			return null;
		}

		boolean done = true;
		for (String possibleValues : currentState.getPossibleValues().values()) {
			if (possibleValues.length() != 1) {
				done = false;
				break;
			}
		}

		if (done) {
			return currentState;
		}

		String squareMinPossibilities = "";
		int minPosssibleCount = 10;
		for (String square : State.getSquares()) {
			int size = currentState.getPossibleValues().get(square).length();
			if (size > 1 && size < minPosssibleCount) {
				squareMinPossibilities = square;
				minPosssibleCount = size;
			}
		}

		String possibilities = currentState.getPossibleValues().get(squareMinPossibilities);
		for (int i = 0; i < possibilities.length(); i++) {
			String value = String.valueOf(possibilities.charAt(i));
			State result = currentState.clone();
			result = search(result.assign(squareMinPossibilities, value));
			if (result != null) {
				return result;
			}
		}
		return null;
	}

}
