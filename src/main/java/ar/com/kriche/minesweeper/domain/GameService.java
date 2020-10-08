package ar.com.kriche.minesweeper.domain;

import org.springframework.stereotype.Service;

/**
 * makes the moves to the game and updates the game state accordingly.
 *
 * @Author Kriche 2020
 */
@Service
public class GameService {

    private static Game theCurrentGame = new Game();

    public Game getGame() {
        return theCurrentGame;
    }

    /**
     * @param game
     * @param row
     * @param column
     * @return
     */
    public Game revealCell(Game game, int row, int column) {
        // TODO
        return getGame();
    }

    /**
     * @param game
     * @param row
     * @param column
     * @return
     */
    public Game markQuestionCell(Game game, int row, int column) {
        // TODO
        return getGame();
    }

    /**
     * @param game
     * @param row
     * @param column
     * @return
     */
    public Game markRedFlagCell(Game game, int row, int column) {
        // TODO
        return getGame();
    }

}
