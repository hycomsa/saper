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

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import pl.hycom.exception.ActionException;
import pl.hycom.exception.BombException;
import pl.hycom.exception.GameConfigurationException;
import pl.hycom.model.Board;
import pl.hycom.model.Level;
import pl.hycom.service.BoardService;

@RunWith(MockitoJUnitRunner.class)
public class BoardServiceTest {

    private final File gameFile = new File(getClass().getClassLoader().getResource("file/test.txt").getFile());

    @InjectMocks
    private BoardService boardService;

    @Test(expected = GameConfigurationException.class)
    public void createBoard_forFile_null() throws GameConfigurationException {
        File file = null;

        boardService.createBoard(file);
    }

    @Test
    public void createBoard_forLevel_null() throws GameConfigurationException {
        Level level = null;

        Board board = boardService.createBoard(level);

        Assert.assertNotNull("Board should be generated for level EASY", board);
    }

    @Test
    public void createBoard_forLevel() throws GameConfigurationException {
        Board board = boardService.createBoard(Level.EASY);

        Assert.assertNotNull("Board should not be null", board);
    }

    @Test(expected = ActionException.class)
    public void action_notValid() throws ActionException, GameConfigurationException {
        Board board = boardService.createBoard(Level.EASY);

        boardService.action(board, -3, -3);
    }

    @Test(expected = BombException.class)
    public void action_bomb() throws ActionException, GameConfigurationException {
        Board board = boardService.createBoard(gameFile);

        boardService.action(board, 3, 3);
    }

    @Test
    public void action_ok() throws GameConfigurationException, ActionException {
        int row = 2;
        int column = 3;
        Board board = boardService.createBoard(gameFile);

        boardService.action(board, row, column);

        Assert.assertNotNull("Board should not be null", board);
        Assert.assertFalse("Point at posiotion [" + row + "," + column + "] should be open", !board.prepareDTO().get(row).get(column).isDisabled());
    }

}
