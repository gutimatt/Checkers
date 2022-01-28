package com.checkersgame;

import com.checkersgame.core.Coordinate;
import com.checkersgame.core.enums.Color;
import com.checkersgame.core.enums.ColumnLabel;
import com.checkersgame.core.enums.Player;
import com.checkersgame.ui.CheckersGUI;
import com.checkersgame.ui.InputHelper;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.Socket;

/**This is the client class to connect to the server and plays againest somebody else on the server.
 * Extends the checkers gui
 *
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/
public class CheckersGameClient extends CheckersGUI{

    private String host = "localhost";
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private boolean waiting = true;
    private ObjectOutputStream toServerObject;
    private ObjectInputStream fromServerObject;
    private boolean activeGame = true;
    private Socket socket;

    /**
     * constructor to connect to the server
     */
    public CheckersGameClient() {
        connectToServer();
    }

    /**
     * runs the cui
     */
    public static void run() {
        launch();
        new CheckersGameClient();
    }

    /**
     * connects to the server and starts a new thread for a client to recieve and send moves in the game
     */
    private void connectToServer() {
        try {
            socket = new Socket(host, 8000);

            fromServer = new DataInputStream(socket.getInputStream());

            toServer = new DataOutputStream(socket.getOutputStream());

            toServerObject = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        // Control the game on a separate thread
        new Thread(() -> {
            try {
                // Get notification from the server
                Player player = Player.valueOf(fromServer.readInt());

                opponent = (player == Player.PlayerX) ? Player.PlayerO : Player.PlayerX;

                Platform.runLater(() -> {
                    playerTokenLabel.setText("Your are" +
                            ((player == Player.PlayerX) ? " Player 1 " : " Player 2 ") +
                            "using" +
                            ((player == Player.PlayerX) ? " black " : " red ") +
                            "pieces.");
                });

                System.out.println(player.toString());

                if (player == Player.PlayerX) {
                    inputTitle.setText("Waiting for Player 2 to join...");

                    fromServer.readInt();

                    Platform.runLater(() ->
                            inputTitle.setText("Player 2 has joined. \n" +
                                    instructionText)
                    );
                } else {
                    Platform.runLater(() -> {
                        inputTitle.setText("Waiting for Player 1 to move...");
                    });
                }

                while (activeGame) {
                    if (player == Player.PlayerX) {
                        waitForPlayerAction(); // Wait for player 1 to move.
                        receiveInfoFromServer(); // Receive info from the server

                    } else {
                        receiveInfoFromServer(); // Receive info from the server
                        waitForPlayerAction(); // Wait for player 2 to move
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * waits to for the server to send the inputted move from the opponent.  Once done so, waits the player
     * input and send it back
     * @throws Exception
     */
    private void receiveInfoFromServer() throws Exception {
        fromServer.readInt();

        Coordinate[] opposingMove = receiveMove();

        if (opposingMove[2] != null)
            logic.handleTurn(opponent, opposingMove[0], opposingMove[1], opposingMove[2]);
        else
            logic.handleTurn(opponent, opposingMove[0], opposingMove[1]);

        Platform.runLater(this::updateBoard);

        Player myPlayer = (opponent == Player.PlayerO) ? Player.PlayerX : Player.PlayerO;

        if (logic.declaredWinner()){
            activeGame = false;

            if (myPlayer == Player.PlayerX){
                setStatus(myPlayer, "I won");
            }
            else {
                setStatus(myPlayer, "Player X won");
            }
        } else {
            Platform.runLater(() -> {
                inputTitle.setText("My turn");
            });

        }
    }

    /**
     * gets the move from the server and turns it into a coordinate
     * @return Coordinate[]
     */
    private Coordinate[] receiveMove() {
        Coordinate[] c = new Coordinate[3];
        try {
            int oldRow = fromServer.readInt();
            ColumnLabel oldCol = ColumnLabel.valueOf(fromServer.readInt());
            int newRow = fromServer.readInt();
            ColumnLabel newCol = ColumnLabel.valueOf(fromServer.readInt());

            c[0] = InputHelper.convertStringToCoordinate(oldRow + "" + oldCol);
            c[1] = InputHelper.convertStringToCoordinate(newRow + "" + newCol);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * sets the status on the ui with the status of whose turn it is
     * @param p
     * @param t
     */
    private void setStatus(Player p, String t) {
        Platform.runLater(() -> {
            menuPane.getChildren().add(new TextField(t));
        });
    }

    /**
     * event handlers to submit the move.  Listens for enter keyboard click or submit button clicked
     */
    protected void createEventHandlers() {
        Color myColor = (opponent == Player.PlayerO) ? Color.DARK : Color.LIGHT;

        // event handlers
        submitBtn.setOnAction(e -> {
            if (logic.getTurnColor() == myColor) {
                enterMove();
                waiting = false;
            }
        });

        submitBtn.setOnKeyPressed(e -> {
            if (logic.getTurnColor() == myColor && e.getCode() == KeyCode.ENTER){
                enterMove();
                waiting = false;
            }

        });

        input.setOnKeyPressed(e -> {
            if (logic.getTurnColor() == myColor && e.getCode() == KeyCode.ENTER) {
                enterMove();
                waiting = false;
            }
        });
    }

    /**
     * repeats a loop waiting for the player input
     * @throws InterruptedException
     */
    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }

        waiting = true;
    }

    /**
     * submits the move on the board, and sends it to the server
     */
    private void enterMove() {
        try {
            Coordinate[] cordArray = new Coordinate[3];
            String[] split = input.getCharacters().toString().split("-");
            if (split.length < 2) {
                throw new Exception();
            }

            Coordinate rowCord = InputHelper.convertStringToCoordinate(split[0]);
            Coordinate colCord = InputHelper.convertStringToCoordinate(split[1]);

            cordArray[0] = rowCord;
            cordArray[1] = colCord;

            if (split.length > 2) {
                Coordinate lastCord = InputHelper.convertStringToCoordinate(split[2]);

                cordArray[2] = lastCord;

                logic.handleTurn(opponent, rowCord, colCord, lastCord);
            } else {
                logic.handleTurn(opponent, rowCord, colCord);
            }

            Platform.runLater(() -> {
                updateBoard();
                input.clear();

                inputTitle.setText("Waiting for " + ((opponent == Player.PlayerX) ? "Player X " : "PLayer O ") + "to make move.");
            });

            toServerObject.writeObject(cordArray);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
