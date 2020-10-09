package ar.com.kriche.minesweeper.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.com.kriche.minesweeper.util.RandomService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
public class GameTest {

    private static final Log LOGGER = LogFactory.getLog(GameTest.class);

    @MockBean
    private RandomService randomService;

    @Test
    public void adjacentMinesNumbersMustBeConsistentWithExistingMines() {

        // setup:

        int rowLength = 10;
        int columnLength = 10;
        int mines = 7;

        List mineLocations = Arrays.asList(
                false, false, false, false, true, false, false, false, false, true,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, true, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, true, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, true,
                false, false, false, true, false, false, false, false, false, false,
                false, false, true, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false
        );

        // note: we also check mine count for mined cells.
        int[] expectedMineCounts = {
                0, 0, 0, 1, 0, 1, 0, 0, 1, 0,
                0, 0, 0, 2, 2, 2, 0, 0, 1, 1,
                0, 0, 0, 1, 0, 1, 0, 0, 0, 0,
                0, 1, 1, 2, 1, 1, 0, 0, 0, 0,
                0, 1, 0, 1, 0, 0, 0, 0, 0, 0,
                0, 1, 1, 1, 0, 0, 0, 0, 1, 1,
                0, 0, 1, 1, 1, 0, 0, 0, 1, 0,
                0, 1, 2, 1, 1, 0, 0, 0, 1, 1,
                0, 1, 1, 2, 1, 0, 0, 0, 0, 0,
                0, 1, 1, 1, 0, 0, 0, 0, 0, 0,
        };

        Mockito.when(randomService.shuffledBooleans(mines, rowLength * columnLength)).thenReturn(mineLocations);

        // exercise:
        Game actualGame = new Game(randomService);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("actual game:\n" + actualGame);
        }

        // verify:
        for (int r = 0; r < rowLength; r++) {
            for (int c = 0; c < columnLength; c++) {
                Cell actualCell = actualGame.cellAt(r, c);
                assertEquals("at row: " + r + ", col: " + c,
                        expectedMineCounts[r * columnLength + c], actualCell.getAdjacentMines());
            }
        }
        Mockito.verify(randomService).shuffledBooleans(mines, rowLength * columnLength);

    }

    @Test
    public void givenMineLocationsWhenCreatingGameThenMinesMustInTheRightCells() {

        // setup:

        int rowLength = 10;
        int columnLength = 10;
        int mines = 7;

        List mineLocations = Arrays.asList(
                true, false, false, false, false, false, false, false, false, false,
                true, false, false, false, false, false, false, false, true, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, true, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, true, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, true, false, false,
                false, false, false, false, true, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false
        );

        Mockito.when(randomService.shuffledBooleans(mines, rowLength * columnLength)).thenReturn(mineLocations);

        // exercise:
        Game actualGame = new Game(randomService);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("actual game:\n" + actualGame);
        }


        // verify:
        for (int r = 0; r < rowLength; r++) {
            for (int c = 0; c < columnLength; c++) {
                Cell actualCell = actualGame.cellAt(r, c);
                assertEquals("at row: " + r + ", col: " + c,
                        mineLocations.get(r * columnLength + c), actualCell.isMined());
            }
        }
        Mockito.verify(randomService).shuffledBooleans(mines, rowLength * columnLength);

    }

}