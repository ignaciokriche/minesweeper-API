package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.Player;
import ar.com.kriche.minesweeper.service.player.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Author Kriche 2020
 */
@WebMvcTest(PlayerController.class)
@ActiveProfiles("test")

public class PlayerControllerTest {

    private static final String USER_NAME = "Ignacio";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PlayerService playerService;

    @Test
    public void createPlayer() throws Exception {
        // setup:
        Player aPlayer = new Player(USER_NAME);
        when(playerService.createPlayer(USER_NAME)).thenReturn(aPlayer);
        // exercise and verify:
        ResultActions resultActions = mockMvc.perform(post("/player/{userName}", USER_NAME)).andDo(print()).andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String json = result.getResponse().getContentAsString();
        Player response = objectMapper.readValue(json, Player.class);
        assertEquals(USER_NAME, response.getUserName());
        verify(playerService).createPlayer(USER_NAME);
    }

    @Test
    public void getPlayerByUserName() throws Exception {
        // setup:
        Player aPlayer = new Player(USER_NAME);
        when(playerService.getPlayerByUserName(USER_NAME)).thenReturn(aPlayer);
        // exercise and verify:
        ResultActions resultActions = mockMvc.perform(get("/player/{userName}", USER_NAME)).andDo(print()).andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String json = result.getResponse().getContentAsString();
        Player response = objectMapper.readValue(json, Player.class);
        assertEquals(USER_NAME, response.getUserName());
        verify(playerService).getPlayerByUserName(USER_NAME);
    }

}
