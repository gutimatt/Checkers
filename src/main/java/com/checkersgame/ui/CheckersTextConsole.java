package com.checkersgame.ui;

/**
 * This is the ui for the console for a checkers game.  Makes a new game and displays it.  Has the players take turns
 * the board then updates until there is a winner
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/

import com.checkersgame.core.*;
import com.checkersgame.core.enums.*;

import java.util.Scanner;

public class CheckersTextConsole {
    private CheckersLogic logic;
    private Player opponent;

    private final String x_TOKEN = "X";
    private final String o_TOKEN = "O";
    private final String WALL = " | ";
    private final String SPACE = "_";

    /**
     * constructor creates a new checker game and runs it
     */
    public CheckersTextConsole() {
        logic = new CheckersLogic();
        runGame();
    }

    /**
     * responsible for getting the moves of the game and displaying the board to keep playing.
     */
    private void runGame() {

        displayBoard();

        System.out.println("Begin Game. Enter ‘P’ if you want to play against another player; enter ‘C’ to play against computer.");

        Scanner scan = new Scanner(System.in);
        String s ="";

        s = scan.nextLine();
        System.out.println(s);
        Boolean validOpponent = false;

        while (!validOpponent){
            if (s.toUpperCase().charAt(0) == 'C') {
                opponent = Player.ComputerPlayer;
                System.out.println("Start Game against Computer");
                validOpponent = true;
            } else if (s.toUpperCase().charAt(0) == 'P') {
                opponent = Player.PlayerO;
                System.out.println("Start Game against Player");
                validOpponent = true;
            } else {
                System.out.println("Invalid Opponent. Enter ‘P’ if you want to play against another player; enter ‘C’ to play against computer.");
                s = scan.nextLine();
            }
        }

        System.out.println("You are Player X – your turn. " +
                "\nChoose a cell position of piece to be moved and the new position. e.g., 3a-4b");

        while (!logic.declaredWinner()) {
            s = scan.nextLine();
            System.out.println(s);
            String[] split = s.split("-");

            try{
                if (split.length < 2) {
                    throw new IllegalArgumentException();
                }

                Coordinate rowCord = InputHelper.convertStringToCoordinate(split[0]);
                Coordinate colCord = InputHelper.convertStringToCoordinate(split[1]);
                if (split.length > 2) {
                    Coordinate lastCord = InputHelper.convertStringToCoordinate(split[2]);
                    logic.handleTurn(opponent, rowCord, colCord, lastCord);
                } else {
                    logic.handleTurn(opponent, rowCord, colCord);
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Example move is 3C-4D");
                continue;
            }

            displayBoard();
            String currentPlayerToken = logic.getTurnColor() == Color.DARK ? x_TOKEN : o_TOKEN;
            System.out.println("Player" + currentPlayerToken + " – your turn. \n" +
                    "Choose a cell position of piece to be moved and the new position. e.g., 3a-4b");
        }
        scan.close();

        String winner = logic.getWinner() == Player.PlayerX ? "Player X" : "Player O";
        System.out.println("Winner: " + winner);
    }

    /**
     * displays the baard in the console, but gets the matrix from the core package
     */
    private void displayBoard() {
        Square[][] board = logic.getBoard();
        for (int i = 0; i < board.length; i++) {
            System.out.print(8 - i + WALL);
            for (ColumnLabel col : ColumnLabel.values()) {
                if (!board[i][col.getValue()].isOccupied())
                    System.out.print(SPACE);
                else if (board[i][col.getValue()].getCurrentPiece().getColor() == Color.DARK)
                    System.out.print(x_TOKEN);
                else
                    System.out.print(o_TOKEN);
                System.out.print(WALL);
            }
            System.out.println();
        }
        System.out.print("    ");
        for (ColumnLabel col : ColumnLabel.values())
            System.out.print(col + "   ");
        System.out.println();
    }
}
