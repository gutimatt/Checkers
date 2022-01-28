package com.checkersgame.core;

/** A piece that is on the board.  Can move one diagonal if  no obstructed and can jump a opponent if it has an
 * empty space to land
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/

import com.checkersgame.core.enums.Color;
import com.checkersgame.core.enums.ColumnLabel;

import java.util.LinkedList;
import java.util.List;

public class Piece {
    private final Color color;
    private Square currentSquare;
    private Board board;
    private List<Coordinate> validMoves = new LinkedList<>();


    public Piece(Color color, Square square, Board board) {
        this.color = color;
        this.currentSquare = square;
        this.board = board;
    }

    /**
     * gets the color of the piece
     * @return Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * gets the square that the piece is on
     * @return Square
     */
    public Square getCurrentSquare() {
        return currentSquare;
    }

    /**
     * moves the piece from its current posiiton to the new Coordinate
     * @param coordinate
     * @throws Exception
     */
    public void move(Coordinate coordinate) throws Exception{
        move(coordinate.getRow(), coordinate.getCol());
    }

    /**
     * moves the piece from its current posiiton to the new Coordinate
     * @param row
     * @param col
     * @throws Exception
     */
    public void move(Integer row, ColumnLabel col) throws Exception {
        if (isValid(new Coordinate(col, row))) {
            this.currentSquare.removePiece();
            this.currentSquare = board.getSquare(row, col);
            currentSquare.setPiece(this);
            board.updateBoard();
        }
        else {
            throw new Exception();
        }
    }

    /**
     * returns the opponents piece if it was jumped
     *
     * @param newCord
     * @return Piece
     */
    public Piece getJumpedPiece(Coordinate newCord) {
        Coordinate oldCoordinate = this.getCurrentSquare().getCoordinate();
        Integer removalCordRow;
        ColumnLabel removalCordCol;

        if (newCord.getRow() - oldCoordinate.getRow() > 0)
            removalCordRow = lookUp();
        else
            removalCordRow = lookDown();

        if (newCord.getCol().getValue() - oldCoordinate.getCol().getValue() > 0)
            removalCordCol = lookRight();
        else
            removalCordCol = lookLeft();

        return board.getPieceAt(removalCordRow, removalCordCol);
    }

    /**
     * Jumps over an opponent and removes their piece from the board
     * @param newCord
     * @throws Exception
     */
    public void jump(Coordinate newCord) throws Exception{
        jump(newCord.getRow(), newCord.getCol());
    }

    /**
     * Jumps over an opponent and removes their piece from the board
     * @param row
     * @param col
     * @throws Exception
     */
    public void jump(Integer row, ColumnLabel col) throws Exception{
        if (isValidJumpMove(new Coordinate(col, row))) {
            Coordinate oldCoordinate = this.getCurrentSquare().getCoordinate();
            Integer removalCordRow;
            ColumnLabel removalCordCol;

            if (row - oldCoordinate.getRow() > 0)
                removalCordRow = lookUp();
            else
                removalCordRow = lookDown();

            if (col.getValue() - oldCoordinate.getCol().getValue() > 0)
                removalCordCol = lookRight();
            else
                removalCordCol = lookLeft();

            this.move(row, col);
            board.getPieceAt(removalCordRow, removalCordCol).getCurrentSquare().removePiece();
        } else {
            throw new Exception();
        }
    }

    /**
     * verifies if a jump move is valid given a coordinate to jump to
     *
     * @param coordinate
     * @return
     */
    public boolean isValidJumpMove(Coordinate coordinate) {
        return getValidJumpMoves().contains(coordinate);
    }

    /**
     * returns a list of jumpmoves that a piece can make
     *
     * @return List of Coordinates
     */
    public List<Coordinate> getValidJumpMoves() {
        for (Coordinate cord : getValidMoves())
            if (Math.abs(this.getCurrentSquare().getCoordinate().getRow() - cord.getRow()) < 2)
                validMoves.remove(cord);
        return validMoves;
    }


    /**
     * checks if the new move is valid
     * @param coordinate
     * @return Boolean
     */
    private boolean isValid(Coordinate coordinate) {
        return (getValidMoves().contains(coordinate));
    }

    /**
     * gets a list of valid moves that the piece can make
     * @return List of Coordinates
     */
    public List<Coordinate> getValidMoves() {
        validMoves.clear();

        if (currentSquare.getColor() != Color.DARK)
            return null;

        if (this.color == Color.DARK) {
            validMoves.add(new Coordinate(lookLeft(), lookUp()));
            validMoves.add(new Coordinate(lookRight(), lookUp()));
        } else {
            validMoves.add(new Coordinate(lookLeft(), lookDown()));
            validMoves.add(new Coordinate(lookRight(), lookDown()));
        }

        filterInvalidMoves();
        addJumps();

        return validMoves;
    }

    /**
     * removes moves that would be outside the board range or if it is going to land on its own piece
     */
    private void filterInvalidMoves() {
        validMoves.removeIf(cord -> cord.getCol() == null || cord.getRow().compareTo(8) > 0 || cord.getRow().compareTo(1) < 0);
        validMoves.removeIf(cord -> board.getSquare(cord.getRow(), cord.getCol()).isOccupied() &&
                board.getSquare(cord.getRow(), cord.getCol()).getCurrentPiece().getColor() == this.color);

    }

    /**
     * adds the doublejumps that would be possible for a piece
     */
    private void addJumps() {
        validMoves.addAll(jumpMoves());
        filterInvalidMoves();
    }

    /**gets the list of doublejumps that would be possible for a piece
     *
     * @return List of Coordinates
     */
    private List<Coordinate> jumpMoves() {
        List<Coordinate> dj = new LinkedList<>();
        List<Coordinate> removalMoves = new LinkedList<>();

        for (Coordinate cord : validMoves) {
            Square moveSquare = board.getSquare(cord.getRow(), cord.getCol());
            if (!moveSquare.isOccupied())
                continue;
            Piece moveSquarePiece = moveSquare.getCurrentPiece();

            if (moveSquarePiece.color == Color.LIGHT && this.lookLeft() == moveSquare.getCoordinate().getCol()){
                removalMoves.add(moveSquare.getCoordinate());
                if (board.getSquare(moveSquarePiece.lookUp(), moveSquarePiece.lookLeft()) != null &&
                        !board.getSquare(moveSquarePiece.lookUp(), moveSquarePiece.lookLeft()).isOccupied()) {
                    dj.add(new Coordinate(moveSquarePiece.lookLeft(), moveSquarePiece.lookUp()));
                }
            }
            else if (moveSquarePiece.color == Color.LIGHT && this.lookRight() == moveSquare.getCoordinate().getCol()){
                removalMoves.add(moveSquare.getCoordinate());
                if (board.getSquare(moveSquarePiece.lookUp(), moveSquarePiece.lookRight()) != null &&
                        !board.getSquare(moveSquarePiece.lookUp(), moveSquarePiece.lookRight()).isOccupied()) {
                    dj.add(new Coordinate(moveSquarePiece.lookRight(), moveSquarePiece.lookUp()));
                }
            }
            else if (moveSquarePiece.color == Color.DARK && this.lookLeft() == moveSquare.getCoordinate().getCol()){
                removalMoves.add(moveSquare.getCoordinate());
                if (board.getSquare(moveSquarePiece.lookDown(), moveSquarePiece.lookLeft()) != null &&
                        !board.getSquare(moveSquarePiece.lookDown(), moveSquarePiece.lookLeft()).isOccupied()) {
                    dj.add(new Coordinate(moveSquarePiece.lookLeft(), moveSquarePiece.lookDown()));
                }
            }
            else if (moveSquarePiece.color == Color.DARK && this.lookRight() == moveSquare.getCoordinate().getCol()){
                removalMoves.add(moveSquare.getCoordinate());
                if (board.getSquare(moveSquarePiece.lookDown(), moveSquarePiece.lookRight()) != null &&
                        !board.getSquare(moveSquarePiece.lookDown(), moveSquarePiece.lookRight()).isOccupied()) {
                    dj.add(new Coordinate(moveSquarePiece.lookRight(), moveSquarePiece.lookDown()));
                }
            }
        }

        removeOccupiedSquare(removalMoves);
        return dj;
    }

    /**
     * removes occupied possible moves because of another piece
     * @param removalMoves
     */
    private void removeOccupiedSquare(List<Coordinate> removalMoves) {
        for (Coordinate r : removalMoves) {
            validMoves.remove(r);
        }
    }

    /**
     * checks if a piece has a jump
     * @return Boolean
     */
    public Boolean hasJump() {
        return getValidJumpMoves().size() > 0;
    }

    /**
     * gets the ColumnLabel of the column to the right of the piece
     * @return ColumnLabel
     */
    private ColumnLabel lookRight() {
        return ColumnLabel.valueOf(currentSquare.getCoordinate().getCol().getValue() + 1);
    }

    /**
     * gets the ColumnLabel of the column to the left of the piece
     * @return ColumnLabel
     */
    private ColumnLabel lookLeft() {
        return ColumnLabel.valueOf(currentSquare.getCoordinate().getCol().getValue() - 1);
    }

    /**
     * gets the Row above the piece
     * @return Integer
     */
    private Integer lookUp() {
        return (currentSquare.getCoordinate().getRow() + 1);
    }

    /**
     * gets the Row above the piece
     * @return Integer
     */
    private Integer lookDown() {
        return (currentSquare.getCoordinate().getRow() - 1);
    }

    /**
     * displays the available moves of a piece
     * @return
     */
    public String displayAvailableMoves() {
        return this.getCurrentSquare().getCoordinate().toString() +
                " " + getValidMoves();
    }

    @Override
    public String toString() {
        return "Piece{" +
                "color=" + color +
                '}';
    }

}
