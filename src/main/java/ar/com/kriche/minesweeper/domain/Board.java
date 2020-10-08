package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.util.RandomHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kriche 2020
 */
public class Board {

    private static final int DEFAULT_ROW_SIZE = 10;
    private static final int DEFAULT_COLUMN_SIZE = 10;
    private static final int DEFAULT_TOTAL_MINES = 5;

    private final int rowLength;
    private final int columnLength;
    private final int totalMines;
    private final List<BoardRow> rows;

    public Board() {
        this(DEFAULT_ROW_SIZE, DEFAULT_COLUMN_SIZE, DEFAULT_TOTAL_MINES);
    }

    private Board(int rowLength, int columnLength, int totalMines) {

        this.rowLength = rowLength;
        this.columnLength = columnLength;
        this.totalMines = totalMines;
        this.rows = new ArrayList<>(DEFAULT_ROW_SIZE);

        // iterate the board once to instantiate the cells randomly assigning the mines:
        List<Boolean> rndBooleans = RandomHelper.shuffledBooleans(totalMines, rowLength * columnLength);
        for (int r = 0, rndIndex = 0; r < rowLength; r++) {
            BoardRow row = new BoardRow(columnLength);
            for (int c = 0; c < columnLength; c++) {
                row.getColumns().add(new Cell(rndBooleans.get(rndIndex++)));
            }
            rows.add(row);
        }

        // iterate the board again to compute adjacent mine numbers:
        for (int r = 0; r < rowLength; r++) {
            for (int c = 0; c < columnLength; c++) {
                // we don't need to compute for mined cell, however for consistency we keep it.
                // if this becomes a performance issue then mined cells could be skipped.
                Cell cell = cellAt(r, c);
                List<Cell> cellNeighbours = getCellNeighbours(r, c);
                int minedNeighbours = (int) cellNeighbours.stream().filter(n -> n.isMined()).count();
                cell.setAdjacentMines(minedNeighbours);
            }
        }

    }

    private List<Cell> getCellNeighbours(int cellRow, int cellColumn) {

        // a cell will have at most 8 adjacent cells
        List<Cell> neighbours = new ArrayList<>(8);

        // for border cases:
        int lowerRow = Math.max(0, cellRow - 1);
        int upperRow = Math.min(rowLength - 1, cellRow + 1);
        int lowerColumn = Math.max(0, cellColumn - 1);
        int upperColumn = Math.min(columnLength - 1, cellColumn + 1);

        for (int r = lowerRow; r <= upperRow; r++) {
            for (int c = lowerColumn; c <= upperColumn; c++) {
                neighbours.add(cellAt(r, c));
            }
        }

        return neighbours;
    }

    private Cell cellAt(int row, int column) {
        return rows.get(row).getColumns().get(column);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        rows.forEach(r -> {
            r.getColumns().forEach(cell -> stringBuilder.append(cell));
            stringBuilder.append('\n');
        });
        return stringBuilder.toString();
    }

}
