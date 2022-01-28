package com.checkersgame.core.enums;

import java.util.HashMap;
import java.util.Map;

/** Player enum to denote player
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/

public enum Player {
    PlayerX(0),
    PlayerO(1),
    ComputerPlayer(2);

    private final int value;
    private static Map map = new HashMap<>();

    Player(int value) {
        this.value = value;
    }

    static {
        for (Player label : Player.values())
            map.put(label.value, label);
    }

    public static Player valueOf(int columnLabel) {

        return (Player) map.get(columnLabel);
    }

    public int getValue() {
        return value;
    }
}
