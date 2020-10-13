package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.CellState;
import ar.com.kriche.minesweeper.domain.Game;
import ar.com.kriche.minesweeper.domain.Player;
import ar.com.kriche.minesweeper.service.game.GameService;
import ar.com.kriche.minesweeper.service.player.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static ar.com.kriche.minesweeper.controller.MoveType.*;
import static ar.com.kriche.minesweeper.domain.CellState.REVEALED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Author Kriche 2020
 */
@WebMvcTest(GameController.class)
@ActiveProfiles("test")
public class GameControllerTest {

    private static final String USER_NAME = "Ignacio";
    private static final int ROWS = 13;
    private static final int COLUMNS = 7;
    private static final int MINES = 25;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private PlayerService playerService;

    @Test
    public void createGame() throws Exception {
        // setup:
        Player aPlayer = new Player(USER_NAME);
        Game game = new Game(aPlayer, ROWS, COLUMNS, MINES);
        when(playerService.getPlayerByUserName(USER_NAME)).thenReturn(aPlayer);
        when(gameService.newGame(aPlayer)).thenReturn(game);
        // exercise and verify:
        mockMvc.perform(post("/game/{userName}", USER_NAME)).andExpect(status().isOk());
        verify(playerService).getPlayerByUserName(USER_NAME);
        verify(gameService).newGame(aPlayer);
    }

    @Test
    public void createCustomGame() throws Exception {
        // setup:
        Player aPlayer = new Player(USER_NAME);
        Game game = new Game(aPlayer, ROWS, COLUMNS, MINES);
        when(playerService.getPlayerByUserName(USER_NAME)).thenReturn(aPlayer);
        when(gameService.newGame(aPlayer, ROWS, COLUMNS, MINES)).thenReturn(game);

        // exercise and verify:
        ResultActions resultActions =
                mockMvc.perform(post("/game/{userName}/{rows}/{columns}/{mines}", USER_NAME, ROWS, COLUMNS, MINES)).
                        andDo(print()).andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String json = result.getResponse().getContentAsString();
        Game response = objectMapper.readValue(json, Game.class);
        assertEquals(ROWS, response.getRowSize());
        assertEquals(COLUMNS, response.getColumnSize());
        assertEquals(MINES, response.getMines());
        verify(playerService).getPlayerByUserName(USER_NAME);
        verify(gameService).newGame(aPlayer, ROWS, COLUMNS, MINES);
    }

    @Test
    public void getGame() throws Exception {
        // setup:
        long gameId = 123;
        Player aPlayer = new Player(USER_NAME);
        Game game = new Game(aPlayer, ROWS, COLUMNS, MINES);
        game.setId(gameId);
        when(gameService.getGame(gameId)).thenReturn(game);

        // exercise and verify:
        ResultActions resultActions =
                mockMvc.perform(get("/game/{id}", gameId)).andDo(print()).andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String json = result.getResponse().getContentAsString();
        Game response = objectMapper.readValue(json, Game.class);
        assertEquals(gameId, response.getId());
        verify(gameService).getGame(gameId);
    }

    @Test
    public void pauseGame() throws Exception {
        // setup:
        long gameId = 123;
        // exercise:
        mockMvc.perform(patch("/game/{id}/pause", gameId)).andExpect(status().isOk());
        // verify
        verify(gameService).pauseGame(gameId);
    }

    @Test
    public void resumeGame() throws Exception {
        // setup:
        long gameId = 123;
        // exercise:
        mockMvc.perform(patch("/game/{id}/resume", gameId)).andExpect(status().isOk());
        // verify
        verify(gameService).resumeGame(gameId);
    }

    @Test
    public void makeRedFlagMark() throws Exception {
        makeMark(MARK_RED_FLAG);
    }

    @Test
    public void makeQuestionMark() throws Exception {
        makeMark(MARK_QUESTION);
    }

    @Test
    public void makeNoMark() throws Exception {
        makeMark(REMOVE_MARK);
    }

    @Test
    public void revealCell() throws Exception {
        // setup:
        long gameId = 123;
        int row = 1;
        int column = 2;
        Player aPlayer = new Player(USER_NAME);
        Game game = new Game(aPlayer, ROWS, COLUMNS, MINES);
        game.setId(gameId);
        when(gameService.revealCell(gameId, row, column)).thenReturn(game);

        // exercise and verify:
        ResultActions resultActions =
                mockMvc.perform(patch("/game/{id}/board/{row}/{column}", gameId, row, column)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"" + REVEAL.name() + "\"}")
                        .characterEncoding("UTF-8")
                ).andDo(print()).andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String json = result.getResponse().getContentAsString();
        Game response = objectMapper.readValue(json, Game.class);
        assertEquals(ROWS, response.getRowSize());
        assertEquals(COLUMNS, response.getColumnSize());
        assertEquals(MINES, response.getMines());
        verify(gameService).revealCell(gameId, row, column);
    }

    private void makeMark(MoveType type) throws Exception {

        // setup:
        long gameId = 123;
        int row = 1;
        int column = 2;
        Player aPlayer = new Player(USER_NAME);
        Game game = new Game(aPlayer, ROWS, COLUMNS, MINES);
        game.setId(gameId);
        CellState cellState = typeToCellState(type);
        when(gameService.markCell(gameId, row, column, cellState)).thenReturn(game);

        // exercise and verify:
        ResultActions resultActions =
                mockMvc.perform(patch("/game/{id}/board/{row}/{column}", gameId, row, column)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"" + type.name() + "\"}")
                        .characterEncoding("UTF-8")
                ).andDo(print()).andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String json = result.getResponse().getContentAsString();
        Game response = objectMapper.readValue(json, Game.class);
        assertEquals(ROWS, response.getRowSize());
        assertEquals(COLUMNS, response.getColumnSize());
        assertEquals(MINES, response.getMines());
        verify(gameService).markCell(gameId, row, column, cellState);
    }

    private static CellState typeToCellState(MoveType type) {
        switch (type) {
            case REVEAL:
                return REVEALED;
            case MARK_RED_FLAG:
                return CellState.UNREVEALED_RED_FLAG_MARK;
            case MARK_QUESTION:
                return CellState.UNREVEALED_QUESTION_MARK;
            case REMOVE_MARK:
                return CellState.UNREVEALED_NO_MARK;
            default:
                throw new Error("unknown type: " + type);
        }

    }

}
