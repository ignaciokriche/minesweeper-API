package ar.com.kriche.minesweeper.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * Represents a minesweeper board.
 *
 * @Author Kriche 2020
 */
public class Game {

    private final int rowSize;
    private final int columnSize;
    private final int mines;
    private int availableFlags;
    private final List<BoardRow> board;
    private GameState state;

    public Game(int rows, int columns, int mines) {
        this.rowSize = rows;
        this.columnSize = columns;
        this.mines = mines;
        this.availableFlags = mines;
        this.board = new ArrayList<>(rows);
        this.state = GameState.IN_PROGRESS;
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

    public int getAvailableFlags() {
        return availableFlags;
    }

    public void setAvailableFlags(int availableFlags) {
        this.availableFlags = availableFlags;
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

    public Stream<CellCoordinate> getNeighbours(int cellRow, int cellColumn) {

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


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        board.forEach(r -> {
            r.getCells().forEach(cell -> stringBuilder.append(cell));
            stringBuilder.append('\n');
        });
        return stringBuilder.toString();
    }

    public void decreaseAvailableFlags() {
        setAvailableFlags(getAvailableFlags() - 1);
    }

    public void increaseAvailableFlags() {
        setAvailableFlags(getAvailableFlags() + 1);
    }
}
