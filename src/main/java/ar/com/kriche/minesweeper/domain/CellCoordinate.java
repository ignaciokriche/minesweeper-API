package ar.com.kriche.minesweeper.domain;

/**
 * @Author Kriche 2020
 */

public class CellCoordinate {

    private int row, column;

    public CellCoordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}
