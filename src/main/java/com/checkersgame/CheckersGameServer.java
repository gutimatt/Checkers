package com.checkersgame;

import com.checkersgame.core.CheckersLogic;
import com.checkersgame.core.Coordinate;
import com.checkersgame.core.enums.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**server for the checkers game
 * @author : Matthew Gutierrez
 * @version : 1.0
 **/
public class CheckersGameServer extends Application {

    /**
     * keeps count of the sessions
     */
    private int sessionNo = 1;

    /**
     * the text area on the server gui
     */
    TextArea serverLog = new TextArea();

    /**
     * main method to start the gui.  Launches the gui
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * starts the gui by setting up the ui.  Starts a thread for a new session when two players connect
     * @param stage
     */
    @Override
    public void start(Stage stage){

        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(serverLog), 450, 200);
        stage.setTitle("CheckersGameServer"); // Set the stage title
        stage.setScene(scene); // Place the scene in the stage
        stage.show(); // Display the stage

        new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8000);
                Platform.runLater(() -> serverLog.appendText(new Date() +
                        ": Server started at socket 8000\n"));

                while (true) {
                    // starts a new session for two players
                    Platform.runLater(() -> serverLog.appendText(new Date() +
                            ": Wait for players to join session " + sessionNo + '\n'));

                    // gets player 1
                    Socket player1 = serverSocket.accept();

                    Platform.runLater(() -> {
                        serverLog.appendText(new Date() + ": Player 1 joined session "
                                + sessionNo + '\n');
                        serverLog.appendText("Player 1's IP address" +
                                player1.getInetAddress().getHostAddress() + '\n');
                    });

                    // Notify that the player is Player 1
                    new DataOutputStream(
                            player1.getOutputStream()).writeInt(Player.PlayerX.getValue());

                    // gets player 2
                    Socket player2 = serverSocket.accept();

                    Platform.runLater(() -> {
                        serverLog.appendText(new Date() + ": Player 2 joined session "
                                + sessionNo + '\n');
                        serverLog.appendText("Player 2's IP address" +
                                player2.getInetAddress().getHostAddress() + '\n');
                    });

                    // Notify that the player is Player 2
                    new DataOutputStream(
                            player2.getOutputStream()).writeInt(Player.PlayerO.getValue());

                    sessionNo++;

                    new Thread(new HandleSession(player1, player2)).start();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * handle for the session that implements runnable
     */
    class HandleSession implements Runnable {

        private Socket player1;
        private Socket player2;
        private CheckersLogic logic;
        private Player opponent = Player.PlayerO;

        /**
         * constructor to place the players and logic
         * @param player1
         * @param player2
         */
        public HandleSession(Socket player1, Socket player2) {
            this.player1 = player1;
            this.player2 = player2;
            logic = new CheckersLogic();
        }

        /**
         * repeats to serve the client for each move
         */
        @Override
        public void run() {
            while (true){
                try {
                    serveClient(player1, player2);
                    serveClient(player2, player1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * sends the coordinates of the move from the opponent.
         * @param playerTurn
         * @param oppPlayer
         * @throws Exception
         */
        private void serveClient (Socket playerTurn, Socket oppPlayer )
                throws Exception {

            new DataOutputStream(
                    playerTurn.getOutputStream()).writeInt(1);

            ObjectInputStream fromPlayerObject = new ObjectInputStream(
                    playerTurn.getInputStream());
            Coordinate[] cordArray = (Coordinate[]) fromPlayerObject.readObject();

            if (cordArray[2] != null)
                logic.handleTurn(opponent, cordArray[0], cordArray[1], cordArray[2]);
            else
                logic.handleTurn(opponent, cordArray[0], cordArray[1]);

            new DataOutputStream(
                    oppPlayer.getOutputStream()).writeInt(1);

            sendMove(new DataOutputStream(
                    oppPlayer.getOutputStream()), cordArray);
        }

        /**
         * writes the coordinates to the other client
         * @param out
         * @param cordArray
         * @throws IOException
         */
        private void sendMove(DataOutputStream out, Coordinate[] cordArray) throws IOException {
            out.writeInt(cordArray[0].getRow());
            out.writeInt(cordArray[0].getCol().getValue());
            out.writeInt(cordArray[1].getRow());
            out.writeInt(cordArray[1].getCol().getValue());
        }

    }
}
