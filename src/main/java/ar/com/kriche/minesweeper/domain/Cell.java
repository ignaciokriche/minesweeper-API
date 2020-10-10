package ar.com.kriche.minesweeper.domain;

import static ar.com.kriche.minesweeper.domain.CellMark.REVEALED;
import static ar.com.kriche.minesweeper.domain.CellMark.UNREVEALED_NO_MARK;

/**
 * @Author Kriche 2020
 */
public class Cell {

    private final boolean mined;
    private int adjacentMines;
    private CellMark mark;

    public Cell(boolean mined) {
        this.mined = mined;
        this.mark = UNREVEALED_NO_MARK;
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
        return this.getMark() == REVEALED;
    }

    @Override
    public String toString() {
        switch (getMark()) {
            case UNREVEALED_NO_MARK:
                return "* ";
            case UNREVEALED_RED_FLAG_MARK:
                return "F ";
            case UNREVEALED_QUESTION_MARK:
                return "? ";
            case REVEALED:
                if (isMined()) {
                    return "M ";
                }
                return getAdjacentMines() + " ";
        }
        throw new Error("unknown mark:" + getMark());
    }

}