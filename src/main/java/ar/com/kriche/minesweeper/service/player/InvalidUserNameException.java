package ar.com.kriche.minesweeper.service.player;

/**
 * @Author Kriche 2020
 */
public class InvalidUserNameException extends RuntimeException {

    public InvalidUserNameException(String message) {
        super(message);
    }
}
