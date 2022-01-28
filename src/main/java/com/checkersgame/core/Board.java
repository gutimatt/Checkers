package com.checkersgame.core;

/** This respresents a board within a game of checker composed of light and dark pieces.
 * It allows you to get a square location using a coordinate object and get pieces within the square.
 * When iniated, sets pieces to default positions for checkers.
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/
import com.checkersgame.core.enums.Color;
import com.checkersgame.core.enums.ColumnLabel;

import java.util.LinkedList;
import java.util.List;

public class Board {

    private final int BOARD_SIZE = 8;
    private List<Piece> darkPieces;
    private List<Piece> lightPieces;
    private Square[][] board;

    /**
     * Constructor that creates new board with pieces in position
     */
    public Board() {
        darkPieces = new LinkedList<>();
        lightPieces = new LinkedList<>();
        board = new Square[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < board.length; i++) {
            int column = 0;
            Color currentColor = (i % 2 == 0) ? Color.LIGHT : Color.DARK;
            for (ColumnLabel col : ColumnLabel.values()) {
                Square square = new Square(new Coordinate(col, BOARD_SIZE - i), currentColor, this);
                if (currentColor == Color.DARK) {
                    if (i < 3) {
                        Piece lp = new Piece(Color.LIGHT, square, this);
                        square.setPiece(lp);
                    }
                    if (i > 4) {
                        Piece dp = new Piece(Color.DARK, square, this);
                        square.setPiece(dp);
                    }
                }
                board[i][column] = square;
                currentColor = (currentColor == Color.DARK) ? Color.LIGHT : Color.DARK;
                column++;
            }
        }
    }

    /**
     * Updates the Square matrix with the new posiiton of pieces
     */
    public void updateBoard() {
        for (int i = 0; i < board.length; i++) {
            int column = 0;
            for (ColumnLabel col : ColumnLabel.values()) {
                if (getSquare(BOARD_SIZE - i, col).getColor() == Color.DARK)
                    board[i][column] = getSquare(BOARD_SIZE - i, col);
                column++;
            }
        }
    }

    /**
     *
     * @return Square[][]
     */
    public Square[][] getBoard() {
        return board;
    }

    /**
     * returns the pieces of a certain color - light or dark.
     * @param color
     * @return List composed of Pieces
     */
    public List<Piece> getPieces(Color color) {
        return (color == Color.DARK) ? darkPieces : lightPieces;
    }

    /**
     * gets the Square object at a particular coordinate
     * @param coordinate
     * @return Square
     */
    public Square getSquare(Coordinate coordinate) {
        return getSquare(coordinate.getRow(), coordinate.getCol());
    }

    /**
     * gets the Square object at a particular row and column
     * @param row
     * @param col
     * @return Square
     */
    public Square getSquare(Integer row, ColumnLabel col) {
        return (col != null && row > 0 && row < 9) ? board[BOARD_SIZE - row][col.getValue()] : null;
    }

    /**
     * gets the piece at a coordinate on the board
     * @param coordinate
     * @return Piece
     */
    public Piece getPieceAt(Coordinate coordinate) {
        return getPieceAt(coordinate.getRow(), coordinate.getCol());
    }

    /**
     * gets the pieces at a row and column on the board
     * @param row
     * @param col
     * @return Piece
     */
    public Piece getPieceAt(Integer row, ColumnLabel col) {
        return getSquare(row, col).getCurrentPiece();
    }

    /**
     * returns a list of all the moves that are currently possible for a color
     * @param color
     * @return List of Coordinates
     */
    public List<Coordinate> getColorPiecesMove(Color color) {
        List<Coordinate> coordinatesList = new LinkedList<>();
        List<Piece> pieces = (color == Color.DARK) ? darkPieces : lightPieces;
        for (Piece p : pieces)
            for (Coordinate c : p.getValidMoves())
                if (!coordinatesList.contains(c)) coordinatesList.add(c);
        return coordinatesList;
    }

    /**
     * adds reference to piece to keep count on the board
     * @param piece
     */
    public void setPieceOnBoard(Piece piece) {
        List<Piece> pieces = (piece.getColor() == Color.DARK) ? darkPieces : lightPieces;
        if (!pieces.contains(piece)) pieces.add(piece);
    }

    /**
     * removes reference to piece to keep count on the board
     * @param piece
     */
    public void removePieceOnBoard(Piece piece) {
        if (piece == null) return;
        List<Piece> pieces = (piece.getColor() == Color.DARK) ? darkPieces : lightPieces;
        pieces.removeIf(cord -> cord.getCurrentSquare().getCoordinate().equals(piece.getCurrentSquare().getCoordinate()));
    }

    @Override
    public String toString() {
        String s = "";
        for (Square[] row : board) {
            for (Square sq : row)
                s += sq;
            s += "\n";
        }
        return s;
    }
}
