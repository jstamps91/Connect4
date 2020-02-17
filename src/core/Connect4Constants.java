package core;

 /**
  * An interface to store frequently used constant variables in Connect4 game.
  * <p>
  * @author Joshua Stamps
  * @version v1.0
  */

public interface Connect4Constants {

     /**
      * Holds number of rows on the game board.
      */
     final static int ROW = 6;
     /**
      * Holds number of columns on the game board.
      */
     final static int COL = 7;
      /**
       * Final int to set size of the game board.
       */
      final static int TILE_SIZE = 100;
     /**
      * Holds client/server prompt for player 1.
      */
     final static int PLAYER1 = 1;
     /**
      * Holds client/server prompt for player 2.
      */
     final static int PLAYER2 = 2;
     /**
      * Hold client/server prompt if player 1 wins game.
      */
     final static int P1_WIN = 1;
     /**
      * Holds client/server prompt if player 2 wins game.
      */
     final static int P2_WIN = 2;
     /**
      * Holds client/server prompt if the game session is a draw.
      */
     final static int DRAW = 3;
     /**
      * Holds client/server prompt to continue I/O player turns
      */
     final static int PROCEED = 4;
     /**
      * Designate invalid move to send to client
      */
     final static int INVALID = 5;
     /**
      * Holds color and token association for Red and X
      */
     final static String RED = "X";
     /**
      * Holds color and token association for Yellow and O
      */
     final static String YELLOW = "O";

}