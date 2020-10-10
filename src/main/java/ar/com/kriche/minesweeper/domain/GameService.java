package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.util.RandomService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ar.com.kriche.minesweeper.domain.CellMark.RED_FLAG_MARK;
import static ar.com.kriche.minesweeper.domain.GameState.USER_LOST;


/**
 * makes the moves to the game and updates the game state accordingly.
 *
 * @Author Kriche 2020
 */
@Service
public class GameService {

    private static final Log LOGGER = LogFactory.getLog(GameService.class);

    private static final int DEFAULT_ROW_SIZE = 10;
    private static final int DEFAULT_COLUMN_SIZE = 10;
    private static final int DEFAULT_TOTAL_MINES = 7;

    private RandomService randomService;
    private Game theCurrentGame;


    @Autowired
    public GameService(RandomService randomService) {
        this.randomService = randomService;
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

    void initializeGame() {
        this.theCurrentGame = initializeGame(DEFAULT_ROW_SIZE, DEFAULT_COLUMN_SIZE, DEFAULT_TOTAL_MINES);
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
        if (cell.getMark() == RED_FLAG_MARK) {
            throw new IllegalStateException("cannot reveal a cell marked with a flag. Remove flag first.");
        }
        if (cell.isMined()) {
            cell.setRevealed(true);
            game.setState(USER_LOST);
        } else {
            revealAndPropagate(row, column, game);
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
        //TODO
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
            cell.setRevealed(true);
            cell.setMark(CellMark.NO_MARK);
            if (cell.getAdjacentMines() == 0) {
                game.getNeighbours(row, column).forEach(n -> revealAndPropagate(n.getRow(), n.getColumn(), game));
            }
        }
    }

}
