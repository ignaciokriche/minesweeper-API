package ar.com.kriche.minesweeper.domain;

/**
 * @Author Kriche 2020
 */
public class Cell {

    private final boolean mined;
    private int adjacentMines;
    private CellState state;

    public Cell(boolean mined) {
        this.mined = mined;
        this.state = CellState.UNREVEALED;
    }

    public boolean isMined() {
        return mined;
    }

    public int getAdjacentMines() {
        return adjacentMines;
    }

    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return isMined() ? "* " : getAdjacentMines() + " ";
    }

}