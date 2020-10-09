package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.util.RandomService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;


@SpringBootTest
public class GameServiceTest {

    private static final Log LOGGER = LogFactory.getLog(GameServiceTest.class);

    @MockBean
    private RandomService randomService;

    @Autowired
    private GameService theTested;

    @Test
    public void givenEmptyMinedCellWhenRevealingThenItMustNotPropagate() {

        List mineLocations = Arrays.asList(
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, true, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false
        );

        Boolean[][] expectedRevealed = {
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, true, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
        };

        testMove(mineLocations,
                (game) -> theTested.revealCell(game, 3, 4),
                cell -> cell.isRevealed(),
                expectedRevealed);
    }

    @Test
    public void givenAllEmptyNotMinedCellsWhenRevealingThenItMustPropagateToAllCells() {

        List mineLocations = Arrays.asList(
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false
        );

        Boolean[][] expectedRevealed = {
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
        };

        testMove(mineLocations,
                (game) -> theTested.revealCell(game, 2, 2),
                cell -> cell.isRevealed(),
                expectedRevealed);
    }

    /**
     * setups a game with <code>mineLocations</code>, makes the move calling <code>moveMaker</code> and checks the
     * result against <code>expectedResults</code>.
     *
     * @param mineLocations
     * @param expectedResults
     * @param moveMaker
     * @param cellMapper
     * @param <R>
     */
    private <R> void testMove(List mineLocations,
                              UnaryOperator<Game> moveMaker,
                              Function<Cell, R> cellMapper,
                              R[][] expectedResults) {
        // setup:
        when(randomService.shuffledBooleans(anyInt(), anyInt())).thenReturn(mineLocations);
        theTested.initializeGame();
        Game game = theTested.getGame();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("game before move:\n" + game);
        }

        //exercise:
        game = moveMaker.apply(game);

        // verify:
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("game atfer move:\n" + game);
        }
        verifyCells(expectedResults, game, cellMapper);
        Mockito.verify(randomService).shuffledBooleans(game.getMines(), mineLocations.size());
    }

    /**
     * asserts that <code>expectedResults</code> dimensions are equal to <code>game</code> dimensions and that each
     * expected result matches the result of applying the <code>cellMapper</code> to each cell accordingly.
     *
     * @param expectedResults
     * @param game
     * @param cellMapper
     * @param <R>
     */
    private <R> void verifyCells(R[][] expectedResults, Game game, Function<Cell, R> cellMapper) {
        assertEquals("wrong number of rows", expectedResults.length, game.getRowSize());
        assertEquals("wrong number of columns", expectedResults[0].length, game.getColumnSize());
        for (int r = 0; r < expectedResults.length; r++) {
            for (int c = 0; c < expectedResults[r].length; c++) {
                Cell cell = game.cellAt(r, c);
                assertEquals("at row: " + r + ", col: " + c, expectedResults[r][c], cellMapper.apply(cell));
            }
        }
    }

}
