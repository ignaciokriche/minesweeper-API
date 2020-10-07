package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.util.RandomHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kriche 2020
 */
public class Board {

    static private final int DEFAULT_ROW_SIZE = 5;
    static private final int DEFAULT_COLUMN_SIZE = 5;
    static private final float DEFAULT_MINE_PROBABILITY = 0.1f;

    private List<BoardRow> rows = new ArrayList<>(DEFAULT_ROW_SIZE);

    public Board() {

        // iterate the board once to instantiate the cells:
        for (int r = 0; r < DEFAULT_ROW_SIZE; r++) {
            BoardRow row = new BoardRow(DEFAULT_COLUMN_SIZE);
            for (int c = 0; c < DEFAULT_COLUMN_SIZE; c++) {
                row.getColumns().add(new Cell(RandomHelper.nextBoolean(DEFAULT_MINE_PROBABILITY)));
            }
            rows.add(row);
        }

        // iterate the board again to compute adjacent mines:
        for (int r = 0; r < DEFAULT_ROW_SIZE; r++) {
            for (int c = 0; c < DEFAULT_COLUMN_SIZE; c++) {
                //TODO do we need to compute for mined cell?
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
        int upperRow = Math.min(DEFAULT_ROW_SIZE - 1, cellRow + 1);
        int lowerColumn = Math.max(0, cellColumn - 1);
        int upperColumn = Math.min(DEFAULT_COLUMN_SIZE - 1, cellColumn + 1);

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
            r.getColumns().forEach(cell -> {
                stringBuilder.append(cell.toString());
            });
            stringBuilder.append('\n');
        });
        return stringBuilder.toString();
    }

}
