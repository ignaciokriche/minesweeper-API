package ar.com.kriche.minesweeper.service.player;

/**
 * @Author Kriche 2020
 */
public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
