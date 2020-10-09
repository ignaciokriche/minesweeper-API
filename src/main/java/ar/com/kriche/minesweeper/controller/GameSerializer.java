package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.Cell;
import ar.com.kriche.minesweeper.domain.Game;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.function.Function;

import static ar.com.kriche.minesweeper.domain.GameState.IN_PROGRESS;

@JsonComponent
public class GameSerializer extends JsonSerializer<Game> {

    @Override
    public void serialize(Game game, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeObjectField("state", game.getState());
        jsonGenerator.writeNumberField("rows", game.getRowSize());
        jsonGenerator.writeNumberField("columns", game.getColumnSize());
        jsonGenerator.writeNumberField("mines", game.getMines());

        Function<Cell, CellDTO> cellToCellDTO;
        if (game.getState() == IN_PROGRESS) {
            // while in progress don't tell if a cell is mined and show adjacent mines only if the cell is revealed:
            cellToCellDTO = cell ->
                    new CellDTO(null, cell.isRevealed() ? cell.getAdjacentMines() : null, cell.getMark(), cell.isRevealed());
        } else {
            // game finished: good to show all the information.
            cellToCellDTO =
                    cell -> new CellDTO(cell.isMined(), cell.getAdjacentMines(), cell.getMark(), cell.isRevealed());
        }
        jsonGenerator.writeObjectField("board",
                game.getBoard().stream().map(r -> r.getCells().stream().map(cell -> cellToCellDTO.apply(cell))));

        jsonGenerator.writeEndObject();
    }

}
