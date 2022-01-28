package com.checkersgame.core;

/** A Square on the board that has a color and possibly a piece
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/

import com.checkersgame.core.enums.Color;

public class Square {

    private final Coordinate coordinate;
    private final Color color;
    private final Board board;

    private Piece currentPiece;
    private Boolean occupied = false;

    public Square(Coordinate coordinate, Color color, Board board) {
        this.coordinate = coordinate;
        this.color = color;
        this.board = board;
    }

    /**
     * puts a piece on the square
     * @param piece
     */
    public void setPiece(Piece piece) {
        board.setPieceOnBoard(piece);
        currentPiece = piece;
        occupied = true;
    }

    /**
     * removes a piece on the square/board
     */
    public void removePiece() {
        board.removePieceOnBoard(currentPiece);
        currentPiece = null;
        occupied = false;
    }

    /**
     * gets the piece on teh square
     * @return
     */
    public Piece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * gets if a square has a piece on it
     * @return Boolean
     */
    public Boolean isOccupied() {
        return occupied;
    }

    /**
     * Coordinate of the square on the board
     * @return Coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Color of the square
     * @return
     */
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Square{" +
                "coordinate=" + coordinate +
                ", color=" + color +
                ", currentPiece=" + currentPiece +
                ", occupied=" + occupied +
                '}';
    }
}
