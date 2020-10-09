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

    private RandomService randomService;
    private Game theCurrentGame;

    @Autowired
    public GameService(RandomService randomService) {
        this.randomService = randomService;
    }

    public void initializeGame() {
        theCurrentGame = new Game(randomService);
    }

    public Game getGame() {
        if (theCurrentGame == null) {
            initializeGame();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning game:\n" + theCurrentGame);
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
        LOGGER.debug("reveal cell [" + row + "," + column + "].");
        validateGameInProgress(game, "cannot reveal a cell of a game not in progress.");
        Cell cell = game.cellAt(row, column);
        validateCellNotRevealed(cell, "cannot reveal a cell already revealed.");
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
    public Game markCell(Game game, int row, int column, CellMark mark) {
        LOGGER.debug("mark cell [" + row + "," + column + "] with: " + mark);
        validateGameInProgress(game, "cannot mark cell of a game not in progress.");
        Cell cell = game.cellAt(row, column);
        validateCellNotRevealed(cell, "cannot modify a revealed cell.");
        if (cell.getMark() == mark) {
            throw new IllegalStateException("cell already marked as: " + mark);
        }
        cell.setMark(mark);
        return game;
    }

    private void validateCellNotRevealed(Cell cell, String errMsg) {
        if (cell.isRevealed()) {
            throw new IllegalStateException(errMsg);
        }
    }

    private void validateGameInProgress(Game game, String errMsg) {
        if (!game.isInProgress()) {
            throw new IllegalStateException(errMsg);
        }
    }

}
