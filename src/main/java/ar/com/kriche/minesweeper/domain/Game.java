package ar.com.kriche.minesweeper.domain;

/**
 * represents a minesweeper game.
 * Knows its board, the state of the game and makes moves.
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

    public void makeMove(Move move) {
        // TODO:
        // validate move?
        // apply the move.
        // refresh game state.
    }

}
