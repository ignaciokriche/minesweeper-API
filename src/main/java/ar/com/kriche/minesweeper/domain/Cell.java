package ar.com.kriche.minesweeper.domain;

/**
 * @Author Kriche 2020
 */
public class Cell {

    private final boolean mined;
    private int adjacentMines;
    private CellMark mark;
    private boolean revealed;

    public Cell(boolean mined) {
        this.mined = mined;
        this.revealed = false;
        this.mark = CellMark.NO_MARK;
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

    public CellMark getMark() {
        return mark;
    }

    public void setMark(CellMark mark) {
        this.mark = mark;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    @Override
    public String toString() {
        return isMined() ? "* " : getAdjacentMines() + " ";
    }

}