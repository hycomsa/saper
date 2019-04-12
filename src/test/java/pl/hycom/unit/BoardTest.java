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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import pl.hycom.exception.ActionException;
import pl.hycom.exception.BombException;
import pl.hycom.exception.GameConfigurationException;
import pl.hycom.model.Board;
import pl.hycom.model.Board.BoardFactory;
import pl.hycom.model.FieldDTO;
import pl.hycom.model.State;

@RunWith(MockitoJUnitRunner.class)
public class BoardTest {

    @Test(expected = GameConfigurationException.class)
    public void testCreateStaticNullFile() throws GameConfigurationException {
        BoardFactory.createStatic(null);
    }

    @Test(expected = GameConfigurationException.class)
    public void testCreateStaticFileIsEmpty() throws GameConfigurationException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("file/emptyTest.txt").getFile());
        BoardFactory.createStatic(file);
    }

    @Test
    public void testCreateStatic() throws GameConfigurationException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("file/test.txt").getFile());
        Board board = BoardFactory.createStatic(file);
        Map<Integer, List<FieldDTO>> boardPoints = board.prepareDTO();
        assertEquals(8, board.prepareDTO().size());

        assertTrue(boardPoints.get(0).get(4).isHasBomb());
        assertTrue(boardPoints.get(1).get(2).isHasBomb());
        assertTrue(boardPoints.get(2).get(0).isHasBomb());
        assertTrue(boardPoints.get(2).get(7).isHasBomb());
        assertTrue(boardPoints.get(3).get(3).isHasBomb());
        assertTrue(boardPoints.get(3).get(7).isHasBomb());
        assertTrue(boardPoints.get(4).get(1).isHasBomb());
        assertTrue(boardPoints.get(4).get(5).isHasBomb());
        assertTrue(boardPoints.get(5).get(4).isHasBomb());
        assertTrue(boardPoints.get(6).get(2).isHasBomb());
        assertTrue(boardPoints.get(7).get(7).isHasBomb());
    }

    @Test(expected = BombException.class)
    public void testCheckClickBomb() throws GameConfigurationException, ActionException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("file/test.txt").getFile());
        Board board = BoardFactory.createStatic(file);
        board.check(0, 4);
    }

    @Test
    public void testCheckBombAround() throws GameConfigurationException, ActionException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("file/test.txt").getFile());
        Board board = BoardFactory.createStatic(file);
        board.check(0, 3);
        Map<Integer, List<FieldDTO>> boardPoints = board.prepareDTO();
        assertTrue(boardPoints.get(0).get(3).isDisabled());
        Assert.assertEquals(State.ACTIVE, board.state());
    }

    @Test(expected = ActionException.class)
    public void testCheckNegativeRow() throws GameConfigurationException, ActionException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("file/test.txt").getFile());
        Board board = BoardFactory.createStatic(file);
        board.check(-1, 3);
    }

    @Test(expected = ActionException.class)
    public void testCheckNegativeColumn() throws GameConfigurationException, ActionException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("file/test.txt").getFile());
        Board board = BoardFactory.createStatic(file);
        board.check(1, -3);
    }

    @Test(expected = ActionException.class)
    public void testCheckNegativeRowAndColumn() throws GameConfigurationException, ActionException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("file/test.txt").getFile());
        Board board = BoardFactory.createStatic(file);
        board.check(-1, -3);
    }

    @Test(expected = GameConfigurationException.class)
    public void testCreateRandomNumberOfBombsHigherThanBoardSize() throws GameConfigurationException {
        BoardFactory.createRandom(37, 6);
    }

    @Test(expected = GameConfigurationException.class)
    public void testCreateRandomNegativeNumberOfBombs() throws GameConfigurationException {
        BoardFactory.createRandom(-1, 10);
    }

    @Test(expected = GameConfigurationException.class)
    public void testCreateRandomNegativeBoardSize() throws GameConfigurationException {
        BoardFactory.createRandom(1, -10);
    }

    @Test(expected = GameConfigurationException.class)
    public void testCreateRandomNegativeNumberOfBombsAndBoardSize() throws GameConfigurationException {
        BoardFactory.createRandom(-1, -10);
    }

    @Test
    public void testCreateRandom() throws GameConfigurationException {
        int numberOfBombs = 6;
        int boardSize = 10;
        Board board = BoardFactory.createRandom(numberOfBombs, boardSize);

        Map<Integer, List<FieldDTO>> boardPoints = board.prepareDTO();

        long findedBombs = boardPoints.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .filter(FieldDTO::isHasBomb)
                .count();

        Assert.assertEquals(boardSize, boardPoints.size());
        Assert.assertEquals(numberOfBombs, findedBombs);
    }

    @Test
    public void testCheckIfUserWinner() throws GameConfigurationException, ActionException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("file/test.txt").getFile());
        Board board = BoardFactory.createStatic(file);
        board.check(0, 0);
        board.check(0, 2);
        board.check(0, 3);
        board.check(0, 7);
        board.check(1, 3);
        board.check(1, 4);
        board.check(7, 0);
        board.check(7, 6);
        board.check(6, 6);
        board.check(6, 7);
        board.check(2, 1);
        board.check(2, 2);
        board.check(2, 3);
        board.check(2, 4);
        board.check(3, 0);
        board.check(3, 1);
        board.check(3, 2);
        board.check(4, 0);
        board.check(4, 2);
        board.check(4, 3);
        board.check(4, 7);
        board.check(5, 2);
        board.check(5, 3);
        board.check(5, 5);
        board.check(5, 6);
        board.check(6, 3);
        board.check(6, 4);
        board.check(7, 2);
        board.check(7, 3);
        board.check(7, 4);
        board.check(2, 5);
        board.check(5, 7);
        board.check(4, 4);

        Assert.assertEquals(State.WIN, board.state());
    }

    @Test
    public void testUpdateStateIfSessionExpiredActive() throws GameConfigurationException {
        int numberOfBombs = 6;
        int boardSize = 10;
        Board board = BoardFactory.createRandom(numberOfBombs, boardSize);
        board.updateStateIfGameExpired(600);
        Assert.assertEquals(State.ACTIVE, board.state());
    }

    @Test
    public void testUpdateStateIfSessionExpiredFail() throws GameConfigurationException {
        int numberOfBombs = 6;
        int boardSize = 10;
        Board board = BoardFactory.createRandom(numberOfBombs, boardSize);
        board.updateStateIfGameExpired(0);
        Assert.assertEquals(State.FAIL, board.state());
    }

}
