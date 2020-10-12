package ar.com.kriche.minesweeper.repository;

import ar.com.kriche.minesweeper.domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Unit tests for the player repository.
 *
 * @Author Kriche 2020
 */
@SpringBootTest
@Transactional
public class PlayerRepositoryTest {

    private static final String USER_NAME = "Ignacio";

    @Autowired
    private PlayerRepository theTestedRepo;

    @BeforeEach
    public void setup() {
        theTestedRepo.save(new Player(USER_NAME));
    }

    @Test
    public void givenValidUserNameWhenSearchingByUserNameThenPlayerMustBeFound() {
        // exercise:
        Player found = theTestedRepo.getPlayerByUserName(USER_NAME);
        // verify:
        assertEquals(USER_NAME, found.getUserName());
    }

    @Test
    public void givenInvalidUserNameWhenSearchingByUserNameThenPlayerMustNotBeFound() {
        // exercise:
        Player found = theTestedRepo.getPlayerByUserName(USER_NAME + " Krichevsky");
        // verify:
        assertNull(found);
    }

    @Test
    public void givenValidUserNameWhenCheckingExistenceThenMustBeTrue() {
        // exercise:
        boolean found = theTestedRepo.existsByUserName(USER_NAME);
        // verify:
        assertTrue(found);
    }

    @Test
    public void givenInValidUserNameWhenCheckingExistenceThenMustBeFalse() {
        // exercise:
        boolean found = theTestedRepo.existsByUserName(USER_NAME + " Krichevsky");
        // verify:
        assertFalse(found);
    }

}
