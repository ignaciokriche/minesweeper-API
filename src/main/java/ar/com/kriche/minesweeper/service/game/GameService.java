package ar.com.kriche.minesweeper.service.game;

import ar.com.kriche.minesweeper.domain.*;
import ar.com.kriche.minesweeper.repository.GameRepository;
import ar.com.kriche.minesweeper.service.RandomService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ar.com.kriche.minesweeper.domain.CellState.REVEALED;
import static ar.com.kriche.minesweeper.domain.CellState.UNREVEALED_RED_FLAG_MARK;
import static ar.com.kriche.minesweeper.domain.GameState.*;


/**
 * makes the moves to the game and updates the game state accordingly.
 *
 * @Author Kriche 2020
 */
@Service
@Transactional
public class GameService {

    private static final Log LOGGER = LogFactory.getLog(GameService.class);

    @Value("${service.game.gameService.defaultRowSize}")
    private int defaultRowSize;
    @Value("${service.game.gameService.defaultColumnSize}")
    private int defaultColumnSize;
    @Value("${service.game.gameService.defaultMines}")
    private int defaultMines;
    @Value("${service.game.gameService.maxRowSize}")
    private int maxRowSize;
    @Value("${service.game.gameService.maxColumnSize}")
    private int maxColumnSize;

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
    public Game newGame(Player owner) {
        return newGame(owner, defaultRowSize, defaultColumnSize, defaultMines);
    }

    /**
     * @param rows
     * @param columns
     * @param mines
     * @return a newly created game with the given parameters.
     */
    public Game newGame(Player owner, int rows, int columns, int mines) {
        Game theGame = initializeGame(owner, rows, columns, mines);
        owner.addGame(theGame);
        theGame = gameRepo.save(theGame);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\nreturning new game:\n" + theGame);
        }
        return theGame;
    }

    /**
     * @param gameId
     * @return the game with id: <code>gameId</code>
     */
    public Game getGame(Long gameId) {
        Game theGame = validateAndGetGame(gameId);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\nreturning existing game:\n" + theGame);
        }
        return theGame;
    }

    /**
     * @param gameId
     */
    public void pauseGame(Long gameId) {
        LOGGER.debug("pausing game with id: " + gameId);
        Game game = validateAndGetGame(gameId);
        validateGameInProgress(game, "cannot pause a game not in progress.");
        game.setState(PAUSED);
    }

    /**
     * @param gameId
     */
    public void resumeGame(Long gameId) {
        LOGGER.debug("resuming game with id: " + gameId);
        Game game = validateAndGetGame(gameId);
        validateGamePaused(game, "cannot resume a game not paused");
        game.setState(IN_PROGRESS);
    }

    /**
     * @param gameId
     * @param row
     * @param column
     * @return
     */
    public Game revealCell(Long gameId, int row, int column) {
        LOGGER.debug("reveal cell [" + row + "," + column + "].");
        Game game = validateAndGetGame(gameId);
        validateGameInProgress(game, "cannot reveal a cell of a game not in progress.");
        Cell cell = game.cellAt(row, column);
        validateCellNotRevealed(cell, "cannot reveal a cell already revealed.");
        if (cell.getState() == UNREVEALED_RED_FLAG_MARK) {
            throw new IllegalGameActionException("cannot reveal a cell marked with a flag. Remove flag first.");
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame:\n" + game);
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
        Game game = validateAndGetGame(gameId);
        validateGameInProgress(game, "cannot mark cell of a game not in progress.");
        Cell cell = game.cellAt(row, column);
        validateCellNotRevealed(cell, "cannot modify a revealed cell.");
        if (cell.getState() == mark) {
            throw new IllegalGameActionException("cell already marked as: " + mark);
        }
        markCellAndUpdateGameCounters(mark, cell, game);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame:\n" + game);
        }
        return game;
    }

    private Game initializeGame(Player owner, int rowSize, int columnSize, int mines) {

        validateGameParameters(rowSize, columnSize, mines);

        Game game = new Game(owner, rowSize, columnSize, mines);

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

    private void validateGameParameters(int rowSize, int columnSize, int mines) {
        if (rowSize <= 0 || rowSize > maxRowSize) {
            throw new IllegalGameConfigurationException("invalid rows parameter.");
        }
        if (columnSize <= 0 || columnSize > maxColumnSize) {
            throw new IllegalGameConfigurationException("invalid columns parameter.");
        }
        if (mines < 0 || mines > rowSize * columnSize) {
            throw new IllegalGameConfigurationException("invalid mines parameter.");
        }
    }

    private Game validateAndGetGame(Long gameId) {
        Optional<Game> game = gameRepo.findById(gameId);
        if (!game.isPresent()) {
            throw new GameNotFoundException("game not found.");
        }
        return game.get();
    }

    private void validateCellNotRevealed(Cell cell, String errMsg) {
        if (cell.isRevealed()) {
            throw new IllegalGameActionException(errMsg);
        }
    }

    private void validateGameInProgress(Game game, String errMsg) {
        if (!game.isInProgress()) {
            throw new IllegalGameActionException(errMsg);
        }
    }

    private void validateGamePaused(Game game, String errMsg) {
        if (!game.isPaused()) {
            throw new IllegalGameActionException(errMsg);
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
                    throw new IllegalGameActionException("No available flags.");
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
                //should never happen
                throw new Error("unknown state:" + state);
        }

        cell.setState(state);

    }

}
