package com.checkersgame.ui;

import com.checkersgame.core.Coordinate;
import com.checkersgame.core.enums.ColumnLabel;

/**
 * static methods to help the UIs convert a string to a coordinate
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/
public class InputHelper {

    /**
     * takes in a string like 3e and coverts it to a new Coordinate object
     * @param s
     * @return Coordinate
     * @throws Exception
     */
    public static Coordinate convertStringToCoordinate(String s) throws Exception {
        Integer row = getInputRow(s.charAt(0));
        ColumnLabel col = getInputCol(s.toUpperCase().charAt(1));
        return new Coordinate(col, row);
    }

    /**
     * reads the first char of a string to get the row
     * @param s
     * @return Integer
     */
    public static Integer getInputRow(char s) {
        return Integer.parseInt(String.valueOf(s));
    }

    /**
     * reads the second char of a string to get the Column
     * @param s
     * @return ColumnLabel
     */
    public static ColumnLabel getInputCol(char s) {
        return ColumnLabel.valueOf(Character.getNumericValue(s)-10);
    }

}
