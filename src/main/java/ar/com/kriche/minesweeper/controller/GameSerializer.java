package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.Cell;
import ar.com.kriche.minesweeper.domain.Game;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.function.Function;

import static ar.com.kriche.minesweeper.domain.GameState.IN_PROGRESS;

/**
 * @Author Kriche 2020
 */
@JsonComponent
@Profile("!test") // we want the "real domain" game for testing.
public class GameSerializer extends JsonSerializer<Game> {

    @Override
    public void serialize(Game game, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("id", game.getId());
        jsonGenerator.writeObjectField("state", game.getState());
        jsonGenerator.writeNumberField("rows", game.getRowSize());
        jsonGenerator.writeNumberField("columns", game.getColumnSize());
        jsonGenerator.writeNumberField("mines", game.getMines());
        jsonGenerator.writeNumberField("availableFlags", game.getAvailableFlags());
        jsonGenerator.writeNumberField("revealedCells", game.getRevealedCells());
        jsonGenerator.writeNumberField("elapsedTimeMilliseconds", game.getElapsedTimeMilliseconds());

        Function<Cell, CellDTO> cellToCellDTO;
        if (game.getState() == IN_PROGRESS) {
            // while in progress don't tell if a cell is mined and show adjacent mines only if the cell is revealed:
            cellToCellDTO = cell -> new CellDTO(null, cell.isRevealed() ? cell.getAdjacentMines() : null, cell.getState());
        } else {
            // game finished: good to show all the information.
            cellToCellDTO = cell -> new CellDTO(cell.isMined(), cell.getAdjacentMines(), cell.getState());
        }
        jsonGenerator.writeObjectField("board",
                game.getBoard().stream().map(r -> r.getCells().stream().map(cell -> cellToCellDTO.apply(cell))));

        jsonGenerator.writeEndObject();
    }

}
