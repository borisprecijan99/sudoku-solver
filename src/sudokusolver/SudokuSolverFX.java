package sudokusolver;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SudokuSolverFX extends Application {

	private static final String TITLE = "Sudoku Solver";
	private static final int WIDTH = 600;
	private static final int HEIGHT = 700;

	private TextField[][] board;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(initGui());
		primaryStage.setScene(scene);
		primaryStage.setTitle(TITLE);
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private BorderPane initGui() {
		BorderPane gui = new BorderPane();
		GridPane board = new GridPane();
		this.board = new TextField[9][9];
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[0].length; j++) {
				this.board[i][j] = new TextField();
				this.board[i][j].setPrefWidth(50);
				this.board[i][j].setPrefHeight(50);
				this.board[i][j].setFont(Font.font(18));
				this.board[i][j].setAlignment(Pos.CENTER);
				this.board[i][j].setFocusTraversable(false);
				this.board[i][j].setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
				this.board[i][j].setTextFormatter(new TextFormatter<String>(change -> {
					String newText = change.getControlNewText();
					if (newText.length() == 0 || (newText.length() == 1 && (newText.charAt(0) >= '1' && newText.charAt(0) <= '9')))
						return change;
					else
						return null;
				}));
				if (j == 0 || j == 3 || j == 6) {
					if (i == 0 || i == 3 || i == 6) {
						this.board[i][j].setStyle("-fx-border-color: black; -fx-border-width: 2 0 0 2;");
					} else if (i == 8) {
						this.board[i][j].setStyle("-fx-border-color: black; -fx-border-width: 2 2 0 0;");
					} else {
						this.board[i][j].setStyle("-fx-border-color: black; -fx-border-width: 2 0 0 0;");
					}
				} else if (j == 1 || j == 2 || j == 4 || j == 5 || j == 7) {
					if (i == 0 || i == 3 || i == 6) {
						this.board[i][j].setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 2;");
					} else if (i == 8) {
						this.board[i][j].setStyle("-fx-border-color: black; -fx-border-width: 0 2 0 0;");
					}
				} else {
					if (i == 0 || i == 3 || i == 6) {
						this.board[i][j].setStyle("-fx-border-color: black; -fx-border-width: 0 0 2 2;");
					} else if (i == 8) {
						this.board[i][j].setStyle("-fx-border-color: black; -fx-border-width: 0 2 2 0;");
					} else {
						this.board[i][j].setStyle("-fx-border-color: black; -fx-border-width: 0 0 2 0;");
					}
				}
				board.add(this.board[i][j], i, j);
			}
		}
		board.setGridLinesVisible(true);
		board.setAlignment(Pos.CENTER);
		gui.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, null, null)));
		Label title = new Label(TITLE);
		title.setFont(Font.font(36));
		gui.setTop(title);
		gui.setCenter(board);
		BorderPane.setAlignment(title, Pos.CENTER);
		Button solve = new Button("SOLVE");
		solve.setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
		solve.setFont(Font.font(18));
		solve.setOnAction(e -> onSolve());
		Button reset = new Button("RESET");
		reset.setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
		reset.setFont(Font.font(18));
		reset.setOnAction(e -> onReset());
		HBox buttons = new HBox(100, solve, reset);
		buttons.setPadding(new Insets(0, 0, 50, 0));
		buttons.setAlignment(Pos.CENTER);
		gui.setBottom(buttons);
		BorderPane.setAlignment(buttons, Pos.CENTER);
		return gui;
	}

	private void onSolve() {
		int[][] board = new int[9][9];
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[0].length; j++) {
				String currentValue = this.board[i][j].getText();
				if (!currentValue.equals("")) {
					board[i][j] = Integer.parseInt(currentValue);
				}
			}
		}
		State initialState = new State(board);
		Solver solver = new Solver(initialState);
		State goalState = solver.search();
		if (goalState == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(TITLE);
			alert.setContentText("Cannot be solved!");
			alert.setHeaderText("Error");
			alert.showAndWait();
		} else {
			for (int i = 0; i < this.board.length; i++) {
				for (int j = 0; j < this.board[0].length; j++) {
					this.board[i][j].setText(String.valueOf(goalState.getBoard()[i][j]));
				}
			}
		}
	}

	private void onReset() {
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[0].length; j++) {
				this.board[i][j].setText("");
			}
		}
	}

}
