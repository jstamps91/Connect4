/**
 * UI package contains the Connect4TextConsole.java file.
 */
package UI;

import java.util.InputMismatchException;
import java.util.Scanner;

import core.Connect4;
import core.Connect4ComputerPlayer;

 /**
  * This class is the user interface and input for the connect4 game.  Handles all
  * user input, and calls Connect4 methods for logic in the program.
  * <p>
  * Required for Functionality:
  * <ul>
  * <li>Connect4TextConsole.java
  * <li>Connect4ComputerPlayer.java
  * </ul>
  * <p>
  * @author Joshua Stamps
  * @version v2.0
  */
class Connect4TextConsole {

    /**
     * Connect4 object for game_board to initialize and play the game.
     */
    private Connect4 game_board = new Connect4();

    /**
     * static Scanner object to handle all inputs outside of the startGame() method.
     */
    private static Scanner sc = new Scanner(System.in);

    /**
     * Constructor to initialize and play against the computer.
     */
    private Connect4ComputerPlayer comp = new Connect4ComputerPlayer();


    /**
     * Constructor to make a text console game object.
     * @param game Connect4 object needed for game play.
     */
    Connect4TextConsole(Connect4 game) {
        this.game_board = game;
    }

    /**
     * Initializes the game and receives user input.
     */
    void startGame() {

        System.out.println("Welcome to Connect Four!");
        System.out.println("Press \"P\" if you want to play against another player.");
        System.out.println("Press \"C\" to play against the computer.");
        System.out.println("Press \"Q\" to exit game.");

        Scanner startGame = new Scanner(System.in);
        String status = startGame.next();

        while (status != null) {
            if (status.equalsIgnoreCase("P")) {
                playGameHuman();
                startGame.close();
            }
            if (status.equalsIgnoreCase("C")) {
                playComputer();
                startGame.close();
            }
            if (status.equalsIgnoreCase("Q")) {
                System.out.println("Exiting game...");
                startGame.close();
                System.exit(0);
            } else {
                System.out.println("Please select E to play game or Q to quit.");
                status = startGame.next();
            }
        }
    }


     /**
      * Method where the game is played; gets move, validates move,
      * adds token to the game board, and checks for winner.
      * @throws InputMismatchException if something other than an int is scanned.
      */
     private void playGameHuman() throws InputMismatchException {
         String token = game_board.getPlayerToken();
         boolean valid;
         int temp; //temp variable to hold column selected by user
         int move; // variable to hold adjusted column

         game_board.drawBoard();

         while (!game_board.getWinner() && game_board.getTurnCount() <= 42) {
             // try/catch handles input mismatch
             try {
                 System.out.println("Player " + token + ", Please choose a "
                         + "column between 1-7.");
                 temp = sc.nextInt();
                 move = temp - 1;
                 valid = game_board.validateMove(move);

                 while (!valid) {
                     System.out.println("Invalid option. Please choose a column between 1-7.");
                     temp = sc.nextInt();
                     move = temp - 1;
                     valid = game_board.validateMove(move);
                 }
                 //add token to the game board
                 game_board.dropToken(token, move);
                 //check for winner
                 game_board.checkWinner(token);
                 game_board.playerTurn(token);
                 token = game_board.getPlayerToken();
                 game_board.drawBoard();

             } catch (InputMismatchException e) {
                 System.out.println("That is not a number. Please select a valid column.");
                 //catch weird input and do nothing
                 String retry = sc.nextLine();
             }
         }

         //output winner
         if (game_board.getWinner()) {
             game_board.playerTurn(token);
             token = game_board.getPlayerToken();
             if (token.equals("X")) {
                 System.out.println("Congratulations!");
                 System.out.println("Player X has won the game!  Game over.");
                 System.exit(0);
             } else {
                 System.out.println("Congratulations!");
                 System.out.println("Player O has won the game!  Game over.");
                 System.exit(0);
             }
         } else {
             System.out.println("Draw! Game over");
             System.exit(0);
         }
     }


     /**
      * Method where the game is played against the computer;
      * gets move, validates move, adds token to the game board, and checks
      * for winner.
      * @throws InputMismatchException if something other than an int is scanned.
      */
     private void playComputer() {
         String token = comp.getPlayerToken();
         boolean valid;
         int temp;   //temp variable to hold column selected by user
         int move;       // variable to hold adjusted column
         comp.drawBoardComputer();
         while (!comp.getWinner() && comp.getTurnCount() <= 42) {
             // try/catch handles input mismatch
             try {
                 if (comp.getPlayerToken().equals("X")) {
                     System.out.println("Player " + token + ", Please choose a column between 1-7.");
                     temp = sc.nextInt();
                     move = temp - 1;

                     valid = comp.validateMove(move);

                     while (!valid) {
                         System.out.println("Invalid option. Please choose a column between 1-7.");
                         temp = sc.nextInt();
                         move = temp - 1;
                         valid = comp.validateMove(move);
                     }
                 } else {
                     comp.computerPlayer();
                     move = comp.getCompCol();
                     valid = comp.validateMove(move);

                     while (!valid) {
                         comp.computerPlayer();
                         move = comp.getCompCol();
                         valid = comp.validateMove(move);
                     }
                 }
                 //add token to game board
                 comp.dropToken(token, move);
                 //check for winner
                 comp.checkWinner(token);
                 comp.playerTurn(token);
                 token = comp.getPlayerToken();
                 comp.drawBoardComputer();

             } catch (InputMismatchException e) {
                 System.out.println("Invalid input. Please select a valid column.");
                 //catch invalid input and do nothing
                 String retry = sc.nextLine();
                 retry.replace("", "");
             }
         }

         //output winner
         if (comp.getWinner()) {
             comp.playerTurn(token);
             token = comp.getPlayerToken();
             if (token.equals("X")) {
                 System.out.println("Congratulations!");
                 System.out.println("Player X has won!  Game over.");
                 System.exit(0);
             } else {
                 System.out.println("Congratulations!");
                 System.out.println("The Computer has won!  Game over.");
                 System.exit(0);
             }
         } else {
             System.out.println("Draw!  Game over.");
             System.exit(0);
         }
     }

 }


