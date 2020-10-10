package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.Game;
import ar.com.kriche.minesweeper.domain.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static ar.com.kriche.minesweeper.domain.CellState.*;

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

    /**
     * @return a new game.
     */
    @PostMapping
    public Game createGame() {
        return gameService.newGame();
    }

    /**
     * @return the existing game by <code>id</code>.
     */
    @GetMapping("/{id}")
    public Game getGame(@PathVariable("id") Long id) {
        return gameService.getGame(id);
    }

    /**
     * @param row    from the cell to make the move.
     * @param column from the cell to make the move
     * @param move   type of move to make.
     * @return the updated game.
     */
    @PatchMapping("/{id}/board/{row}/{column}")
    public Game makeMove(@PathVariable("id") Long gameId,
                         @PathVariable("row") int row,
                         @PathVariable("column") int column,
                         @RequestBody MoveDTO move) {

        // TODO validations and errors.

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
                throw new IllegalArgumentException("unknown type: " + move.getType());
        }
    }

}
