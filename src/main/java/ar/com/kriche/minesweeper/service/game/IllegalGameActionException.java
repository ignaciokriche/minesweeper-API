package ar.com.kriche.minesweeper.service.game;

/**
 * @Author Kriche 2020
 */
public class IllegalGameActionException extends RuntimeException {

    public IllegalGameActionException(String message) {
        super(message);
    }
}
