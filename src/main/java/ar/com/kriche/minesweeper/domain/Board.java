package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.util.RandomService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kriche 2020
 */
public class Board {

    private static final int DEFAULT_ROW_SIZE = 10;
    private static final int DEFAULT_COLUMN_SIZE = 10;
    private static final int DEFAULT_TOTAL_MINES = 7;

    private final int rowSize;
    private final int columnSize;
    private final int mines;
    private final List<BoardRow> rows;

    public Board() {
        this(DEFAULT_ROW_SIZE, DEFAULT_COLUMN_SIZE, DEFAULT_TOTAL_MINES, new RandomService());
    }

    public Board(RandomService randomService) {
        this(DEFAULT_ROW_SIZE, DEFAULT_COLUMN_SIZE, DEFAULT_TOTAL_MINES, randomService);
    }

    private Board(int rows, int columns, int mines, RandomService randomService) {

        this.rowSize = rows;
        this.columnSize = columns;
        this.mines = mines;
        this.rows = new ArrayList<>(DEFAULT_ROW_SIZE);

        // iterate the board once to instantiate the cells randomly assigning the mines:
        List<Boolean> rndBooleans = randomService.shuffledBooleans(mines, rowSize * columnSize);
        for (int r = 0, rndIndex = 0; r < rowSize; r++) {
            BoardRow row = new BoardRow(columnSize);
            for (int c = 0; c < columnSize; c++) {
                row.getCells().add(new Cell(rndBooleans.get(rndIndex++)));
            }
            this.rows.add(row);
        }

        // iterate the board again to compute adjacent mine numbers:
        for (int r = 0; r < rowSize; r++) {
            for (int c = 0; c < columnSize; c++) {
                // we don't need to compute for mined cell, however for consistency we keep it.
                // if this becomes a performance issue then mined cells could be skipped.
                Cell cell = cellAt(r, c);
                List<Cell> cellNeighbours = getCellNeighbours(r, c);
                int minedNeighbours = (int) cellNeighbours.stream().filter(n -> n.isMined()).count();
                cell.setAdjacentMines(minedNeighbours);
            }
        }

    }

    public List<BoardRow> getRows() {
        return rows;
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

    public Cell cellAt(int row, int column) {
        return rows.get(row).getCells().get(column);
    }

    private List<Cell> getCellNeighbours(int cellRow, int cellColumn) {

        // a cell will have at most 8 adjacent cells
        List<Cell> neighbours = new ArrayList<>(8);

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
                neighbours.add(cellAt(r, c));
            }
        }

        return neighbours;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        rows.forEach(r -> {
            r.getCells().forEach(cell -> stringBuilder.append(cell));
            stringBuilder.append('\n');
        });
        return stringBuilder.toString();
    }

}
