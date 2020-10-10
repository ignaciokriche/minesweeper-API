package ar.com.kriche.minesweeper.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kriche 2020
 */
public class BoardRow {

    private final List<Cell> cells = new ArrayList<>();

    public List<Cell> getCells() {
        return cells;
    }
}
