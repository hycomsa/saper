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

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import pl.hycom.exception.GameConfigurationException;
import pl.hycom.exception.GameNotFoundException;
import pl.hycom.model.Board;
import pl.hycom.model.Board.BoardFactory;
import pl.hycom.model.Level;
import pl.hycom.service.BoardService;
import pl.hycom.service.GamesHolder;

@RunWith(MockitoJUnitRunner.class)
public class GamesHolderTest {

    @Mock
    private BoardService boardService;

    @InjectMocks
    private GamesHolder gamesHolder;

    @Test
    public void initGame_fromFile() throws GameNotFoundException, IOException, GameConfigurationException {
        File file = File.createTempFile("test", ".tmp");
        file.deleteOnExit();

        Board board = BoardFactory.createEmpty();

        Mockito.when(boardService.createBoard(file)).thenReturn(board);

        String gameId = gamesHolder.initGame(file);
        assertEquals("Board state not created correctly", board, gamesHolder.getGame(gameId));
    }

    @Test(expected = GameConfigurationException.class)
    public void initGame_fromFile_null() throws GameNotFoundException, IOException, GameConfigurationException {
        File file = null;

        Mockito.when(boardService.createBoard(file)).thenThrow(new GameConfigurationException("test"));

        gamesHolder.initGame(file);
    }

    public void initGame_forLevel_enum() throws GameNotFoundException, GameConfigurationException {
        Board board = BoardFactory.createEmpty();

        Mockito.when(boardService.createBoard(Level.HARD)).thenReturn(board);

        String gameId = gamesHolder.initGame("hard");
        assertEquals("Board state not created correctly", board, gamesHolder.getGame(gameId));
    }

    public void initGame_forLevel_null() throws GameNotFoundException, GameConfigurationException {
        Board board = BoardFactory.createEmpty();

        Mockito.when(boardService.createBoard(Level.EASY)).thenReturn(board);

        String level = null;
        String gameId = gamesHolder.initGame(level);
        assertEquals("Board state not created correctly", board, gamesHolder.getGame(gameId));
    }

    public void initGame_forLevel_xxx() throws GameNotFoundException, GameConfigurationException {
        Board board = BoardFactory.createEmpty();

        Mockito.when(boardService.createBoard(Level.EASY)).thenReturn(board);

        String gameId = gamesHolder.initGame("xxx");
        assertEquals("Board state not created correctly", board, gamesHolder.getGame(gameId));
    }

    @Test(expected = GameNotFoundException.class)
    public void gamesSupervisor() throws GameNotFoundException, GameConfigurationException {
        Board board = BoardFactory.createEmpty();

        Mockito.when(boardService.createBoard(Level.EASY)).thenReturn(board);
        ReflectionTestUtils.setField(gamesHolder, "secondsAfterGameExpires", 0);

        String gameId = gamesHolder.initGame("easy");
        gamesHolder.gamesSupervisor();
        gamesHolder.getGame(gameId);
    }
}
