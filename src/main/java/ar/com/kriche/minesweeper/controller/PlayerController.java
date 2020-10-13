package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.Player;
import ar.com.kriche.minesweeper.service.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


/**
 * API entry point for managing players.
 *
 * @Author Kriche 2020
 */
@RestController
@RequestMapping("/player")
public class PlayerController {

    private static final int MIN_USER_NAME_SIZE = 1;
    private static final int MAX_USER_NAME_SIZE = 200;

    @Autowired
    private PlayerService playerService;

    /**
     * @param userName
     * @return a new player.
     */
    @PostMapping("/{userName}")
    public Player createPlayer(
            @Min(MIN_USER_NAME_SIZE)
            @Max(MAX_USER_NAME_SIZE)
            @PathVariable("userName") String userName) {
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
