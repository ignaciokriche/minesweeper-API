package ar.com.kriche.minesweeper.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{id}")
    public String getGame(@PathVariable("id") Long id) {
        return "work in progress " + id;
    }
}
