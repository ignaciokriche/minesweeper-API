package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.Game;
import ar.com.kriche.minesweeper.domain.Player;
import ar.com.kriche.minesweeper.service.game.GameService;
import ar.com.kriche.minesweeper.service.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ar.com.kriche.minesweeper.domain.CellState.*;

/**
 * API entry point for playing the game.
 *
 * @Author Kriche 2020
 */
@RestController
@RequestMapping("/game")
@Transactional // tx here since this controller works with 2 services and we want to keep all calls within the same tx.
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    /**
     * @param userName of an existing user.
     * @return a new game.
     */
    @PostMapping("/{userName}")
    public Game createGame(@PathVariable("userName") String userName) {
        Player player = playerService.getPlayerByUserName(userName);
        return gameService.newGame(player);
    }

    /**
     * @param userName of an existing user.
     * @param rows
     * @param columns
     * @param mines
     * @return
     */
    @PostMapping("/{userName}/{rows}/{columns}/{mines}")
    public Game createCustomGame(
            @PathVariable("userName") String userName,
            @Min(1) @Max(50)
            @PathVariable("rows") int rows,
            @Min(1) @Max(50)
            @PathVariable("columns") int columns,
            @Min(0)
            @PathVariable("mines") int mines) {
        Player player = playerService.getPlayerByUserName(userName);
        return gameService.newGame(player, rows, columns, mines);
    }

    /**
     * @param id the game id.
     * @return the existing game by <code>id</code>.
     */
    @GetMapping("/{id}")
    public Game getGame(@PathVariable("id") Long id) {
        return gameService.getGame(id);
    }

    /**
     * pauses a game in progress.
     *
     * @param id the game id.
     * @return http ok.
     */
    @PatchMapping("/{id}/pause")
    public ResponseEntity pauseGame(@PathVariable("id") Long id) {
        gameService.pauseGame(id);
        return ResponseEntity.ok().build();
    }

    /**
     * resumes a paused game.
     *
     * @param id the game id.
     * @return http ok
     */
    @PatchMapping("/{id}/resume")
    public ResponseEntity resumeGame(@PathVariable("id") Long id) {
        gameService.resumeGame(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @param gameId the game id.
     * @param row    from the cell to make the move.
     * @param column from the cell to make the move
     * @param move   type of move to make.
     * @return the updated game.
     */
    @PatchMapping("/{id}/board/{row}/{column}")
    public Game makeMove(
            @PathVariable("id") Long gameId,
            @Min(0) @Max(49)
            @PathVariable("row") int row,
            @Min(0) @Max(49)
            @PathVariable("column") int column,
            @RequestBody MoveDTO move) {

        switch (move.getType()) {
            case REVEAL:
                return gameService.revealCell(gameId, row, column);
            case MARK_QUESTION:
                return gameService.markCell(gameId, row, column, UNREVEALED_QUESTION_MARK);
            case MARK_RED_FLAG:
                return gameService.markCell(gameId, row, column, UNREVEALED_RED_FLAG_MARK);
            case REMOVE_MARK:
                return gameService.markCell(gameId, row, column, UNREVEALED_NO_MARK);
            default:
                // should never happen
                throw new IllegalArgumentException("unknown type: " + move.getType());
        }
    }

}
