package ar.com.kriche.minesweeper.service.game;

/**
 * @Author Kriche 2020
 */
public class IllegalGameConfigurationException extends RuntimeException {

    public IllegalGameConfigurationException(String message) {
        super(message);
    }
}
