package UI;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import core.Connect4;
import core.Connect4Client;
import core.Connect4Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/**
 * This class is the graphical user interface for the connect4 game.  Handles all
 * user input, and calls Connect4 methods for logic in the program.
 * <p>
 * Required for Functionality:
 * <ul>
 * <li>Connect4.java
 * <li>Connect4Constants.java
 * <li>Connect4TextConsole.java
 * <li>Connect4Server.java
 * <li>Connect4Client.java
 * </ul>
 * <p>
 * @author Joshua Stamps
 * @version v1.0
 */
public class Connect4_GUI extends Application implements Connect4Constants {

    /**
     * Stage window to host start up of game.
     */
    private Stage window;
    /**
     * Scenes to launch game and play from GUI.
     */
    private Scene startUp;
    /**
     * Pane to track root node for tokens in the game.
     */
    private Pane tokenRoot = new Pane();
    /**
     * Token[][] gameBoard to hold logic of game board.
     */
    private Token[][] gridBoard = new Token[COL][ROW];
    /**
     * Boolean to track token color move. Initialized as color yellow.
     */
    private boolean redToken = false;
    /**
     * Boolean to determine if playing against the computer.
     */
    private boolean comp = false;
    /**
     * List to add AI moves with player moves in single player.
     */
    private List<Token> cpList = new ArrayList<>();

    /**
     * Private inner class to initialize token object for game board. Extends
     * circle.
     */
    private static class Token extends Circle {

        private final boolean red;

        /**
         * Inner Token class to initialize token object placed on board game.
         *
         * @param red boolean to determine color of token(red/yellow).
         */
        Token(boolean red) {
            super(TILE_SIZE / 2, red ? Color.YELLOW : Color.RED);
            this.red = red;
            setCenterX(TILE_SIZE / 2);
            setCenterY(TILE_SIZE / 2);
        }
    }

    /**
     * Method to launch start menu of game.
     *
     * @param stage stage
     */
    private void launchStartMenu(Stage stage) {
        window = stage;

        Label startLabel = new Label("How would you like to play Connect4?\n");

        Button bOnline = new Button("Play Online");
        bOnline.setOnMouseClicked(e
                -> {
            try {
                launchOnline(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button bLocal = new Button("Local");
        bLocal.setOnAction(e
                -> {
            System.out.println("Starting to offline menu.....");
            launchOffline(window);
            stage.show();
        });

        // Text in HBox
        HBox label = new HBox(8);
        label.getChildren().add(startLabel);
        label.setPadding(new Insets(10, 10, 10, 10));
        label.setAlignment(Pos.TOP_CENTER);

        // Buttons options in HBox
        VBox bStart = new VBox(8);
        bStart.getChildren().addAll(bOnline, bLocal);
        bStart.setPadding(new Insets(10, 10, 10, 10));
        bStart.setAlignment(Pos.CENTER);

        // add HBoxes to BorderPane window
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #ffffff");
        bp.setTop(label);
        bp.setCenter(bStart);

        // set the scene and show
        startUp = new Scene(bp, 300, 150);
        window.setScene(startUp);
        window.setTitle("Connect Four");
        window.show();

    }

    /**
     * Method to launch offline version of Connect4 game.
     * @param stage window to ask for GUI or Console versions of offline game.
     */
    private void launchOffline(Stage stage) {
        window = stage;

        Label startLabel = new Label("How would you like to play the game?\n");
        Button bLocalGUI = new Button("GUI");
        bLocalGUI.setOnMouseClicked(e -> {
            launchOfflineGUI(window);
            stage.show();
        });

        Button bLocalConsole = new Button("Console");
        bLocalConsole.setOnAction(e -> {
            System.out.println("Switching to Local Text Console...");
            Connect4TextConsole start = new Connect4TextConsole(new Connect4());
            stage.close();
            start.startGame();
        });

        // Text in HBox
        HBox label = new HBox(8);
        label.getChildren().add(startLabel);
        label.setPadding(new Insets(10, 10, 10, 10));
        label.setAlignment(Pos.TOP_CENTER);

        // Buttons options in HBox
        HBox localBox = new HBox(8);
        localBox.getChildren().addAll(bLocalGUI, bLocalConsole);
        localBox.setPadding(new Insets(10, 10, 10, 10));
        localBox.setAlignment(Pos.CENTER);

        // add HBoxes to BorderPane window
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #b8c6bb");
        bp.setTop(label);
        bp.setCenter(localBox);

        // set the scene and show
        startUp = new Scene(bp, 300, 150);
        window.setScene(startUp);
        window.setTitle("Connect Four");
        window.show();
    }

    /**
     * Menu options for offline GUI mode.
     * @param stage Create new window for 1/2 player selection.
     */
    private void launchOfflineGUI(Stage stage) {
        window = stage;

        //add boxes and buttons to the scene
        Button cp = new Button("1 Player");
        Button player = new Button("2 Players");

        Label startLabel = new Label("How would you like to play the game?\n");
        // Text in HBox
        HBox label = new HBox(8);
        label.getChildren().add(startLabel);
        label.setPadding(new Insets(10, 10, 10, 10));
        label.setAlignment(Pos.TOP_CENTER);

        // Buttons options in HBox
        HBox localBox = new HBox(8);
        localBox.getChildren().addAll(cp, player);
        localBox.setPadding(new Insets(10, 10, 10, 10));
        localBox.setAlignment(Pos.CENTER);

        // add HBoxes to BorderPane window
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #b8c6bb");
        bp.setTop(label);
        bp.setCenter(localBox);

        // set the scene and show
        startUp = new Scene(bp, 300, 150);
        window.setScene(startUp);
        window.setTitle("Connect Four");
        window.show();

        //2 player mode
        player.setOnAction(e -> local2Player(new Stage()));

        // 1 player mode
        cp.setOnAction(e
                -> {
            comp = true;
            local1Player(new Stage());
        });
    }

    /**
     * Set online Status to initialize board.
     */
    private void setOnlineStatus()
    {
        /**
         * Boolean flag to designate if online game is active.
         */
        boolean online = true;
    }

    /**
     * Method to launch Online version of game.
     * @param stage window to launch online game.
     */
    private void launchOnline(Stage stage) throws Exception {
        System.out.println("Starting Online Play...");
        Connect4Client client = new Connect4Client();
        setOnlineStatus();
        client.start(stage);
    }

    /**
     * Void method to launch 2-player GUI game.
     * @param stage Passes new stage to create game window
     */
    private void local2Player(Stage stage) {
        //prompt closing window if selected
        stage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });
        stage.setTitle("Connect Four: Two Player");
        stage.setScene(new Scene(createBoard()));
        stage.getScene().getWindow();
        stage.show();
        window.close();
        System.out.println((redToken ? "Player 2 (YELLOW)" : "Player 1(RED)") + " turn");
    }

    /**
     * Void method to launch 1-player GUI game.
     * @param stage Passes new stage to create game window.
     */
    private void local1Player(Stage stage) {

        //prompt closing window if selected
        stage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        stage.setTitle("Connect Four: Single Player");
        stage.setScene(new Scene(createBoard()));
        stage.getScene().getWindow();
        stage.show();
        window.close();
        System.out.println((redToken ? "COMPUTER (YELLOW)" : "Player (RED)") + " turn");
    }

    /**
     * Method adds lighting and color effects to game board.
     * @param pShape Takes game board shape created in createGrid().
     */
    private void setLighting(Shape pShape) {
        Light.Distant light = new Light.Distant();
        light.setAzimuth(95.0);
        light.setElevation(50.0);
        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);
        pShape.setFill(Color.ROYALBLUE);
        pShape.setEffect(lighting);
    }

    /**
     * Parent Method to create grid to visualize game board.
     * @return <code>rootPane</code> The Pane containing the game board.
     */
    private Parent createBoard() {

        Pane rootPane = new Pane();

        System.out.println("Initializing Board...");
        rootPane.getChildren().add(tokenRoot);
        Shape gridShape = createGrid();
        rootPane.getChildren().add(gridShape);
        rootPane.getChildren().addAll(localOverlay());

        return rootPane;
    }

    /**
     * Method to create grid on the game board.
     * @return shape : the new board with grid to handle placement of tokens.
     */
    private Shape createGrid() {

        //use rectangles to create grid
        Shape shape = new Rectangle((COL + 1) * TILE_SIZE, (ROW + 1) * TILE_SIZE);

        //use circles to put holes in the grid
        for (int y = 0; y < ROW; y++) {
            for (int x = 0; x < COL; x++) {
                Circle circle = new Circle(TILE_SIZE / 2);
                circle.setCenterX(TILE_SIZE / 2);
                circle.setCenterY(TILE_SIZE / 2);
                //small space between the circles
                circle.setTranslateX(x * (TILE_SIZE + 6) + TILE_SIZE / 3);
                circle.setTranslateY(y * (TILE_SIZE + 6) + TILE_SIZE / 3);

                shape = Shape.subtract(shape, circle);
            }
        }
        setLighting(shape);
        return shape;
    }

    /**
     * Gives overlay to grid so the user knows what columns they are
     * selecting in the game.
     * @return Columns list for board overlay.
     */
    private List<Rectangle> localOverlay() {
        List<Rectangle> list = new ArrayList<>();

        for (int x = 0; x < COL; x++) {
            Rectangle r = new Rectangle(TILE_SIZE, (ROW + 1) * TILE_SIZE);
            // Highlight each column while hovering
            r.setTranslateX(x * (TILE_SIZE + 6) + TILE_SIZE / 3);
            r.setFill(Color.TRANSPARENT);
            r.setOnMouseEntered(e -> r.setFill(Color.rgb(204, 250, 250, 0.10)));
            r.setOnMouseExited(e -> r.setFill(Color.TRANSPARENT));

            final int col = x;

            r.setOnMouseClicked(e
                    -> {
                placeToken(new Token(redToken), col);

                if (comp) {
                    placeCPToken(new Token(true));
                }
            });
            list.add(r);
        }
        return list;

    }

    /**
     * Method logic to return the token for the appropriate col on the game
     * board.
     * @param col Selected column on the board.
     * @param row Selected row on the board.
     * @return Token for the game board. Empty if selection is off the board or null.
     */
    private Optional<Token> getToken(int col, int row) {
        if (col < 0 || col >= COL || row < 0 || row >= ROW) {
            return Optional.empty();
        }
        return Optional.ofNullable(gridBoard[col][row]);
    }

    /**
     * Method logic to place token in selected column on mouse-click. Checks if
     * the Token is already in the column, then places.
     * @param token The player token object.
     * @param col   The column selected by player on mouse click.
     */
    private void placeToken(Token token, int col) {
        int row = ROW - 1;

        //player drops token
        do {
            if (getToken(col, row).isPresent()) {
                row--;
            } else {
                break;
            }
        } while (row >= 0);

        if (row < 0) {
            return;
        }

        //add token to board
        gridBoard[col][row] = token;
        cpList.add(token);
        //visualize token to game board
        tokenRoot.getChildren().add(token);
        token.setTranslateX(col * (TILE_SIZE + 6) + TILE_SIZE / 3);
        token.setTranslateY(row * (TILE_SIZE + 6) + TILE_SIZE / 3);

        final int currentRow = row; //current row added for gameWinner check

        //check for winner
        if (gameWinner(col, currentRow)) {
            gameOver();
        }
        if (comp) {
            redToken = !redToken;
            System.out.println((redToken ? "Computer (YELLOW)" : "Player (RED)")
                    + " turn");
        } else {
            //switches player
            redToken = !redToken;
            System.out.println((redToken ? "Player (YELLOW)" : "Player (RED)")
                    + " turn");
        }
    }

    /**
     * Places opponent token when playing against the computer.
     * @param token Passes CP token object to be placed on board.
     */
    private void placeCPToken(Token token) {
        int row = ROW - 1;
        Random rand = new Random();
        int col = rand.nextInt(7);

        do {
            if (getToken(col, row).isPresent()) {
                row--;
            } else {
                break;
            }
        } while (row >= 0);

        if (row < 0) {
            return;
        }

        gridBoard[col][row] = token;
        cpList.add(token);
        tokenRoot.getChildren().add(token);
        token.setTranslateX(col * (TILE_SIZE + 6) + TILE_SIZE / 3);
        token.setTranslateY(row * (TILE_SIZE + 6) + TILE_SIZE / 3);

        //currentRow position for gameWinner check
        final int currentRow = row;

        //check for winner
        if (gameWinner(col, currentRow)) {
            gameOver();
        }
        //switch turns
        redToken = !redToken;
        System.out.println((redToken ? "Computer(YELLOW)" : "Player (RED) turn"));

    }

    /**
     * Checks if there is a winner. Can be four in a row either
     * horizontally, vertically, diagonally-up right, or diagonally-up left.
     * @param col Passes column of token added.
     * @param row Passes row of token added.
     * @return <code>true</code> if there is a winner; <code>false</code>
     * if no winner.
     */
    private boolean gameWinner(int col, int row) {

        // Horizontal check
        List<Point2D> horiz = IntStream.rangeClosed(col - 3, col + 3)
                .mapToObj(c -> new Point2D(c, row)).collect(Collectors.toList());
        // Vertical check
        List<Point2D> vert = IntStream.rangeClosed(row - 3, row + 3)
                .mapToObj(r -> new Point2D(col, r)).collect(Collectors.toList());

        // Diagonal-up left
        Point2D dStartPointL = new Point2D(col - 3, row - 3);
        List<Point2D> diagLeft = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> dStartPointL.add(i, i)).collect(Collectors.toList());

        // Diagonal-up right
        Point2D dStartPointR = new Point2D(col - 3, row + 3);
        List<Point2D> diagRight = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> dStartPointR.add(i, -i)).collect(Collectors.toList());

        return winCheck(horiz) || winCheck(vert)
                || winCheck(diagLeft) || winCheck(diagRight);
    }

    /**
     * Helper method - Checks if there is a combination that will equal four in a row.
     * @param slot passes token slot placement on game board to check combinations.
     * @return <code>true</code> if four in a row combination is found;
     *         <code>false</code> otherwise.
     */
    private boolean winCheck(List<Point2D> slot) {
        int tokenMatch = 0;
        for (Point2D s : slot) {
            int col = (int) s.getX();
            int row = (int) s.getY();
            //get token. If no token, give dummy token to fail check and stop here
            Token token = getToken(col, row).orElse(new Token(!redToken));
            if (token.red == redToken) {
                tokenMatch++;
                if (tokenMatch == 4) {
                    return true;
                }
            } else {
                tokenMatch = 0;
            }
        }
        return false;
    }

    /**
     * Method to announce the game winner as player one or player two.
     */
    private void gameOver() {
        Stage stage = new Stage();
        stage.setTitle("We Have a Winner!");
        Label congrats = new Label("CONGRATULATIONS!");
        Button ok = new Button("Exit");
        VBox pane = new VBox(8);

        if (redToken) {
            Label label = new Label("YELLOW has won!  Game Over.");
            pane.getChildren().addAll(congrats, label, ok);
            pane.setAlignment(Pos.CENTER);
            ok.setOnAction(a
                    -> {
                System.out.println("Exiting...");
                System.exit(0);
            });
        } else {
            Label label = new Label("RED has won!  Game Over.");
            pane.getChildren().addAll(congrats, label, ok);
            pane.setAlignment(Pos.CENTER);
            ok.setOnAction(a
                    -> {
                System.out.println("Exiting...");
                System.exit(0);
            });
        }
        Scene scene = new Scene(pane, 300, 150);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Method that prompts to see if user would like to exit the game.
     */
    private void closeProgram() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Program");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Choose your option");
        alert.setGraphic(new ImageView(this.getClass().getResource("C4.png")
                .toString()));

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");

        alert.getButtonTypes().setAll(yes, no);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.get() == yes) {
            Platform.exit();
        } else if (res.get() == no) {
            alert.close();
        }
    }

    /**
     * Main method to launch program.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Launch point method of game.
     *
     * @param primaryStage main stage of game.
     * @throws Exception exception.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        launchStartMenu(primaryStage);
    }

}