package ar.com.kriche.minesweeper.service.game;

/**
 * @Author Kriche 2020
 */
public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(String message) {
        super(message);
    }

}
