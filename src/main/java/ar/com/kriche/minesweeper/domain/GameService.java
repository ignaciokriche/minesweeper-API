package ar.com.kriche.minesweeper.domain;

import org.springframework.stereotype.Service;

/**
 * @Author Kriche 2020
 */
@Service
public class GameService {

    private static Game theCurrentGame = new Game();

    public Game getGame() {
        return theCurrentGame;
    }

}
