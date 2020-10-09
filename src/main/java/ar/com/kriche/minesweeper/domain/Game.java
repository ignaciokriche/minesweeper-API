package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.util.RandomService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * Represents a minesweeper game.
 * Knows its board and it states.
 *
 * @Author Kriche 2020
 */
public class Game {

    private static final int DEFAULT_ROW_SIZE = 10;
    private static final int DEFAULT_COLUMN_SIZE = 10;
    private static final int DEFAULT_TOTAL_MINES = 7;
    private static final Log LOGGER = LogFactory.getLog(Game.class);

    private final int rowSize;
    private final int columnSize;
    private final int mines;
    private final List<BoardRow> board;
    private GameState state;


    // TODO better a game builder?

    public Game() {
        this(new RandomService());
    }

    public Game(RandomService randomService) {
        this(DEFAULT_ROW_SIZE, DEFAULT_COLUMN_SIZE, DEFAULT_TOTAL_MINES, randomService);
    }

    private Game(int rows, int columns, int mines, RandomService randomService) {

        this.rowSize = rows;
        this.columnSize = columns;
        this.mines = mines;
        this.board = new ArrayList<>(rows);
        this.state = GameState.IN_PROGRESS;

        // iterate the board once to instantiate the cells randomly assigning the mines:
        List<Boolean> rndBooleans = randomService.shuffledBooleans(mines, rowSize * columnSize);
        for (int r = 0, rndIndex = 0; r < rowSize; r++) {
            BoardRow row = new BoardRow(columnSize);
            for (int c = 0; c < columnSize; c++) {
                row.getCells().add(new Cell(rndBooleans.get(rndIndex++)));
            }
            this.board.add(row);
        }

        // iterate the board again to compute adjacent mine numbers:
        for (int r = 0; r < rowSize; r++) {
            for (int c = 0; c < columnSize; c++) {
                // we don't need to compute for mined cell, however for data completeness we keep it.
                // if this becomes a performance issue then mined cells could be skipped.
                int minedNeighbours = (int) getNeighbours(r, c).
                        map(coords -> cellAt(coords.getRow(), coords.getColumn())).
                        filter(n -> n.isMined()).
                        count();
                cellAt(r, c).setAdjacentMines(minedNeighbours);
            }
        }

    }

    public int getRowSize() {
        return rowSize;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getMines() {
        return mines;
    }

    public List<BoardRow> getBoard() {
        return board;
    }

    public Cell cellAt(int row, int column) {
        return board.get(row).getCells().get(column);
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    public boolean isInProgress() {
        return getState() == GameState.IN_PROGRESS;
    }

    private Stream<CellCoordinate> getNeighbours(int cellRow, int cellColumn) {

        Stream.Builder<CellCoordinate> neighbours = Stream.builder();

        // for border cases:
        int lowerRow = Math.max(0, cellRow - 1);
        int upperRow = Math.min(rowSize - 1, cellRow + 1);
        int lowerColumn = Math.max(0, cellColumn - 1);
        int upperColumn = Math.min(columnSize - 1, cellColumn + 1);

        for (int r = lowerRow; r <= upperRow; r++) {
            for (int c = lowerColumn; c <= upperColumn; c++) {
                if (r == cellRow && c == cellColumn) {
                    continue; // skip self
                }
                neighbours.accept(new CellCoordinate(r, c));
            }
        }

        return neighbours.build();
    }

    /**
     * if the cell at <code>row</code>, <code>column</code> is not revealed then reveal it.
     * Repeats for cell's neighbours if cell has no adjacent mines.
     *
     * @param row
     * @param column
     */
    public void revealAndPropagate(int row, int column) {
        Cell cell = cellAt(row, column);
        if (!cell.isRevealed()) {
            cell.setRevealed(true);
            if (cell.getAdjacentMines() == 0) {
                getNeighbours(row, column).forEach(n -> revealAndPropagate(n.getRow(), n.getColumn()));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        board.forEach(r -> {
            r.getCells().forEach(cell -> stringBuilder.append(cell));
            stringBuilder.append('\n');
        });
        return stringBuilder.toString();
    }

}
