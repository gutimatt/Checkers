package com.checkersgame.core;

import com.checkersgame.core.enums.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/
public class CheckersComputerPlayer {
    private CheckersLogic logic;
    private Color color;
    private Board board;

    public CheckersComputerPlayer(Color color, Board board, CheckersLogic logic) {
        this.color = color;
        this.board = board;
        this.logic = logic;
    }

    /**
     * gets a random piece from the computers pieces and make a random move on that piece if available.
     *
     * @throws Exception
     */
    public void takeTurn() throws Exception {
        Coordinate oldCord;
        List<Piece> pieces = new LinkedList<>(board.getPieces(color));

        while (true) {
            Random randomNumber = new Random();
            int randomInt = randomNumber.nextInt(pieces.size());

            Piece randomPiece = pieces.get(randomInt);
            if (randomPiece.getValidMoves().size() > 0) {
                oldCord = randomPiece.getCurrentSquare().getCoordinate();
                break;
            }
        }

        Random randomNumber = new Random();
        int randomInt = randomNumber.nextInt(board.getPieceAt(oldCord).getValidMoves().size());

        Coordinate newCord = board.getPieceAt(oldCord).getValidMoves().get(randomInt);
        System.out.println("Computer move: " + oldCord.getRow() + oldCord.getCol() + "-" + newCord.getRow() + newCord.getCol());
        logic.takeTurn(oldCord, newCord);
    }
}
