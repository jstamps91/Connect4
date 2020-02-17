package core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * This is the server application for Connect4 to host and play online games.
 * <p>
 * Required for Functionality:
 * <ul>
 * <li>Connect4TextConsole.java
 * <li>Connect4ComputerPlayer.java
 * <li>Connect4.java
 * <li>Connect4GUI.java
 * <li>Connect4Client.java
 * <li>Connect4Constants.java
 * </ul>
 * <p>
 * @author Joshua Stamps
 * @version v1.0
 */
public class Connect4Server extends Application implements Connect4Constants {

    /**
     * Number of server sessions.
     */
    private int sessionNo = 1;

    /**
     * Method to launch server.
     * @param primaryStage takes the main stage as input to make server window.
     */
    @Override
    public void start(Stage primaryStage) {
        TextArea taLog = new TextArea();

        // Creates scene, places it within stage
        Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
        primaryStage.setTitle("Connect Four Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() ->
        {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8000);
                Platform.runLater(() -> taLog.appendText(new Date()
                        + ": Server started at socket 8000\n"));

                // Ready to create a session for every two players
                while (true) {
                    Platform.runLater(() -> taLog.appendText(new Date()
                            + ": Wait for players to join session " + sessionNo + '\n'));

                    // Connect to player 1
                    Socket player1 = serverSocket.accept();

                    Platform.runLater(() ->
                    {
                        taLog.appendText(new Date() + ": Player 1 joined session "
                                + sessionNo + '\n');
                        taLog.appendText("Player 1's IP address"
                                + player1.getInetAddress().getHostAddress() + '\n');
                    });

                    // Notify that the player is Player 1
                    new DataOutputStream(
                            player1.getOutputStream()).writeInt(PLAYER1);

                    // Connect to player 2
                    Socket player2 = serverSocket.accept();

                    Platform.runLater(() ->
                    {
                        taLog.appendText(new Date()
                                + ": Player 2 joined session " + sessionNo + '\n');
                        taLog.appendText("Player 2's IP address"
                                + player2.getInetAddress().getHostAddress() + '\n');
                    });

                    // Notify that the player is Player 2
                    new DataOutputStream(
                            player2.getOutputStream()).writeInt(PLAYER2);

                    // Display this session and increment session number
                    Platform.runLater(()
                            -> taLog.appendText(new Date()
                            + ": Start a thread for session " + sessionNo++ + '\n'));

                    // Launch a new thread for this session of two players
                    new Thread(new HandleASession(player1, player2)).start();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Define the thread class for handling a new session for two players.
     */
    static class HandleASession implements Runnable, Connect4Constants {

        /**
         * Socket for player 1.
         */
        private Socket player1;
        /**
         * Socket for player 2.
         */
        private Socket player2;
        /**
         * Variable to track number of turns taken.
         */
        private int turnCount = 1;
        /**
         * Holds row selection by player.
         */
        private int rowSelect;

        /**
         * Create and initialize gameCells.
         */
        private String[][] gameCell = new String[ROW][COL];

        /**
         * Constructor to make a thread.
         *
         * @param player1 Socket
         * @param player2 Socket
         */
        HandleASession(Socket player1, Socket player2) {
            this.player1 = player1;
            this.player2 = player2;

            // Initialize cells
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    gameCell[i][j] = " ";
                }
            }
        }

        /**
         * Implement the run() method for the thread.
         */
        public void run() {
            try {
                // Create data input and output streams
                /**
                 * Input stream for player 1.
                 */
                DataInputStream fromPlayer1 = new DataInputStream(player1.getInputStream());
                /**
                 * Output stream for player 1.
                 */
                DataOutputStream toPlayer1 = new DataOutputStream(player1.getOutputStream());
                /**
                 * Input stream for player 2.
                 */
                DataInputStream fromPlayer2 = new DataInputStream(player2.getInputStream());
                /**
                 * Output stream for player 2.
                 */
                DataOutputStream toPlayer2 = new DataOutputStream(player2.getOutputStream());

                // Write anything to notify player 1 to start
                // This is just to let player 1 know to start
                toPlayer1.writeInt(1);

                // Continuously serve the players and determine and report
                // the game status to the players
                while (true) {
                    // Receive a move from player 1
                    //      int tempRow = fromPlayer1.readInt();
                    int column = fromPlayer1.readInt();
                    boolean valid = validate(column);

                    while (!valid) {
                        column = fromPlayer1.readInt();
                        valid = validate(column);
                    }
                    addRed(column);
                    turnCount++;

                    //gameCell[rowSelect][column] = RED;

                    // Check if Player 1 wins
                    if (isWon(RED)) {
                        toPlayer1.writeInt(P1_WIN);
                        toPlayer2.writeInt(P1_WIN);
                        sendMove(toPlayer2, rowSelect, column);
                        break; // Break the loop
                    } else if (isFull()) { // Check if all gameCells are filled
                        toPlayer1.writeInt(DRAW);
                        toPlayer2.writeInt(DRAW);
                        sendMove(toPlayer2, rowSelect, column);
                        break;
                    } else {
                        // Notify player 2 to take the turn
                        toPlayer2.writeInt(PROCEED);

                        // Send player 1's selected row and column to player 2
                        sendMove(toPlayer2, rowSelect, column);
                    }

                    // Receive a move from Player 2
                    //      tempRow = fromPlayer2.readInt();
                    column = fromPlayer2.readInt();
                    valid = validate(column);
                    while (!valid) {
                        column = fromPlayer1.readInt();
                        valid = validate(column);
                    }
                    addYellow(column);
                    turnCount++;

                    //gameCell[rowSelect][column] = YELLOW;

                    // Check if Player 2 wins
                    if (isWon(YELLOW)) {
                        toPlayer1.writeInt(P2_WIN);
                        toPlayer2.writeInt(P2_WIN);
                        sendMove(toPlayer1, rowSelect, column);
                        break;
                    } else {
                        // Notify player 1 to take the turn
                        toPlayer1.writeInt(PROCEED);

                        // Send player 2's selected row and column to player 1
                        sendMove(toPlayer1, rowSelect, column);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        /**
         * Send the move to other player.
         *
         * @param out    Server output stream to players.
         * @param row    of board move.
         * @param column of board move.
         */
        private void sendMove(DataOutputStream out, int row, int column)
                throws IOException {
            out.writeInt(row); // Send row index
            out.writeInt(column); // Send column index
        }

        /**
         * Boolean flag to to ensure board move is a valid move.
         *
         * @param pCol takes player's column selection for validation.
         * @return <code>true</code> if move is valid;
         * <code>false</code> otherwise.
         */
        private boolean validate(int pCol) {
            if (turnCount <= 42) {
                if (pCol > gameCell[0].length - 1 || pCol < 0) {
                    return false;
                }
                return gameCell[0][pCol].equals(" ");
            }
            return true;
        }

        /**
         * Adds red token to game board in lowest available cell.
         *
         * @param pMove is player's selected column.
         */
        private void addRed(int pMove) {
            //add token to board in bottom-most slot available
            for (int row = gameCell.length - 1; row >= 0; row--) {
                if (gameCell[row][pMove].equals(" ")) {
                    rowSelect = row;
                    gameCell[row][pMove] = RED;
                    break;
                }
            }
        }

        /**
         * Adds yellow token to game board in lowest available cell.
         *
         * @param pMove is player's selected column.
         */
        private void addYellow(int pMove) {
            //add token to board in bottom-most slot available
            for (int row = gameCell.length - 1; row >= 0; row--) {
                if (gameCell[row][pMove].equals(" ")) {
                    rowSelect = row;
                    gameCell[row][pMove] = YELLOW;
                    break;
                }
            }
        }

        /**
         * Boolean to check if the game board is full.
         *
         * @return <code>true</code> if board is full;
         * <code>false</code> otherwise.
         */
        private boolean isFull() {
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    if (gameCell[i][j].equals(" ")) {
                        return false;
                    }
                }
            }
            return true;

        }

        /**
         * Method to check for winner.
         *
         * @param pPlayerToken takes in current player token to check against.
         * @return <code> true </code> if winner found;
         * <code> false</code> otherwise.
         */
        private boolean isWon(String pPlayerToken) {
            //horizontal check
            for (String[] strings : gameCell) {
                for (int col = 0; col < gameCell[0].length - 3; col++) {
                    if (strings[col].equals(pPlayerToken)
                            && strings[col + 1].equals(pPlayerToken)
                            && strings[col + 2].equals(pPlayerToken)
                            && strings[col + 3].equals(pPlayerToken)) {
                        return true;
                    }
                }
            }
            //vertical check
            for (int row = 0; row < gameCell.length - 3; row++) {
                for (int col = 0; col < gameCell[0].length; col++) {
                    if (gameCell[row][col].equals(pPlayerToken)
                            && gameCell[row + 1][col].equals(pPlayerToken)
                            && gameCell[row + 2][col].equals(pPlayerToken)
                            && gameCell[row + 3][col].equals(pPlayerToken)) {
                        return true;
                    }
                }
            }
            //diagonal-up right check
            for (int row = 3; row < gameCell.length; row++) {
                for (int col = 0; col < gameCell[0].length - 3; col++) {
                    if (gameCell[row][col].equals(pPlayerToken)
                            && gameCell[row - 1][col + 1].equals(pPlayerToken)
                            && gameCell[row - 2][col + 2].equals(pPlayerToken)
                            && gameCell[row - 3][col + 3].equals(pPlayerToken)) {
                        return true;
                    }
                }
            }
            //diagonal-down right check
            for (int row = 0; row < gameCell.length - 3; row++) {
                for (int col = 0; col < gameCell[0].length - 3; col++) {
                    if (gameCell[row][col].equals(pPlayerToken)
                            && gameCell[row + 1][col + 1].equals(pPlayerToken)
                            && gameCell[row + 2][col + 2].equals(pPlayerToken)
                            && gameCell[row + 3][col + 3].equals(pPlayerToken)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Main method to start server.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
