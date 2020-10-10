package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.util.RandomService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


@SpringBootTest
public class GameServiceTest {

    private static final Log LOGGER = LogFactory.getLog(GameServiceTest.class);

    @MockBean
    private RandomService randomService;

    @Autowired
    private GameService theTested;


    @Test
    public void adjacentMinesNumbersMustBeConsistentWithExistingMines() {

        // setup:

        List mineLocations = Arrays.asList(
                false, false, false, false, true, false, false, false, false, true,
                false, false, false, false, false, false, false, false, false, false,
                true, false, false, false, true, false, false, false, false, false,
                false, false, false, true, false, false, false, false, false, false,
                false, false, true, true, false, false, false, false, false, true,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, true, false, false, false, false, false, true,
                false, false, false, true, false, false, false, false, true, false,
                false, false, true, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false
        );

        // note: we also check mine count for mined cells.
        Integer[][] expectedMineCounts = {
                {0, 0, 0, 1, 0, 1, 0, 0, 1, 0},
                {1, 1, 0, 2, 2, 2, 0, 0, 1, 1},
                {0, 1, 1, 2, 1, 1, 0, 0, 0, 0},
                {1, 2, 3, 3, 3, 1, 0, 0, 1, 1},
                {0, 1, 2, 2, 2, 0, 0, 0, 1, 0},
                {0, 1, 3, 3, 2, 0, 0, 0, 2, 2},
                {0, 0, 2, 1, 2, 0, 0, 1, 2, 1},
                {0, 1, 3, 2, 2, 0, 0, 1, 1, 2},
                {0, 1, 1, 2, 1, 0, 0, 1, 1, 1},
                {0, 1, 1, 1, 0, 0, 0, 0, 0, 0}
        };

        Mockito.when(randomService.shuffledBooleans(anyInt(), anyInt())).thenReturn(mineLocations);

        // exercise:
        theTested.initializeGame();
        Game game = theTested.getGame();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame:\n" + game);
        }

        // verify:
        verifyCells(expectedMineCounts, game, c -> c.getAdjacentMines());
        Mockito.verify(randomService).shuffledBooleans(game.getMines(), mineLocations.size());

    }

    @Test
    public void givenMineLocationsWhenCreatingGameThenMinesMustInTheRightCells() {

        // setup:

        Boolean[][] minesLocation = {
                {true, false, false, false, false, false, false, false, false, false},
                {true, false, false, false, false, false, false, false, true, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, true, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, true, false, true, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, true, false, false},
                {false, false, false, false, true, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, true},
        };

        List minesLocationFlatted = Arrays.stream(minesLocation).flatMap(row -> Arrays.stream(row)).collect(Collectors.toList());
        Mockito.when(randomService.shuffledBooleans(anyInt(), anyInt())).thenReturn(minesLocationFlatted);

        // exercise:
        theTested.initializeGame();
        Game game = theTested.getGame();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("actual game:\n" + game);
        }

        verifyCells(minesLocation, game, c -> c.isMined());
        Mockito.verify(randomService).shuffledBooleans(game.getMines(), minesLocationFlatted.size());

    }


    @Test
    public void givenNoMinesWhenRevealingThenItMustPropagateToAllCells() {

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

    @Test
    public void givenMinedCellWhenRevealingThenItMustNotPropagate() {

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
    public void givenMinedBorderWhenRevealingCellWithAdjacentMineThenItMustNotPropagate() {

        List mineLocations = Arrays.asList(
                true, true, true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true, true, true
        );

        Boolean[][] expectedRevealed = {
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, true, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},

        };

        testMove(mineLocations,
                (game) -> theTested.revealCell(game, 2, 2),
                cell -> cell.isRevealed(),
                expectedRevealed);
    }

    @Test
    public void givenRectangleWithNoMineNoAdjacentCellsWhenRevealingThenItMustPropagateWithinThatRectangle() {

        List mineLocations = Arrays.asList(
                true, true, true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, false, false, false, false, false, false, true, true,
                true, true, true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true, true, true
        );

        Boolean[][] expectedRevealed = {
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, true, true, true, true, true, true, false, false},
                {false, false, true, true, true, true, true, true, false, false},
                {false, false, true, true, true, true, true, true, false, false},
                {false, false, true, true, true, true, true, true, false, false},
                {false, false, true, true, true, true, true, true, false, false},
                {false, false, true, true, true, true, true, true, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false}
        };

        testMove(mineLocations,
                (game) -> theTested.revealCell(game, 3, 3),
                cell -> cell.isRevealed(),
                expectedRevealed);
    }

    @Test
    public void givenBorderWithNoMineNoAdjacentCellsWhenRevealingCornerCellThenItMustPropagateWithinThatBorder() {

        List mineLocations = Arrays.asList(
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, true, true, true, true, true, true, false, false,
                false, false, true, true, true, true, true, true, false, false,
                false, false, true, true, true, true, true, true, false, false,
                false, false, true, true, true, true, true, true, false, false,
                false, false, true, true, true, true, true, true, false, false,
                false, false, true, true, true, true, true, true, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false
        );

        Boolean[][] expectedRevealed = {
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, false, false, false, false, false, false, true, true},
                {true, true, false, false, false, false, false, false, true, true},
                {true, true, false, false, false, false, false, false, true, true},
                {true, true, false, false, false, false, false, false, true, true},
                {true, true, false, false, false, false, false, false, true, true},
                {true, true, false, false, false, false, false, false, true, true},
                {true, true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true, true}
        };

        testMove(mineLocations,
                (game) -> theTested.revealCell(game, 0, 0),
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
            LOGGER.debug("\ngame before move:\n" + game);
        }

        //exercise:
        game = moveMaker.apply(game);

        // verify:
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame after move:\n" + game);
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
