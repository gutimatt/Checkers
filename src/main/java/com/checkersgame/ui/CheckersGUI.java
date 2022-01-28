package com.checkersgame.ui;

/**
 * This class runs the main method to get which ui to use.  It then runs that ui.  It also contains the code for
 * the gui interface
 *
 * @author Matthew Gutierrez
 * @version 1.0
 */

import com.checkersgame.core.Coordinate;
import com.checkersgame.core.Square;
import com.checkersgame.core.enums.Color;
import com.checkersgame.core.enums.ColumnLabel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import com.checkersgame.core.CheckersLogic;
import com.checkersgame.core.enums.Player;

/**
 * GUI class which extends Application.  GUI interface and event handlers to play checkers.
 */
public class CheckersGUI extends Application {
    protected CheckersLogic logic;
    protected Player opponent;
    private final int SQUARE_SIZE = 60;

    protected Label playerTokenLabel = new Label();
    protected Label inputTitle = new Label();
    protected VBox menuPane = new VBox();
    protected TextField input = new TextField();
    protected Button submitBtn = new Button("Submit");
    protected HBox pane = new HBox();
    protected VBox inputContainer = new VBox();
    private ComboBox playerDropdown;
    protected GridPane boardUI = new GridPane();

    protected final String instructionText = "Choose a cell position of piece to be moved and the " +
            "new position. e.g., 3a-4b";
    private SquarePane[] squarePanes = new SquarePane[64];

    /**
     * launches the gui when called
     */
    public static void run() {
        launch();
    }

    /**
     * override method of Application to start the gui
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        logic = new CheckersLogic();

        // opponent dropdown
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Computer",
                        "Player"
                );
        playerDropdown = new ComboBox(options);
        Button choiceBtn = new Button("Ok");

        choiceBtn.setOnAction(e -> {
            opponent = (playerDropdown.getValue() == "Computer") ? Player.ComputerPlayer : Player.PlayerO;
            playerDropdown.setDisable(true);
        });

        // input container
        inputContainer.setSpacing(10);
        inputContainer.getChildren().addAll(playerTokenLabel, inputTitle,
                input, submitBtn);


        // menu pane
        menuPane.setSpacing(10);
        menuPane.getChildren().addAll(new Label("Select your Opponent."),
                playerDropdown, choiceBtn, inputContainer);

        createEventHandlers();

        displayBoard();

        menuPane.setPrefWidth(500);

        // main pane
        pane.getChildren().addAll(boardUI, menuPane);
        pane.setPadding(new Insets(20));
        pane.setSpacing(20);

        Scene scene = new Scene(pane);

        stage.setTitle("Checkers Game");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * creates events handlers for the submit button and enter key press
     */
    protected void createEventHandlers() {
        // event handlers
        submitBtn.setOnAction(e -> {
            enterMove();
        });

        submitBtn.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                enterMove();
        });

        input.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                enterMove();
        });

    }

    /**
     * gets called when submitting a move to perform it on the board
     * parameters are the objects in the gui needed to get values from
     *
     */
    private void enterMove() {
        if (playerDropdown.getValue() == null) {
            opponent = Player.PlayerO;
        }

        try{
            String[] split = input.getCharacters().toString().split("-");
            if (split.length < 2) {
                throw new Exception();
            }

            Coordinate rowCord = InputHelper.convertStringToCoordinate(split[0]);
            Coordinate colCord = InputHelper.convertStringToCoordinate(split[1]);
            if (split.length > 2) {
                Coordinate lastCord = InputHelper.convertStringToCoordinate(split[2]);
                logic.handleTurn(opponent, rowCord, colCord, lastCord);
            } else {
                logic.handleTurn(opponent, rowCord, colCord);
            }
        } catch (Exception ex) {
            inputTitle.setText("Invalid input. Example move is 3C-4D");
        }

        String currentPlayerToken = logic.getTurnColor().equals(Color.DARK) ? "X" : "O";
        inputTitle.setText("Player " + currentPlayerToken + " – your turn. \n" + instructionText );

        if (logic.declaredWinner()) {
            String winner = logic.getWinner() == Player.PlayerX ? "Player X" : "Player O";
            menuPane.getChildren().add(new Label("Winner: " + winner));
            submitBtn.setDisable(true);
        }

        updateBoard();

        input.clear();
    }

    /**
     * responsible for displaying the board only on the interface
     */
    protected void displayBoard() {
        Square[][] board = logic.getBoard();

        int squareIndex = 0;

        // creates board
        for (int i = 0; i < board.length; i++) {
            boardUI.getRowConstraints().add(new RowConstraints(SQUARE_SIZE));
            boardUI.getColumnConstraints().add(new ColumnConstraints(SQUARE_SIZE));
            boardUI.add(new Label(String.valueOf(i + 1)), 0, 7 - i);
            Color currentColor = (i % 2 == 0) ? Color.LIGHT : Color.DARK;
            for (ColumnLabel col : ColumnLabel.values()) {
                SquarePane sp = new SquarePane(currentColor);
                squarePanes[squareIndex] = sp;
                squareIndex++;
                boardUI.add(sp, col.getValue() + 1, i);
                currentColor = (currentColor == Color.DARK) ? Color.LIGHT : Color.DARK;
            }
        }
        for (ColumnLabel col : ColumnLabel.values()) {
            boardUI.add(new Label(col.toString()), col.getValue() + 1, 8);
        }

        updateBoard();
    }

    /**
     * updates the pieces on the board in the squares
     */
    protected void updateBoard() {
        Square[][] b = logic.getBoard();
        int id = 0;

        for (int i = 0; i < b.length; i++) {
            for (ColumnLabel col : ColumnLabel.values()) {
                Square s = b[i][col.getValue()];
                if (s.isOccupied()) {
                    squarePanes[id].getChildren().clear();
                    squarePanes[id].getChildren().add(new Piece(s.getCurrentPiece().getColor()));
                } else {
                    squarePanes[id].getChildren().clear();
                }

                id++;
            }
        }
    }

    /**
     * draws a piece on a square
     */
    class Piece extends Circle {

        /**
         * constructor taking in the color of the circle piece to be drawn
         * @param color
         */
        public Piece(Color color) {
            setRadius(SQUARE_SIZE * .4);
            setStroke(javafx.scene.paint.Color.WHITE);
            javafx.scene.paint.Color fill = (color == Color.DARK) ?
                    javafx.scene.paint.Color.BLACK :
                    javafx.scene.paint.Color.RED;
            setFill(fill);
        }
    }

    /**
     * draws a square for the board
     */
    class SquarePane extends StackPane {

        /**
         * constructor that takes in a color for the square
         * @param color
         */
        public SquarePane(Color color) {
            setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
            String fill = (color == Color.DARK) ? "darkGrey" : "white";
            setStyle("-fx-background-color: " + fill);
        }
    }
}
