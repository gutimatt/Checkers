package com.checkersgame.core.enums;

/** Column enum to map columns to letters
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/

import java.util.HashMap;
import java.util.Map;

public enum ColumnLabel {
    A(0),
    B(1),
    C(2),
    D(3),
    E(4),
    F(5),
    G(6),
    H(7);

    private int value;
    private static Map map = new HashMap<>();

    ColumnLabel(int value) {
        this.value = value;
    }

    static {
        for (ColumnLabel label : ColumnLabel.values())
            map.put(label.value, label);
    }

    public static ColumnLabel valueOf(int columnLabel) {

        return (ColumnLabel) map.get(columnLabel);
    }

    public Integer getValue() {
        return value;
    }
}
