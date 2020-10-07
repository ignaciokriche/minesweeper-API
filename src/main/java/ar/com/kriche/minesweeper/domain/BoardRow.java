package ar.com.kriche.minesweeper.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kriche 2020
 */
public class BoardRow {

    private List<Cell> columns;

    public BoardRow(int size) {
        columns = new ArrayList<>(size);
    }

    public List<Cell> getColumns() {
        return columns;
    }
}
