package ar.com.kriche.minesweeper.domain;

/**
 * The possible values a cell state can be.
 *
 * @Author Kriche 2020
 */
public enum CellState {
    UNREVEALED_NO_MARK,
    UNREVEALED_RED_FLAG_MARK,
    UNREVEALED_QUESTION_MARK,
    REVEALED // terminal
}
