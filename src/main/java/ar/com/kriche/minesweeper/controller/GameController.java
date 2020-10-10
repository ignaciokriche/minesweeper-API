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
     * @return the existing game.
     */
    @GetMapping()
    public Game getGame() {
        return gameService.getGame();
    }

    /**
     * @param row    from the cell to make the move.
     * @param column from the cell to make the move
     * @param move   type of move to make.
     * @return the updated game.
     */
    @PatchMapping("/board/{row}/{column}")
    public Game makeMove(@PathVariable("row") int row,
                         @PathVariable("column") int column,
                         @RequestBody MoveDTO move) {
        // TODO validations and errors.
        switch (move.getType()) {
            case REVEAL:
                return gameService.revealCell(getGame(), row, column);
            case MARK_QUESTION:
                return gameService.markCell(getGame(), row, column, UNREVEALED_QUESTION_MARK);
            case MARK_RED_FLAG:
                return gameService.markCell(getGame(), row, column, UNREVEALED_RED_FLAG_MARK);
            case REMOVE_MARK:
                return gameService.markCell(getGame(), row, column, UNREVEALED_NO_MARK);
            default:
                throw new IllegalArgumentException("unknown type: " + move.getType());
        }
    }

}
