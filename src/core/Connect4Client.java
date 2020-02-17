package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

/**
 * This is the client application for Connect4 to play online games.
 * <p>
 * Required for Functionality:
 * <ul>
 * <li>Connect4TextConsole.java
 * <li>Connect4ComputerPlayer.java
 * <li>Connect4.java
 * <li>Connect4GUI.java
 * <li>Connect4Server.java
 * <li>Connect4Constants.java
 * </ul>
 * <p>
 * @author Joshua Stamps
 * @version v1.0
 */

public class Connect4Client extends Application implements Connect4Constants {

    /**
     * Indicate whether the player has the turn.
     */
    private boolean myTurn = false;
    /**
     * Indicate the token for the player.
     */
    private String myToken = " ";
    /**
     * Indicate the token for the other player.
     */
    private String otherToken = " ";
    /**
     * Create and initialize cells.
     */
    private Cell[][] cell = new Cell[ROW][COL];
    /**
     * Create and initialize a title label.
     */
    private Label title = new Label();
    /**
     * Create and initialize a status label.
     */
    private Label status = new Label();
    /**
     * Indicate selected column by the current move.
     */
    private int colSelected;
    /**
     * Input stream from/to server.
     */
    private DataInputStream dIS;
    /**
     * Output streams from/to server.
     */
    private DataOutputStream dOS;
    /**
     * Flag to continue to play.
     */
    private boolean proceed = true;
    /**
     * Wait for the player to mark a cell.
     */
    private boolean waiting = true;

    /**
     * Start method to launch client gui.
     * @param primaryStage builds game interface window.
     * @throws Exception e
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        // Pane to hold cell
        GridPane pane = new GridPane();
        pane.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE,
                CornerRadii.EMPTY, Insets.EMPTY)));
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                pane.add(cell[i][j] = new Cell(j), j, i);
            }
        }

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(title);
        borderPane.setCenter(pane);
        borderPane.setBottom(status);

        // Create a scene and place it in the stage
        Scene scene = new Scene(borderPane, 600, 550);
        primaryStage.setTitle("Connect Four: Online"); // Set the stage title
        primaryStage.setScene(scene); // Place scene in the stage
        primaryStage.show(); // Display stage

        connectToServer();
    }

    /**
     * Method to connect to server.
     */
    private void connectToServer() {
        try {
            // Create a socket to connect to the server
            /**Host name or ip. */
            String host = "localhost";
            Socket socket = new Socket(host, 8000);

            // Receives data from the server
            dIS = new DataInputStream(socket.getInputStream());

            // Sends data to the server
            dOS = new DataOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Control the game on a separate thread
        new Thread(() -> {
            try {
                // Get notification from the server
                int player = dIS.readInt();

                // Am I player 1 or 2?
                if (player == PLAYER1) {
                    myToken = RED;
                    otherToken = YELLOW;
                    Platform.runLater(() -> {
                        title.setText("Player 1 with token RED");
                        status.setText("Waiting for player 2 to join");
                    });

                    // Receive startup notification from the server
                    dIS.readInt();

                    // Player 2 has joined
                    Platform.runLater(()
                            -> status.setText("Player 2 has joined. I start first"));


                    myTurn = true;
                } else if (player == PLAYER2) {
                    myToken = YELLOW;
                    otherToken = RED;
                    Platform.runLater(() -> {
                        title.setText("Player 2 with token YELLOW");
                        status.setText("Waiting for player 1 to move");
                    });
                }

                // Continue to play
                while (proceed) {
                    if (player == PLAYER1) {
                        waitForPlayerAction(); // Wait for player 1
                        sendMove(); // Send move to server
                        receiveInfoFromServer(); // Receive info from server
                    } else if (player == PLAYER2) {
                        receiveInfoFromServer(); // Receive info from server
                        waitForPlayerAction(); // Wait for player 2
                        sendMove(); // Send player 2's move to the server

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Wait for the player to mark a cell.
     * @throws InterruptedException InterruptedException
     */
    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }

        waiting = true;
    }

    /**
     * Method to send this player's move to the server.
     * @throws IOException IOException
     */
    private void sendMove() throws IOException {
        dOS.writeInt(colSelected);
    }


    /**
     * Method to receive info from the server.
     * @throws IOException IOException
     */
    private void receiveInfoFromServer() throws IOException {
        // Game status
        int status = dIS.readInt();

        if (status == P1_WIN) {
            // Winner = player 1, end game
            proceed = false;
            if (myToken.equals(RED)) {
                Platform.runLater(() -> this.status.setText("I won! (RED)"));
            } else if (myToken.equals(YELLOW)) {
                Platform.runLater(()
                        -> this.status.setText("Player 1 (RED) has won!"));
                receiveMove();
            }
        } else if (status == P2_WIN) {
            // Winner = player 2, end game
            proceed = false;
            if (myToken.equals(YELLOW)) {
                Platform.runLater(() -> this.status.setText("I won! (YELLOW)"));
            } else if (myToken.equals(RED)) {
                Platform.runLater(()
                        -> this.status.setText("Player 2 (YELLOW) has won!"));
                receiveMove();
            }
        } else if (status == DRAW) {
            // No winner, game is over
            proceed = false;
            Platform.runLater(()
                    -> this.status.setText("Game is over, no winner!"));

            if (myToken.equals(YELLOW)) {
                receiveMove();
            }
        } else {
            receiveMove();
            Platform.runLater(() -> this.status.setText("My turn"));
            myTurn = true;
        }
    }

    /**
     * Method to receive opponent's move from the server.
     * @throws IOException IOException
     */
    private void receiveMove() throws IOException {
        // Retrieve opponent's move
        int row = dIS.readInt();
        int column = dIS.readInt();
        Platform.runLater(() -> cell[row][column].setToken(otherToken));
    }

    /**
     * Inner class to build each cell
     */
    public class Cell extends Pane {

        /**
         * Column of cell
         */
        private int column;
        /**
         * Token used for cell.
         */
        private String token = " ";

        /**
         * Constructor to build each cell
         * @param column int
         */
        public Cell(int column) {
            this.column = column;
            this.setPrefSize(2000, 2000); // What happens without this?
            setStyle("-fx-border-color: black"); // Set cell's border
            this.setOnMouseClicked(e -> handleMouseClick());
        }

        /**
         * Handles a mouse click event.
         */
        private void handleMouseClick() {

            if (token.equals(" ") && myTurn) {
                setToken(myToken);  // Set player's token in cell
                myTurn = false;
                colSelected = column;
                status.setText("Waiting on opponent's move");
                waiting = false; // Just completed a successful move
            }
        }

        /**
         * Get current token.
         * @return token
         */
        public String getToken() {
            return token;
        }

        /**
         * Set a new token.
         * @param s String to be taken and set to token.
         */
        public void setToken(String s) {
            token = s;
            repaint();
        }


        /**
         * Method to add red and yellow tokens to the game board.
         */
        protected void repaint() {
            if (token.equals(RED)) {
                Ellipse ellipse = new Ellipse(this.getWidth() / 2,
                        this.getHeight() / 2, this.getWidth() / 2 - 10,
                        this.getHeight() / 2 - 10);
                ellipse.centerXProperty().bind(
                        this.widthProperty().divide(2));
                ellipse.centerYProperty().bind(
                        this.heightProperty().divide(2));
                ellipse.radiusXProperty().bind(
                        this.widthProperty().divide(2).subtract(10));
                ellipse.radiusYProperty().bind(
                        this.heightProperty().divide(2).subtract(10));
                ellipse.setStroke(Color.BLACK);
                ellipse.setFill(Color.RED);

                getChildren().add(ellipse); // Add ellipse to the pane

            } else if (token.equals(YELLOW)) {
                Ellipse ellipse = new Ellipse(this.getWidth() / 2,
                        this.getHeight() / 2, this.getWidth() / 2 - 10,
                        this.getHeight() / 2 - 10);
                ellipse.centerXProperty().bind(
                        this.widthProperty().divide(2));
                ellipse.centerYProperty().bind(
                        this.heightProperty().divide(2));
                ellipse.radiusXProperty().bind(
                        this.widthProperty().divide(2).subtract(10));
                ellipse.radiusYProperty().bind(
                        this.heightProperty().divide(2).subtract(10));
                ellipse.setStroke(Color.BLACK);
                ellipse.setFill(Color.YELLOW);

                getChildren().add(ellipse); // Add ellipse to the pane
            }
        }
    }
}
