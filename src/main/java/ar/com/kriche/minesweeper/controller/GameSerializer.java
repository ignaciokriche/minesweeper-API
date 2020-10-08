package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.Board;
import ar.com.kriche.minesweeper.domain.Cell;
import ar.com.kriche.minesweeper.domain.Game;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

import static ar.com.kriche.minesweeper.domain.GameState.IN_PROGRESS;

@JsonComponent
public class GameSerializer extends JsonSerializer<Game> {

    @Override
    public void serialize(Game game, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeObjectField("state", game.getState());

        Board theBoard = game.getBoard();
        jsonGenerator.writeNumberField("rows", theBoard.getRowSize());
        jsonGenerator.writeNumberField("columns", theBoard.getColumnSize());
        jsonGenerator.writeNumberField("mines", theBoard.getMines());

        CellToCellDtoConverter cellToCellDtoConverter;
        if (game.getState() == IN_PROGRESS) {
            // while in progress don't tell if a cell is mined and show adjacent mines only if the cell is revealed:
            cellToCellDtoConverter = cell -> new CellDTO(null, cell.isRevealed() ? cell.getAdjacentMines() : null, cell.getMark(), cell.isRevealed());
        } else {
            // game finished: good to show all the information.
            cellToCellDtoConverter = cell -> new CellDTO(cell.isMined(), cell.getAdjacentMines(), cell.getMark(), cell.isRevealed());
        }

        jsonGenerator.writeObjectField("rows", theBoard.getRows().stream().map(r -> r.getCells().stream().map(c -> cellToCellDtoConverter.convert(c))));

        jsonGenerator.writeEndObject();
    }

    interface CellToCellDtoConverter {
        CellDTO convert(Cell cell);
    }
}
