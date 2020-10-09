package ar.com.kriche.minesweeper.domain;


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
