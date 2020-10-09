package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.util.RandomService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ar.com.kriche.minesweeper.domain.GameState.USER_LOST;

/**
 * makes the moves to the game and updates the game state accordingly.
 *
 * @Author Kriche 2020
 */
@Service
public class GameService {

    private static final Log LOGGER = LogFactory.getLog(GameService.class);
    private static Game theCurrentGame;
    @Autowired
    private RandomService randomService;

    public Game getGame() {
        if (theCurrentGame == null) {
            theCurrentGame = new Game(randomService);
        }
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
        Cell cell = game.cellAt(row, column);
        if (cell.isRevealed()) {
            // TODO illegal argument;
            return game;
        }
        if (cell.isMined()) {
            cell.setRevealed(true);
            game.setState(USER_LOST);
        } else {
            game.revealAndPropagate(row, column);
        }

        return game;
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
