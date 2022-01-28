package com.checkersgame.core;

/** A Coordinate to lookup on the board
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/

import com.checkersgame.core.enums.ColumnLabel;

import java.util.Objects;

public class Coordinate implements java.io.Serializable{
    private final ColumnLabel col;
    private final Integer row;

    public Coordinate(ColumnLabel col, int row) {
        this.col = col;
        this.row = row;
    }

    /**
     * gets the column
     * @return ColumnLabel
     */
    public ColumnLabel getCol() {
        return col;
    }

    /**
     * gets the row
     * @return Integer
     */
    public Integer getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return col == that.col && Objects.equals(row, that.row);
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "col=" + col +
                ", row=" + row +
                '}';
    }
}
