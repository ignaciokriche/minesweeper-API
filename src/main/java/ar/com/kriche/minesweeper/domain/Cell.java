package ar.com.kriche.minesweeper.domain;

import javax.persistence.*;

import static ar.com.kriche.minesweeper.domain.CellState.REVEALED;
import static ar.com.kriche.minesweeper.domain.CellState.UNREVEALED_NO_MARK;

/**
 * @Author Kriche 2020
 */
@Entity
@Table(name = "cell")
public class Cell {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean mined;
    private int adjacentMines;
    @Enumerated(EnumType.STRING)
    private CellState state;

    /**
     * ORM only.
     */
    protected Cell() {
    }

    public Cell(boolean mined) {
        this.mined = mined;
        this.state = UNREVEALED_NO_MARK;
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

    public boolean isRevealed() {
        return this.getState() == REVEALED;
    }

    @Override
    public String toString() {
        switch (getState()) {
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
        throw new Error("unknown state:" + getState());
    }

}