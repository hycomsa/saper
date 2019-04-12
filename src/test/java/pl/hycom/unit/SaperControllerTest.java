/**
 * MIT License
 *
 * Copyright (c) 2019 Hycom S.A.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package pl.hycom.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import pl.hycom.controller.SaperController;
import pl.hycom.exception.BombException;
import pl.hycom.exception.GameNotFoundException;
import pl.hycom.model.Board;
import pl.hycom.model.Board.BoardFactory;
import pl.hycom.service.BoardService;
import pl.hycom.service.GamesHolder;

@RunWith(MockitoJUnitRunner.class)
public class SaperControllerTest {

    @Mock
    private GamesHolder gamesHolder;

    @Mock
    private BoardService boardService;

    @InjectMocks
    private SaperController saperController;

    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = standaloneSetup(saperController).build();
    }

    @Test
    public void get_saper() throws Exception {
        Board board = BoardFactory.createEmpty();
        String gameId = "123abc";

        Mockito.when(gamesHolder.initGame((String) null)).thenReturn(gameId);
        Mockito.when(gamesHolder.getGame(gameId)).thenReturn(board);

        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(get("/saper").session(mockHttpSession)).andReturn();

        assertEquals("GameId should be [" + gameId + "]", mockHttpSession.getAttribute(SaperController.ATTR_GAME_ID), gameId);
    }

    @Test
    public void post_saper_ok() throws Exception {
        Mockito.when(gamesHolder.getGame(any())).thenReturn(BoardFactory.createEmpty());

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.putValue(SaperController.ATTR_GAME_ID, "123");

        MvcResult result = mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "1:1")).andReturn();

        assertEquals("Board screen should be presented", result.getModelAndView().getViewName(), "index");
    }

    @Test
    public void post_saper_winner() throws Exception {
        Board board = BoardFactory.createRandom(0, 1);
        board.check(0, 0);

        Mockito.when(gamesHolder.getGame(any())).thenReturn(board);

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.putValue(SaperController.ATTR_GAME_ID, "123");

        MvcResult result = mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "1:1")).andReturn();

        assertEquals("Win screen should be presented", result.getModelAndView().getViewName(), "winner");
    }

    @Test
    public void post_saper_bomb() throws Exception {
        Board board = BoardFactory.createEmpty();

        Mockito.when(gamesHolder.getGame(any())).thenReturn(board);
        Mockito.doThrow(new BombException("test")).when(boardService).action(board, 1, 1);

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.putValue(SaperController.ATTR_GAME_ID, "123");

        MvcResult result = mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "1:1")).andReturn();

        assertEquals("Game over should returned after hitting bomb", result.getModelAndView().getViewName(), "gameOver");
    }

    @Test
    public void post_saper_action_null() throws Exception {
        Mockito.when(gamesHolder.getGame(any())).thenReturn(BoardFactory.createEmpty());

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.putValue(SaperController.ATTR_GAME_ID, "123");

        MvcResult result = mockMvc.perform(post("/saper").session(mockHttpSession)).andReturn();

        assertEquals("Game over should returned for wrong string", result.getModelAndView().getViewName(), "gameOver");
    }

    @Test
    public void post_saper_action_wrong_string() throws Exception {
        Mockito.when(gamesHolder.getGame(any())).thenReturn(BoardFactory.createEmpty());

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.putValue(SaperController.ATTR_GAME_ID, "123");

        MvcResult result = mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "xxx")).andReturn();

        assertEquals("Game over should returned for wrong string", result.getModelAndView().getViewName(), "gameOver");
    }

    @Test
    public void post_saper_no_game() throws Exception {

        Mockito.when(gamesHolder.getGame(any())).thenThrow(new GameNotFoundException("test"));

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.putValue(SaperController.ATTR_GAME_ID, "123");

        MvcResult result = mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "0:0")).andReturn();

        assertEquals("When game not exists gameOver view should be presented", result.getModelAndView().getViewName(), "gameOver");
    }

}
