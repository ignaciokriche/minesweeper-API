package ar.com.kriche.minesweeper.domain;

/**
 * Represents a minesweeper game.
 * Knows its board and it states.
 *
 * @Author Kriche 2020
 */
public class Game {

    private GameState state = GameState.IN_PROGRESS;
    private Board board = new Board();

    public GameState getState() {
        return state;
    }

    public Board getBoard() {
        return board;
    }

}
