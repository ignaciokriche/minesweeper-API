package ar.com.kriche.minesweeper.domain;

import org.junit.jupiter.api.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.junit.jupiter.api.Assertions.fail;

public class BoardTest {

    private static final Log LOGGER = LogFactory.getLog(BoardTest.class);

    @Test
    void adjacentMinesNumbersMustBeConsistentWithExistingMines() {
        Board theTestedBoard = new Board();
        fail("work in progress");
    }

}
