package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.Game;
import ar.com.kriche.minesweeper.domain.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API entry point for playing the game.
 *
 * @Author Kriche 2020
 */
@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping
    public Game getGame() {
        return gameService.getGame();
    }

}
