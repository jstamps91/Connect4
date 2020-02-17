package core;

import java.util.Random;

/**
 *  This is the implementation of Connect Four computer player.
 *  This class handles the pseudo-AI components of the program when playing against the computer.
 *  <p>
 *  Required for Functionality:
 *  <ul>
 *  <li>Connect4TextConsole.java
 *  <li>Connect4ComputerPlayer.java
 *  </ul>
 *  <p>
 *  @author Joshua Stamps
 *  @version v2.0
*/
public class Connect4ComputerPlayer extends Connect4 {

    /**
     * Computer move variable to hold the col selected by the computer.
     */
    private int compMove;

    /**
     * Randomly generates a valid move to be played on the game board by computer player.
     */
    public void computerPlayer() {

        Random rand = new Random();
        compMove = rand.nextInt(7);
        System.out.println("Computer played at column " + (compMove + 1) + ".");
    }


    /**
     * Accessor to return chosen computer column.
     * @return compMove
     */
    public int getCompCol() {
        return compMove;
    }


}