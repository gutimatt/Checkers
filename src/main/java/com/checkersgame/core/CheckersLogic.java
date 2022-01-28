package com.checkersgame.core;

/** This represents a new checkers game between two players
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/

import com.checkersgame.core.enums.Color;
import com.checkersgame.core.enums.Player;

public class CheckersLogic {

    private Board board;
    private Boolean active = true;
    private Player winner;
    private Player turn;
    private CheckersComputerPlayer computerOpponent = null;

    /**
     * constructor to create new board and the logic for the game
     */
    public CheckersLogic() {
        board = new Board();
        turn = Player.PlayerX;
    }

    /**
     * returns a board matrix for the game
     * @return Square[][]
     */
    public Square[][] getBoard() {
        return board.getBoard();
    }

    /**
     * gets the color for the player whose turn it is
     * @return Color
     */
    public Color getTurnColor() {
        return turn == Player.PlayerX ? Color.DARK : Color.LIGHT;
    }

    /**
     * gets called from ui with coordinates and who the player is playing against.  If playing againest a computer then
     * calls the computer to take a turn
     *
     * @param opponent
     * @param oldCord
     * @param finalCord
     */
    public void handleTurn(Player opponent, Coordinate oldCord, Coordinate finalCord) throws Exception{
        takeTurn(oldCord, finalCord);

        if (opponent == Player.ComputerPlayer && isActive(getTurnColor())) {
            if (computerOpponent == null)
                computerOpponent = new CheckersComputerPlayer(Color.LIGHT, board, this);
            computerOpponent.takeTurn();
        }
    }

    /**
     * This is called from the ui and does the same thing as handleturn however for a double jump
     *
     * @param opponent
     * @param oldCord
     * @param middleCord
     * @param lastCord
     */
    public void handleTurn(Player opponent, Coordinate oldCord, Coordinate middleCord, Coordinate lastCord) throws Exception{
        takeTurn(oldCord, middleCord, lastCord);

        if (opponent == Player.ComputerPlayer && isActive(getTurnColor())) {
            if (computerOpponent == null)
                computerOpponent = new CheckersComputerPlayer(Color.LIGHT, board, this);
            computerOpponent.takeTurn();
        }
    }

    /**
     * takes a fromCoordinate and a toCoordinate to move a piece where told if valid
     * @param oldCord
     * @param newCord
     */
    public void takeTurn(Coordinate oldCord, Coordinate newCord) throws Exception{
        if (board.getPieceAt(oldCord).getColor() != getTurnColor())
            throw new Exception();

        if (Math.abs(oldCord.getRow() - newCord.getRow()) > 1)
            board.getPieceAt(oldCord).jump(newCord);
        else {
            board.getPieceAt(oldCord).move(newCord);
        }
        switchTurn();
    }

    /**
     * this is for a double jump when a person tries to make one
     * @param oldCord
     * @param middleCord
     * @param lastCord
     */
    public void takeTurn(Coordinate oldCord, Coordinate middleCord, Coordinate lastCord) throws Exception{
        if (board.getPieceAt(oldCord).getColor() != getTurnColor())
            throw new Exception();

        if (Math.abs(oldCord.getRow() - middleCord.getRow()) > 1 &&
                Math.abs(middleCord.getRow() - lastCord.getRow()) > 1 &&
                board.getPieceAt(oldCord).isValidJumpMove(middleCord))
        {
            Piece jumpedPiece = board.getPieceAt(oldCord).getJumpedPiece(middleCord);
            board.getPieceAt(oldCord).jump(middleCord);

            if (board.getPieceAt(middleCord).isValidJumpMove(lastCord)) {
                board.getPieceAt(middleCord).jump(lastCord);
                switchTurn();
            }
            else {
                board.getSquare(oldCord).setPiece(board.getPieceAt(middleCord));
                board.getSquare(jumpedPiece.getCurrentSquare().getCoordinate()).setPiece(jumpedPiece);
                board.getSquare(middleCord).removePiece();
                throw new Exception();
            }
        } else {
            throw new Exception();
        }
    }

    /**
     * returns if the game is currently active
     * @return Boolean
     */
    public Boolean liveGame() {
        return active;
    }

    /**
     * checks the conditions for an ending game
     * @param color
     * @return Boolean
     */
    public Boolean isActive(Color color) {
        if (board.getPieces(color).size() < 1 ||
                board.getColorPiecesMove(color).size() < 1) {
            active = false;
            winner = (color == Color.DARK) ? Player.PlayerO : Player.PlayerX;
        }
        return liveGame();
    }

    /**
     * checks if the game is still valid to play and returns if there has been a winner
     * @return Boolean
     */
    public Boolean declaredWinner() {
        return !isActive(getTurnColor());
    }

    /**
     * gets the winner of the game
     * @return Player
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * switches the turn of the player
     */
    public void switchTurn() {
        this.turn = this.turn == Player.PlayerX ? Player.PlayerO : Player.PlayerX;
    }
}
