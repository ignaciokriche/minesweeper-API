package ar.com.kriche.minesweeper.service.game;

import ar.com.kriche.minesweeper.domain.Cell;
import ar.com.kriche.minesweeper.domain.Game;
import ar.com.kriche.minesweeper.domain.Player;
import ar.com.kriche.minesweeper.service.RandomService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static ar.com.kriche.minesweeper.domain.CellState.*;
import static ar.com.kriche.minesweeper.domain.GameState.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Game logic tests.
 *
 * @Author Kriche 2020
 */
@SpringBootTest
@Transactional
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
        Game game = getGame();
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
        Game game = getGame();
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

        Game theGame = testMove(mineLocations,
                (game) -> theTested.revealCell(game.getId(), 2, 2),
                cell -> cell.isRevealed(),
                expectedRevealed);
        assertEquals(mineLocations.size(), theGame.getRevealedCells(), "wrong count of revealed cells.");
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

        Game theGame = testMove(mineLocations,
                (game) -> theTested.revealCell(game.getId(), 3, 4),
                cell -> cell.isRevealed(),
                expectedRevealed);
        assertEquals(1, theGame.getRevealedCells(), "wrong count of revealed cells.");
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

        Game theGame = testMove(mineLocations,
                (game) -> theTested.revealCell(game.getId(), 2, 2),
                cell -> cell.isRevealed(),
                expectedRevealed);
        assertEquals(1, theGame.getRevealedCells(), "wrong count of revealed cells.");

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

        Game theGame = testMove(mineLocations,
                (game) -> theTested.revealCell(game.getId(), 3, 3),
                cell -> cell.isRevealed(),
                expectedRevealed);
        assertEquals(36, theGame.getRevealedCells(), "wrong count of revealed cells.");

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

        Game theGame = testMove(mineLocations,
                (game) -> theTested.revealCell(game.getId(), 0, 0),
                cell -> cell.isRevealed(),
                expectedRevealed);
        assertEquals(64, theGame.getRevealedCells(), "wrong count of revealed cells.");
    }

    @Test
    public void givenMinedCellWhenRevealThenGameOver() {

        // setup:
        Boolean[][] mineLocations = {
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, true, false, false},
                {false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false},
        };
        List<Boolean> mineLocationsList = Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).collect(Collectors.toList());
        int rows = mineLocations.length;
        int cols = mineLocations[0].length;
        int mines = (int) Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).filter(l -> l).count();
        int cellCount = mineLocationsList.size();

        Mockito.when(randomService.shuffledBooleans(mines, cellCount)).thenReturn(mineLocationsList);
        Game game = getGame(rows, cols, mines);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame before move:\n" + game);
        }

        // exercise:
        theTested.revealCell(game.getId(), 3, 7);

        // verify:
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame after move:\n" + game);
        }
        assertEquals(USER_LOST, game.getState(), "expecting game over");
        Mockito.verify(randomService).shuffledBooleans(game.getMines(), cellCount);

    }

    @Test
    public void givenCellsNotMinedWhenRevealThemAllThenUserWon() {

        // setup:
        Boolean[][] mineLocations = {
                {true, false, false, true, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, true, false, false},
                {false, false, true, false, false, true, false, false, false, false},
                {false, false, false, false, false, false, false, true, false, false},
                {false, true, false, false, false, false, false, true, false, false},
                {false, false, false, true, false, false, false, false, false, false},
        };
        List<Boolean> mineLocationsList = Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).collect(Collectors.toList());
        int rows = mineLocations.length;
        int cols = mineLocations[0].length;
        int mines = (int) Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).filter(l -> l).count();
        int cellCount = mineLocationsList.size();

        Mockito.when(randomService.shuffledBooleans(mines, cellCount)).thenReturn(mineLocationsList);
        Game game = getGame(rows, cols, mines);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame before move:\n" + game);
        }

        // exercise:
        boolean atLeastOneRevealed = false;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!mineLocations[r][c] && !game.cellAt(r, c).isRevealed()) {
                    atLeastOneRevealed = true;
                    theTested.revealCell(game.getId(), r, c);
                }
            }
        }

        // verify:
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame after move:\n" + game);
        }
        // defensive programming, test the test :O
        assertTrue(atLeastOneRevealed, "test did not meet exercise criteria.");
        assertEquals(USER_WON, game.getState(), "expecting game over");
        Mockito.verify(randomService).shuffledBooleans(mines, cellCount);

    }

    @Test
    public void givenCellsNotMinedWhileAtLeastOneUnrevealedThenUserHasNotWon() {

        // setup:
        Boolean[][] mineLocations = {
                {false, true, false, true, false, true, false, true, false, true, false, true, false},
                {true, false, true, false, true, false, true, false, true, false, true, false, true},
                {false, true, false, true, false, true, false, true, false, true, false, true, false},
                {true, false, true, false, true, false, true, false, true, false, true, false, true},
                {false, true, false, true, false, true, false, true, false, true, false, true, false},
                {true, false, true, false, true, false, true, false, true, false, true, false, true},
                {false, true, false, true, false, true, false, true, false, true, false, true, false},

        };
        List<Boolean> mineLocationsList = Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).collect(Collectors.toList());
        int rows = mineLocations.length;
        int cols = mineLocations[0].length;
        int mines = (int) Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).filter(l -> l).count();
        int cellCount = mineLocationsList.size();

        Mockito.when(randomService.shuffledBooleans(mines, cellCount)).thenReturn(mineLocationsList);
        Game game = getGame(rows, cols, mines);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame before move:\n" + game);
        }

        // exercise:
        boolean oneSkipped = false;
        boolean atLeastOneRevealed = false;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!mineLocations[r][c] && !game.cellAt(r, c).isRevealed()) {
                    if (!oneSkipped) {
                        oneSkipped = true;
                        continue;
                    }
                    theTested.revealCell(game.getId(), r, c);
                    atLeastOneRevealed = true;
                }
            }
        }

        // verify:
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame after move:\n" + game);
        }
        // defensive programming, test the test :O
        assertTrue(oneSkipped, "test did not meet exercise criteria.");
        assertTrue(atLeastOneRevealed, "test did not meet exercise criteria.");

        assertEquals(IN_PROGRESS, game.getState(), "expecting game over");

        Mockito.verify(randomService).shuffledBooleans(mines, cellCount);
    }

    @Test
    public void givenCellWhenRedFlaggedThenCannotReveal() {

        // setup:
        Boolean[][] mineLocations = {
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},

        };
        List<Boolean> mineLocationsList = Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).collect(Collectors.toList());
        int rows = mineLocations.length;
        int cols = mineLocations[0].length;
        int mines = (int) Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).filter(l -> l).count();
        int cellCount = mineLocationsList.size();
        int r = 2;
        int c = 2;

        Mockito.when(randomService.shuffledBooleans(mines, cellCount)).thenReturn(mineLocationsList);
        Game game = getGame(rows, cols, mines);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame before:\n" + game);
        }

        // exercise:
        theTested.markCell(game.getId(), r, c, UNREVEALED_RED_FLAG_MARK);

        // verify:
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame after:\n" + game);
        }
        try {
            theTested.revealCell(game.getId(), r, c);
        } catch (IllegalGameActionException ex) {
            // verify OK.
            Mockito.verify(randomService).shuffledBooleans(mines, cellCount);
            return;
        }
        // verify failed.
        fail("expecting illegal game action.");
    }

    @Test
    public void givenCellWhenMarkWithQuestionThenCanReveal() {

        // setup:
        Boolean[][] mineLocations = {
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},

        };
        List<Boolean> mineLocationsList = Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).collect(Collectors.toList());
        int rows = mineLocations.length;
        int cols = mineLocations[0].length;
        int mines = (int) Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).filter(l -> l).count();
        int cellCount = mineLocationsList.size();
        int r = 3;
        int c = 0;

        Mockito.when(randomService.shuffledBooleans(mines, cellCount)).thenReturn(mineLocationsList);
        Game game = getGame(rows, cols, mines);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame before:\n" + game);
        }

        // exercise:
        theTested.markCell(game.getId(), r, c, UNREVEALED_QUESTION_MARK);

        // verify:
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame after:\n" + game);
        }
        theTested.revealCell(game.getId(), r, c);
        assertTrue(game.cellAt(r, c).isRevealed(), "cell must be revealed.");
        Mockito.verify(randomService).shuffledBooleans(mines, cellCount);
    }

    @Test
    public void whenRedFlaggingCellsThenAvailableFlagsMustDecrease() {

        // setup:
        Boolean[][] mineLocations = {
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},

        };
        List<Boolean> mineLocationsList = Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).collect(Collectors.toList());
        int rows = mineLocations.length;
        int cols = mineLocations[0].length;
        int mines = (int) Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).filter(l -> l).count();
        int cellCount = mineLocationsList.size();

        Mockito.when(randomService.shuffledBooleans(mines, cellCount)).thenReturn(mineLocationsList);
        Game game = getGame(rows, cols, mines);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame before:\n" + game);
        }

        // exercise and verify:
        assertEquals(mines, game.getAvailableFlags(), "wrong count of red flags.");
        int usedFlags = 0;
        for (int r = 0; r < rows && usedFlags < mines; r++) {
            for (int c = 0; c < cols && usedFlags < mines; c++) {
                theTested.markCell(game.getId(), r, c, UNREVEALED_RED_FLAG_MARK);
                usedFlags++;
                assertEquals(mines - usedFlags, game.getAvailableFlags(), "wrong count of red flags.");
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame after:\n" + game);
        }

        Mockito.verify(randomService).shuffledBooleans(mines, cellCount);
    }

    @Test
    public void whenRemovingRedFlagThenAvailableFlagsMustIncrease() {

        // setup:
        Boolean[][] mineLocations = {
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},
                {false, true, false, true, false},

        };
        List<Boolean> mineLocationsList = Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).collect(Collectors.toList());
        int rows = mineLocations.length;
        int cols = mineLocations[0].length;
        int mines = (int) Arrays.stream(mineLocations).flatMap(row -> Arrays.stream(row)).filter(l -> l).count();
        int cellCount = mineLocationsList.size();

        Mockito.when(randomService.shuffledBooleans(mines, cellCount)).thenReturn(mineLocationsList);
        Game game = getGame(rows, cols, mines);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame before:\n" + game);
        }

        // red flag the cells:
        int usedFlags = 0;
        for (int r = 0; r < rows && usedFlags < mines; r++) {
            for (int c = 0; c < cols && usedFlags < mines; c++) {
                theTested.markCell(game.getId(), r, c, UNREVEALED_RED_FLAG_MARK);
                usedFlags++;
            }
        }

        // exercise and verify:
        assertEquals(0, game.getAvailableFlags(), "wrong count of red flags.");
        assertEquals(usedFlags, mines, "all flags should have been used.");
        for (int r = 0; r < rows && usedFlags > 0; r++) {
            for (int c = 0; c < cols && usedFlags > 0; c++) {
                theTested.markCell(game.getId(), r, c, usedFlags % 2 == 0 ? UNREVEALED_NO_MARK : UNREVEALED_QUESTION_MARK);
                usedFlags--;
                assertEquals(mines - usedFlags, game.getAvailableFlags(), "wrong count of red flags.");
            }
        }
        assertEquals(usedFlags, 0, "all flags should have been used.");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame after:\n" + game);
        }

        Mockito.verify(randomService).shuffledBooleans(mines, cellCount);
    }

    /**
     * setups a game with <code>mineLocations</code>, makes the move calling <code>moveMaker</code> and checks the
     * result against <code>expectedResults</code>.
     *
     * @param mineLocations
     * @param moveMaker
     * @param cellMapper
     * @param expectedResults
     * @param <R>
     * @return the used game.
     */
    private <R> Game testMove(List mineLocations,
                              UnaryOperator<Game> moveMaker,
                              Function<Cell, R> cellMapper,
                              R[][] expectedResults) {
        // setup:
        when(randomService.shuffledBooleans(anyInt(), anyInt())).thenReturn(mineLocations);
        Game game = getGame();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame before move:\n" + game);
        }

        // exercise:
        game = moveMaker.apply(game);

        // verify:
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\ngame after move:\n" + game);
        }
        verifyCells(expectedResults, game, cellMapper);
        Mockito.verify(randomService).shuffledBooleans(game.getMines(), mineLocations.size());

        return game;
    }

    private Game getGame() {
        return getGame(10, 10, 10);
    }

    private Game getGame(int rows, int columns, int mines) {
        return theTested.newGame(new Player("Kriche"), rows, columns, mines);
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
        assertEquals(expectedResults.length, game.getRowSize(), "wrong number of rows.");
        assertEquals(expectedResults[0].length, game.getColumnSize(), "wrong number of columns.");
        for (int r = 0; r < expectedResults.length; r++) {
            for (int c = 0; c < expectedResults[r].length; c++) {
                Cell cell = game.cellAt(r, c);
                assertEquals(expectedResults[r][c], cellMapper.apply(cell), "at row: " + r + ", col: " + c);
            }
        }
    }

}
