package ar.com.kriche.minesweeper.domain;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

/**
 * @Author Kriche 2020
 */
public class GameVerifier {

    /**
     * asserts that <code>expectedValues</code> dimensions are equal to <code>game</code> dimensions and that each
     * expected value matches the result of applying the <code>cellMapper</code> to each cell accordingly.
     *
     * @param expectedValues
     * @param game
     * @param cellMapper
     * @param <T>
     */
    public static <T> void verifyCells(T[][] expectedValues, Game game, Function<Cell, T> cellMapper) {
        assertEquals("wrong number of rows", expectedValues.length, game.getRowSize());
        assertEquals("wrong number of columns", expectedValues[0].length, game.getColumnSize());
        for (int r = 0; r < expectedValues.length; r++) {
            for (int c = 0; c < expectedValues[r].length; c++) {
                Cell cell = game.cellAt(r, c);
                assertEquals("at row: " + r + ", col: " + c, expectedValues[r][c], cellMapper.apply(cell));
            }
        }
    }

}
