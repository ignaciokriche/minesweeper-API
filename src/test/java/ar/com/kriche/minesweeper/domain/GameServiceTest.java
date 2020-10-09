package ar.com.kriche.minesweeper.domain;

import ar.com.kriche.minesweeper.util.RandomService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;


@SpringBootTest
public class GameServiceTest {

    private static final Log LOGGER = LogFactory.getLog(GameServiceTest.class);

    @MockBean
    private RandomService randomService;

    @Autowired
    private GameService theTested;


    @Test
    public void givenEmptyNotMinedCellWhenRevealingThenItMustPropagate() {

        // setup:

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
        when(randomService.shuffledBooleans(anyInt(), anyInt())).thenReturn(mineLocations);

        // exercise:
        Game game = theTested.getGame();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("actual game:\n" + game);
        }
        theTested.revealCell(game, 9, 0);

        // verify:

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("actual game:\n" + game);
        }

        // note: we also check mine count for mined cells.
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

        GameVerifier.verifyCells(expectedRevealed, game, cell -> cell.isRevealed());
        Mockito.verify(randomService).shuffledBooleans(game.getMines(), mineLocations.size());

    }

}
