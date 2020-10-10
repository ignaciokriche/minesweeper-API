package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.repository.GameRepository;
import ar.com.kriche.minesweeper.util.RandomService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ar.com.kriche.minesweeper.domain.CellState.REVEALED;
import static ar.com.kriche.minesweeper.domain.CellState.UNREVEALED_RED_FLAG_MARK;
import static ar.com.kriche.minesweeper.domain.GameState.USER_LOST;
import static ar.com.kriche.minesweeper.domain.GameState.USER_WON;


/**
 * makes the moves to the game and updates the game state accordingly.
 *
 * @Author Kriche 2020
 */
@Service
@Transactional
public class GameService {

    private static final Log LOGGER = LogFactory.getLog(GameService.class);

    private static final int DEFAULT_ROW_SIZE = 10;
    private static final int DEFAULT_COLUMN_SIZE = 10;
    private static final int DEFAULT_TOTAL_MINES = 7;

    private RandomService randomService;
    private GameRepository gameRepo;

    @Autowired
    public GameService(RandomService randomService, GameRepository gameRepository) {
        this.randomService = randomService;
        this.gameRepo = gameRepository;
    }

    /**
     * @return a newly created game.
     */
    public Game newGame() {
        Game theGame = initializeGame();
        theGame = gameRepo.save(theGame);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\nreturning new game:\n" + theGame);
        }
        return theGame;
    }


    /**
     * @param gameId
     * @return
     */
    public Game getGame(Long gameId) {
        Game theGame = gameRepo.getOne(gameId);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\nreturning existing game:\n" + theGame);
        }
        return theGame;
    }

    /**
     * @param gameId
     * @param row
     * @param column
     * @return
     */
    public Game revealCell(Long gameId, int row, int column) {
        LOGGER.debug("reveal cell [" + row + "," + column + "].");
        Game game = gameRepo.getOne(gameId);
        validateGameInProgress(game, "cannot reveal a cell of a game not in progress.");
        Cell cell = game.cellAt(row, column);
        validateCellNotRevealed(cell, "cannot reveal a cell already revealed.");
        if (cell.getState() == UNREVEALED_RED_FLAG_MARK) {
            throw new IllegalStateException("cannot reveal a cell marked with a flag. Remove flag first.");
        }
        if (cell.isMined()) {
            markCellAndUpdateGameCounters(REVEALED, cell, game);
            game.setState(USER_LOST);
            LOGGER.info("game over!");
        } else {
            revealAndPropagate(row, column, game);
            if (game.getRevealedCells() == game.getRowSize() * game.getColumnSize() - game.getMines()) {
                game.setState(USER_WON);
                LOGGER.info("user won!");
            }
        }
        return game;
    }

    /**
     * @param gameId
     * @param row
     * @param column
     * @return
     */
    public Game markCell(Long gameId, int row, int column, CellState mark) {
        LOGGER.debug("mark cell [" + row + "," + column + "] with: " + mark);
        Game game = gameRepo.getOne(gameId);
        validateGameInProgress(game, "cannot mark cell of a game not in progress.");
        Cell cell = game.cellAt(row, column);
        validateCellNotRevealed(cell, "cannot modify a revealed cell.");
        if (cell.getState() == mark) {
            throw new IllegalStateException("cell already marked as: " + mark);
        }
        markCellAndUpdateGameCounters(mark, cell, game);
        return game;
    }

    private Game initializeGame() {
        return initializeGame(DEFAULT_ROW_SIZE, DEFAULT_COLUMN_SIZE, DEFAULT_TOTAL_MINES);
    }

    private Game initializeGame(int rowSize, int columnSize, int mines) {

        Game game = new Game(rowSize, columnSize, mines);

        // iterate the board once to instantiate the cells randomly assigning the mines:
        List<Boolean> rndBooleans = randomService.shuffledBooleans(mines, rowSize * columnSize);
        for (int r = 0, rndIndex = 0; r < rowSize; r++) {
            BoardRow row = new BoardRow();
            for (int c = 0; c < columnSize; c++) {
                row.getCells().add(new Cell(rndBooleans.get(rndIndex++)));
            }
            game.getBoard().add(row);
        }

        // iterate the board again to compute adjacent mine numbers:
        for (int r = 0; r < rowSize; r++) {
            for (int c = 0; c < columnSize; c++) {
                // we don't need to compute for mined cell, however for data completeness we keep it.
                // if this becomes a performance issue then mined cells could be skipped.
                int minedNeighbours = (int) game.getNeighbours(r, c).
                        map(coords -> game.cellAt(coords.getRow(), coords.getColumn())).
                        filter(n -> n.isMined()).
                        count();
                game.cellAt(r, c).setAdjacentMines(minedNeighbours);
            }
        }

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

    /**
     * if the cell at <code>row</code>, <code>column</code> is not revealed then reveal it and remove its mark.
     * Repeats for cell's neighbours if cell has no adjacent mines.
     *
     * @param row
     * @param column
     */
    private void revealAndPropagate(int row, int column, Game game) {
        Cell cell = game.cellAt(row, column);
        if (!cell.isRevealed()) {
            markCellAndUpdateGameCounters(REVEALED, cell, game);
            if (cell.getAdjacentMines() == 0) {
                game.getNeighbours(row, column).forEach(n -> revealAndPropagate(n.getRow(), n.getColumn(), game));
            }
        }
    }

    /**
     * updates cell state and available flags and revealed cell counters accordingly.
     * use only this method to mutate a cell mark!
     *
     * @param state
     * @param cell
     * @param game
     */
    private void markCellAndUpdateGameCounters(CellState state, Cell cell, Game game) {

        if (state == cell.getState()) {
            return;
        }

        switch (state) {

            case UNREVEALED_RED_FLAG_MARK:
                if (game.getAvailableFlags() == 0) {
                    throw new IllegalStateException("No available flags.");
                }
                // setting a flag decreases available flags.
                game.decreaseAvailableFlags();
                break;

            case REVEALED:
                // revealing increases revealed cells.
                game.increaseRevealedCells();
            case UNREVEALED_NO_MARK:
            case UNREVEALED_QUESTION_MARK:
                // for these 3 cases above if there was a flag then available flags must increase.
                if (cell.getState() == UNREVEALED_RED_FLAG_MARK) {
                    game.increaseAvailableFlags();
                }
                break;

            default:
                throw new Error("unknown state:" + state);
        }

        cell.setState(state);

    }

}
