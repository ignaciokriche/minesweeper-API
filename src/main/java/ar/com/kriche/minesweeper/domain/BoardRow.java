package ar.com.kriche.minesweeper.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kriche 2020
 */
public class BoardRow {

    private final List<Cell> cells;

    public BoardRow(int size) {
        cells = new ArrayList<>(size);
    }

    public List<Cell> getCells() {
        return cells;
    }
}
