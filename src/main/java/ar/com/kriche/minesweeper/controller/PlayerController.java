package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.Player;
import ar.com.kriche.minesweeper.service.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * API entry point for managing players.
 *
 * @Author Kriche 2020
 */
@RestController
@RequestMapping("/player")
public class PlayerController {

    // TODO validations and errors.

    @Autowired
    private PlayerService playerService;

    /**
     * @param userName
     * @return a new player.
     */
    @PostMapping("/{userName}")
    public Player createPlayer(@PathVariable("userName") String userName) {
        return playerService.createPlayer(userName);
    }

    /**
     * @param userName
     * @return the existing player by <code>userName</code>.
     */
    @GetMapping("/{userName}")
    public Player getPlayerByUserName(@PathVariable("userName") String userName) {
        return playerService.getPlayerByUserName(userName);
    }

}
