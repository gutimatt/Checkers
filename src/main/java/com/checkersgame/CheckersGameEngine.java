package com.checkersgame;

import com.checkersgame.core.enums.Player;
import com.checkersgame.ui.CheckersGUI;
import com.checkersgame.ui.CheckersTextConsole;

import java.util.Scanner;

/**
 * runs the main method and ask for input for console or gui
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/
public class CheckersGameEngine {
    /**
     * Main method to run the program
     * @param args
     */
    public static void main(String[] args) {
        run();
    }

    /**
     * ask for input for console or gui.  Calls the respective class on decision
     */
    private static void run() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("Enter C for console game, G for GUI game without client, S for gui with server/client");
            String s = scan.nextLine();

            if (s.isEmpty())
                continue;

            if (s.toUpperCase().charAt(0) == 'C') {
                new CheckersTextConsole();
                break;
            } else if (s.toUpperCase().charAt(0) == 'G') {
                CheckersGUI.run();
                break;
            } else if (s.toUpperCase().charAt(0) == 'S') {
                CheckersGameClient.run();
                break;
            }
        }
    }
}
