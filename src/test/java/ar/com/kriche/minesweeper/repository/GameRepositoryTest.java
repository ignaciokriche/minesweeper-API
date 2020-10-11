package ar.com.kriche.minesweeper.repository;

import ar.com.kriche.minesweeper.domain.Game;
import ar.com.kriche.minesweeper.domain.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit tests for the game repository.
 *
 * @Author Kriche 2020
 */
@SpringBootTest
@Transactional
public class GameRepositoryTest {

    @Autowired
    private GameRepository theTestedRepo;

    @Test
    public void givenValidGameIdWhenSearchingByIdThenGameMustBeFound() {
        // setup:
        Game game = new Game(new Player("kriche"), 100, 50, 2000);
        game = theTestedRepo.save(game);
        // exercise:
        Game foundGame = theTestedRepo.getOne(game.getId());
        // verify:
        assertEquals(game, foundGame);
    }

    @Test
    public void givenInvalidGameIdWhenSearchingByIdThenNoGameMustBeFound() {
        // setup:
        Game game = new Game(new Player("ignaciokriche"), 50, 50, 50);
        game = theTestedRepo.save(game);
        // exercise, verify:
        if (theTestedRepo.findById(game.getId() + 1).isPresent()) {
            fail("expecting game not found.");
        }
    }

}
